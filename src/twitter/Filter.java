/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Filter consists of methods that filter a list of tweets for those matching a
 * condition.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Filter {

    /**
     * Find tweets written by a particular user.
     */
    public static List<Tweet> writtenBy(List<Tweet> tweets, String username) {
        List<Tweet> result = new ArrayList<>();
        for (Tweet tweet : tweets) {
            if (tweet.getAuthor().equalsIgnoreCase(username)) {
                result.add(tweet);
            }
        }
        return result;
    }

    /**
     * Find tweets that were sent during a particular timespan.
     */
    public static List<Tweet> inTimespan(List<Tweet> tweets, Timespan timespan) {
        List<Tweet> result = new ArrayList<>();
        Instant start = timespan.getStart();
        Instant end = timespan.getEnd();

        for (Tweet tweet : tweets) {
            Instant t = tweet.getTimestamp();
            if ((t.equals(start) || t.isAfter(start)) && (t.equals(end) || t.isBefore(end))) {
                result.add(tweet);
            }
        }
        return result;
    }

    /**
     * Find tweets that contain certain words.
     */
    public static List<Tweet> containing(List<Tweet> tweets, List<String> words) {
        List<Tweet> result = new ArrayList<>();
        if (words.isEmpty()) return result;

        Set<String> lowerWords = new HashSet<>();
        for (String word : words) {
            lowerWords.add(word.toLowerCase());
        }

        for (Tweet tweet : tweets) {
            String[] textWords = tweet.getText().split("\\s+");
            for (String textWord : textWords) {
                if (lowerWords.contains(textWord.toLowerCase())) {
                    result.add(tweet);
                    break; // include tweet only once
                }
            }
        }
        return result;
    }
}
