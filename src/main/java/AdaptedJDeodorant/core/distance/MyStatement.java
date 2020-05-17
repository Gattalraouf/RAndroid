package AdaptedJDeodorant.core.distance;

import AdaptedJDeodorant.core.ast.decomposition.AbstractStatement;

class MyStatement extends MyAbstractStatement {

    public MyStatement(AbstractStatement statement) {
        super(statement);
    }

    public MyStatement(MyMethodInvocation methodInvocation) {
        super(methodInvocation);
    }
}
