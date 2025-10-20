package twitter;

import java.util.*;
import java.util.stream.Collectors;

public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();

            // Use Extract.getMentionedUsers() from the problem set
            Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(tweet));

            // Remove self-mentions
            mentionedUsers.remove(author);

            if (!mentionedUsers.isEmpty()) {
                followsGraph.putIfAbsent(author, new HashSet<>());
                followsGraph.get(author).addAll(
                    mentionedUsers.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet())
                );
            }
        }

        return followsGraph;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCount = new HashMap<>();

        // Count followers for each mentioned user
        for (Set<String> followedUsers : followsGraph.values()) {
            for (String user : followedUsers) {
                String normalizedUser = user.toLowerCase();
                followerCount.put(normalizedUser, followerCount.getOrDefault(normalizedUser, 0) + 1);
            }
        }

        // Sort users by follower count (descending)
        return followerCount.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
