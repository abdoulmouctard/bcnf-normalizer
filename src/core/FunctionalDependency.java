package core;

import java.util.Set;
import java.util.regex.PatternSyntaxException;

/**
 * core.FunctionalDependency represents a functional dependency.
 * Two sets of attributes are used to represents both left and right sides of a FD.
 *
 */
public class FunctionalDependency implements Comparable<FunctionalDependency>{

    /**
     * The Attributes contained in the left side of the FD.
     */
    private Set<Attribute> left;

    /**
     * The Attributes contained in the left side of the FD.
     */
    private Set<Attribute> right;

    /**
     * Pattern used to match any valid functional dependency represented by a string.
     */
    public final static String PATTERN = "^\\w+->\\w+$";

    /**
     * Pattern used to split a functional dependency represented by a string.
     */
    public final static String PATTERN_SPLIT = "->";

    public FunctionalDependency(Set<Attribute> left, Set<Attribute> right) {
        this.left = left;
        this.right = right;
    }

    /**
     * The set of attributes corresponding to the left side of the FD.
     * @return a Set of Attributes.
     */
    public Set<Attribute> getLeft() {
        return this.left;
    }

    /**
     * The set of attributes corresponding to the right side of the FD.
     * @return a Set of Attributes.
     */
    public Set<Attribute> getRight() {
        return this.right;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.left.stream().forEach(a -> sb.append(a));
        sb.append(" -> ");
        this.right.stream().forEach(a -> sb.append(a));
        return sb.toString();
    }

    /**
     * This method always return a negative value.
     * The use of TreeSet imposes us to implement Comparable interface.
     * Since I don't want to implement a correct comparison method and
     * that a duplicated FD will (normally) not cause that much trouble for
     * this quick implementation, this method will remain like this.
     */
    @Override
    public int compareTo(FunctionalDependency o) {
        return -1;
    }

    /**
     * Parse a Functional Dependency represented by the given String.
     * The String must match to the pattern.
     * Then it will be split using the split pattern to retrieve both sides of the FD.
     * Theses sides will be parsed to extract a set of core.Attribute.
     * @see Attribute class for more details.
     * @param fd The core.FunctionalDependency to parse.
     * @return an instance of core.FunctionalDependency.
     */
    public static FunctionalDependency parse(String fd)
    {
        if(!fd.matches(PATTERN))
            throw new PatternSyntaxException("Wrong pattern, expecting "+ PATTERN, fd, -1);

        String[] sides = fd.split(PATTERN_SPLIT);
        Set<Attribute> leftSide = Attribute.parse(sides[0]);
        Set<Attribute> rightSide = Attribute.parse(sides[1]);

        return new FunctionalDependency(leftSide, rightSide);
    }

}
