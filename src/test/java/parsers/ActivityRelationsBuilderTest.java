package parsers;

import org.junit.jupiter.api.Test;
import reader.ProcessLogReader;
import structures.Activity;
import structures.ActivityDirectSuccession;
import structures.ActivityRelation;
import structures.EntryPair;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static structures.ActivityRelation.RelationType.*;

class ActivityRelationsBuilderTest {
    /**
     * Activities list should be always without duplicates
     */
    @Test
    void generateUniqueActivities() {
        Scanner log = new Scanner("ABCB\nACB");
        List<List<Activity>> input = new ProcessLogReader().readProcessLog(log);

        Set<Activity> response = new ActivityRelationsBuilder().generateUniqueActivities(input);

        assertEquals(3, response.size());
        assertTrue(response.contains(new Activity("A")));
        assertTrue(response.contains(new Activity("B")));
        assertTrue(response.contains(new Activity("C")));
    }

    /**
     * Direct Successions based only on activities in the single case record
     */
    @Test
    void directSuccessions() {
        Activity F = new Activity("F");
        Activity A = new Activity("A");
        Activity K = new Activity("K");

        Scanner log = new Scanner("FF\nAK");
        List<List<Activity>> input = new ProcessLogReader().readProcessLog(log);

        Set<ActivityDirectSuccession> response = new ActivityRelationsBuilder().generateDirectSuccessions(input);

        assertEquals(2, response.size());
        assertTrue(response.contains(new ActivityDirectSuccession(F, F)));
        assertTrue(response.contains(new ActivityDirectSuccession(A, K)));
    }

    /**
     * In direct successions list, we do NOT expect to see duplicates
     */
    @Test
    void noDuplicationsInRelations() {
        Activity F = new Activity("F");

        Scanner log = new Scanner("FFF");
        List<List<Activity>> input = new ProcessLogReader().readProcessLog(log);

        Set<ActivityDirectSuccession> response = new ActivityRelationsBuilder().generateDirectSuccessions(input);

        assertEquals(1, response.size());
        assertTrue(response.contains(new ActivityDirectSuccession(F, F)));
    }

    /**
     * Direction succession is not transitive
     */
    @Test
    void directSuccessionIsNotTransitive() {
        Activity F = new Activity("F");
        Activity A = new Activity("A");
        Activity K = new Activity("K");

        Scanner log = new Scanner("AKF");
        List<List<Activity>> input = new ProcessLogReader().readProcessLog(log);

        Set<ActivityDirectSuccession> response = new ActivityRelationsBuilder().generateDirectSuccessions(input);

        assertEquals(2, response.size());
        assertTrue(response.contains(new ActivityDirectSuccession(A, K)));
        assertTrue(response.contains(new ActivityDirectSuccession(K, F)));
        assertFalse(response.contains(new ActivityDirectSuccession(A, F)));
    }

    /**
     * Test relations between activities in log:
     * 1. abcd
     * 2. acbd
     * 3. aed
     * <p>
     * We expect to receive
     * ... a   b   c   d   e
     * a   #   ->  ->  #   ->
     * b   <-  #   ||  ->  #
     * c   <-  ||  #   ->  #
     * d   #   <-  <-  #   <-
     * e   <-  #   #   ->  #
     */
    @Test
    void checkRelationsBetweenActivities() {
        Activity A = new Activity("a");
        Activity B = new Activity("b");
        Activity C = new Activity("c");
        Activity D = new Activity("d");
        Activity E = new Activity("e");
        Scanner log = new Scanner("abcd\nacbd\naed");
        List<List<Activity>> input = new ProcessLogReader().readProcessLog(log);

        List<ActivityRelation> response = new ActivityRelationsBuilder().generateRelationsBetweenActivities(input);

        assertEquals(5 * 5, response.size());

        assertEquals(new ActivityRelation(A, A, CHOICE), response.get(0));
        assertEquals(new ActivityRelation(A, B, CAUSALITY), response.get(1));
        assertEquals(new ActivityRelation(A, C, CAUSALITY), response.get(2));
        assertEquals(new ActivityRelation(A, D, CHOICE), response.get(3));
        assertEquals(new ActivityRelation(A, E, CAUSALITY), response.get(4));

        assertEquals(new ActivityRelation(B, A, REVERSED_CAUSALITY), response.get(5));
        assertEquals(new ActivityRelation(B, B, CHOICE), response.get(6));
        assertEquals(new ActivityRelation(B, C, PARALLEL), response.get(7));
        assertEquals(new ActivityRelation(B, D, CAUSALITY), response.get(8));
        assertEquals(new ActivityRelation(B, E, CHOICE), response.get(9));

        assertEquals(new ActivityRelation(C, A, REVERSED_CAUSALITY), response.get(10));
        assertEquals(new ActivityRelation(C, B, PARALLEL), response.get(11));
        assertEquals(new ActivityRelation(C, C, CHOICE), response.get(12));
        assertEquals(new ActivityRelation(C, D, CAUSALITY), response.get(13));
        assertEquals(new ActivityRelation(C, E, CHOICE), response.get(14));

        assertEquals(new ActivityRelation(D, A, CHOICE), response.get(15));
        assertEquals(new ActivityRelation(D, B, REVERSED_CAUSALITY), response.get(16));
        assertEquals(new ActivityRelation(D, C, REVERSED_CAUSALITY), response.get(17));
        assertEquals(new ActivityRelation(D, D, CHOICE), response.get(18));
        assertEquals(new ActivityRelation(D, E, REVERSED_CAUSALITY), response.get(19));

        assertEquals(new ActivityRelation(E, A, REVERSED_CAUSALITY), response.get(20));
        assertEquals(new ActivityRelation(E, B, CHOICE), response.get(21));
        assertEquals(new ActivityRelation(E, C, CHOICE), response.get(22));
        assertEquals(new ActivityRelation(E, D, CAUSALITY), response.get(23));
        assertEquals(new ActivityRelation(E, E, CHOICE), response.get(24));
    }

    /**
     * In response we expect receive each possible pair where ({left} -> {right})
     * and both left, right has inside # as relation
     */
    @Test
    void relationsBasedOnActivityLogContainAllPossibleGroups() {
        Scanner log = new Scanner("abcd\nacbd\naed");
        List<List<Activity>> input = new ProcessLogReader().readProcessLog(log);

        List<EntryPair> response = new ActivityRelationsBuilder().findRelationsBasedOnLog(input);

        assertEquals(10, response.size());

        assertTrue(response.contains(new EntryPair(createSet(new String[]{"a"}), createSet(new String[]{"b"}))));
        assertTrue(response.contains(new EntryPair(createSet(new String[]{"a"}), createSet(new String[]{"c"}))));
        assertTrue(response.contains(new EntryPair(createSet(new String[]{"a"}), createSet(new String[]{"e"}))));
        assertTrue(response.contains(new EntryPair(createSet(new String[]{"b"}), createSet(new String[]{"d"}))));
        assertTrue(response.contains(new EntryPair(createSet(new String[]{"c"}), createSet(new String[]{"d"}))));
        assertTrue(response.contains(new EntryPair(createSet(new String[]{"e"}), createSet(new String[]{"d"}))));
        assertTrue(response.contains(new EntryPair(createSet(new String[]{"a"}), createSet(new String[]{"b", "e"}))));
        assertTrue(response.contains(new EntryPair(createSet(new String[]{"a"}), createSet(new String[]{"c", "e"}))));
        assertTrue(response.contains(new EntryPair(createSet(new String[]{"b", "e"}), createSet(new String[]{"d"}))));
        assertTrue(response.contains(new EntryPair(createSet(new String[]{"c", "e"}), createSet(new String[]{"d"}))));
    }

    /**
     * Private function to prepare set of activities based on list of strings (names)
     *
     * @param activities Activities names list
     * @return Reference to HashSet
     */
    private HashSet<Activity> createSet(String[] activities) {
        HashSet<Activity> set = new HashSet<>();
        for (String name : activities) {
            set.add(new Activity(name));
        }

        return set;
    }
}