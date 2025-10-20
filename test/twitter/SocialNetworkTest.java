package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.junit.Test;

public class SocialNetworkTest {

    // ===== Task 1: guessFollowsGraph() =====

    // 1. Empty List of Tweets
    @Test
    public void testEmptyTweetsList() {
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(List.of());
        assertTrue(graph.isEmpty());
    }

    // 2. Tweets Without Mentions
    @Test
    public void testNoMentions() {
        Tweet t = new Tweet(1, "alice", "good morning everyone", Instant.now());
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(List.of(t));
        assertTrue(graph.isEmpty());
    }

    // 3. Single Mention
    @Test
    public void testSingleMention() {
        Tweet t = new Tweet(1, "alice", "hi @bob", Instant.now());
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(List.of(t));
        assertTrue(graph.containsKey("alice"));
        assertTrue(graph.get("alice").contains("bob"));
    }

    // 4. Multiple Mentions
    @Test
    public void testMultipleMentions() {
        Tweet t = new Tweet(1, "alice", "hi @bob and @charlie", Instant.now());
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(List.of(t));
        assertEquals(Set.of("bob", "charlie"), graph.get("alice"));
    }

    // 5. Multiple Tweets from One User
    @Test
    public void testMultipleTweetsSameUser() {
        Tweet t1 = new Tweet(1, "alice", "hi @bob", Instant.now());
        Tweet t2 = new Tweet(2, "alice", "yo @charlie", Instant.now());
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(List.of(t1, t2));
        assertEquals(Set.of("bob", "charlie"), graph.get("alice"));
    }

    // ===== Task 2 & 3: influencers() =====

    // 6. Empty Graph for influencers()
    @Test
    public void testEmptyGraphInfluencers() {
        List<String> influencers = SocialNetwork.influencers(Map.of());
        assertTrue(influencers.isEmpty());
    }

    // 7. Single User Without Followers
    @Test
    public void testSingleUserNoFollowers() {
        Map<String, Set<String>> graph = Map.of("alice", Set.of());
        List<String> influencers = SocialNetwork.influencers(graph);
        assertTrue(influencers.isEmpty());
    }

    // 8. Single Influencer
    @Test
    public void testSingleInfluencer() {
        Map<String, Set<String>> graph = Map.of("alice", Set.of("bob"));
        List<String> influencers = SocialNetwork.influencers(graph);
        assertEquals(List.of("bob"), influencers);
    }

    // 9. Multiple Influencers
    @Test
    public void testMultipleInfluencers() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alice", Set.of("bob", "charlie"));
        graph.put("david", Set.of("bob"));
        List<String> influencers = SocialNetwork.influencers(graph);

        // bob should appear first (2 followers), then charlie (1 follower)
        assertEquals("bob", influencers.get(0));
        assertTrue(influencers.indexOf("bob") < influencers.indexOf("charlie"));
    }

    // 10. Tied Influence
    @Test
    public void testTiedInfluence() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alice", Set.of("bob"));
        graph.put("charlie", Set.of("david"));
        List<String> influencers = SocialNetwork.influencers(graph);

        // both bob and david have one follower each
        assertTrue(influencers.containsAll(List.of("bob", "david")));
        assertEquals(2, influencers.size());
    }
}
