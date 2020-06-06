package UserInterface.Actions;

import Corrector.Recommanding.UIORefactoringUtility;
import Corrector.Refactoring.HSSRefactoringUtility;
import UserInterface.Components.HSSSettingsScreen;
import UserInterface.Components.UIOSettingsScreen;
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

class RefactorUIOTest {
    @Mock
    private Project project;
    @Mock
    private RefactorUIO action;
    @Mock
    private UIOSettingsScreen screen;
    @Mock
    private AnActionEvent event;
    @Mock
    private UIORefactoringUtility corrector;
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
        corrector=mock(UIORefactoringUtility.class);
        action = new RefactorUIO();
        screen=mock(UIOSettingsScreen.class);

        when(event.getDataContext()).thenReturn(dataContext);
        when(event.getDataContext()).thenReturn(dataContext);
        when(screen.getCorrectionUtility()).thenReturn(corrector);
        when(corrector.getCodeSmellName()).thenReturn("HSS");
        when(dataContext.getData(DataConstants.PROJECT)).thenReturn(project);

        action.actionPerformed(event);

        assertEquals("UIO",action.screen.getCorrectionUtility().getCodeSmellName());
    }

}