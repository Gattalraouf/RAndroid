package AdaptedJDeodorant.core.distance;

import AdaptedJDeodorant.Utils.PsiUtils;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Collects information about project: files, classes, and methods.
 */
public class ProjectInfo {
    private final List<PsiJavaFile> psiFiles;
    private final List<PsiClass> psiClasses;
    private final List<PsiMethod> psiMethods;
    private final Project project;

    public ProjectInfo(Project project) {
        this.project = project;
        this.psiFiles = PsiUtils.extractFiles(project);
        this.psiClasses = psiFiles.stream()
                .flatMap(psiFile -> PsiUtils.extractClasses(psiFile).stream())
                .collect(Collectors.toList());

        this.psiMethods = psiClasses.stream()
                .flatMap(psiClass -> PsiUtils.extractMethods(psiClass).stream())
                .collect(Collectors.toList());
    }

    public boolean containsSuperMethodInvocation(final @NotNull PsiMethod psiMethod) {
        final Ref<Boolean> resultRef = new Ref<>(true);

        new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(
                    final @NotNull PsiMethodCallExpression expression
            ) {
                super.visitMethodCallExpression(expression);

                if (expression.getMethodExpression().getQualifierExpression() instanceof PsiSuperExpression) {
                    resultRef.set(false);
                }
            }
        }.visitElement(psiMethod);

        return resultRef.get();
    }

    public boolean overridesMethod(final @NotNull PsiMethod psiMethod) {
        return AnnotationUtil.findAnnotation(psiMethod, "Override") == null &&
                psiMethod.findSuperMethods().length == 0;
    }

    public List<PsiClass> getClasses() {
        return psiClasses;
    }

    public List<PsiMethod> getMethods() {
        return psiMethods;
    }

    public List<PsiJavaFile> getPsiFiles() {
        return psiFiles;
    }

    public Project getProject() {
        return project;
    }
}
