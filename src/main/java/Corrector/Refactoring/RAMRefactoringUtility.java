package Corrector.Refactoring;

import AdaptedJDeodorant.core.ast.MethodInvocationObject;
import Detctor.Analyzer.IODAnalyzer;
import Detctor.Analyzer.RAMAnalyzer;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;

public class RAMRefactoringUtility extends IRefactor {

    public RAMRefactoringUtility(){
        codeSmellName="RAM";
    }

    @Override
    public void onRefactor(String filePath, String title, Project myProject) {
        analyzer = new RAMAnalyzer(filePath, myProject);
        boolean repeat = false;

        for (String invok : ((RAMAnalyzer)analyzer).getCandidates().keySet()) {
            replaceSetRepeating(((RAMAnalyzer)analyzer).getCandidates().get(invok), myProject, invok);
            if (((RAMAnalyzer)analyzer).getCandidates().get(invok).getMethodName().equals("setRepeating")) {
                repeat = true;
            }
        }
        if (repeat) {
            onRefactor(filePath, title, myProject);
        }

        //TODO Higlight the zone we are fixing
    }

    private void replaceSetRepeating(MethodInvocationObject invok, Project myProject, String var) {
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(myProject);
        if (invok.getMethodName().equals("setRepeating")) {

            PsiElement navigationElement = invok.getMethodInvocation().getNavigationElement();
            if (navigationElement instanceof Navigatable && ((Navigatable) navigationElement).canNavigate())
            {
                ((Navigatable) navigationElement).navigate(true);
            }

            WriteCommandAction.runWriteCommandAction(myProject, () -> {
                PsiExpression[] list = invok.getMethodInvocation().getArgumentList().getExpressions();
                PsiStatement callExpression = factory.createStatementFromText(
                        var.split(" ")[0] + ".setInexactRepeating ( " +
                                list[0].getText() + " , " + list[1].getText() + " , " + list[2].getText()
                                + " , " + list[3].getText() + " )", invok.getMethodInvocation());
                invok.getMethodInvocation().replace(callExpression);

            });
        }
    }
}
