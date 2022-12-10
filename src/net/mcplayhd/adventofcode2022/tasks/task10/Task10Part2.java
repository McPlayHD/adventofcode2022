package net.mcplayhd.adventofcode2022.tasks.task10;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Task10Part2 extends Task {
    @Override
    public String getResult(String[] input) {
        int X = 1;
        int cycle = 0;
        StringBuilder[] lines = new StringBuilder[6];
        for (int i = 0; i < 6; i ++) {
            lines[i] = new StringBuilder();
        }
        for (String line : input) {
            if (line.isEmpty())
                continue;
            int add = 0;
            int duration;
            if (line.startsWith("noop")) {
                duration = 1;
            } else {
                add = Integer.parseInt(line.split(" ")[1]);
                duration = 2;
            }
            for (int i = 0; i < duration; i ++) {

                int xPos = cycle % 40;
                int yPos = cycle / 40;

                if (X - 1 == xPos || X == xPos || X + 1 == xPos) {
                    lines[yPos].append('#');
                } else {
                    lines[yPos].append('.');
                }

                cycle ++;
            }
            X += add;
        }
        StringBuilder result = new StringBuilder();
        for (StringBuilder line : lines) {
            result.append(line).append("\n");
        }
        return result.toString();
    }
}
