package net.mcplayhd.adventofcode2022.tasks.task12;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.*;

public class Task12Part2 extends Task {
    int height, width;
    Tile[][] rawMap;

    @Override
    public String getResult(String[] input) {
        height = input.length - 1;
        width = input[0].length();
        rawMap = new Tile[height][width];
        List<Tile> starts = new ArrayList<>();
        Tile goal = null;
        int Y = 0;
        for (String line : input) {
            if (line.isEmpty())
                continue;
            char[] chars = line.toCharArray();
            for (int X = 0; X < width; X ++) {
                Tile tile = new Tile(chars[X]);
                rawMap[Y][X] = tile;
                if (chars[X] == 'S' || chars[X] == 'a') {
                    starts.add(tile);
                    tile.elevation = 'a';
                }
                if (chars[X] == 'E') {
                    goal = tile;
                    goal.elevation = 'z';
                }
            }
            Y ++;
        }
        connectNeighbours();
        int bestMoves = Integer.MAX_VALUE;
        for (Tile start : starts) {
            reset(start);
            PriorityQueue<State> queue = new PriorityQueue<>();
            queue.add(new State(0, start));
            queueTask:
            while (!queue.isEmpty()) {
                State state = queue.poll();
                char elevation = state.tile.elevation;
                int moves = state.moves;
                for (Tile tile : state.tile.neighbours) {
                    if (tile.moves != -1)
                        continue;
                    if (tile.elevation > elevation + 1)
                        continue;
                    if (tile == goal) {
                        bestMoves = Math.min(bestMoves, moves + 1);
                        break queueTask;
                    }
                    tile.moves = moves + 1;
                    queue.add(new State(moves + 1, tile));
                }
            }
        }
        return Integer.toString(bestMoves);
    }

    void reset(Tile start) {
        for (int Y = 0; Y < height; Y ++) {
            for (int X = 0; X < width; X++) {
                rawMap[Y][X].moves = -1;
            }
        }
        start.moves = 0;
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
