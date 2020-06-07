package Detctor.Analyzer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RAMAnalyzerTest {

    @Test
    void getCandidates() {
        RAMAnalyzer analyzer=new RAMAnalyzer(" ");
        assertEquals(analyzer.getCandidates(),analyzer.candidates);
    }
}