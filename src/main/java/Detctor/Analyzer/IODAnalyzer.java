package Detctor.Analyzer;

import AdaptedJDeodorant.core.ast.decomposition.cfg.ASTSlice;
import AdaptedJDeodorant.core.ast.decomposition.cfg.PDGNode;
import Corrector.Refactoring.IODRefactoringUtility;
import com.intellij.psi.PsiStatement;
import com.intellij.util.SmartList;

import java.util.Set;

import static AdaptedJDeodorant.Utils.PsiUtils.isChild;

public class IODAnalyzer extends PaprikaAnalyzer {

    public IODAnalyzer(String filepath){
        super(filepath);
        this.codeSmell="IOD";
    }

    public IODAnalyzer(){
        super();
        this.codeSmell="IOD";
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

}
