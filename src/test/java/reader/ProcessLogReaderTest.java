package reader;

import org.junit.jupiter.api.Test;
import structures.Activity;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessLogReaderTest {
    @Test
    void noCaseDuplicatesInResponse() {
        Scanner logFile = new Scanner("aaaaa\nduplicated\nduplicated\nddddd\n");
        List<List<Activity>> response = new ProcessLogReader().readProcessLog(logFile);

        assertEquals(3, response.size());
    }

    @Test
    void allActivitiesHaveNonEmptyNames() {
        Scanner logFile = new Scanner(" a b ");
        List<Activity> response = new ProcessLogReader().readProcessLog(logFile).get(0);

        assertEquals("a", response.get(0).activityName());
        assertEquals("b", response.get(1).activityName());
    }
}