package UserInterface.Actions;

import UserInterface.Components.UIOSettingsScreen;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RefactorUIO extends RAndroidAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        super.actionPerformed(e);
        screen = new UIOSettingsScreen();
        screen.openComponent();
    }
}
