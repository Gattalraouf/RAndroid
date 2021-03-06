package AdaptedJDeodorant.ide.refactoring.moveMethod;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import AdaptedJDeodorant.ide.refactoring.Refactoring;
import AdaptedJDeodorant.ide.refactoring.RefactoringVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

import static AdaptedJDeodorant.Utils.PsiUtils.getHumanReadableName;
import static AdaptedJDeodorant.Utils.PsiUtils.getNumberOfLinesInMethod;

/**
 * Representation of a refactoring, which moves method to a target class.
 * Once the method moved, the corresponding pointer becomes invalid.
 */
public class MoveMethodRefactoring implements Refactoring {
    private final @NotNull
    SmartPsiElementPointer<PsiMethod> method;
    private final @NotNull
    SmartPsiElementPointer<PsiClass> targetClass;
    private final @NotNull
    String qualifiedMethodName;
    private final int sourceAccessedMembers;
    private final int targetAccessedMembers;
    private final int methodLength;
    private final int methodParametersCount;

    /**
     * Creates refactoring.
     *
     * @param method      a method that is moved in this refactoring.
     * @param targetClass destination class in which given method is placed in this refactoring.
     */
    public MoveMethodRefactoring(
            final @NotNull PsiMethod method,
            final @NotNull PsiClass targetClass,
            int sourceAccessedMembers,
            int targetAccessedMembers
    ) {
        this.method = ApplicationManager.getApplication().runReadAction(
                (Computable<SmartPsiElementPointer<PsiMethod>>) () ->
                        SmartPointerManager.getInstance(method.getProject()).createSmartPsiElementPointer(method)
        );
        this.targetClass = ApplicationManager.getApplication().runReadAction(
                (Computable<SmartPsiElementPointer<PsiClass>>) () ->
                        SmartPointerManager.getInstance(targetClass.getProject()).createSmartPsiElementPointer(targetClass)
        );
        this.qualifiedMethodName = getHumanReadableName(this.method.getElement());
        this.sourceAccessedMembers = sourceAccessedMembers;
        this.targetAccessedMembers = targetAccessedMembers;
        this.methodLength = getNumberOfLinesInMethod(method);
        this.methodParametersCount = method.getParameterList().getParametersCount();
    }

    /**
     * Returns method that is moved in this refactoring.
     */
    public @NotNull
    Optional<PsiMethod> getOptionalMethod() {
        return Optional.ofNullable(method.getElement());
    }

    /**
     * Returns method that is moved in this refactoring.
     */
    public @NotNull
    PsiMethod getMethod() {
        return Optional.ofNullable(method.getElement()).orElseThrow(() ->
                new IllegalStateException("Cannot get method. Reference is invalid."));
    }

    public @Nullable
    Optional<PsiClass> getOptionalContainingClass() {
        return method.getElement() == null ?
                Optional.empty() : Optional.ofNullable(method.getElement().getContainingClass());
    }

    @NotNull
    public PsiClass getContainingClass() {
        return Optional.ofNullable(getMethod().getContainingClass())
                .orElseThrow(() -> new IllegalStateException("No containing class."));
    }

    /**
     * Returns class in which method is placed in this refactoring.
     */
    public @NotNull
    Optional<PsiClass> getOptionalTargetClass() {
        return Optional.ofNullable(targetClass.getElement());
    }

    /**
     * Returns class in which method is placed in this refactoring.
     */
    public @NotNull
    PsiClass getTargetClass() {
        return Optional.ofNullable(targetClass.getElement()).orElseThrow(() ->
                new IllegalStateException("Cannot get target class. Reference is invalid."));
    }

    @NotNull
    public <R> R accept(final @NotNull RefactoringVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MoveMethodRefactoring that = (MoveMethodRefactoring) o;

        return Objects.equals(method.getElement(), that.method.getElement())
                && Objects.equals(targetClass.getElement(), that.targetClass.getElement());
    }

    public boolean methodEquals(@NotNull MoveMethodRefactoring that) {
        if (this == that) return true;

        return Objects.equals(method.getElement(), that.method.getElement());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(method.getElement());
        result = 31 * result + Objects.hashCode(targetClass.getElement());
        return result;
    }

    @Override
    public String toString() {
        return "MoveToClassRefactoring{" +
                "method=" + method +
                ", targetClass=" + targetClass +
                '}';
    }

    @NotNull
    @Override
    public String getDescription() {
        return getHumanReadableName(method.getElement()) + DELIMITER +
                getHumanReadableName(targetClass.getElement()) + DELIMITER +
                getSourceAccessedMembers() + "/" + getTargetAccessedMembers();
    }

    @NotNull
    @Override
    public String getExportDefaultFilename() {
        return "Feature-Envy";
    }

    @NotNull
    public String getQualifiedMethodName() {
        return qualifiedMethodName;
    }

    public int getSourceAccessedMembers() {
        return sourceAccessedMembers;
    }

    public int getTargetAccessedMembers() {
        return targetAccessedMembers;
    }

    public int getMethodLength() {
        return methodLength;
    }

    public int getMethodParametersCount() {
        return methodParametersCount;
    }
}