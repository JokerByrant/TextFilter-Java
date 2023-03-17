package util;


import org.apache.commons.collections4.CollectionUtils;
import pojo.Range;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RangeUtil {
    /**
     * Finds the intersection of a list of ranges.
     *
     * @param ranges The list of ranges to intersect.
     * @return The list of ranges representing the intersection.
     */
    public static List<Range> findIntersection(List<Range> ranges) {
        List<Range> intersection = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ranges)) {
            // Sort the ranges by their start values
            ranges.sort(Comparator.comparingInt(Range::getStart));

            // Initialize the intersection with the first range
            intersection.add(ranges.get(0));

            // Iterate through the remaining ranges and update the intersection
            for (int i = 1; i < ranges.size(); i++) {
                Range current = ranges.get(i);
                Range last = intersection.get(intersection.size() - 1);
                if (current.getStart() <= last.getEnd()) {
                    // The current range intersects with the last range in the intersection
                    int end = Math.max(current.getEnd(), last.getEnd());
                    intersection.set(intersection.size() - 1, new Range(last.getStart(), end));
                } else {
                    // The current range does not intersect with the last range in the intersection
                    intersection.add(current);
                }
            }
        }
        return intersection;
    }

    public static void main(String[] args) {
        // Sample input ranges
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(1, 5));
        ranges.add(new Range(23, 23));
        ranges.add(new Range(3, 9));
        ranges.add(new Range(6, 8));
        ranges.add(new Range(10, 12));

        // Find the intersection of the ranges
        List<Range> intersection = findIntersection(ranges);

        // Print the intersection
        for (Range range : intersection) {
            System.out.println(range.getStart() + "-" + range.getEnd());
        }
    }
}

