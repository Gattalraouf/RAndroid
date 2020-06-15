package Detctor.Analyzer;

import AdaptedJDeodorant.core.ast.*;
import AdaptedJDeodorant.core.ast.decomposition.MethodBodyObject;
import AdaptedJDeodorant.core.ast.decomposition.cfg.AbstractVariable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;

import java.util.*;

public class RAMAnalyzer extends aDoctorAnalyzer {

    private HashMap<String, MethodInvocationObject> candidates;

    public RAMAnalyzer(String filePath, Project myProject) {
        super(filePath);
        codeSmell = "RAM";
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
        ramClasses = ((aDoctorAnalyzer) getTargetClass(null, filePath, " ", myProject)).getClasses();
        int i = 0;
        for (ClassObject ramClass : ramClasses) {

            for (PsiMethod subMethod : extractMethodsAnonymousClassesFields(ramClass)) {
                i = processPsiMethod(candidates, i, subMethod);
            }

            for (MethodObject method : ramClass.getMethodList()) {
                i = processMethod(candidates, i, method);

                for (AnonymousClassDeclarationObject anonymousClassDeclarationObject : method.getAnonymousClassDeclarations()) {
                    for (MethodObject subMethod : anonymousClassDeclarationObject.getMethodList()) {
                        i = processMethod(candidates, i, subMethod);
                    }
                }
            }
        }
        return candidates;
    }

    private List<PsiMethod> extractMethodsAnonymousClassesFields(ClassObject classObjects) {
        List<PsiMethod> anonymousClassInFieldsList = new ArrayList<>();
        ListIterator<FieldObject> itr = classObjects.getFieldIterator();
        while (itr.hasNext()) {
            FieldObject fieldObject = itr.next();
            if (fieldObject.getVariableDeclaration().getInitializer() != null)
                for (PsiElement elem : fieldObject.getVariableDeclaration().getInitializer().getChildren()) {
                    if (elem instanceof PsiAnonymousClass) {
                        for (PsiElement subElem : elem.getChildren()) {
                            if (subElem instanceof PsiMethod) {
                                anonymousClassInFieldsList.add((PsiMethod) subElem);
                            }
                        }
                    }
                }
        }
        return anonymousClassInFieldsList;
    }

    private int processMethod(HashMap<String, MethodInvocationObject> candidates, int i, MethodObject method) {
        HashMap<AbstractVariable, LinkedHashSet<MethodInvocationObject>> InvokeCandidates = new HashMap<>();
        InvokeCandidates.putAll(method.getMethodBody().getInvokedMethodsThroughLocalVariables());
        InvokeCandidates.putAll(method.getMethodBody().getInvokedMethodsThroughFields());
        InvokeCandidates.putAll(method.getMethodBody().getInvokedMethodsThroughParameters());
        for (Map.Entry<AbstractVariable, LinkedHashSet<MethodInvocationObject>> usedVariable : InvokeCandidates.entrySet()) {
            if (usedVariable.getKey().getType().equals("android.app.AlarmManager")) {
                for (MethodInvocationObject invok : usedVariable.getValue()) {
                    i++;
                    candidates.put(usedVariable.getKey().getName() + " " + i, invok);
                }
            }
        }
        return i;
    }

    private int processPsiMethod(HashMap<String, MethodInvocationObject> candidates, int i, PsiMethod method) {
        HashMap<AbstractVariable, LinkedHashSet<MethodInvocationObject>> InvokeCandidates = new HashMap<>();
        MethodBodyObject body = new MethodBodyObject(method.getBody());
        InvokeCandidates.putAll(body.getInvokedMethodsThroughLocalVariables());
        InvokeCandidates.putAll(body.getInvokedMethodsThroughFields());
        InvokeCandidates.putAll(body.getInvokedMethodsThroughParameters());
        for (Map.Entry<AbstractVariable, LinkedHashSet<MethodInvocationObject>> usedVariable : InvokeCandidates.entrySet()) {
            if (usedVariable.getKey().getType().equals("android.app.AlarmManager")) {
                for (MethodInvocationObject invok : usedVariable.getValue()) {
                    i++;
                    candidates.put(usedVariable.getKey().getName() + " " + i, invok);
                }
            }
        }
        return i;
    }
}
