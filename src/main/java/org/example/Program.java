package org.example;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class Program {
    TreeMap<Path, MyFile> storage;
    Path root;

    Program(String directory) {
        root = Path.of(directory);
        storage = new TreeMap<>();
        getAllFiles(root.toFile());
        //storage.put(Path.of("hahah", "heh"), new MyFile(""));
        for (Path name: storage.keySet()) {
            System.out.println(name);
        }
    }

    public void getAllFiles(File directory) {; //path указывает на директорию
        File[] arrFiles = directory.listFiles();
        if (arrFiles == null) {
            return;
        }
        //List<File> lst = Arrays.asList(arrFiles);
        for (File file: arrFiles) {
            if (file.isDirectory()) {
                //,System.out.println(file.getAbsolutePath()+ '\\' + file.getName());
                getAllFiles(file.getAbsoluteFile());
            } else {
                storage.put(file.getAbsoluteFile().toPath(), new MyFile(file.getAbsoluteFile().toPath()));
            }
        }
    }
}
