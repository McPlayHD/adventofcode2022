package net.mcplayhd.adventofcode2022.tasks.task10;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Task10Part1 extends Task {
    @Override
    public String getResult(String[] input) {
        int X = 1;
        int cycle = 0;
        List<Integer> interest = new ArrayList<>();
        interest.add(20); interest.add(60); interest.add(100); interest.add(140); interest.add(180); interest.add(220);
        int sum = 0;
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
                cycle ++;
                if (interest.contains(cycle)) {
                    sum += X * cycle;
                }
            }
            X += add;
        }
        return Integer.toString(sum);
    }
}
