package UserInterface.Actions;

import Corrector.Refactoring.HSSRefactoringUtility;
import Corrector.Refactoring.IDSRefactoringUtility;
import UserInterface.Components.HSSSettingsScreen;
import UserInterface.Components.IDSSettingsScreen;
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

class RefactorIDSTest {
    @Mock
    private Project project;
    @Mock
    private RefactorIDS action;
    @Mock
    private IDSSettingsScreen screen;
    @Mock
    private AnActionEvent event;
    @Mock
    private IDSRefactoringUtility corrector;
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
        corrector=mock(IDSRefactoringUtility.class);
        action = new RefactorIDS();
        screen=mock(IDSSettingsScreen.class);

        when(event.getDataContext()).thenReturn(dataContext);
        when(event.getDataContext()).thenReturn(dataContext);
        when(screen.getCorrectionUtility()).thenReturn(corrector);
        when(corrector.getCodeSmellName()).thenReturn("HSS");
        when(dataContext.getData(DataConstants.PROJECT)).thenReturn(project);

        action.actionPerformed(event);

        assertEquals("IDS",action.screen.getCorrectionUtility().getCodeSmellName());
    }
}