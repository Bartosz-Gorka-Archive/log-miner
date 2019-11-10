package structures;

public class ActivityRelation {
    private final Activity predecessor;
    private final Activity successor;
    private final RelationType relationType;


    /**
     * Relation between two activities
     * predecessor OP successor, where OP can be one of `->`, `||`, `#` or `<-` (stored as enum value)
     *
     * @param predecessor  Activity which precedes
     * @param successor    Activity being preceded
     * @param relationType Relation type between activities
     */
    public ActivityRelation(Activity predecessor, Activity successor, RelationType relationType) {
        this.predecessor = predecessor;
        this.successor = successor;
        this.relationType = relationType;
    }

    public Activity getPredecessor() {
        return predecessor;
    }

    public Activity getSuccessor() {
        return successor;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    /**
     * Two objects are equal if the same class AND predecessor AND successor AND relation type are the same
     *
     * @param obj Object to compare
     * @return true if equals, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;

        ActivityRelation struct = ((ActivityRelation) obj);
        return struct.predecessor.equals(this.predecessor)
                && struct.successor.equals(this.successor)
                && struct.relationType.equals(this.relationType);
    }

    /**
     * Custom relation type as Enum value to easy compare elements and store operator
     */
    public enum RelationType {
        CAUSALITY("->"),
        REVERSED_CAUSALITY("<-"),
        PARALLEL("||"),
        CHOICE("#");

        private String mark;

        RelationType(String mark) {
            this.mark = mark;
        }

        @Override
        public String toString() {
            return this.mark;
        }
    }
}
