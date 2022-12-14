package net.mcplayhd.adventofcode2022.tasks.task14;

import net.mcplayhd.adventofcode2022.tasks.Task;

public class Task14Part1 extends Task {
    static GridState[][] grid;

    @Override
    public String getResult(String[] input) {
        grid = new GridState[200][600];
        for (int Y = 0; Y < 200; Y++) {
            for (int X = 0; X < 600; X++) {
                grid[Y][X] = GridState.FREE;
            }
        }
        for (String line : input) {
            if (line.isEmpty())
                continue;
            String[] steps = line.replace(" -> ", "\n").split("\n");
            Point lastPoint = null;
            for (String stepSt : steps) {
                String[] xy = stepSt.split(",");
                int x = Integer.parseInt(xy[0]);
                int y = Integer.parseInt(xy[1]);
                if (lastPoint != null) {
                    int dx = x - lastPoint.x;
                    int dy = y - lastPoint.y;
                    while (lastPoint.x != x) {
                        grid[y][lastPoint.x] = GridState.ROCK;
                        lastPoint.x += Math.signum(dx);
                        grid[y][lastPoint.x] = GridState.ROCK;
                    }
                    while (lastPoint.y != y) {
                        grid[lastPoint.y][x] = GridState.ROCK;
                        lastPoint.y += Math.signum(dy);
                        grid[lastPoint.y][x] = GridState.ROCK;
                    }
                }
                lastPoint = new Point(x, y);
            }
        }
        drawTestGrid();
        int counter = 0;
        while (spawnSand(new Point(500, 0))) {
            counter++;
        }
        drawTestGrid();
        return Integer.toString(counter);
    }

    void drawTestGrid() {
        for (int Y = 0; Y < 10; Y++) {
            for (int X = 494; X < 504; X++) {
                if (grid[Y][X] == GridState.FREE) {
                    System.out.print(".");
                } else if (grid[Y][X] == GridState.SAND) {
                    System.out.print("o");
                } else {
                    System.out.print("#");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    boolean spawnSand(Point point) {
        boolean canMove = true;
        while (canMove) {
            Point below;
            while (!(below = point.getRelative(0, 1)).isOutOfBounds() && below.isFree()) {
                point = below;
            }
            if (below.isOutOfBounds()) {
                return false;
            }
            Point downLeft = point.getRelative(-1, 1);
            Point downRight = point.getRelative(1, 1);
            if (downLeft.isFree()) {
                point = downLeft;
            } else if (downRight.isFree()) {
                point = downRight;
            } else {
                canMove = false;
            }
        }
        grid[point.y][point.x] = GridState.SAND;
        return true;
    }

    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        boolean isOutOfBounds() {
            return x < 0 || x >= grid[0].length || y < 0 || y >= grid.length;
        }

        GridState getState() {
            return grid[y][x];
        }

        boolean isFree() {
            return getState() == GridState.FREE;
        }

        Point getRelative(int dx, int dy) {
            return new Point(x + dx, y + dy);
        }
    }

    enum GridState {
        FREE,
        ROCK,
        SAND
    }
}
