package net.mcplayhd.adventofcode2022.tasks.task7;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task7Part2 extends Task {
    Folder root = new Folder("/", null);

    @Override
    public String getResult(String[] input) {
        Folder current = root;
        for (int pointer = 0; pointer < input.length; pointer++) {
            String command = input[pointer]; // should always be a command
            if (command.isEmpty())
                continue;
            if (command.startsWith("$ cd ")) {
                String folderName = command.substring("$ cd ".length());
                if (folderName.equals("/")) {
                    current = root;
                } else if (folderName.equals("..")) {
                    current = current.parent;
                } else {
                    current = current.getSubFolder(folderName);
                }
            } else {
                // we know this has to be a list command... read lines until a new command appears.
                while (input.length > pointer + 1 && !input[pointer + 1].isEmpty() && !input[pointer + 1].startsWith("$")) {
                    String entry = input[++pointer];
                    if (entry.startsWith("dir ")) {
                        String dirName = entry.substring("dir ".length());
                        current.addFolder(new Folder(dirName, current));
                    } else {
                        String[] split = entry.split(" ");
                        int size = Integer.parseInt(split[0]);
                        String fileName = split[1];
                        current.addFile(new File(fileName, size));
                    }
                }
            }
        }
        int totalDiskSpace = 70000000;
        int spaceRequired = 30000000;
        int spaceUsed = root.getTotalSize();
        int spaceLeft = totalDiskSpace - spaceUsed;
        int needToDelete = spaceRequired - spaceLeft;
        Folder best = root.getSmallestFolderOfAtLeastSize(needToDelete);
        return Integer.toString(best.getTotalSize());
    }

    static class Folder {
        String name;
        Folder parent;
        Map<String, Folder> subFolders = new HashMap<>();
        List<File> files = new ArrayList<>();

        public Folder(String name, Folder parent) {
            this.name = name;
            this.parent = parent;
        }

        void addFolder(Folder folder) {
            subFolders.put(folder.name, folder);
        }

        void addFile(File file) {
            files.add(file);
        }

        Folder getSubFolder(String name) {
            return subFolders.get(name);
        }

        int getFolderSize() {
            int sum = 0;
            for (File file : files) {
                sum += file.size;
            }
            return sum;
        }

        int getTotalSize() {
            int sum = getFolderSize();
            for (Folder folder : subFolders.values()) {
                sum += folder.getTotalSize();
            }
            return sum;
        }

        Folder getSmallestFolderOfAtLeastSize(int size) {
            if (getTotalSize() < size)
                return null;
            Folder currentBest = this;
            for (Folder folder : subFolders.values()) {
                Folder smallestThere = folder.getSmallestFolderOfAtLeastSize(size);
                if (smallestThere == null)
                    continue;
                if (smallestThere.getTotalSize() < currentBest.getTotalSize()) {
                    currentBest = smallestThere;
                }
            }
            return currentBest;
        }
    }

    static class File {
        String name;
        int size;

        public File(String name, int size) {
            this.name = name;
            this.size = size;
        }
    }
}
