package Corrector;

import com.intellij.openapi.project.Project;

public abstract class ICorrector {
    public abstract void onRefactor(String filePath, String title, Project myProject);
}
