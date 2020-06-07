package Detctor.Analyzer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HSSAnalyzerTest {

    @Test
    void getTargetMethodName() {
        String[] target= new String[]{"1","8","109","onStartCommand#com.skanderjabouzi.salat.AdhanService"};
        HSSAnalyzer a=new HSSAnalyzer("");
        assertEquals("onStartCommand",a.getTargetMethodName(target));
    }

}