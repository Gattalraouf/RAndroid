package Actions;

import Components.HSSSettingsScreen;
import Components.UIOSettingsScreen;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RefactorHSS extends AnAction {
    public static Project myProject;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        myProject = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        HSSSettingsScreen hss = new HSSSettingsScreen();
        hss.openComponent();
    }
}