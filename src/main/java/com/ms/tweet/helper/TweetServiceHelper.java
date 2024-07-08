package com.ms.tweet.helper;

import main.java.com.leon.baobui.dto.request.NotificationRequest;
import main.java.com.leon.baobui.dto.response.tweet.TweetResponse;
import main.java.com.leon.baobui.enums.LinkCoverSize;
import main.java.com.leon.baobui.enums.NotificationType;
import main.java.com.leon.baobui.mapper.BasicMapper;
import main.java.com.leon.baobui.util.AuthUtil;
import com.ms.tweet.client.NotificationClient;
import com.ms.tweet.client.UserClient;
import com.ms.tweet.client.YoutubeClient;
import com.ms.tweet.model.Tweet;
import com.ms.tweet.repository.TweetRepository;
import com.ms.tweet.repository.projection.RetweetProjection;
import com.ms.tweet.repository.projection.TweetProjection;
import com.ms.tweet.repository.projection.TweetUserProjection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.weaver.ast.Not;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class TweetServiceHelper {
    private final TweetRepository tweetRepository;
    private final TweetValidationHelper tweetValidationHelper;
    private final UserClient userClient;
    private final YoutubeClient youtubeClient;
    private final NotificationClient notificationClient;
    private final BasicMapper basicMapper;

    // https?:\/\/?[\w\d\._\-%\/\?=&#]+
    private static final String urlRegexString = "https?:\\/\\/?[\\w\\d\\._\\-%\\/\\?=&#]+";
    // .(jpeg|jpg|gif|png)
    private static final String imgRegexString = "\\.(jpeg|jpg|gif|png)$";
    public static final String youtubeRegexString = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

    // This createTweet method here is not a good design
    // because it holds too many business logic and utils method as well
    // TODO: need to refactor later, separate tag, mention, notify action and parse data into other utils class
    @Transactional
    public TweetResponse createTweet(Tweet tweet)  {
        tweetValidationHelper.checkTweetTextLength(tweet.getText());
        Long userId = AuthUtil.getAuthenticatedUserId();
        tweet.setAuthorId(userId);
        boolean isMediaTweetCreated = parseMetadataFromURL(tweet);
        tweetRepository.save(tweet);

        if (tweet.getScheduledDate() == null) {
            //TODO: check N+1 query here for images (Currently it's not cause this images field is retrieved directly from json object)
            if (isMediaTweetCreated || !tweet.getImages().isEmpty()) {
                userClient.updateMediaTweetCount(true);
            } else {
                userClient.updateTweetCount(true);
            }
        }

        TweetResponse tweetResponse = buildTweetResponse(tweet);

        parseUserMentionFromTextAndNotifyUser(tweetResponse);
//        tagClient.parseHashtagsFromText(tweet.getId(), new TweetTextRequest(tweet.getText()));
//        notificationClient.sendTweetNotificationToSubscribers(tweet.getId());
        return tweetResponse;
    }

    private TweetResponse buildTweetResponse(Tweet tweet) {
        TweetProjection tweetProjection = tweetRepository
                .getTweetById(tweet.getId(), TweetProjection.class).get();
        return basicMapper.convertToResponse(tweetProjection, TweetResponse.class);
    }

    private void parseUserMentionFromTextAndNotifyUser(TweetResponse tweetResponse) {
        Pattern pattern = Pattern.compile("(@\\w+)\\b");
        Matcher match = pattern.matcher(tweetResponse.getText());
        List<String> usernames = new ArrayList<>();

        while (match.find()) {
            usernames.add(match.group(1));
        }

        if (!usernames.isEmpty()) {
            usernames.forEach(username -> {
                Long userId = userClient.getUserIdByUsername(username);

                if (userId != null) {
                    Long authUserId = AuthUtil.getAuthenticatedUserId();
                    NotificationRequest request = NotificationRequest.builder()
                            .tweetId((tweetResponse.getAddressedTweetId() != null)
                                    ? tweetResponse.getAddressedTweetId()
                                    : tweetResponse.getId())
                            .notificationType(NotificationType.MENTION)
                            .tweet(tweetResponse)
                            .notifiedUserId(userId)
                            .userId(authUserId)
                            .build();
                    notificationClient.sendTweetMentionNotification(request);
                }
            });
        }
    }

    @SneakyThrows
    private boolean parseMetadataFromURL(Tweet tweet) {
        Pattern urlRegex = Pattern.compile(urlRegexString, Pattern.CASE_INSENSITIVE);
        Pattern imgRegex = Pattern.compile(imgRegexString);
        Pattern youtubeRegex = Pattern.compile(youtubeRegexString);

        String text = tweet.getText();
        Matcher matcher = urlRegex.matcher(text);
        if (matcher.find()) {
            String url = text.substring(matcher.start(), matcher.end());
            matcher = imgRegex.matcher(url);
            tweet.setLink(url);
            if (matcher.find()) {
                tweet.setLinkCover(url);
            } else if (url.contains("youtu")){
                String youTubeVideoId  = null;
                Matcher youtubeMatcher = youtubeRegex.matcher(url);

                if (youtubeMatcher.find()) {
                    youTubeVideoId  = youtubeMatcher.group();
                }

                String videoData = youtubeClient.getYouTubeVideoData(youTubeVideoId);
                JSONObject vidResourceJson = new JSONObject(videoData);
                JSONArray items = vidResourceJson.getJSONArray("items");

                String videoTitle = items.getJSONObject(0)
                        .getJSONObject("snippet")
                        .getString("title");

                String videoCoverImage = items.getJSONObject(0)
                        .getJSONObject("snippet")
                        .getJSONObject("thumbnails")
                        .getJSONObject("medium")
                        .getString("url");

                tweet.setLinkTitle(videoTitle);
                tweet.setLinkCover(videoCoverImage);

                return true;
            } else {
                //HTML docs
                Document doc = Jsoup.connect(url).get();
                Elements title = doc.select("meta[name$=title],meta[property$=title]");
                Elements description = doc.select("meta[name$=description],meta[property$=description]");
                Elements cover = doc.select("meta[name$=image],meta[property$=image]");

                BufferedImage coverData = ImageIO.read(new URL(getContent(cover.first())));
                double coverDataSize = (504.0 / (double) coverData.getWidth()) * coverData.getHeight();

                tweet.setLinkTitle(getContent(title.first()));
                tweet.setLinkDescription(getContent(description.first()));
                tweet.setLinkCover(getContent(cover.first()));
                tweet.setLinkCoverSize(coverDataSize > 267.0 ? LinkCoverSize.SMALL : LinkCoverSize.LARGE);
            }
        }
        return false;
    }

    private String getContent(Element element) {
        return element == null ? "" : element.attr("content");
    }

    public <T> Page<T> getPageableTweetProjectionList(
            Pageable pageable,
            List<T> tweets,
            int totalPages
    ) {
        PagedListHolder<T> page = new PagedListHolder<>(tweets);
        page.setPage(pageable.getPageNumber());
        page.setPageSize(pageable.getPageSize());
        return new PageImpl<>(page.getPageList(), pageable, totalPages);
    }

    public List<TweetUserProjection> combineTweetsArraysOnDateOrderDsc(List<TweetUserProjection> tweets,
                                                         List<RetweetProjection> retweets) {
        List<TweetUserProjection> allTweets = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < tweets.size() && j < retweets.size()) {
            if (tweets.get(i).getDateTime().isAfter(retweets.get(j).getRetweetDate())) {
                allTweets.add(tweets.get(i));
                i++;
            } else {
                allTweets.add(retweets.get(j).getTweet());
                j++;
            }
        }

        if (i < tweets.size()) {
            allTweets.addAll(tweets.subList(i, tweets.size()));
        }

        if (j < retweets.size()) {
            List<TweetUserProjection> retweetsRemaining = retweets
                    .stream()
                    .map(RetweetProjection::getTweet)
                    .toList();

            allTweets.addAll(retweetsRemaining);
        }

        return allTweets;
    }
}
