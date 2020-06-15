package Detctor.Analyzer;

import Detctor.CSVReadingManager.CSVPaprikaReadingManager;
import Detctor.CSVReadingManager.CSVaDoctorReadingManager;
import com.intellij.openapi.project.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

class aDoctorAnalyzerTest {

    private aDoctorAnalyzer analyzer=new aDoctorAnalyzer();
    private aDoctorAnalyzer analyzer2=new aDoctorAnalyzer(" ");

    @Mock
    private Project project;

    @ParameterizedTest
    @MethodSource("AnalyzerProvider")
    void findClass(aDoctorAnalyzer analyzer) {
        analyzer.findClass();
        CSVaDoctorReadingManager manager= (CSVaDoctorReadingManager) analyzer.getCSVReader();
        assertEquals(manager.getClasses(),analyzer.getClasses());
    }

    @ParameterizedTest
    @MethodSource("AnalyzerProvider")
    void getClasses(aDoctorAnalyzer analyzer) {
        assertEquals(analyzer.getClasses(),analyzer.Classes);
    }

    private Stream AnalyzerProvider() throws IOException {

        project = mock(Project.class);

        String filepath="/Users/abir/Desktop/test 2.csv";
        RAMAnalyzer RAMAnalyzer=new RAMAnalyzer(filepath, project);
        IDSAnalyzer IDSAnalyzer=new IDSAnalyzer(filepath);
        aDoctorAnalyzer aDoctorAnalyzer=new aDoctorAnalyzer();


        return Stream.of(
                arguments(RAMAnalyzer),
                arguments(IDSAnalyzer),
                arguments(aDoctorAnalyzer)
        );

    }
}