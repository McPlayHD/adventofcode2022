package net.mcplayhd.adventofcode2022.tasks.task1;

import net.mcplayhd.adventofcode2022.tasks.Task;

public class Task1Part1 extends Task {

    @Override
    public String getResult(String input) {
        String[] lines = input.split("\n", -1);
        int max = 0;
        int current = 0;
        for (String line : lines) {
            if ("".equals(line)) {
                max = Math.max(current, max);
                current = 0;
                continue;
            }
            current += Integer.parseInt(line);
        }
        return max + "";
    }
}
