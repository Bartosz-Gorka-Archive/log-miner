package parsers;

import structures.Activity;
import structures.ActivityDirectSuccession;
import structures.ActivityRelation;
import structures.EntryPair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static structures.ActivityRelation.RelationType.*;

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
            return CAUSALITY;
        } else if (!AB && BA) {
            return REVERSED_CAUSALITY;
        } else if (AB && BA) {
            return PARALLEL;
        } else {
            return CHOICE;
        }
    }

    /**
     * Generate list of relations wrote as ({left set}, {right set}).
     * All activities in left set should be with `CHOICE(#)` between each other. Also in right set.
     * Between left and right part (each elements) we expect to find `CAUSALITY(->)` relation.
     * <b>No reduction to a maximum count of elements - you will receive all possible pairs</b>
     *
     * @param cases All relations in the log file - source of truth
     * @return List of relations which we can find between activities
     */
    public List<EntryPair> findRelationsBasedOnLog(List<List<Activity>> cases) {
        List<ActivityRelation> activityRelations = this.generateRelationsBetweenActivities(cases);
        Set<Activity> uniqueActivities = this.generateUniqueActivities(cases);
        List<EntryPair> choiceActivities = new ArrayList<>();

        // Step 1: Prepare basic relations with one element in left and right part
        for (Activity A : uniqueActivities) {
            HashSet<Activity> setWithActivityA = new HashSet<>();
            setWithActivityA.add(A);

            for (Activity B : uniqueActivities) {
                if (A.equals(B)) continue;

                HashSet<Activity> setWithActivityB = new HashSet<>();
                setWithActivityB.add(B);
                EntryPair candidate = new EntryPair(setWithActivityA, setWithActivityB);

                if (isCorrectRelation(candidate, activityRelations)) {
                    choiceActivities.add(candidate);
                }
            }
        }

        // Step 2: Join parts into bigger group, as big as can do (respecting the rules)
        boolean addedNewRelation = true;
        int startIndex = 0;
        while (addedNewRelation) {
            int collectionSize = choiceActivities.size();
            addedNewRelation = false;

            for (int indexFirstGroup = startIndex; indexFirstGroup < collectionSize; indexFirstGroup++) {
                EntryPair firstGroup = choiceActivities.get(indexFirstGroup);

                for (int indexSecondGroup = indexFirstGroup + 1; indexSecondGroup < collectionSize; indexSecondGroup++) {
                    EntryPair secondGroup = choiceActivities.get(indexSecondGroup);

                    HashSet<Activity> left = new HashSet<>(firstGroup.getLeft());
                    left.addAll(secondGroup.getLeft());

                    HashSet<Activity> right = new HashSet<>(firstGroup.getRight());
                    right.addAll(secondGroup.getRight());

                    EntryPair candidate = new EntryPair(left, right);
                    if (isCorrectRelation(candidate, activityRelations)) {
                        addedNewRelation = choiceActivities.add(candidate);
                    }
                }
            }

            startIndex = collectionSize;
        }

        return choiceActivities;
    }

    /**
     * Check is correct relation wrote as ({left set}, {right set}).
     * All activities in left set should be with `CHOICE(#)` between each other. Also in right set.
     * Between left and right part (each elements) we expect to find `CAUSALITY(->)` relation.
     *
     * @param pair      ({left set}, {right set}) as single element
     * @param relations All relations in the log file - source of truth
     * @return True when relation is correct, false otherwise
     */
    private boolean isCorrectRelation(EntryPair pair, List<ActivityRelation> relations) {
        boolean correctRelation = true;

        // In left part all elements should be as CHOICE
        for (Activity activity : pair.getLeft()) {
            for (Activity refActivity : pair.getLeft()) {
                if (!existRelationBetweenActivitiesWithRelationType(activity, refActivity, CHOICE, relations)) {
                    correctRelation = false;
                }
            }
        }

        // In right part all elements should be as CHOICE
        for (Activity activity : pair.getRight()) {
            for (Activity refActivity : pair.getRight()) {
                if (!existRelationBetweenActivitiesWithRelationType(activity, refActivity, CHOICE, relations)) {
                    correctRelation = false;
                }
            }
        }

        // Each element of left should -> right element
        for (Activity activity : pair.getLeft()) {
            for (Activity refActivity : pair.getRight()) {
                if (!existRelationBetweenActivitiesWithRelationType(activity, refActivity, CAUSALITY, relations)) {
                    correctRelation = false;
                }
            }
        }

        return correctRelation;
    }

    /**
     * Check is exist relation between Predecessor and Successor with selected type
     *
     * @param predecessor  Predecessor activity
     * @param successor    Successor activity
     * @param relationType Relation type which we want to find between Predecessor and Successor
     * @param relations    All relations in the log file - source of truth
     * @return True when relation exist, false otherwise
     */
    private boolean existRelationBetweenActivitiesWithRelationType(Activity predecessor, Activity successor, ActivityRelation.RelationType relationType, List<ActivityRelation> relations) {
        return relations.stream().anyMatch(relation -> relation.getPredecessor().equals(predecessor) &&
                relation.getSuccessor().equals(successor) &&
                relation.getRelationType().equals(relationType));
    }

    /**
     * Based on list of pairs return only pairs with maximum count elements.
     *
     * @param pairs List of relations which we can find between activities
     * @return List of maximum count relations which we can find between activities
     */
    public List<EntryPair> selectOnlyMaximumElements(List<EntryPair> pairs) {
        List<EntryPair> maximumElements = new ArrayList<>();
        int totalElements = pairs.size();

        for (int indexFirstElement = 0; indexFirstElement < totalElements; indexFirstElement++) {
            EntryPair firstPair = pairs.get(indexFirstElement);
            boolean isMaximum = true;

            for (int indexSecondElement = 0; indexSecondElement < totalElements; indexSecondElement++) {
                if (indexFirstElement == indexSecondElement) continue;

                EntryPair secondPair = pairs.get(indexSecondElement);
                // If second rule contain all elements (probably also some extra) - we are not maximum
                if (secondPair.getLeft().containsAll(firstPair.getLeft()) && secondPair.getRight().containsAll(firstPair.getRight())) {
                    isMaximum = false;
                    break;
                }
            }

            if (isMaximum) {
                maximumElements.add(firstPair);
            }
        }

        return maximumElements;
    }
}
