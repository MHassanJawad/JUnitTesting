/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * Testing strategy:
     * 
     * writtenBy():
     *  - No tweets
     *  - One tweet matches
     *  - Multiple tweets, multiple matches
     *  - Case-insensitive match
     * 
     * inTimespan():
     *  - No tweets
     *  - Some tweets inside timespan
     *  - All tweets inside timespan
     *  - Tweet exactly on start/end boundaries
     * 
     * containing():
     *  - No tweets
     *  - Some tweets contain one or more words
     *  - Case-insensitive match
     *  - Multiple words matched
     *  - No match at all
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    // Note: tweet1 contains the exact word "talk" so tests expecting "talk" to match will pass.
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "Alyssa", "Another talk by Rivest", d3);

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // ensure assertions enabled (-ea)
    }

    // writtenBy()

    @Test
    public void testWrittenByNoTweets() {
        List<Tweet> result = Filter.writtenBy(Collections.emptyList(), "alyssa");
        assertTrue("expected empty result", result.isEmpty());
    }

    @Test
    public void testWrittenBySingleMatch() {
        List<Tweet> result = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        assertEquals(1, result.size());
        assertTrue(result.contains(tweet1));
    }

    @Test
    public void testWrittenByCaseInsensitive() {
        List<Tweet> result = Filter.writtenBy(Arrays.asList(tweet1, tweet3), "ALYSSA");
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(tweet1, tweet3)));
    }

    // inTimespan()

    @Test
    public void testInTimespanAllTweets() {
        Timespan span = new Timespan(d1, d3);
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), span);
        assertEquals(3, result.size());
    }

    @Test
    public void testInTimespanBoundary() {
        Timespan span = new Timespan(d1, d2);
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), span);
        assertTrue(result.containsAll(Arrays.asList(tweet1, tweet2)));
        assertFalse(result.contains(tweet3));
    }

    @Test
    public void testInTimespanEmpty() {
        Timespan span = new Timespan(Instant.parse("2016-02-17T13:00:00Z"), Instant.parse("2016-02-17T14:00:00Z"));
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet1, tweet2), span);
        assertTrue(result.isEmpty());
    }

    // containing()

    @Test
    public void testContainingSingleWordMatch() {
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(tweet1, tweet2)));
    }

    @Test
    public void testContainingCaseInsensitive() {
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("RIVEST"));
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(tweet1, tweet2)));
    }

    @Test
    public void testContainingMultipleWordsSomeMatch() {
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("minutes", "none"));
        assertEquals(1, result.size());
        assertTrue(result.contains(tweet2));
    }

    @Test
    public void testContainingNoMatch() {
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("apple"));
        assertTrue(result.isEmpty());
    }
}
