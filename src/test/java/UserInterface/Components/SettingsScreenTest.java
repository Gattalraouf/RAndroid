package UserInterface.Components;

import Corrector.ICorrector;
import Corrector.Refactoring.HSSRefactoringUtility;
import UserInterface.Actions.RAndroidAction;
import UserInterface.Actions.RefactorHSS;
import UserInterface.UI.SettingsUIDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SettingsScreenTest {

    SettingsScreen screenTest=new SettingsScreen();

    @Mock
    private Project project;
    @Mock
    private RAndroidAction action;
    @Mock
    private ICorrector corrector;
    @Mock
    private SettingsScreen screen;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void openComponent() {
        project = mock(Project.class);
        corrector=mock(ICorrector.class);
        screen=mock(SettingsScreen.class);
        action = mock(RAndroidAction.class);

        when(action.getMyProject()).thenReturn(project);
        when(corrector.getCodeSmellName()).thenReturn("Code Smell");
        when(screen.getDialog()).thenReturn(new SettingsUIDialog("Refactoring Code Smell code smell",corrector,project));

        screen.openComponent();
        assertNotNull(screen.getDialog());
    }

    @Test
    void getCorrectionUtility() {
        assertEquals(screenTest.getCorrectionUtility(),screenTest.correctionUtility);
    }

    @Test
    void getState() {
        assertEquals(screenTest.getState(),screenTest);
    }

    @Test
    void getDialog() {
        assertEquals(screenTest.getDialog(),screenTest.dialog);
    }

    @Test
    void loadState() {
        screenTest.loadState(screenTest);
        assertNotNull(XmlSerializerUtil.getAccessors(SettingsScreen.class));
    }
}