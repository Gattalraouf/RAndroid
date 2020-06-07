package Detctor.Analyzer;

import Detctor.CSVReadingManager.CSVPaprikaReadingManager;
import Detctor.CSVReadingManager.CSVReadingManager;
import Detctor.CSVReadingManager.CSVaDoctorReadingManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import java.util.ArrayList;

public class  PaprikaAnalyzer extends IAnalyzer {
    protected PsiClass targetClass;

    public PaprikaAnalyzer(String filepath) {
        super(filepath);
        CSVReader= new CSVPaprikaReadingManager();
    }

    public PaprikaAnalyzer(){
        CSVReader= new CSVPaprikaReadingManager();
    }

    @Override
    public void findClass() {
        targetClass=((CSVPaprikaReadingManager) CSVReader).getTargetC();
    }

    public PsiClass getTargetC(){
        return targetClass;
    }

    public ArrayList<String[]> getFile(){
        return CSVReadingManager.ReadFile(filePath);
    }

    public String getTargetMethodName(String[] target){
        String[] targetDetails =target[index].split("#", 0);
        return targetDetails[0];
    }
}


