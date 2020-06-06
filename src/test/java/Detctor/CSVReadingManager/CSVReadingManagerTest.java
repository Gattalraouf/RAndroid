package Detctor.CSVReadingManager;

import com.opencsv.exceptions.CsvValidationException;
import org.codehaus.groovy.syntax.Numbers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CSVReadingManagerTest {

    @Test
    void readFile() {
        ArrayList<String[]> expectedFile=new ArrayList<>();
        expectedFile.add(new String[]{"Class", "IDS"});
        expectedFile.add(new String[]{"a", "3"});

        String filepath="/Users/abir/Desktop/test 2.csv";
        int i=0;
        for(String[] line:CSVReadingManager.ReadFile(filepath)){
            assertArrayEquals(expectedFile.get(i),line);
            i++;
        }
    }


//    @Test
//    void testExpectedException() {
//        Assertions.assertThrows(CsvValidationException.class, () -> {
//            CSVReadingManager.ReadFile("/Users/abir/Desktop/test3.xlt");
//        });
//    }


}