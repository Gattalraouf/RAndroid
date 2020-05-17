package AdaptedJDeodorant.core.ast;

import com.intellij.psi.PsiNewExpression;

import static Utils.PsiUtils.toPointer;

public class ArrayCreationObject extends CreationObject {

    public ArrayCreationObject(TypeObject type) {
        super(type);
    }

    public PsiNewExpression getArrayCreation() {
        return (PsiNewExpression) this.creation.getElement();
    }

    public void setArrayCreation(PsiNewExpression creation) {
        this.creation = toPointer(creation);
    }
}
