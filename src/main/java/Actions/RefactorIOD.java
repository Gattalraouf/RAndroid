package Actions;

import Components.IODSettingsScreen;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnAction;

public class RefactorIOD extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        IODSettingsScreen uio = new IODSettingsScreen();
        uio.openComponent();
    }
}
