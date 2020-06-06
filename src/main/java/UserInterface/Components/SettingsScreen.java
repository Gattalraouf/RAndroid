package UserInterface.Components;

import Corrector.ICorrector;
import UserInterface.Actions.RAndroidAction;
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
public class SettingsScreen implements Serializable, ProjectComponent,
        PersistentStateComponent<SettingsScreen> {

    public ICorrector correctionUtility;
    public RAndroidAction action;
    public SettingsUIDialog dialog=null;

    public void openComponent() {
        Project project = action.getMyProject();
        dialog = new SettingsUIDialog("Refactoring "+correctionUtility.getCodeSmellName()+" code smell",correctionUtility,project);
        dialog.pack();
        dialog.setSize(600, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public SettingsUIDialog getDialog(){
        return dialog;
    }

    public ICorrector getCorrectionUtility(){
        return correctionUtility;
    }

    @Nullable
    @Override
    public SettingsScreen getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SettingsScreen state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}