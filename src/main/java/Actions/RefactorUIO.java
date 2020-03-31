package Actions;

import Settings.SettingsUIDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnAction;

public class RefactorUIO extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        SettingsUIDialog dialog = new SettingsUIDialog();
        dialog.createComponent();
        dialog.pack();
        dialog.setSize(600,200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        System.exit(0);
    }
}
