package net.mcplayhd.adventofcode2022.tasks.task8;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Task8Part2 extends Task {
    int width = 0;
    int height = 0;
    List<List<Integer>> heightMap = new ArrayList<>();

    int get(int x, int y) {
        return heightMap.get(y).get(x);
    }

    @Override
    public String getResult(String[] input) {
        for (String line : input) {
            if (line.isEmpty())
                continue;
            height++;
            List<Integer> row = new ArrayList<>();
            heightMap.add(row);
            char[] chars = line.toCharArray();
            width = chars.length;
            for (char c : chars) {
                row.add(Integer.parseInt(c + ""));
            }
        }
        int maxScore = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int score = getScore(x, y);
                maxScore = Math.max(score, maxScore);
            }
        }
        return Integer.toString(maxScore);
    }

    int getScore(int X, int Y) {
        int treeHeight = get(X, Y);
        int up = 0;
        for (int y = Y - 1; y >= 0; y--) {
            up ++;
            if (get(X, y) >= treeHeight) {
                break;
            }
        }
        int down = 0;
        for (int y = Y + 1; y < height; y++) {
            down ++;
            if (get(X, y) >= treeHeight) {
                break;
            }
        }
        int left = 0;
        for (int x = X - 1; x >= 0; x--) {
            left ++;
            if (get(x, Y) >= treeHeight) {
                break;
            }
        }
        int right = 0;
        for (int x = X + 1; x < width; x++) {
            right ++;
            if (get(x, Y) >= treeHeight) {
                break;
            }
        }
        return up * down * right * left;
    }
}
