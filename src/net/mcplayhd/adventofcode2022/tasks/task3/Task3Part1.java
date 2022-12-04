package net.mcplayhd.adventofcode2022.tasks.task3;

import net.mcplayhd.adventofcode2022.tasks.Task;

public class Task3Part1 extends Task {
    @Override
    public String getResult(String[] input) {
        int prioritySum = 0;
        for (String line : input) {
            if (line.isEmpty())
                continue;
            int length = line.length();
            String left = line.substring(0, length / 2);
            String right = line.substring(length / 2);
            char inBoth = '\0';
            for (char c : left.toCharArray()) {
                for (char o : right.toCharArray()) {
                    if (c == o) {
                        inBoth = c;
                        break;
                    }
                }
            }
            int priority = inBoth >= 97 ? (inBoth - 96) : (inBoth - 63 + 25);
            prioritySum += priority;
        }
        return Integer.toString(prioritySum);
    }
}
