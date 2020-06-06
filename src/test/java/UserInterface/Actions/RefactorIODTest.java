package UserInterface.Actions;

import Corrector.Refactoring.HSSRefactoringUtility;
import Corrector.Refactoring.IODRefactoringUtility;
import UserInterface.Components.HSSSettingsScreen;
import UserInterface.Components.IODSettingsScreen;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RefactorIODTest {
    @Mock
    private Project project;
    @Mock
    private RefactorIOD action;
    @Mock
    private IODSettingsScreen screen;
    @Mock
    private AnActionEvent event;
    @Mock
    private IODRefactoringUtility corrector;
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
        corrector=mock(IODRefactoringUtility.class);
        action = new RefactorIOD();
        screen=mock(IODSettingsScreen.class);

        when(event.getDataContext()).thenReturn(dataContext);
        when(event.getDataContext()).thenReturn(dataContext);
        when(screen.getCorrectionUtility()).thenReturn(corrector);
        when(corrector.getCodeSmellName()).thenReturn("HSS");
        when(dataContext.getData(DataConstants.PROJECT)).thenReturn(project);

        action.actionPerformed(event);

        assertEquals("IOD",action.screen.getCorrectionUtility().getCodeSmellName());
    }
}