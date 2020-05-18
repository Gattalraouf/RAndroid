package Detctor.CSVReadingManager;

import AdaptedJDeodorant.core.ast.ASTReader;
import AdaptedJDeodorant.core.ast.ClassObject;
import AdaptedJDeodorant.core.distance.ProjectInfo;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;

public class CSVaDoctorReadingManager extends CSVReadingManager {
    ArrayList<ClassObject> Classes;

    public ArrayList<ClassObject> getClasses(){
        return Classes;
    }

    @Override
    public void getTargetClass(String[] tar, String filePath, String title, Project myProject, String codesmell, int index) {
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
                for (String codesmellName : target) {
                    if (codesmellName.equals(codesmell)) {
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

        Classes= classes;
    }
}
