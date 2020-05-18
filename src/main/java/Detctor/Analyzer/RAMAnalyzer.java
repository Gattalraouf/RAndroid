package Detctor.Analyzer;

import AdaptedJDeodorant.core.ast.ClassObject;
import AdaptedJDeodorant.core.ast.MethodInvocationObject;
import AdaptedJDeodorant.core.ast.MethodObject;
import AdaptedJDeodorant.core.ast.decomposition.cfg.AbstractVariable;
import Detctor.CSVReadingManager;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class RAMAnalyzer {

    private HashMap<MethodInvocationObject, String> candidates;

    public RAMAnalyzer(String filePath, Project myProject) {
        this.candidates = getRAMCandidates(filePath, myProject);
    }

    public HashMap<MethodInvocationObject, String> getCandidates() {
        return candidates;
    }

    public void setCandidates(HashMap<MethodInvocationObject, String> candidates) {
        this.candidates = candidates;
    }


    private HashMap<MethodInvocationObject, String> getRAMCandidates(String filePath, Project myProject) {

        ArrayList<ClassObject> ramClasses;
        HashMap<MethodInvocationObject, String> candidates = new HashMap<>();
        ramClasses = CSVReadingManager.getaDoctorTargetClass(filePath, "RAM", myProject);

        for (ClassObject ramClass : ramClasses) {
            for (MethodObject method : ramClass.getMethodList()) {
                for (Map.Entry<AbstractVariable, LinkedHashSet<MethodInvocationObject>> usedVariable : method.getMethodBody().getInvokedMethodsThroughLocalVariables().entrySet()) {
                    if (usedVariable.getKey().getType().equals("android.app.AlarmManager")) {
                        for (MethodInvocationObject invok : usedVariable.getValue()) {
                            candidates.put(invok, usedVariable.getKey().getName());
                        }
                    }
                }
            }
        }
        return candidates;
    }
}
