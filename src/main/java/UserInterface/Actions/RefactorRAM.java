package UserInterface.Actions;

import UserInterface.Components.RAMSettingsScreen;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RefactorRAM extends RAndroidAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        super.actionPerformed(e);
        screen = new RAMSettingsScreen();
        screen.openComponent();
    }
}
