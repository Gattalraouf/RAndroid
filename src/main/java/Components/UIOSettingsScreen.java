package Components;

import Settings.SettingsUIDialog;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.Serializable;
import java.util.ArrayList;

import Utils.CSVReadingManager;


@State(
        name = "MyConfiguration",
        storages = {
                @Storage(value = "MyConfiguration.xml")
        }
)
public class UIOSettingsScreen implements Serializable, ProjectComponent,
        PersistentStateComponent<UIOSettingsScreen> {

    public String UIOFilePath = "";
    public ArrayList<String[]> file;

    public void openComponent() {
        SettingsUIDialog dialog = new SettingsUIDialog("Refactoring UIO code smell");
        dialog.pack();
        dialog.setSize(600,200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        UIOFilePath = dialog.filePath;
        file = CSVReadingManager.ReadFile(UIOFilePath);
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
