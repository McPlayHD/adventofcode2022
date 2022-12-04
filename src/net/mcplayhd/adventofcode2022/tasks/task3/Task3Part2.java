package net.mcplayhd.adventofcode2022.tasks.task3;

import net.mcplayhd.adventofcode2022.tasks.Task;

public class Task3Part2 extends Task {

    private boolean isIn(char c, String s) {
        return s.indexOf(c) != -1;
    }

    @Override
    public String getResult(String[] input) {
        int prioritySum = 0;
        a:
        for (int i = 0; i < input.length - 1; i += 3) {
            String line1 = input[i];
            String line2 = input[i + 1];
            String line3 = input[i + 2];
            for (char c : line1.toCharArray()) {
                if (isIn(c, line2) && isIn(c, line3)) {
                    int priority = c >= 97 ? (c - 96) : (c - 63 + 25);
                    prioritySum += priority;
                    continue a;
                }
            }
            throw new RuntimeException("Found no common character");
        }
        return Integer.toString(prioritySum);
    }
}
