package core.ast.decomposition.cfg;

import com.intellij.psi.PsiElement;

import java.util.*;

public class PDGSliceUnionCollection {
    private final Map<BasicBlock, PDGSliceUnion> sliceUnionMap;

    public PDGSliceUnionCollection(PDG pdg, PlainVariable localVariableCriterion) {
        this.sliceUnionMap = new LinkedHashMap<>();
        Set<PDGNode> nodeCriteria = pdg.getAssignmentNodesOfVariableCriterion(localVariableCriterion);
        Map<PDGNode, Set<BasicBlock>> boundaryBlockMap = new LinkedHashMap<>();
        for (PDGNode nodeCriterion : nodeCriteria) {
            Set<BasicBlock> boundaryBlocks = pdg.boundaryBlocks(nodeCriterion);
            boundaryBlockMap.put(nodeCriterion, boundaryBlocks);
        }
        List<Set<BasicBlock>> list = new ArrayList<>(boundaryBlockMap.values());
        if (!list.isEmpty()) {
            Set<BasicBlock> basicBlockIntersection = new LinkedHashSet<>(list.get(0));
            for (int i = 1; i < list.size(); i++) {
                basicBlockIntersection.retainAll(list.get(i));
            }
            for (BasicBlock basicBlock : basicBlockIntersection) {
                PDGSliceUnion sliceUnion = new PDGSliceUnion(pdg, basicBlock, nodeCriteria, localVariableCriterion);
                if (sliceUnion.satisfiesRules())
                    sliceUnionMap.put(basicBlock, sliceUnion);
            }
        }
    }

    public PDGSliceUnionCollection(PDG pdg, PsiElement statement, PlainVariable localVariableCriterion) {
        this.sliceUnionMap = new LinkedHashMap<>();
        Set<PDGNode> nodeCriteria = pdg.getNodesIncludingInstances(statement);
        Map<PDGNode, Set<BasicBlock>> boundaryBlockMap = new LinkedHashMap<>();
        for (PDGNode nodeCriterion : nodeCriteria) {
            Set<BasicBlock> boundaryBlocks = pdg.boundaryBlocks(nodeCriterion);
            boundaryBlockMap.put(nodeCriterion, boundaryBlocks);
        }
        List<Set<BasicBlock>> list = new ArrayList<>(boundaryBlockMap.values());
        if (!list.isEmpty()) {
            Set<BasicBlock> basicBlockIntersection = new LinkedHashSet<>(list.get(0));
            for (int i = 1; i < list.size(); i++) {
                basicBlockIntersection.retainAll(list.get(i));
            }
            for (BasicBlock basicBlock : basicBlockIntersection) {
                PDGSliceUnion sliceUnion = new PDGSliceUnion(pdg, basicBlock, nodeCriteria, localVariableCriterion);
                if (sliceUnion.satisfiesRules())
                    sliceUnionMap.put(basicBlock, sliceUnion);
            }
        }
    }

    public Collection<PDGSliceUnion> getSliceUnions() {
        return sliceUnionMap.values();
    }
}
