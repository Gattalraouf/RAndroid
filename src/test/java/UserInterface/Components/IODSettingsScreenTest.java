package UserInterface.Components;

import Corrector.ICorrector;
import Corrector.Refactoring.IODRefactoringUtility;
import UserInterface.Actions.RAndroidAction;
import UserInterface.Actions.RefactorIOD;
import UserInterface.UI.SettingsUIDialog;
import com.intellij.openapi.project.Project;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IODSettingsScreenTest {

    @Mock
    private Project project;
    @Mock
    private RefactorIOD action;
    @Mock
    private IODRefactoringUtility corrector;
    @Mock
    private SettingsScreen screen;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void openComponent() {
        project = mock(Project.class);
        corrector=mock(IODRefactoringUtility.class);
        screen=mock(SettingsScreen.class);
        action = mock(RefactorIOD.class);

        when(action.getMyProject()).thenReturn(project);
        when(corrector.getCodeSmellName()).thenReturn("IOD");
        when(screen.getDialog()).thenReturn(new SettingsUIDialog("Refactoring IOD code smell",corrector,project));

        screen.openComponent();
        assertNotNull(screen.getDialog());
    }

}