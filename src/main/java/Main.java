import parsers.ActivityRelationsBuilder;
import reader.ProcessLogReader;
import structures.Activity;
import structures.EntryPair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("You should call program with one parameter - reference to log file");
            return;
        }

        try {
            File logFile = new File(args[0]);
            if (logFile.exists() && logFile.canRead()) {
                Scanner logFileScanner = new Scanner(new File(args[0]));

                ProcessLogReader reader = new ProcessLogReader();
                List<List<Activity>> processLog = reader.readProcessLog(logFileScanner);

                ActivityRelationsBuilder builder = new ActivityRelationsBuilder();
                List<EntryPair> relationsBasedOnLog = builder.findRelationsBasedOnLog(processLog);

                builder.selectOnlyMaximumElements(relationsBasedOnLog).forEach(System.out::println);
            } else {
                System.err.println("Log file can not be read");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Log file not found. Check reference and run again");
        }
    }
}
