package ide.refactoring.extractMethod;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import core.ast.decomposition.cfg.ASTSlice;
import ide.refactoring.Refactoring;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import static Utils.PsiUtils.*;

/**
 * Representation of a refactoring, which suggests to extract code into separate method.
 */
public class ExtractMethodCandidateGroup implements Refactoring {
    private final @NotNull
    SmartPsiElementPointer<PsiElement> method;
    private @NotNull
    Set<ASTSlice> candidates;
    private final String qualifiedMethodName;

    /**
     * Creates refactoring instance.
     *
     * @param slices slice group that consist of candidates to extract.
     */
    public ExtractMethodCandidateGroup(Set<ASTSlice> slices) {
        this.method = toPointer(slices.iterator().next().getSourceMethodDeclaration());
        this.candidates = slices;
        this.qualifiedMethodName = getHumanReadableName(method.getElement());
    }

    /**
     *  a method from which code is proposed to be extracted into a separate method.
     */
    public @NotNull
    PsiMethod getMethod() {
        return Optional.ofNullable((PsiMethod) method.getElement()).orElseThrow(() ->
                new IllegalStateException("Cannot get method. Reference is invalid."));
    }

    /**
     * Returns a method that is proposed to be moved in this refactoring.
     */
    public @NotNull
    Optional<PsiMethod> getOptionalMethod() {
        return Optional.ofNullable((PsiMethod) method.getElement());
    }

    @NotNull
    public Set<ASTSlice> getCandidates() {
        return candidates;
    }

    @NotNull
    @Override
    public String getDescription() {
        Optional<PsiMethod> method = getOptionalMethod();
        if (!method.isPresent()) return "";
        String methodName = getHumanReadableName(method.get());
        StringBuilder sb = new StringBuilder();
        Iterator<ASTSlice> candidatesIterator = candidates.iterator();
        for (int i = 0; i < candidates.size(); i++) {
            ASTSlice slice = candidatesIterator.next();
            sb.append(methodName).append(DELIMITER);
            sb.append(slice.getLocalVariableCriterion().getName()).append(DELIMITER);
            sb.append("B").append(slice.getBoundaryBlock().getId()).append(DELIMITER);
            sb.append(slice.getNumberOfDuplicatedStatements()).append("/").append(slice.getNumberOfSliceStatements());
            sb.append('\n');
        }
        return sb.toString();
    }

    @NotNull
    @Override
    public String getExportDefaultFilename() {
        return "Long-Method";
    }

    @Override
    public String toString() {
        return qualifiedMethodName;
    }
}
