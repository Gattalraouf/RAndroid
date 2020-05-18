package AdaptedJDeodorant.core.ast.decomposition.cfg;

import AdaptedJDeodorant.core.ast.decomposition.AbstractStatement;

class CFGBlockNode extends CFGNode {

    private CFGNode controlParent;

    CFGBlockNode(AbstractStatement statement) {
        super(statement);
    }

    CFGNode getControlParent() {
        return controlParent;
    }

    void setControlParent(CFGNode controlParent) {
        this.controlParent = controlParent;
    }

}