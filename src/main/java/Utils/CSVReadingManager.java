package Utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import core.ast.ASTReader;
import core.ast.ClassObject;
import core.distance.ProjectInfo;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVReadingManager {
    public static ArrayList<String[]> ReadFile(String path) {
        ArrayList<String[]> file = new ArrayList<>();
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(path));
            String[] line;
            while ((line = reader.readNext()) != null) {
                file.add(line);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static ArrayList<ClassObject> getaDoctorTargetClass(String filePath, String codesmell, Project myProject) {
        ArrayList<String[]> file;
        ASTReader astReader;
        ArrayList<ClassObject> classes = new ArrayList<>();
        PsiClass Class;
        ClassObject c;

        file = CSVReadingManager.ReadFile(filePath);
        int i = -1;
        for (String[] target : file) {
            if (i == -1) {
                i = 0;
                for (String ram : target) {
                    if (ram.equals(codesmell)) {
                        break;
                    }
                    i++;
                }
            } else {
                if (target[i].equals("1")) {
                    Class = JavaPsiFacade.getInstance(myProject).findClass(target[0], GlobalSearchScope.allScope(myProject));
                    astReader = new ASTReader(new ProjectInfo(myProject), Class);
                    c = astReader.getSystemObject().getClassObject(Class.getQualifiedName());
                    classes.add(c);
                }
            }
        }

        return classes;
    }

    public static PsiClass getPaprikaTargetClass(String[] target, String title, Project myProject, String codeSmell) {
        PsiClass innerClass;
        String[] targetDetails;
        if (title.contains("HSS") && codeSmell.equals("HSS")) {
            targetDetails = target[3].split("#", 0);
        } else if (title.contains("IOD") && codeSmell.equals("IOD")) {
            targetDetails = target[1].split("#", 0);
        } else if (title.contains("UIO") && codeSmell.equals("UIO")) {
            targetDetails = target[1].split("#", 0);
        } else {
            targetDetails = new String[]{""};
        }
        String[] InnerClass = targetDetails[1].split("\\$", 0);
        String[] targetClass = InnerClass[0].split("\\.", 0);
        PsiFile[] targetClassFile = FilenameIndex.getFilesByName(myProject, targetClass[targetClass.length - 1] + ".java", GlobalSearchScope.allScope(myProject));
        PsiJavaFile psiJavaFile = (PsiJavaFile) targetClassFile[0];
        PsiClass[] classes = psiJavaFile.getClasses();

        if (InnerClass.length == 2) {
            innerClass = classes[0].findInnerClassByName(InnerClass[1], false);
            return innerClass;
        }


        return classes[0];
    }

    public static String getTargetMethodName(String[] target, String title, String codeSmell) {
        String[] targetDetails = new String[]{""};
        if (title.contains("HSS") && codeSmell.equals("HSS")) {
            targetDetails = target[3].split("#", 0);
        } else if (title.contains("IOD") && codeSmell.equals("IOD")) {
            targetDetails = target[1].split("#", 0);
        } else if (title.contains("UIO") && codeSmell.equals("UIO")) {
            targetDetails = target[1].split("#", 0);
        }
        return targetDetails[0];
    }

}
