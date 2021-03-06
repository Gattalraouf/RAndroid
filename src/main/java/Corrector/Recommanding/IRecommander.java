package Corrector.Recommanding;

import Corrector.ICorrector;
import com.intellij.openapi.project.Project;

public abstract class IRecommander extends ICorrector {
    public abstract void onRefactor(String filePath, String title, Project myProject);
}
