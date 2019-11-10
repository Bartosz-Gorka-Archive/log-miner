package structures;

public class Activity {
    private final String name;

    public Activity(String name) {
        this.name = name;
    }

    public String activityName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Two objects are equal if the same class AND names are the same
     *
     * @param obj Object to compare
     * @return true if equals, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;

        return ((Activity) obj).name.equals(this.name);
    }
}
