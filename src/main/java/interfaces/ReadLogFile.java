package interfaces;

import structures.Activity;

import java.util.List;
import java.util.Scanner;

public interface ReadLogFile {
    List<List<Activity>> readProcessLog(Scanner scanner);
}
