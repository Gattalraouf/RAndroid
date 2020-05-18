package Corrector.Refactoring;

import com.intellij.openapi.project.Project;

public interface IRefactor {
    public void onRefactor(String filePath, String title, Project myProject);
}
