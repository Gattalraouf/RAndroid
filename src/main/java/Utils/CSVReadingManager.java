package Utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVReadingManager {
    public static ArrayList<String[]> ReadFile(String path) {
        ArrayList<String[]> file = new ArrayList<>();
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(path));
            String[] line;
            while ((line = reader.readNext()) != null) {
                file.add(line);
                System.out.println(line[0] + " | " + line[1] );
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return file;
    }
}
