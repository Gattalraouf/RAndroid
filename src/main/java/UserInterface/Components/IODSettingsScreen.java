package UserInterface.Components;

import Corrector.Refactoring.IODRefactoringUtility;
import UserInterface.Actions.RefactorHSS;
import UserInterface.Actions.RefactorIOD;
import UserInterface.UI.SettingsUIDialog;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;


@State(
        name = "MyConfiguration",
        storages = {
                @Storage(value = "MyConfiguration.xml")
        }
)
public class IODSettingsScreen implements Serializable, ProjectComponent,
        PersistentStateComponent<IODSettingsScreen> {

    public void openComponent() {
        Project project = RefactorIOD.myProject;
        SettingsUIDialog dialog = new SettingsUIDialog("Refactoring IOD code smell", new IODRefactoringUtility(),project);
        dialog.pack();
        dialog.setSize(600, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Nullable
    @Override
    public IODSettingsScreen getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull IODSettingsScreen state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
