package Detctor.Analyzer;

import AdaptedJDeodorant.core.ast.ClassObject;
import Detctor.CSVReadingManager.CSVPaprikaReadingManager;
import Detctor.CSVReadingManager.CSVReadingManager;
import Detctor.CSVReadingManager.CSVaDoctorReadingManager;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;

public class aDoctorAnalyzer extends IAnalyzer {

    ArrayList<ClassObject> Classes;

    public aDoctorAnalyzer(String filepath) {
        super(filepath);
        CSVReader = new CSVaDoctorReadingManager();
    }


    @Override
    public void findClass() {
        Classes=((CSVaDoctorReadingManager) CSVReader).getClasses();
    }

    public ArrayList<ClassObject> getClasses(){
        return Classes;
    }

}
