package parsers;

import org.junit.jupiter.api.Test;
import reader.ProcessLogReader;
import structures.Activity;
import structures.ActivityDirectSuccession;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
}