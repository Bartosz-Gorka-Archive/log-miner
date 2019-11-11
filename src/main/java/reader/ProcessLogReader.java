package reader;

import interfaces.ReadLogFile;
import structures.Activity;

import java.util.*;

public class ProcessLogReader implements ReadLogFile {
    /**
     * Read process event log and prepare list of activities in each line
     *
     * @param scanner Scanner - log file
     * @return List with list of activities (each line as separated list)
     */
    public List<List<Activity>> readProcessLog(Scanner scanner) {
        List<List<Activity>> activityInCases = new ArrayList<>();

        for (String caseRecord : readWholeLog(scanner)) {
            activityInCases.add(stringCaseToActivityList(caseRecord));
        }

        return activityInCases;
    }

    /**
     * Read whole log file with reducing duplicates of cases
     *
     * @param scanner Log file
     * @return Unique cases as set of strings
     */
    private Set<String> readWholeLog(Scanner scanner) {
        Set<String> casesInLog = new HashSet<>();
        while (scanner.hasNextLine()) {
            casesInLog.add(scanner.nextLine());
        }

        return casesInLog;
    }

    /**
     * Remove empty spaces from case record, parse into list of activities
     *
     * @param caseRecord Single case from log file
     * @return List of activities
     */
    private List<Activity> stringCaseToActivityList(String caseRecord) {
        List<Activity> activityInCase = new ArrayList<>();

        for (String name : caseRecord.split("(?!^)")) {
            if (!name.trim().equals(""))
                activityInCase.add(new Activity(name.trim()));
        }

        return activityInCase;
    }
}
