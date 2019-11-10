package parsers;

import structures.Activity;
import structures.ActivityDirectSuccession;
import structures.ActivityRelation;

import java.util.ArrayList;
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

    /**
     * Generate a list of relations between activities.
     * Each pair of activities has own relation stored in response
     *
     * @param cases Parsed log file as activities list
     * @return Relations between activities in log file (an activities list)
     */
    public List<ActivityRelation> generateRelationsBetweenActivities(List<List<Activity>> cases) {
        Set<Activity> uniqueActivities = this.generateUniqueActivities(cases);
        List<ActivityRelation> relations = new ArrayList<>();
        Set<ActivityDirectSuccession> directSuccessions = this.generateDirectSuccessions(cases);

        for (Activity A : uniqueActivities) {
            for (Activity B : uniqueActivities) {
                boolean AB = directSuccessions.contains(new ActivityDirectSuccession(A, B));
                boolean BA = directSuccessions.contains(new ActivityDirectSuccession(B, A));
                ActivityRelation.RelationType type = this.determineRelation(AB, BA);

                relations.add(new ActivityRelation(A, B, type));
            }
        }

        return relations;
    }

    /**
     * Decide which relation assign to pair of activities.
     * <b>Causality</b>: a > b and not b > a
     * <b>Reversed Causality</b>: b > a and not a > b
     * <b>Parallel</b>: a > b and b > a
     * <b>Choice</b>: not a > b and not b > a
     *
     * @param AB Boolean value with check a > b
     * @param BA Boolean value with check b > a
     * @return Relation type
     */
    private ActivityRelation.RelationType determineRelation(boolean AB, boolean BA) {
        if (AB && !BA) {
            return ActivityRelation.RelationType.CAUSALITY;
        } else if (!AB && BA) {
            return ActivityRelation.RelationType.REVERSED_CAUSALITY;
        } else if (AB && BA) {
            return ActivityRelation.RelationType.PARALLEL;
        } else {
            return ActivityRelation.RelationType.CHOICE;
        }
    }
}
