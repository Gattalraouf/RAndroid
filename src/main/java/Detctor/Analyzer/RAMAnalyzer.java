package Detctor.Analyzer;

import AdaptedJDeodorant.core.ast.ClassObject;
import AdaptedJDeodorant.core.ast.MethodInvocationObject;
import AdaptedJDeodorant.core.ast.MethodObject;
import AdaptedJDeodorant.core.ast.decomposition.cfg.AbstractVariable;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class RAMAnalyzer extends aDoctorAnalyzer {

    private HashMap<String, MethodInvocationObject> candidates;

    public RAMAnalyzer(String filePath, Project myProject) {
        super(filePath);
        codeSmell="RAM";
        this.candidates = getRAMCandidates(filePath, myProject);
    }

    public HashMap<String, MethodInvocationObject> getCandidates() {
        return candidates;
    }

    public void setCandidates(HashMap<String, MethodInvocationObject> candidates) {
        this.candidates = candidates;
    }

    private HashMap<String, MethodInvocationObject> getRAMCandidates(String filePath, Project myProject) {

        ArrayList<ClassObject> ramClasses;
        HashMap<String, MethodInvocationObject> candidates = new HashMap<>();
        HashMap<AbstractVariable, LinkedHashSet<MethodInvocationObject>> InvokeCandidates = new HashMap<>();
        ramClasses = ((aDoctorAnalyzer)getTargetClass(null,filePath," ",myProject)).getClasses();
        int i = 0;
        for (ClassObject ramClass : ramClasses) {
            for (MethodObject method : ramClass.getMethodList()) {
                InvokeCandidates = new HashMap<>();
                InvokeCandidates.putAll(method.getMethodBody().getInvokedMethodsThroughLocalVariables());
                InvokeCandidates.putAll(method.getMethodBody().getInvokedMethodsThroughFields());
                InvokeCandidates.putAll(method.getMethodBody().getInvokedMethodsThroughParameters());
                for (Map.Entry<AbstractVariable, LinkedHashSet<MethodInvocationObject>> usedVariable : InvokeCandidates.entrySet()) {
                    if (usedVariable.getKey().getType().equals("android.app.AlarmManager")) {
                        for (MethodInvocationObject invok : usedVariable.getValue()) {
                            i++;
                            candidates.put(usedVariable.getKey().getName()+" "+i,invok);
                        }
                    }
                }
            }
        }
        return candidates;
    }
}
