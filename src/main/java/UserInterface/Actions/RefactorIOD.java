package UserInterface.Actions;

import UserInterface.Components.IODSettingsScreen;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RefactorIOD extends RAndroidAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        super.actionPerformed(e);
        screen = new IODSettingsScreen(this);
        screen.openComponent();
    }
}
