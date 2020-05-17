package AdaptedJDeodorant.ide.refactoring;

import org.jetbrains.annotations.NotNull;
import AdaptedJDeodorant.ide.refactoring.moveMethod.MoveMethodRefactoring;

public interface RefactoringVisitor<R> {
    @NotNull
    R visit(final @NotNull MoveMethodRefactoring refactoring);
}