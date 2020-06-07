package Corrector.Refactoring;

import Detctor.Analyzer.HSSAnalyzer;
import Detctor.Analyzer.PaprikaAnalyzer;
import com.google.common.collect.Iterables;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import AdaptedJDeodorant.core.ast.ASTReader;
import AdaptedJDeodorant.core.ast.ClassObject;
import AdaptedJDeodorant.core.ast.MethodObject;
import AdaptedJDeodorant.core.distance.ProjectInfo;

import java.util.ArrayList;

public class HSSRefactoringUtility extends IRefactor {

    public HSSRefactoringUtility(){
        codeSmellName="HSS";
    }

    @Override
    public void onRefactor(String filePath, String title, Project myProject) {
        this.analyzer =new HSSAnalyzer(filePath);
        ArrayList<String[]> file= ((HSSAnalyzer)analyzer).getFile();
        PsiClass innerClass;
        PsiMethod[] methods;
        ClassObject c;
        MethodObject method;
        ASTReader astReader;


        for (String[] target : Iterables.skip(file, 1)) {
            innerClass =((PaprikaAnalyzer) analyzer.getTargetClass(target," ", title, myProject)).getTargetC();
            methods = innerClass.findMethodsByName(((PaprikaAnalyzer)analyzer).getTargetMethodName(target), false);
            astReader = new ASTReader(new ProjectInfo(myProject), innerClass);
            c = astReader.getSystemObject().getClassObject(innerClass.getQualifiedName());
            method = c.getMethodByName(methods[0].getName());

            //TODO Handle importing Handler thread and Handler
            //TODO Higlight the zone we are fixing
            RunServiceOnBackground(method, c, myProject);

        }
    }

    private void RunServiceOnBackground(MethodObject onStartCommand, ClassObject c, Project myProject) {
        PsiElementFactory elementFactory = PsiElementFactory.getInstance(myProject);
        PsiJavaFile psiFile = c.getPsiFile();

        WriteCommandAction.runWriteCommandAction(myProject, new Runnable() {
            @Override
            public void run() {

                PsiJavaToken LBrace = onStartCommand.getMethodDeclaration().getBody().getLBrace();
                PsiJavaToken RBrace = onStartCommand.getMethodDeclaration().getBody().getRBrace();

                //declare the context of the service public Context mContext=this;
                PsiField contextField = elementFactory.createFieldFromText("public Context mContext=this;", psiFile);
                c.getPsiClass().addAfter(contextField, c.getPsiClass().getLBrace());

                //replace "this" with mContext
                for (PsiStatement s : onStartCommand.getMethodDeclaration().getBody().getStatements()) {
                    if (s.getText().contains("this;") || s.getText().contains("this,")) {
                        String a = s.getText().replaceAll("this,", "mContext,")
                                .replaceAll("this;", "mContext;");
                        s.replace(elementFactory.createStatementFromText(a, psiFile));
                    }
                }

                //get The return Statement
                PsiReturnStatement returnStat = null;
                PsiStatement[] statements = onStartCommand.getMethodDeclaration().getBody().getStatements();
                for (PsiStatement statement : statements) {
                    if (statement instanceof PsiReturnStatement) {
                        returnStat = (PsiReturnStatement) statement;
                    }
                }

                //HandlerThread thread = new HandlerThread("RunCode");
                PsiTypeElement type = elementFactory.createTypeElementFromText("HandlerThread", psiFile);
                PsiElement elem = psiFile.addBefore(type, onStartCommand.getMethodDeclaration().getBody().getFirstBodyElement());
                PsiStatement stat = elementFactory.createStatementFromText("thread = new HandlerThread(\"RunCode\");", psiFile);
                elem = psiFile.addAfter(stat, elem);

                //thread.start();
                stat = elementFactory.createStatementFromText("thread.start();", psiFile);
                elem = psiFile.addAfter(stat, elem);

                //Runnable tr = new Runnable(){
                type = elementFactory.createTypeElementFromText("Runnable", psiFile);
                elem = psiFile.addAfter(type, elem);
                stat = elementFactory.createStatementFromText("r = new Runnable(){", psiFile);
                elem = psiFile.addAfter(stat, elem);

                //@Ovveride public void run() {
                PsiAnnotation annotation = elementFactory.createAnnotationFromText("@Override", psiFile);
                stat = elementFactory.createStatementFromText("public", psiFile);
                annotation.addAfter(stat, psiFile);
                type = elementFactory.createTypeElementFromText("void", psiFile);
                annotation.addAfter(type, psiFile);
                stat = elementFactory.createStatementFromText("run()", psiFile);
                annotation.addAfter(stat, psiFile);
                annotation.addAfter(LBrace, psiFile);
                psiFile.addAfter(annotation, elem);

                if (returnStat == null) {
                    returnStat = (PsiReturnStatement) onStartCommand.getMethodDeclaration().getBody().getLastBodyElement();
                }

                //close the braces of the Runnable and run
                elem = psiFile.addBefore(RBrace, returnStat);
                elem = psiFile.addAfter(RBrace, elem);
                stat = elementFactory.createStatementFromText(";", psiFile);
                elem = psiFile.addAfter(stat, elem);

                //Handler backgroundHandler = new Handler(thread.getLooper());
                stat = elementFactory.createStatementFromText("Handler backgroundHandler = new Handler(thread.getLooper());", psiFile);
                elem = psiFile.addAfter(stat, elem);

                //backgroundHandler.post(r);
                stat = elementFactory.createStatementFromText("backgroundHandler.post(r);", psiFile);
                psiFile.addAfter(stat, elem);
            }
        });

    }


}
