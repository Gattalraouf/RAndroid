package Detctor.Analyzer;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class IDSAnalyzerTest {

    private IDSAnalyzer analyzer=new IDSAnalyzer(" ");

    @Test
    void getHachMapVariables() {
        assertEquals(analyzer.getHachMapVariables(),analyzer.HachMapVariables);
    }

    @Test
    void getHachMapReturns() {
        assertEquals(analyzer.getHachMapReturns(),analyzer.HachMapReturns);
    }

    @Test
    void getForStatements() {
        assertEquals(analyzer.getForStatements(),analyzer.ForStatements);
    }

    @Test
    void getIdsClasses() {
        assertEquals(analyzer.getIdsClasses(),analyzer.idsClasses);
    }

}