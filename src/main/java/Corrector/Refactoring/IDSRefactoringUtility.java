package Corrector.Refactoring;

import AdaptedJDeodorant.core.ast.ClassObject;
import Detctor.Analyzer.IDSAnalyzer;
import Detctor.Analyzer.aDoctorAnalyzer;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.sun.xml.bind.v2.model.core.ID;

import java.util.ArrayList;

public class IDSRefactoringUtility extends IRefactor {

    public IDSRefactoringUtility(){
        codeSmellName="IDS";
    }

    @Override
    public void onRefactor(String filePath, String title, Project myProject) {
        analyzer = new IDSAnalyzer(filePath, myProject);
        int i = 0;

        //TODO Handle the long case (by casting it to int)
        //TODO Handle importing Sparse Array
        //TODO Handle the initilization of the fixed variables
        //TODO Higlight the zone we are fixing
        for (ClassObject idsClass : ((IDSAnalyzer)analyzer).getIdsClasses()) {
            HashMapToSparseArray(((IDSAnalyzer)analyzer).getHachMapVariables().get(i), ((IDSAnalyzer)analyzer).getHachMapReturns().get(i), ((IDSAnalyzer)analyzer).getForStatements().get(i), idsClass, myProject);
            i++;
        }

    }

    private void HashMapToSparseArray(ArrayList<PsiVariable> hachMapVariables, ArrayList<PsiTypeElement> hachMapReturns, ArrayList<PsiStatement> forStatements, ClassObject idsClass, Project myProject) {
        PsiJavaFile file = idsClass.getPsiFile();
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(myProject);

        WriteCommandAction.runWriteCommandAction(myProject, () -> {
            PsiTypeElement newType;
            PsiExpression newInitializer;

            for (PsiTypeElement hachMapReturn : hachMapReturns) {
                //replace Hashmap<Long|Integer,X> by SparseArray<X>
                String type = hachMapReturn.getType().getPresentableText();
                type = type.replaceAll("HashMap<Long,", "SparseArray<");
                type = type.replaceAll("HashMap<Integer,", "SparseArray<");
                newType = factory.createTypeElementFromText(type, file);
                hachMapReturn.replace(newType);
            }

            for (PsiVariable hachMapVariable : hachMapVariables) {

                System.out.println("the name of the variable is" + hachMapVariable.getName());

                //replace Hashmap<Long|Integer,X> by SparseArray<X>
                String type = hachMapVariable.getTypeElement().getType().getPresentableText();
                type = type.replaceAll("HashMap<Long,", "SparseArray<");
                type = type.replaceAll("HashMap<Integer,", "SparseArray<");
                newType = factory.createTypeElementFromText(type, file);
                hachMapVariable.getTypeElement().replace(newType);

                if (hachMapVariable.getInitializer() != null) {
                    // replace the initializer by new SparseArray<>()
                    newInitializer = factory.createExpressionFromText("new SparseArray<>()", file);
                    String initializerText = hachMapVariable.getInitializer().getText();
                    if (!initializerText.contains("new LinkedHashMap")
                            && !initializerText.contains("new ConcurrentHashMap")
                            && (initializerText.contains("new HashMap<Long")
                            || initializerText.contains("new HashMap<Integer")
                            || initializerText.contains("new HashMap"))) {

                        hachMapVariable.getInitializer().replace(newInitializer);
                    }

                    //Verify if the initializer contains a cast to HashMap
                    else if (!initializerText.contains("LinkedHashMap")
                            && !initializerText.contains("ConcurrentHashMap")
                            && (initializerText.contains("(HashMap<Long")
                            || initializerText.contains("(HashMap<Integer")
                            || initializerText.contains("(HashMap"))) {

                        String[] parts = hachMapVariable.getInitializer().getText().split("\\)");
                        String a = parts[0] + ")";
                        String newInitializerText = initializerText.replaceFirst(a, "SparseArray");
                        PsiExpression newInitializer2 = factory.createExpressionFromText(newInitializerText, file);
                        hachMapVariable.getInitializer().replace(newInitializer2);
                    }
                }
            }

            for (PsiStatement forStatement : forStatements) {

                String forStatementText = forStatement.getText();
                String entity;
                String entityType;
                String test = forStatementText.split(":")[0];

                if (test.contains(">>")) {
                    entity = forStatementText.split(">>")[1].split(" ")[1];

                    entityType = forStatementText.replace(forStatementText
                            .split(",")[0] + ",", "")
                            .split(">>")[0] + ">"
                            .replaceAll(" ", "");
                } else {
                    entity = forStatementText.split(">")[1].split(" ")[1];
                    entityType = forStatementText.split(",")[1].split(">")[0].replaceAll(" ", "");
                }


                if (!entityType.contains("LinkedHashMap") &&
                        !entityType.contains("ConcurrentHashMap")
                        && (entityType.contains("HashMap<Integer")
                        || entityType.contains("HashMap<Long"))) {

                    entityType = entityType.replace("HashMap<Integer,", "SparseArray<");
                    entityType = entityType.replace("HashMap<Long,", "SparseArray<");
                }

                String mapName = forStatementText.split(":")[1]
                        .split(".entrySet()")[0]
                        .replaceAll(" ", "");

                String HeadFor = "for(int i = 0; i < " + mapName + ".size(); i++){ \n" +
                        "            int key = " + mapName + ".keyAt(i);\n" +
                        "            " + entityType + " " + entity + "=  " + mapName + ".get(key);";

                String[] splitted = forStatementText.split("\\{");
                forStatementText = forStatementText.replaceFirst("\\{", " ")
                        .replace(splitted[0], HeadFor)
                        .replaceAll(entity + ".getValue\\(\\)", entity)
                        .replaceAll(entity + ".getKey\\(\\)", entity);

                PsiStatement newStatement = factory.createStatementFromText(forStatementText, file);
                forStatement.replace(newStatement);
            }

        });
    }


}
