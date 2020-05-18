package Corrector.Refactoring;

import AdaptedJDeodorant.core.ast.MethodInvocationObject;
import Detctor.Analyzer.RAMAnalyzer;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiStatement;

public class RAMRefactoringUtility implements IRefactor {


    @Override
    public void onRefactor(String filePath, String title, Project myProject) {
        RAMAnalyzer ram = new RAMAnalyzer(filePath, myProject);

        for (MethodInvocationObject invok : ram.getCandidates().keySet()) {
            replaceSetRepeating(invok, myProject, ram.getCandidates().get(invok));
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
