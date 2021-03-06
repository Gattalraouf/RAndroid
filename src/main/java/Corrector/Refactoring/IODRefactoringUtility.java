package Corrector.Refactoring;

import AdaptedJDeodorant.Utils.JDeodorantFacade;
import AdaptedJDeodorant.Utils.PsiUtils;
import AdaptedJDeodorant.core.ast.ASTReader;
import AdaptedJDeodorant.core.ast.ClassObject;
import AdaptedJDeodorant.core.ast.MethodObject;
import AdaptedJDeodorant.core.ast.decomposition.AbstractStatement;
import AdaptedJDeodorant.core.ast.decomposition.CompositeStatementObject;
import AdaptedJDeodorant.core.ast.decomposition.StatementObject;
import AdaptedJDeodorant.core.ast.decomposition.cfg.ASTSlice;
import AdaptedJDeodorant.core.ast.decomposition.cfg.ASTSliceGroup;
import AdaptedJDeodorant.core.distance.ProjectInfo;
import AdaptedJDeodorant.ide.fus.collectors.IntelliJDeodorantCounterCollector;
import AdaptedJDeodorant.ide.refactoring.extractMethod.ExtractMethodCandidateGroup;
import AdaptedJDeodorant.ide.refactoring.extractMethod.MyExtractMethodProcessor;
import Detctor.Analyzer.IODAnalyzer;
import Detctor.Analyzer.PaprikaAnalyzer;
import com.google.common.collect.Iterables;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.refactoring.HelpID;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.PrepareFailedException;
import com.intellij.util.SmartList;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class IODRefactoringUtility extends IRefactor {

    public IODRefactoringUtility() {
        codeSmellName = "IOD";
    }

    @Override
    public void onRefactor(String filePath, String title, Project myProject) {
        analyzer = new IODAnalyzer(filePath);
        ArrayList<String[]> file = ((IODAnalyzer) analyzer).getFile();
        PsiClass innerClass;
        Set<ASTSliceGroup> candidates;
        PsiMethod[] methods;
        ClassObject c;
        ASTReader astReader;

        for (String[] target : Iterables.skip(file, 1)) {
            candidates = new HashSet<>();
            innerClass = ((PaprikaAnalyzer) analyzer.getTargetClass(target, " ", title, myProject)).getTargetC();
            methods = innerClass.findMethodsByName(((IODAnalyzer) analyzer).getTargetMethodName(target), false);
            astReader = new ASTReader(new ProjectInfo(myProject), innerClass);
            c = astReader.getSystemObject().getClassObject(innerClass.getQualifiedName());
            //Handle long Method case
            MethodObject onDraw = c.getMethodByName(methods[0].getName());
            JDeodorantFacade.processMethod(candidates, c, onDraw);
            extractMethod(candidates, myProject);
            candidates = new HashSet<>();

            //Handle object creation
            onDraw = c.getMethodByName(methods[0].getName());
            JDeodorantFacade.processMethodForInstances(candidates, c, onDraw);
            extractMethod(candidates, myProject);

            //Long treatment inside onDraw
            onDraw = c.getMethodByName(methods[0].getName());
            int count = PsiUtils.countLOC(onDraw.getMethodBody().getCompositeStatement());
            if (count >= 20) {
                recommendLongMethod(onDraw, c, myProject);
            }

        }
    }


    private void recommendLongMethod(MethodObject method, ClassObject c, Project myProject) {
        PsiJavaFile file = c.getPsiFile();
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(myProject);

        String message = "//TODO Make sure to optimize the treatment happening in the method, it is causing an " +
                "IOD code smell instance that may affect the display of your app.";

        WriteCommandAction.runWriteCommandAction(myProject, () -> {
            PsiComment comment = factory.createCommentFromText(message, file);
            file.addBefore(comment, method.getMethodDeclaration().getFirstChild());
        });
    }


    private void extractMethod(Set<ASTSliceGroup> candidates, Project myProject) {
        final List<ExtractMethodCandidateGroup> extractMethodCandidateGroups = candidates.stream().filter(Objects::nonNull)
                .map(sliceGroup ->
                        sliceGroup.getCandidates().stream()
                                .filter(c -> canBeExtracted(c, myProject))
                                .collect(toSet()))
                .filter(set -> !set.isEmpty())
                .map(ExtractMethodCandidateGroup::new)
                .collect(toList());
        if (extractMethodCandidateGroups.size() != 0) {
            TransactionGuard.getInstance().submitTransactionAndWait(doExtract(extractMethodCandidateGroups.get(0).getCandidates().stream().findAny().get()));
        }
    }

    /**
     * Checks that the slice can be extracted into a separate method without compilation errors.
     */
    private boolean canBeExtracted(ASTSlice slice, Project myProject) {
        SmartList<PsiStatement> statementsToExtract = ((IODAnalyzer) analyzer).getStatementsToExtract(slice);

        MyExtractMethodProcessor processor = new MyExtractMethodProcessor(myProject,
                null, statementsToExtract.toArray(new PsiElement[0]), slice.getLocalVariableCriterion().getType(), "Refactoring", "", HelpID.EXTRACT_METHOD,
                slice.getSourceTypeDeclaration(), slice.getLocalVariableCriterion());

        processor.setOutputVariable();

        try {
            processor.setShowErrorDialogs(false);
            return processor.prepare();

        } catch (PrepareFailedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Extracts statements into new method.
     *
     * @param slice computation slice.
     * @return callback to run when "Refactor" button is selected.
     */
    private Runnable doExtract(ASTSlice slice) {
        return () -> {
            Editor editor = FileEditorManager.getInstance(slice.getSourceMethodDeclaration().getProject()).getSelectedTextEditor();
            SmartList<PsiStatement> statementsToExtract = ((IODAnalyzer) analyzer).getStatementsToExtract(slice);

            MyExtractMethodProcessor processor = new MyExtractMethodProcessor(slice.getSourceMethodDeclaration().getProject(),
                    editor, statementsToExtract.toArray(new PsiElement[0]), slice.getLocalVariableCriterion().getType(),
                    "", "", HelpID.EXTRACT_METHOD,
                    slice.getSourceTypeDeclaration(), slice.getLocalVariableCriterion());

            processor.setOutputVariable();

            try {
                processor.setShowErrorDialogs(true);
                if (processor.prepare()) {
                    ExtractMethodHandler.invokeOnElements(slice.getSourceMethodDeclaration().getProject(), processor,
                            slice.getSourceMethodDeclaration().getContainingFile(), true);
                    if (editor != null && processor.getExtractedMethod() != null) {
                        IntelliJDeodorantCounterCollector.getInstance().extractMethodRefactoringApplied(editor.getProject(),
                                slice, processor.getExtractedMethod());
                    }
                }
            } catch (PrepareFailedException e) {
                e.printStackTrace();
            }
        };
    }

}
