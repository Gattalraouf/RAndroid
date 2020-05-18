package UserInterface.Actions;

import UserInterface.Components.HSSSettingsScreen;
import UserInterface.Components.SettingsScreen;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RAndroidAction extends AnAction {
    public static Project myProject;
    public SettingsScreen screen;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        myProject = (Project) e.getDataContext().getData(DataConstants.PROJECT);
    }
}