/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * Testing strategy
     * 
     * getTimespan(tweets):
     *  - Number of tweets:
     *      * 1 tweet → start = end = that timestamp
     *      * >1 tweet → start = earliest, end = latest
     *  - Order of tweets:
     *      * chronological
     *      * reverse order
     *  - Tweets with same timestamp
     * 
     * getMentionedUsers(tweets):
     *  - Number of tweets:
     *      * 0 tweet → empty set
     *      * 1 tweet → depends on content
     *      * >1 tweet → combined mentions
     *  - Mentions:
     *      * no mentions
     *      * valid mention (“@user”)
     *      * invalid (preceded/followed by username-valid character)
     *      * email-like strings (“a@b.com”) → not mention
     *  - Case sensitivity:
     *      * same username with different cases should count once
     *  - Multiple mentions of same name → count once
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "charlie", "mentioning @bob and @alice today", d3);

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // --- getTimespan() tests ---
    
    @Test
    public void testGetTimespanOneTweet() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
        assertEquals("start = end for one tweet", d1, timespan.getStart());
        assertEquals("start = end for one tweet", d1, timespan.getEnd());
    }

    @Test
    public void testGetTimespanTwoTweetsChronological() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

    @Test
    public void testGetTimespanOutOfOrderTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet2, tweet1));
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

    @Test
    public void testGetTimespanSameTimestamp() {
        Tweet t1 = new Tweet(1, "alyssa", "same time", d1);
        Tweet t2 = new Tweet(2, "bob", "same time again", d1);
        Timespan span = Extract.getTimespan(Arrays.asList(t1, t2));
        assertEquals("start and end both d1", d1, span.getStart());
        assertEquals("start and end both d1", d1, span.getEnd());
    }

    // --- getMentionedUsers() tests ---
    
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(tweet1));
        assertTrue("expected empty set", mentioned.isEmpty());
    }

    @Test
    public void testGetMentionedUsersSingleMention() {
        Tweet t = new Tweet(1, "alyssa", "hello @bob", d1);
        Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(t));
        assertTrue("should contain bob", mentioned.contains("bob"));
        assertEquals("one mention", 1, mentioned.size());
    }

    @Test
    public void testGetMentionedUsersCaseInsensitive() {
        Tweet t1 = new Tweet(1, "alyssa", "hello @Bob", d1);
        Tweet t2 = new Tweet(2, "bob", "thanks @BOB", d2);
        Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(t1, t2));
        assertTrue("should contain lowercase bob", mentioned.contains("bob"));
        assertEquals("should only contain bob once", 1, mentioned.size());
    }

    @Test
    public void testGetMentionedUsersInvalidEmailLike() {
        Tweet t = new Tweet(1, "alyssa", "contact us at help@mit.edu", d1);
        Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(t));
        assertTrue("email should not count as mention", mentioned.isEmpty());
    }

    @Test
    public void testGetMentionedUsersMultipleMentions() {
        Tweet t = new Tweet(1, "alyssa", "@bob and @charlie are here", d1);
        Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(t));
        assertEquals("should have bob and charlie", Set.of("bob", "charlie"), mentioned);
    }

    @Test
    public void testGetMentionedUsersCombinedAcrossTweets() {
        Tweet t1 = new Tweet(1, "alyssa", "talking to @bob", d1);
        Tweet t2 = new Tweet(2, "bob", "replying to @charlie", d2);
        Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(t1, t2));
        assertEquals("should have bob and charlie", Set.of("bob", "charlie"), mentioned);
    }

    @Test
    public void testGetMentionedUsersWithPunctuation() {
        Tweet t = new Tweet(1, "alyssa", "hi @bob! and also @alice.", d1);
        Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(t));
        assertEquals("should extract mentions correctly", Set.of("bob", "alice"), mentioned);
    }
}
