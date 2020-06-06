package Corrector.Recommanding;
import Detctor.Analyzer.PaprikaAnalyzer;
import Detctor.Analyzer.UIOAnalyzer;
import com.google.common.collect.Iterables;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import AdaptedJDeodorant.core.ast.ASTReader;
import AdaptedJDeodorant.core.ast.ClassObject;
import AdaptedJDeodorant.core.ast.MethodInvocationObject;
import AdaptedJDeodorant.core.ast.MethodObject;
import AdaptedJDeodorant.core.ast.decomposition.MethodBodyObject;
import AdaptedJDeodorant.core.ast.decomposition.cfg.PlainVariable;
import AdaptedJDeodorant.core.distance.ProjectInfo;

import java.util.ArrayList;
import java.util.Set;

public class UIORefactoringUtility extends IRecommander {

    public UIORefactoringUtility(){
        codeSmellName="UIO";
    }

    private void clipRect(MethodObject onDraw, ClassObject c, Project myProject) {
        PsiJavaFile file = c.getPsiFile();
        int NRectDraw = 0, NClipRect = 0, Ntranslation = 0, NRotation = 0, NDrawPath = 0, NclipPath = 0;

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(myProject);
        MethodBodyObject body = onDraw.getMethodBody();
        Set<MethodInvocationObject> canvasInvokes = body.getInvokedMethodsThroughParameters().get(new PlainVariable(onDraw.getParameter(0).getVariableDeclaration()));
        for (MethodInvocationObject invocationObject : canvasInvokes) {
            if (invocationObject.getMethodName().equals("drawPath")) {
                NDrawPath++;
                if (NDrawPath != NclipPath)
                    WriteCommandAction.runWriteCommandAction(myProject, () -> {
                        PsiStatement statement = factory.createStatementFromText(
                                "canvas.clipPath(" + invocationObject.getMethodInvocation().getArgumentList()
                                        .getExpressions()[0].getText() + ");", file);
                        file.addBefore(statement, invocationObject.getMethodInvocation().getParent());
                    });
            } else if (!invocationObject.getMethodName().equals("drawColor") &&
                    !invocationObject.getMethodName().equals("drawARGB") &&
                    !invocationObject.getMethodName().equals("drawRGB") &&
                    !invocationObject.getMethodName().contains("Text") &&
                    !invocationObject.getMethodName().contains("Paint") &&
                    !invocationObject.getMethodName().contains("Bitmap") &&
                    invocationObject.getMethodName().contains("draw")) {

                NRectDraw++;
            } else if (invocationObject.getMethodName().contains("clipRect")) {
                NClipRect++;
            } else if (invocationObject.getMethodName().contains("clipPath")) {
                NclipPath++;
            } else if (invocationObject.getMethodName().equals("translate")) {
                Ntranslation++;
            } else if (invocationObject.getMethodName().equals("rotate")) {
                NRotation++;
            }
        }

        String clip = "//TODO change the parameters of the used " + NClipRect + "canvas.clipRect(...) to cover the " +
                "visible drawings only on the screen.";

        String rect = "//TODO use canvas.clipRect() or canvas.clipOutRect() taking in consideration the "
                + NRectDraw + " RectF shapes you are drawing in order that you don't draw shapes over each other " +
                "without clipping the hidden parts.";

        String addit = "//You can visualize overdraw using color tinting on your device with the Debug GPU Overdraw " +
                "tool https://developer.android.com/tools/performance/debug-gpu-overdraw/index.html.";

        String translation = "//Pay Attention to " + Ntranslation + "x canvas.translate(...) and " + NRotation
                + "x canvas.rotate(...) that are used in your code.";


        if (NRotation != 0 || Ntranslation != 0) {
            WriteCommandAction.runWriteCommandAction(myProject, () -> {
                PsiComment comment = factory.createCommentFromText(translation, file);
                file.addBefore(comment, onDraw.getMethodDeclaration().getFirstChild());
            });
        }

        if (NRectDraw != 0 && NClipRect == 0) {
            WriteCommandAction.runWriteCommandAction(myProject, () -> {
                PsiComment comment = factory.createCommentFromText(addit, file);
                file.addBefore(comment, onDraw.getMethodDeclaration().getFirstChild());
                comment = factory.createCommentFromText(rect, file);
                file.addBefore(comment, onDraw.getMethodDeclaration().getFirstChild());
            });
        } else if (NRectDraw != 0) {
            WriteCommandAction.runWriteCommandAction(myProject, () -> {
                PsiComment comment = factory.createCommentFromText(addit, file);
                file.addBefore(comment, onDraw.getMethodDeclaration().getFirstChild());
                comment = factory.createCommentFromText(clip, file);
                file.addBefore(comment, onDraw.getMethodDeclaration().getFirstChild());
            });
        }

        for (MethodInvocationObject method : onDraw.getMethodBody().getInvokedMethodsThroughThisReference()) {
            if (method.getParameterList().contains("android.graphics.Canvas")) {
                clipRect(c.getMethodByName(method.getMethodName()), c, myProject);
            }
        }

    }

    @Override
    public void onRefactor(String filePath, String title, Project myProject) {
        analyzer=new UIOAnalyzer(filePath);
        ArrayList<String[]> file=((UIOAnalyzer)analyzer).getFile();
        PsiClass innerClass;
        PsiMethod[] methods;
        ClassObject c;
        MethodObject method;
        ASTReader astReader;

        for (String[] target : Iterables.skip(file, 1)) {
            innerClass = ((PaprikaAnalyzer)analyzer.getTargetClass(target," ", title, myProject)).getTargetC();
            methods = innerClass.findMethodsByName(((UIOAnalyzer)analyzer).getTargetMethodName(target), false);
            astReader = new ASTReader(new ProjectInfo(myProject), innerClass);
            c = astReader.getSystemObject().getClassObject(innerClass.getQualifiedName());
            method = c.getMethodByName(methods[0].getName());
            clipRect(method, c, myProject);
        }
    }
}
