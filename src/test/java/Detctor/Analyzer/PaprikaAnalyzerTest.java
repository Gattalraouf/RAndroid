package Detctor.Analyzer;

import Detctor.CSVReadingManager.CSVPaprikaReadingManager;
import Detctor.CSVReadingManager.CSVReadingManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.*;
import static org.junit.jupiter.api.Assertions.*;

class PaprikaAnalyzerTest {

    private PaprikaAnalyzer analyzer=new PaprikaAnalyzer();

    @ParameterizedTest
    @MethodSource("AnalyzerProvider")
    void findClass(PaprikaAnalyzer analyzer) {
        analyzer.findClass();
        CSVPaprikaReadingManager manager= (CSVPaprikaReadingManager) analyzer.getCSVReader();
        assertEquals(analyzer.getTargetC(),manager.getTargetC());
    }

    @ParameterizedTest
    @MethodSource("AnalyzerProvider")
    void getTargetC(PaprikaAnalyzer analyzer) {
        assertEquals(analyzer.getTargetC(),analyzer.targetClass);
    }

    @ParameterizedTest
    @MethodSource("AnalyzerProvider")
    void getFile(PaprikaAnalyzer analyzer) {
        ArrayList<String[]> expectedFile=new ArrayList<>();
        expectedFile.add(new String[]{"Class", "IDS"});
        expectedFile.add(new String[]{"a", "3"});

        String filepath="/Users/abir/Desktop/test 2.csv";
        int i=0;
        for(String[] line: analyzer.getFile()){
            assertArrayEquals(expectedFile.get(i),line);
            i++;
        }
    }

    private static Stream AnalyzerProvider() throws IOException{

        String filepath="/Users/abir/Desktop/test 2.csv";
        IODAnalyzer IODAnalyzer=new IODAnalyzer(filepath);
        HSSAnalyzer HSSAnalyzer=new HSSAnalyzer(filepath);
        UIOAnalyzer UIOAnalyzer=new UIOAnalyzer(filepath);

        return Stream.of(
                arguments(IODAnalyzer),
                arguments(HSSAnalyzer),
                arguments(UIOAnalyzer)
        );

    }

    @ParameterizedTest
    @MethodSource("getMethodNameProvider")
    void getTargetMethodName(String[] target,PaprikaAnalyzer analyzer,String expectedMethod) {
        assertEquals(analyzer.getTargetMethodName(target),expectedMethod);
    }


    private static Stream getMethodNameProvider() throws IOException{

        String[] IODTarget=new String[]{"1","onStartCommand#com.skanderjabouzi.salat.AdhanService","1"};
        IODAnalyzer IODAnalyzer=new IODAnalyzer();
        String IODMethodNameExpected="onStartCommand";

        String[] HSSTarget=new String[]{"1","8","109","onStartCommand#com.skanderjabouzi.salat.AdhanService","1"};
        HSSAnalyzer HssAnalyzer=new HSSAnalyzer();
        String HssMethodNameExpected="onStartCommand";

        String[] UIOTarget=new String[]{"1","onStartCommand#com.skanderjabouzi.salat.AdhanService","1"};
        UIOAnalyzer UIOAnalyzer=new UIOAnalyzer();
        String UIOMethodNameExpected="onStartCommand";

        return Stream.of(
                arguments(IODTarget,IODAnalyzer,IODMethodNameExpected),
                arguments(HSSTarget,HssAnalyzer,HssMethodNameExpected),
                arguments(UIOTarget,UIOAnalyzer,UIOMethodNameExpected)
        );
    }
}