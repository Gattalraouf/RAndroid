package UserInterface.Actions;

import UserInterface.Components.IDSSettingsScreen;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RefactorIDS extends RAndroidAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        super.actionPerformed(e);
        screen = new IDSSettingsScreen();
        screen.openComponent();
    }
}