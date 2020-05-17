package AdaptedJDeodorant.core.ast.decomposition.cfg;

import AdaptedJDeodorant.core.ast.FieldObject;
import AdaptedJDeodorant.core.ast.VariableDeclarationObject;

import java.util.Set;

class PDGExitNode extends PDGStatementNode {
    private AbstractVariable returnedVariable;

    PDGExitNode(CFGNode cfgNode, Set<VariableDeclarationObject> variableDeclarationsInMethod,
                Set<FieldObject> fieldsAccessedInMethod) {
        super(cfgNode, variableDeclarationsInMethod, fieldsAccessedInMethod);
        if (cfgNode instanceof CFGExitNode) {
            CFGExitNode exitNode = (CFGExitNode) cfgNode;
            returnedVariable = exitNode.getReturnedVariable();
        }
    }

    AbstractVariable getReturnedVariable() {
        return returnedVariable;
    }
}
