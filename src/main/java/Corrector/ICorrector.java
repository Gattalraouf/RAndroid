package Corrector;

import Detctor.Analyzer.IAnalyzer;
import com.intellij.openapi.project.Project;

public abstract class ICorrector {
    protected IAnalyzer analyzer;
    protected String codeSmellName;

    public abstract void onRefactor(String filePath, String title, Project myProject);

    public String getCodeSmellName(){
        return codeSmellName;
    }
}
