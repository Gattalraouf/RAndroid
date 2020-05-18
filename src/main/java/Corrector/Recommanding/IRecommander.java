package Corrector.Recommanding;

import com.intellij.openapi.project.Project;

public interface IRecommander {
    public void onRefactor(String filePath, String title, Project myProject);
}
