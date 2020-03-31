package Actions;

import Components.UIOSettingsScreen;
import Settings.SettingsUIDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnAction;

public class RefactorUIO extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        UIOSettingsScreen uio = new UIOSettingsScreen();
        uio.openComponent();
    }
}
