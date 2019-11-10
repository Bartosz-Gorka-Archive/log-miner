package structures;

import java.util.Iterator;
import java.util.Set;

public class EntryPair {
    private final Set<Activity> left;
    private final Set<Activity> right;

    /**
     * Pair with two sets - left and right part
     * Left -> Right, when all elements in Left and Right between are #
     *
     * @param left  Left set of activities, all should be independent (# as relation between each)
     * @param right Right set of activities, all should be independent (# as relation between each)
     */
    public EntryPair(Set<Activity> left, Set<Activity> right) {
        this.left = left;
        this.right = right;
    }

    public Set<Activity> getLeft() {
        return left;
    }

    public Set<Activity> getRight() {
        return right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;

        EntryPair struct = ((EntryPair) obj);
        return struct.getLeft().equals(this.getLeft())
                && struct.getRight().equals(this.getRight());
    }

    /**
     * Translate internal structure to expected form ({a, b}, {c})
     *
     * @return Pretty formatted pair
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("({");

        Iterator<Activity> iterator = this.getLeft().iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next().activityName());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }

        builder.append("}, {");

        iterator = this.getRight().iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next().activityName());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }

        builder.append("})");
        return builder.toString();
    }
}
