package Components;

import com.intellij.openapi.components.*;
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

    public String UIOFilePath = "";

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
