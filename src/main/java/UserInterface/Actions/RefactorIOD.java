package UserInterface.Actions;

import UserInterface.Components.IODSettingsScreen;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RefactorIOD extends AnAction {
    public static Project myProject;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        myProject = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        IODSettingsScreen iod = new IODSettingsScreen();
        iod.openComponent();
    }
}
