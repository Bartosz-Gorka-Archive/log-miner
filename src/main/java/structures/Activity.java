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
}
