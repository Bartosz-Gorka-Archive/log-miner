package parsers;

import structures.Activity;
import structures.ActivityDirectSuccession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActivityRelationsBuilder {
    /**
     * Generate direct successions list based on the activities list
     * No duplicates in the list (using Set to prevent duplications)
     *
     * @param cases Parsed log file as activities list
     * @return Non-duplicate list of direct successions between activities
     */
    public Set<ActivityDirectSuccession> generateDirectSuccessions(List<List<Activity>> cases) {
        Set<ActivityDirectSuccession> successionsCollection = new HashSet<>();

        for (List<Activity> caseRecord : cases) {
            int caseSize = caseRecord.size();

            for (int index = 1; index < caseSize; index++) {
                Activity predecessor = caseRecord.get(index - 1);
                Activity successor = caseRecord.get(index);

                successionsCollection.add(new ActivityDirectSuccession(predecessor, successor));
            }
        }

        return successionsCollection;
    }

    /**
     * Generate a list of unique activities using Set to eliminate duplicates
     *
     * @param cases Parsed log file as activities list
     * @return Unique activities in log file (an activities list)
     */
    public Set<Activity> generateUniqueActivities(List<List<Activity>> cases) {
        Set<Activity> uniqueActivities = new HashSet<>();

        for (List<Activity> caseRecord : cases) {
            uniqueActivities.addAll(caseRecord);
        }

        return uniqueActivities;
    }
}
