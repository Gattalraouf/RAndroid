package ide.refactoring.CodeSmells;

import Utils.CSVReadingManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiStatement;
import core.ast.ClassObject;
import core.ast.MethodInvocationObject;
import core.ast.MethodObject;
import core.ast.decomposition.cfg.AbstractVariable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;

public class RAMRefactoringUtility {
    public void onRefactorRAM(String filePath, Project myProject) {

        ArrayList<ClassObject> ramClasses;
        ramClasses = CSVReadingManager.getaDoctorTargetClass(filePath, "RAM", myProject);

        for (ClassObject ramClass : ramClasses) {
            for (MethodObject method : ramClass.getMethodList()) {

                for (Map.Entry<AbstractVariable, LinkedHashSet<MethodInvocationObject>> usedVariable : method.getMethodBody().getInvokedMethodsThroughLocalVariables().entrySet()) {
                    if (usedVariable.getKey().getType().equals("android.app.AlarmManager")) {
                        for (MethodInvocationObject invok : usedVariable.getValue()) {
                            replaceSetRepeating(invok, myProject, usedVariable.getKey().getName());
                        }
                    }
                }
            }
        }

    }

    private void replaceSetRepeating(MethodInvocationObject invok, Project myProject, String var) {
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(myProject);
        if (invok.getMethodName().equals("setRepeating")) {
            WriteCommandAction.runWriteCommandAction(myProject, () -> {
                PsiExpression[] list = invok.getMethodInvocation().getArgumentList().getExpressions();
                PsiStatement callExpression = factory.createStatementFromText(
                        var + ".setInexactRepeating ( " +
                                list[0].getText() + " , " + list[1].getText() + " , " + list[2].getText()
                                + " , " + list[3].getText() + " );", invok.getMethodInvocation());
                invok.getMethodInvocation().replace(callExpression);
            });
        }
    }
}
