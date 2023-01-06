package org.example;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Program {
    TreeMap<Path, MyFile> storage;
    ArrayList<MyFile> files;
    static Path root;

    Program(String directory) {
        root = Path.of(directory);
        if (!root.toFile().exists()) {
            System.out.printf("Directory %s doesn't exist%n", directory);
            return;
        }
        storage = new TreeMap<>();
        getAllFiles(root.toFile());
        files = new ArrayList<>(storage.values());
        files.sort(MyFile::compareTo);
        if (!doAllRequiresExist()) {
            return;
        }
        if (isCircled()) {
            return;
        }
        writeDataIntoFile();
    }

    void writeDataIntoFile() {
        int i = 0;
        try (FileWriter writer = new FileWriter(Path.of(root.toString(), "result.txt").toFile())) {
            while (i < files.size()) {
                for (MyFile file : files) {
                    if (file.wasWritten || !isAllRequiresBeenWritten(file)) continue;

                    file.wasWritten = true;
                    writer.write(file.toString());
                    writer.write('\n');
                    i++;
                    break;
                }
            }
        } catch (IOException ioe) {
            System.out.printf("Can't write into %s%n", Path.of(root.toString(), "result.txt"));
        }
    }

    boolean isAllRequiresBeenWritten(MyFile file) {
        for (Path req_file_path : file.dependencies) {
            if (!storage.get(req_file_path).wasWritten) {
                return false;
            }
        }
        return true;
    }

    boolean doAllRequiresExist() {
        for (MyFile file : files) {
            for (Path req_file : file.dependencies) {
                if (storage.get(req_file) == null) {
                    System.out.printf("File %s required in %s doesn't exist%n", req_file, file.filepath);
                    return false;
                }
            }
        }
        return true;
    }
    ArrayList<MyFile> BreadthFirstSearch(MyFile myFile, ArrayList<MyFile> cycle) {
        myFile.color = Color.BLACK;
        for (int i = 0; i < myFile.dependencies.size(); ++i) {
            MyFile file = storage.get(myFile.dependencies.get(i));
            if (file.color == Color.BLANK) {
                cycle.add(myFile);
                ArrayList<MyFile> new_cycle = BreadthFirstSearch(file, cycle);
                if (new_cycle.size() > 0) return new_cycle;
            } else if (file.color == Color.BLACK) {
                cycle.add(myFile);
                cycle.removeAll(cycle.subList(0, cycle.indexOf(file)));
                return cycle;
            }
        }
        myFile.color = Color.RED;
        return new ArrayList<>();
    }

    boolean isCircled() {
        for (MyFile file : files) {
            ArrayList<MyFile> cycle = new ArrayList<>();
            cycle = BreadthFirstSearch(file, cycle);
            if (cycle.size() > 0) {
                System.out.println("Cycle in these files:");
                for (MyFile cycled_files : cycle) {
                    System.out.println(cycled_files.filepath);
                }
                return true;
            }
        }
        return false;
    }

    public void getAllFiles(File directory) {
        File[] arrFiles = directory.listFiles();
        if (arrFiles == null) {
            return;
        }
        for (File file : arrFiles) {
            if (file.isDirectory()) {
                getAllFiles(file.getAbsoluteFile());
            } else if (file.canRead()){
                storage.put(file.getAbsoluteFile().toPath(), new MyFile(file.getAbsoluteFile().toPath()));
            }
        }
    }
}
