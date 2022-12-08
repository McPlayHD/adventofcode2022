package net.mcplayhd.adventofcode2022.tasks.task8;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Task8Part1 extends Task {
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
        int visible = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isVisible(x, y)) {
                    visible++;
                }
            }
        }
        return Integer.toString(visible);
    }

    boolean isVisible(int X, int Y) {
        int treeHeight = get(X, Y);
        // top
        boolean visibleFromTop = true;
        for (int y = Y - 1; y >= 0; y--) {
            if (get(X, y) >= treeHeight) {
                visibleFromTop = false;
                break;
            }
        }
        // bottom
        boolean visibleFromBottom = true;
        for (int y = Y + 1; y < height; y++) {
            if (get(X, y) >= treeHeight) {
                visibleFromBottom = false;
                break;
            }
        }
        // left
        boolean visibleFromLeft = true;
        for (int x = X - 1; x >= 0; x--) {
            if (get(x, Y) >= treeHeight) {
                visibleFromLeft = false;
                break;
            }
        }
        // right
        boolean visibleFromRight = true;
        for (int x = X + 1; x < width; x++) {
            if (get(x, Y) >= treeHeight) {
                visibleFromRight = false;
                break;
            }
        }
        return visibleFromTop || visibleFromLeft || visibleFromBottom || visibleFromRight;
    }
}
