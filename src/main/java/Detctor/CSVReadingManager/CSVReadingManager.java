package Detctor.CSVReadingManager;

import com.intellij.openapi.project.Project;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public abstract class CSVReadingManager {

    public static ArrayList<String[]> ReadFile(String path) {
        ArrayList<String[]> file = new ArrayList<>();
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(path));
            String[] line;
            while ((line = reader.readNext()) != null) {
                file.add(line);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return file;
    }

    public abstract void getTargetClass(String[] target,String filePath, String title, Project myProject, String codeSmell, int index);

}
