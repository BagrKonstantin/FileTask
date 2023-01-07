package org.example;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Program {
    private final TreeMap<Path, MyFile> storage;
    private ArrayList<MyFile> files;
    static Path root;
    private final Path res;

    public Program(String directory, String result) {
        root = Path.of(directory);
        res = Path.of(result);
        storage = new TreeMap<>();
        MyFile.setRoot(root.toString());
    }

    public void run() {
        try {
            getAllFiles(root.toFile());
            files = new ArrayList<>(storage.values());
            files.sort(MyFile::compareTo);
            doAllRequiresExist();
            isCircled();
            writeDataIntoFile();
        } catch (IOException | CyclicDependencyException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void writeDataIntoFile() throws IOException {
        try (FileWriter writer = new FileWriter(res.toFile(), false)) {
            int i = 0;
            while (i < files.size()) {
                for (MyFile file : files) {
                    if (file.wasWritten() || !isAllRequiresBeenWritten(file)) continue;
                    file.setWritten(true);
                    System.out.println(file.getName());
                    writer.write(file.toString());
                    writer.write('\n');
                    i++;
                    break;
                }
            }
        } catch (IOException ioe) {
            throw new IOException(String.format("Can't write into %s%n", res));
        }
    }

    private boolean isAllRequiresBeenWritten(MyFile file) {
        for (Path req_file_path : file.getDependencies()) {
            if (!storage.get(req_file_path).wasWritten()) {
                return false;
            }
        }
        return true;
    }

    private void doAllRequiresExist() throws FileNotFoundException {
        for (MyFile file : files) {
            for (Path req_file : file.getDependencies()) {
                if (storage.get(req_file) == null) {
                    throw new FileNotFoundException(String.format("File %s required in %s doesn't exist%n", req_file, file.getFilepath()));
                }
            }
        }
    }

    private ArrayList<String> BreadthFirstSearch(MyFile myFile, ArrayList<String> cycle) {
        myFile.setColor(Color.BLACK);
        for (int i = 0; i < myFile.getDependencies().size(); ++i) {
            MyFile file = storage.get(myFile.getDependencies().get(i));
            if (file.getColor() == Color.BLANK) {
                cycle.add(myFile.getFilepath().toString());
                ArrayList<String> new_cycle = BreadthFirstSearch(file, cycle);
                if (new_cycle.size() > 0) return new_cycle;
            } else if (file.getColor() == Color.BLACK) {
                cycle.add(myFile.getFilepath().toString());
                ArrayList<String> new_cycle = new ArrayList<>();
                for (int j = cycle.indexOf(file.getFilepath().toString()); j < cycle.indexOf(myFile.getFilepath().toString()); j++) {
                    if (!new_cycle.contains(cycle.get(j))) {
                        new_cycle.add(cycle.get(j));
                    }
                }
                new_cycle.add(myFile.getFilepath().toString());
                return new_cycle;
            }
        }
        myFile.setColor(Color.RED);
        return new ArrayList<>();
    }

    private void isCircled() throws CyclicDependencyException {
        for (MyFile file : files) {
            ArrayList<String> cycle = new ArrayList<>();
            cycle = BreadthFirstSearch(file, cycle);
            if (cycle.size() > 0) {
                throw new CyclicDependencyException(cycle);
            }
        }
    }

    private void getAllFiles(File directory) throws IOException {
        File[] arrFiles = directory.listFiles();
        if (arrFiles == null) {
            return;
        }
        for (File file : arrFiles) {
            if (file.isDirectory()) {
                getAllFiles(file.getAbsoluteFile());
            } else if (file.canRead()) {
                storage.put(file.getAbsoluteFile().toPath(), new MyFile(file.getAbsoluteFile().toPath()));
            }
        }
    }
}
