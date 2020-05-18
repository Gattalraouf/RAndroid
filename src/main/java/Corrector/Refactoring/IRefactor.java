package Corrector.Refactoring;

import Corrector.ICorrector;
import com.intellij.openapi.project.Project;

public abstract class IRefactor extends ICorrector {
    public abstract void onRefactor(String filePath, String title, Project myProject);
}
