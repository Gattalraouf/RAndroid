package Detctor.Analyzer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IAnalyzerTest {

    HSSAnalyzer analyzer = new HSSAnalyzer();

    @Test
    void getTargetClass() {

    }

    @Test
    void getCSVReader() {
        IAnalyzer analyzer=new PaprikaAnalyzer();
        assertEquals(analyzer.getCSVReader(),analyzer.CSVReader);
    }
}