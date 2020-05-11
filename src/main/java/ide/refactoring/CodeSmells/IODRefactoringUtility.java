package ide.refactoring.CodeSmells;

import Utils.CSVReadingManager;
import Utils.JDeodorantFacade;
import com.google.common.collect.Iterables;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;
import com.intellij.refactoring.HelpID;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.PrepareFailedException;
import com.intellij.util.SmartList;
import core.ast.ASTReader;
import core.ast.ClassObject;
import core.ast.decomposition.cfg.ASTSlice;
import core.ast.decomposition.cfg.ASTSliceGroup;
import core.ast.decomposition.cfg.PDGNode;
import core.distance.ProjectInfo;
import ide.fus.collectors.IntelliJDeodorantCounterCollector;
import ide.refactoring.extractMethod.ExtractMethodCandidateGroup;
import ide.refactoring.extractMethod.MyExtractMethodProcessor;

import java.util.*;

import static Utils.PsiUtils.isChild;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class IODRefactoringUtility {

    public void onRefactorIOD(String filePath, String title, Project myProject) {
        ArrayList<String[]> file;
        file = CSVReadingManager.ReadFile(filePath);
        PsiClass innerClass;
        Set<ASTSliceGroup> candidates;
        PsiMethod[] methods;
        ClassObject c;
        ASTReader astReader;

        for (String[] target : Iterables.skip(file, 1)) {
            candidates = new HashSet<>();
            innerClass = CSVReadingManager.getPaprikaTargetClass(target, title, myProject, "IOD");
            methods = innerClass.findMethodsByName(CSVReadingManager.getTargetMethodName(target, title, "IOD"), false);
            astReader = new ASTReader(new ProjectInfo(myProject), innerClass);
            c = astReader.getSystemObject().getClassObject(innerClass.getQualifiedName());
            //Handle long Method case
            JDeodorantFacade.processMethod(candidates, c, c.getMethodByName(methods[0].getName()));
            extractMethod(candidates, myProject);
            candidates = new HashSet<>();

            //Handle object creation
            JDeodorantFacade.processMethodForInstances(candidates, c, c.getMethodByName(methods[0].getName()));
            extractMethod(candidates, myProject);

        }

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
        SmartList<PsiStatement> statementsToExtract = getStatementsToExtract(slice);

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
     * Collects statements that can be extracted into a separate method.
     */
    public SmartList<PsiStatement> getStatementsToExtract(ASTSlice slice) {
        Set<PDGNode> nodes = slice.getSliceNodes();
        SmartList<PsiStatement> statementsToExtract = new SmartList<>();

        for (PDGNode pdgNode : nodes) {
            boolean isNotChild = true;
            for (PDGNode node : nodes) {
                if (isChild(node.getASTStatement(), pdgNode.getASTStatement())) {
                    isNotChild = false;
                }
            }
            if (isNotChild) {
                statementsToExtract.add(pdgNode.getASTStatement());
            }
        }
        return statementsToExtract;
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
            SmartList<PsiStatement> statementsToExtract = getStatementsToExtract(slice);

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
