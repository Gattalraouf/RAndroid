package Detctor.Analyzer;

import Detctor.CSVReadingManager.CSVReadingManager;
import com.intellij.openapi.project.Project;

public abstract class IAnalyzer {
    String codeSmell;
    String filePath;
    int index=1;
    CSVReadingManager CSVReader;


    public IAnalyzer(String filepath){
        this.filePath=filepath;
    }

    public IAnalyzer getTargetClass(String[] target,String filePath, String title, Project myProject){
        CSVReader.getTargetClass(target,filePath, title, myProject,  codeSmell, index);
        findClass();
        return this;
    }

    public abstract void findClass();
}
