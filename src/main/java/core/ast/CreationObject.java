package core.ast;

import com.intellij.psi.PsiExpression;
import com.intellij.psi.SmartPsiElementPointer;

public abstract class CreationObject {
    private final TypeObject type;

    public SmartPsiElementPointer<PsiExpression> getCreation() {
        return creation;
    }

    public void setCreation(SmartPsiElementPointer<PsiExpression> creation) {
        this.creation = creation;
    }

    SmartPsiElementPointer<PsiExpression> creation;

    CreationObject(TypeObject type) {
        this.type = type;
    }

    public TypeObject getType() {
        return type;
    }
}
