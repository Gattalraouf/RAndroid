package Components;

import UI.SettingsUIDialog;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
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
public class RAMSettingsScreen implements Serializable, ProjectComponent,
        PersistentStateComponent<RAMSettingsScreen> {

    public void openComponent() {
        SettingsUIDialog dialog = new SettingsUIDialog("Refactoring RAM code smell");
        dialog.pack();
        dialog.setSize(600, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Nullable
    @Override
    public RAMSettingsScreen getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull RAMSettingsScreen state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
