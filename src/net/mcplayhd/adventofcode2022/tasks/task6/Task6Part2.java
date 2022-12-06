package net.mcplayhd.adventofcode2022.tasks.task6;

import net.mcplayhd.adventofcode2022.tasks.Task;

public class Task6Part2 extends Task {
    @Override
    public String getResult(String[] input) {
        String line = input[0];
        char[] cArray = line.toCharArray();
        int amount = 14;
        for (int i = amount - 1; i < cArray.length; i ++) {
            if (!anyTheSame(cArray, i - (amount - 1), i)) {
                return Integer.toString(i + 1);
            }
        }
        return null;
    }

    boolean anyTheSame(char[] cArray, int begin, int end) {
        for (int i = 0; i <= end - begin; i ++) {
            for (int j = i + 1; j <= end - begin; j ++) {
                if (cArray[i + begin] == cArray[j + begin]) {
                    return true;
                }
            }
        }
        return false;
    }
}
