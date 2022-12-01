package net.mcplayhd.adventofcode2022.tasks.task1;

import net.mcplayhd.adventofcode2022.tasks.Task;

public class Task1Part1 extends Task {

    @Override
    public String getResult(String[] input) {
        int max = 0;
        int current = 0;
        for (String line : input) {
            if (line.isEmpty()) {
                max = Math.max(current, max);
                current = 0;
                continue;
            }
            current += Integer.parseInt(line);
        }
        return Integer.toString(max);
    }
}
