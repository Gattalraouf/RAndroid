package Detctor.Analyzer;

import com.intellij.openapi.project.Project;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RAMAnalyzerTest {

    @Mock
    Project project;

    @Test
    void getCandidates() {
        project = mock(Project.class);
        RAMAnalyzer analyzer=new RAMAnalyzer(" ", project);
        assertEquals(analyzer.getCandidates(),analyzer.getCandidates());
    }
}