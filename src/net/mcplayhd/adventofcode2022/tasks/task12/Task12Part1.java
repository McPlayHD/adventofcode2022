package net.mcplayhd.adventofcode2022.tasks.task12;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.*;

public class Task12Part1 extends Task {
    int height, width;
    Tile[][] rawMap;

    @Override
    public String getResult(String[] input) {
        height = input.length - 1;
        width = input[0].length();
        rawMap = new Tile[height][width];
        Tile start = null;
        Tile goal = null;
        int Y = 0;
        for (String line : input) {
            if (line.isEmpty())
                continue;
            char[] chars = line.toCharArray();
            for (int X = 0; X < width; X ++) {
                rawMap[Y][X] = new Tile(chars[X]);
                if (chars[X] == 'S') {
                    start = rawMap[Y][X];
                    start.elevation = 'a';
                    start.moves = 0;
                }
                if (chars[X] == 'E') {
                    goal = rawMap[Y][X];
                    goal.elevation = 'z';
                }
            }
            Y ++;
        }
        connectNeighbours();
        PriorityQueue<State> queue = new PriorityQueue<>();
        queue.add(new State(0, start));
        while (!queue.isEmpty()) {
            State state = queue.poll();
            char elevation = state.tile.elevation;
            int moves = state.moves;
            for (Tile tile : state.tile.neighbours) {
                if (tile.moves != -1)
                    continue;
                if (tile.elevation > elevation + 1)
                    continue;
                if (tile == goal)
                    return Integer.toString(moves + 1);
                tile.moves = moves + 1;
                queue.add(new State(moves + 1, tile));
            }
        }
        throw new RuntimeException("Goal not reached.");
    }

    void connectNeighbours() {
        for (int Y = 0; Y < height; Y ++) {
            for (int X = 0; X < width; X ++) {
                if (Y > 0) {
                    rawMap[Y][X].neighbours.add(rawMap[Y - 1][X]);
                }
                if (X > 0) {
                    rawMap[Y][X].neighbours.add(rawMap[Y][X - 1]);
                }
                if (Y < height - 1) {
                    rawMap[Y][X].neighbours.add(rawMap[Y + 1][X]);
                }
                if (X < width - 1) {
                    rawMap[Y][X].neighbours.add(rawMap[Y][X + 1]);
                }
            }
        }
    }

    static class Tile {
        char elevation;
        List<Tile> neighbours = new ArrayList<>();

        Map<Character, Integer> movesAtHeight = new HashMap<>();
        int moves = -1;

        public Tile(char elevation) {
            this.elevation = elevation;
        }
    }

    static class State implements Comparable<State> {
        int moves;
        Tile tile;

        public State(int moves, Tile tile) {
            this.moves = moves;
            this.tile = tile;
        }

        @Override
        public int compareTo(State o) {
            return Integer.compare(moves, o.moves);
        }
    }
}
