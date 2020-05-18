package UserInterface.Components;


import Corrector.Refactoring.IDSRefactoringUtility;
import UserInterface.Actions.RefactorHSS;
import UserInterface.Actions.RefactorIDS;
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
public class IDSSettingsScreen implements Serializable, ProjectComponent,
        PersistentStateComponent<IDSSettingsScreen> {

    public void openComponent() {
        Project project = RefactorIDS.myProject;
        SettingsUIDialog dialog = new SettingsUIDialog("Refactoring IDS code smell", new IDSRefactoringUtility(),project);
        dialog.pack();
        dialog.setSize(600, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Nullable
    @Override
    public IDSSettingsScreen getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull IDSSettingsScreen state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}