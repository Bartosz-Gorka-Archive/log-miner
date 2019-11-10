package structures;

public class ActivityDirectSuccession {
    private final Activity predecessor;
    private final Activity successor;

    /**
     * Direct succession structure.
     * You can read it as predecessor > successor, where > mean precedes.
     *
     * @param predecessor Activity which precedes
     * @param successor   Activity being preceded
     */
    public ActivityDirectSuccession(Activity predecessor, Activity successor) {
        this.predecessor = predecessor;
        this.successor = successor;
    }

    @Override
    public int hashCode() {
        return this.predecessor.hashCode() + this.successor.hashCode();
    }

    /**
     * Two objects are equal if the same class AND predecessor AND successor are the same
     *
     * @param obj Object to compare
     * @return true if equals, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;

        ActivityDirectSuccession struct = ((ActivityDirectSuccession) obj);
        return struct.predecessor.equals(this.predecessor) && struct.successor.equals(this.successor);
    }
}
