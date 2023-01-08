package org.example.program;

import org.example.exceptions.CyclicDependencyException;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * Program that takes all files from directory and merge them into result file
 */
public class FileProcessor {
    private final TreeMap<Path, MyFile> storage;
    private ArrayList<MyFile> files;
    static Path root;
    private final Path res;

    /**
     * Constructor
     * @param directory directory with files to merge
     * @param result path to file where merge files should be written
     */
    public FileProcessor(Path directory, Path result) {
        root = directory;
        res = result;
        storage = new TreeMap<>();
        MyFile.setRoot(root.toString());
    }

    /**
     * Runs the program
     *
     * @throws IOException if file doesn't exist or closed for reading or writing
     * @throws CyclicDependencyException if files contains cyclic dependency
     */
    public void run() throws IOException, CyclicDependencyException {
        getAllFiles(root.toFile());
        files = new ArrayList<>(storage.values());
        files.sort(MyFile::compareTo);
        doAllRequiresExist();
        isCircled();
        writeDataIntoFile();

    }

    /**
     * Writes result to file.
     *
     * @throws IOException if file closed for writing
     */
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

    /**
     * Check for all required files to be written.
     *
     * @param file that requires need to check
     * @return true if all requires was written else false
     */
    private boolean isAllRequiresBeenWritten(MyFile file) {
        for (Path req_file_path : file.getDependencies()) {
            if (!storage.get(req_file_path).wasWritten()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all requires files contains in main directory.
     *
     * @throws FileNotFoundException if file was not found in directory
     */
    private void doAllRequiresExist() throws FileNotFoundException {
        for (MyFile file : files) {
            for (Path req_file : file.getDependencies()) {
                if (storage.get(req_file) == null) {
                    throw new FileNotFoundException(String.format("File %s required in %s doesn't exist%n", req_file, file.getFilepath()));
                }
            }
        }
    }

    /**
     * Checks for cyclic dependencies in files;
     *
     * @param myFile file to check
     * @param cycle  array for files which are in cycle
     * @return array of files which are in cycle
     */

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

    /**
     * Creates string from cyclic paths
     * @param files array of paths
     * @return string
     */
    private String createStringWithCycleFiles(ArrayList<String> files) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cycle in these files:\n");
        for (String cycled_files : files) {
            stringBuilder.append(cycled_files);
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    /**
     * Throws CyclicDependencyException if cycle exists
     * @throws CyclicDependencyException if files contains cyclic dependency
     */
    private void isCircled() throws CyclicDependencyException {
        for (MyFile file : files) {
            ArrayList<String> cycle = new ArrayList<>();
            cycle = BreadthFirstSearch(file, cycle);
            if (cycle.size() > 0) {
                throw new CyclicDependencyException(createStringWithCycleFiles(cycle));
            }
        }
    }

    /**
     * Recursively goes throw all directories in directory and adds files into array
     * @param directory directory to search files in
     * @throws IOException if file closed for reading
     */
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
