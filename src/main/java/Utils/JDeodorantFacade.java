package Utils;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.psi.PsiVariable;
import core.ast.ASTReader;
import core.ast.ClassObject;
import core.ast.MethodObject;
import core.ast.SystemObject;
import core.ast.decomposition.cfg.*;
import core.distance.*;

import java.util.*;

public class JDeodorantFacade {

    public static List<MoveMethodCandidateRefactoring> getMoveMethodRefactoringOpportunities(ProjectInfo project, ProgressIndicator indicator) {
        new ASTReader(project, indicator);
        Set<ClassObject> classObjectsToBeExamined = new LinkedHashSet<>(ASTReader.getSystemObject().getClassObjects());

        Set<String> classNamesToBeExamined = new LinkedHashSet<>();
        for (ClassObject classObject : classObjectsToBeExamined) {
            if (!classObject.isEnum() && !classObject.isInterface() && !classObject.isGeneratedByParserGenerator())
                classNamesToBeExamined.add(classObject.getName());
        }
        MySystem system = new MySystem(ASTReader.getSystemObject(), false);
        DistanceMatrix distanceMatrix = new DistanceMatrix(system);

        List<MoveMethodCandidateRefactoring> candidateRefactoring =
                distanceMatrix.getMoveMethodCandidateRefactoringsByAccess(classNamesToBeExamined, indicator);
        List<MoveMethodCandidateRefactoring> moveMethodCandidateList = new ArrayList<>(candidateRefactoring);
        Collections.sort(moveMethodCandidateList);
        return moveMethodCandidateList;
    }

    public static TreeSet<ExtractClassCandidateGroup> getExtractClassRefactoringOpportunities(ProjectInfo project, ProgressIndicator indicator) {
        new ASTReader(project, indicator);
        SystemObject systemObject = ASTReader.getSystemObject();
        if (systemObject != null) {
            Set<ClassObject> classObjectsToBeExamined = new LinkedHashSet<>(systemObject.getClassObjects());
            Set<String> classNamesToBeExamined = new LinkedHashSet<>();
            for (ClassObject classObject : classObjectsToBeExamined) {
                if (!classObject.isEnum() && !classObject.isInterface() && !classObject.isGeneratedByParserGenerator())
                    classNamesToBeExamined.add(classObject.getName());
            }
            MySystem system = new MySystem(systemObject, true);
            DistanceMatrix distanceMatrix = new DistanceMatrix(system);

            List<ExtractClassCandidateRefactoring> extractClassCandidateList = new ArrayList<>(distanceMatrix.getExtractClassCandidateRefactorings(classNamesToBeExamined, indicator));

            HashMap<String, ExtractClassCandidateGroup> groupedBySourceClassMap = new HashMap<>();
            for (ExtractClassCandidateRefactoring candidate : extractClassCandidateList) {
                if (groupedBySourceClassMap.containsKey(candidate.getSourceEntity())) {
                    groupedBySourceClassMap.get(candidate.getSourceEntity()).addCandidate(candidate);
                } else {
                    ExtractClassCandidateGroup group = new ExtractClassCandidateGroup(candidate.getSourceEntity());
                    group.addCandidate(candidate);
                    groupedBySourceClassMap.put(candidate.getSourceEntity(), group);
                }
            }
            for (String sourceClass : groupedBySourceClassMap.keySet()) {
                groupedBySourceClassMap.get(sourceClass).groupConcepts();
            }
            return new TreeSet<>(groupedBySourceClassMap.values());
        } else {
            return new TreeSet<>();
        }
    }

    public static Set<ASTSliceGroup> getExtractMethodRefactoringOpportunities(ProjectInfo project, ProgressIndicator indicator) {
        new ASTReader(project, indicator);

        SystemObject systemObject = ASTReader.getSystemObject();
        Set<ASTSliceGroup> extractedSliceGroups = new TreeSet<>();
        if (systemObject != null) {
            Set<ClassObject> classObjectsToBeExamined = new LinkedHashSet<>(systemObject.getClassObjects());

            for (ClassObject classObject : classObjectsToBeExamined) {
                if (!classObject.isEnum() && !classObject.isInterface() && !classObject.isGeneratedByParserGenenator()) {
                    ListIterator<MethodObject> methodIterator = classObject.getMethodIterator();
                    while (methodIterator.hasNext()) {
                        MethodObject methodObject = methodIterator.next();
                        processMethod(extractedSliceGroups, classObject, methodObject);
                    }
                }
            }
        }
        return extractedSliceGroups;
    }

    private static void processMethod(final Set<ASTSliceGroup> extractedSliceGroups, ClassObject classObject, MethodObject methodObject) {
        if (methodObject.getMethodBody() != null) {
            CFG cfg = new CFG(methodObject);
            PDG pdg = new PDG(cfg, classObject.getPsiFile(), classObject.getFieldsAccessedInsideMethod(methodObject));
            for (PsiVariable declaration : pdg.getVariableDeclarationsInMethod()) {
                PlainVariable variable = new PlainVariable(declaration);
                PDGSliceUnionCollection sliceUnionCollection = new PDGSliceUnionCollection(pdg, variable);
                double sumOfExtractedStatementsInGroup = 0.0;
                double sumOfDuplicatedStatementsInGroup = 0.0;
                double sumOfDuplicationRatioInGroup = 0.0;
                int maximumNumberOfExtractedStatementsInGroup = 0;
                int groupSize = sliceUnionCollection.getSliceUnions().size();
                ASTSliceGroup sliceGroup = new ASTSliceGroup();
                for (PDGSliceUnion sliceUnion : sliceUnionCollection.getSliceUnions()) {
                    ASTSlice slice = new ASTSlice(sliceUnion);
                    if (!slice.isVariableCriterionDeclarationStatementIsDeeperNestedThanExtractedMethodInvocationInsertionStatement()) {
                        int numberOfExtractedStatements = slice.getNumberOfSliceStatements();
                        int numberOfDuplicatedStatements = slice.getNumberOfDuplicatedStatements();
                        double duplicationRatio = (double) numberOfDuplicatedStatements / (double) numberOfExtractedStatements;
                        sumOfExtractedStatementsInGroup += numberOfExtractedStatements;
                        sumOfDuplicatedStatementsInGroup += numberOfDuplicatedStatements;
                        sumOfDuplicationRatioInGroup += duplicationRatio;
                        if (numberOfExtractedStatements > maximumNumberOfExtractedStatementsInGroup)
                            maximumNumberOfExtractedStatementsInGroup = numberOfExtractedStatements;
                        sliceGroup.addCandidate(slice);
                    }
                }
                if (!sliceGroup.getCandidates().isEmpty()) {
                    sliceGroup.setAverageNumberOfExtractedStatementsInGroup(sumOfExtractedStatementsInGroup / (double) groupSize);
                    sliceGroup.setAverageNumberOfDuplicatedStatementsInGroup(sumOfDuplicatedStatementsInGroup / (double) groupSize);
                    sliceGroup.setAverageDuplicationRatioInGroup(sumOfDuplicationRatioInGroup / (double) groupSize);
                    sliceGroup.setMaximumNumberOfExtractedStatementsInGroup(maximumNumberOfExtractedStatementsInGroup);
                    extractedSliceGroups.add(sliceGroup);
                }
            }
            for (PsiVariable declaration : pdg.getVariableDeclarationsAndAccessedFieldsInMethod()) {
                PlainVariable variable = new PlainVariable(declaration);
                PDGObjectSliceUnionCollection objectSliceUnionCollection = new PDGObjectSliceUnionCollection(pdg, variable);
                double sumOfExtractedStatementsInGroup = 0.0;
                double sumOfDuplicatedStatementsInGroup = 0.0;
                double sumOfDuplicationRatioInGroup = 0.0;
                int maximumNumberOfExtractedStatementsInGroup = 0;
                int groupSize = objectSliceUnionCollection.getSliceUnions().size();
                ASTSliceGroup sliceGroup = new ASTSliceGroup();
                for (PDGObjectSliceUnion objectSliceUnion : objectSliceUnionCollection.getSliceUnions()) {
                    ASTSlice slice = new ASTSlice(objectSliceUnion);
                    if (!slice.isVariableCriterionDeclarationStatementIsDeeperNestedThanExtractedMethodInvocationInsertionStatement()) {
                        int numberOfExtractedStatements = slice.getNumberOfSliceStatements();
                        int numberOfDuplicatedStatements = slice.getNumberOfDuplicatedStatements();
                        double duplicationRatio = (double) numberOfDuplicatedStatements / (double) numberOfExtractedStatements;
                        sumOfExtractedStatementsInGroup += numberOfExtractedStatements;
                        sumOfDuplicatedStatementsInGroup += numberOfDuplicatedStatements;
                        sumOfDuplicationRatioInGroup += duplicationRatio;
                        if (numberOfExtractedStatements > maximumNumberOfExtractedStatementsInGroup)
                            maximumNumberOfExtractedStatementsInGroup = numberOfExtractedStatements;
                        sliceGroup.addCandidate(slice);
                    }
                }
                if (!sliceGroup.getCandidates().isEmpty()) {
                    sliceGroup.setAverageNumberOfExtractedStatementsInGroup(sumOfExtractedStatementsInGroup / (double) groupSize);
                    sliceGroup.setAverageNumberOfDuplicatedStatementsInGroup(sumOfDuplicatedStatementsInGroup / (double) groupSize);
                    sliceGroup.setAverageDuplicationRatioInGroup(sumOfDuplicationRatioInGroup / (double) groupSize);
                    sliceGroup.setMaximumNumberOfExtractedStatementsInGroup(maximumNumberOfExtractedStatementsInGroup);
                    extractedSliceGroups.add(sliceGroup);
                }
            }
        }
    }

}
