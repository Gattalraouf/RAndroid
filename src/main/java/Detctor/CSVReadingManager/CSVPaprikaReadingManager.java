package Detctor.CSVReadingManager;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import javassist.runtime.Inner;

public class CSVPaprikaReadingManager extends CSVReadingManager{
    PsiClass targetC;

    public PsiClass getTargetC(){
        return targetC;
    }

    @Override
    public void getTargetClass(String[] target, String filePath, String title, Project myProject, String codeSmell,int index) {

        String[] InnerClass=null;

        String[] targetClass=getTargetClassName(target,index,InnerClass);

        PsiFile[] targetClassFile = FilenameIndex.getFilesByName(myProject, targetClass[targetClass.length - 1] + ".java", GlobalSearchScope.allScope(myProject));

        targetC=getTargetClassFromTargetClassFile(targetClassFile,InnerClass);
    }

    public String[] getTargetClassName(String[] target,int index, String[] InnerClass){

        String[] targetDetails = target[index].split("#", 0);
        InnerClass = targetDetails[1].split("\\$", 0);
        String[] targetClass = InnerClass[0].split("\\.", 0);

        return targetClass;
    }

    public PsiClass getTargetClassFromTargetClassFile(PsiFile[] targetClassFile, String[] InnerClass){

        PsiJavaFile psiJavaFile = (PsiJavaFile) targetClassFile[0];
        PsiClass[] classes = psiJavaFile.getClasses();

        if (InnerClass.length == 2) {
            PsiClass innerClass = classes[0].findInnerClassByName(InnerClass[1], false);
            return innerClass;
        }
        else return classes[0];
    }

}
