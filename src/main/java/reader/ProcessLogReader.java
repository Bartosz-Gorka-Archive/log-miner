package reader;

import interfaces.ReadLogFile;
import structures.Activity;

import java.util.*;

public class ProcessLogReader implements ReadLogFile {
    public List<List<Activity>> readProcessLog(Scanner scanner) {
        List<List<Activity>> activityInCases = new ArrayList<>();

        for (String caseRecord : readWholeLog(scanner)) {
            activityInCases.add(stringCaseToActivityList(caseRecord));
        }

        return activityInCases;
    }

    private Set<String> readWholeLog(Scanner scanner) {
        Set<String> casesInLog = new HashSet<>();
        while (scanner.hasNextLine()) {
            casesInLog.add(scanner.nextLine());
        }

        return casesInLog;
    }

    private List<Activity> stringCaseToActivityList(String caseRecord) {
        List<Activity> activityInCase = new ArrayList<>();

        for (String name : caseRecord.split("(?!^)")) {
            if (!name.trim().equals(""))
                activityInCase.add(new Activity(name.trim()));
        }

        return activityInCase;
    }
}
