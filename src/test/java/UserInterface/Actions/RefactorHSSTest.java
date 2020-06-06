package UserInterface.Actions;

import Corrector.Refactoring.HSSRefactoringUtility;
import UserInterface.Components.HSSSettingsScreen;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class RefactorHSSTest {
    @Mock
    private Project project;
    @Mock
    private RefactorHSS action;
    @Mock
    private HSSSettingsScreen screen;
    @Mock
    private AnActionEvent event;
    @Mock
    private HSSRefactoringUtility corrector;
    @Mock
    private DataContext dataContext;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void actionPerformed() {
        project = mock(Project.class);
        event = mock(AnActionEvent.class);
        dataContext = mock(DataContext.class);
        corrector=mock(HSSRefactoringUtility.class);
        action = new RefactorHSS();
        screen=mock(HSSSettingsScreen.class);

        when(event.getDataContext()).thenReturn(dataContext);
        when(event.getDataContext()).thenReturn(dataContext);
        when(screen.getCorrectionUtility()).thenReturn(corrector);
        when(corrector.getCodeSmellName()).thenReturn("HSS");
        when(dataContext.getData(DataConstants.PROJECT)).thenReturn(project);

        action.actionPerformed(event);

        assertEquals("HSS",action.screen.getCorrectionUtility().getCodeSmellName());
    }
}