package UserInterface.Components;

import Corrector.Recommanding.UIORefactoringUtility;
import UserInterface.Actions.RefactorHSS;
import UserInterface.Actions.RefactorUIO;
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
public class UIOSettingsScreen implements Serializable, ProjectComponent,
        PersistentStateComponent<UIOSettingsScreen> {

    public void openComponent() {
        Project project = RefactorUIO.myProject;
        SettingsUIDialog dialog = new SettingsUIDialog("Refactoring UIO code smell",new UIORefactoringUtility(),project);
        dialog.pack();
        dialog.setSize(600, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Nullable
    @Override
    public UIOSettingsScreen getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull UIOSettingsScreen state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
