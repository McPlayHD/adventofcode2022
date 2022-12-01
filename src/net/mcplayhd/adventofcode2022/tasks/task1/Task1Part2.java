package net.mcplayhd.adventofcode2022.tasks.task1;

import net.mcplayhd.adventofcode2022.tasks.Task;

public class Task1Part2 extends Task {

    @Override
    public String getResult(String input) {
        String[] lines = input.split("\n", -1);
        int[] max = new int[3];
        int current = 0;
        for (String line : lines) {
            if ("".equals(line)) {
                int smallestIndex = 0;
                for (int index = 1; index < max.length; index++) {
                    if (max[index] < max[smallestIndex]) {
                        smallestIndex = index;
                    }
                }
                max[smallestIndex] = Math.max(current, max[smallestIndex]);
                current = 0;
                continue;
            }
            current += Integer.parseInt(line);
        }
        int sum = 0;
        for (int capacity : max) {
            sum += capacity;
        }
        return sum + "";
    }
}
