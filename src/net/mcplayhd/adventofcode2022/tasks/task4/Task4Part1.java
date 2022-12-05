package net.mcplayhd.adventofcode2022.tasks.task4;

import net.mcplayhd.adventofcode2022.tasks.Task;

public class Task4Part1 extends Task {
    @Override
    public String getResult(String[] input) {
        int count = 0;
        for (String line : input) {
            if (line.isEmpty())
                continue;
            String[] ranges = line.split(",");
            String[] leftSp = ranges[0].split("-");
            String[] rightSp = ranges[1].split("-");
            Range left = new Range(Integer.parseInt(leftSp[0]), Integer.parseInt(leftSp[1]));
            Range right = new Range(Integer.parseInt(rightSp[0]), Integer.parseInt(rightSp[1]));
            if (left.contains(right) || right.contains(left)) {
                count ++;
            }
        }
        return Integer.toString(count);
    }

    static class Range {
        int lower;
        int upper;

        public Range(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }

        boolean contains(Range other) {
            return lower <= other.lower && upper >= other.upper;
        }

        boolean overlaps(Range other) {
            return upper >= other.lower || other.upper >= lower;
        }
    }
}
