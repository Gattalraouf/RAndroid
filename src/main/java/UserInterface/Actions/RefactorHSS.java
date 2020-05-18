package UserInterface.Actions;

import UserInterface.Components.HSSSettingsScreen;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RefactorHSS extends RAndroidAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        super.actionPerformed(e);
        screen = new HSSSettingsScreen();
        screen.openComponent();
    }
}