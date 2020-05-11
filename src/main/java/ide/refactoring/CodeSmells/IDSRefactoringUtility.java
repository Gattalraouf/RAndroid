package ide.refactoring.CodeSmells;

import Utils.CSVReadingManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import core.ast.AnonymousClassDeclarationObject;
import core.ast.ClassObject;
import core.ast.LocalVariableDeclarationObject;
import core.ast.MethodObject;

import java.util.ArrayList;
import java.util.List;

public class IDSRefactoringUtility {

    public void onRefactorIDS(String filePath, Project myProject) {
        ArrayList<ClassObject> idsClasses;
        idsClasses = CSVReadingManager.getaDoctorTargetClass(filePath, "IDS", myProject);

        for (ClassObject idsClass : idsClasses) {
            List<LocalVariableDeclarationObject> variableList;
            ArrayList<PsiVariable> HachMapVariables = new ArrayList<>();
            ArrayList<PsiTypeElement> HachMapReturns = new ArrayList<>();
            ArrayList<PsiStatement> ForStatements = new ArrayList<>();
            List<AnonymousClassDeclarationObject> anonumousClasses;
            List<MethodObject> anonymousMethods;

            //get the class global fields
            for (PsiField field : idsClass.getPsiClass().getFields()) {
                String fieldtype = field.getTypeElement().getType().getPresentableText();
                getHashMap(HachMapVariables, fieldtype, field);
            }

            //get the local variables, for statements, return statements, parameters and anonymous classes of each method
            for (MethodObject method : idsClass.getMethodList()) {

                //get the for statements to remove entryset()
                for (PsiStatement s : method.getMethodDeclaration().getBody().getStatements()) {
                    if (s.getText().startsWith("for")) {
                        if ((s.getText().contains("Map.Entry<Integer") || s.getText().contains("Map.Entry<Long")) && s.getText().contains(".entrySet()")) {
                            ForStatements.add(s);
                        }
                    }
                }

                //verify the variables and their initializer
                variableList = method.getLocalVariableDeclarations();
                String variableType = "";
                for (LocalVariableDeclarationObject localVariableDeclarationObject : variableList) {
                    PsiVariable var = localVariableDeclarationObject.getVariableDeclaration();
                    variableType = var.getTypeElement().getType().getPresentableText();
                    getHashMap(HachMapVariables, variableType, var);
                }

                //verify the return type of the method
                String returnType = method.getMethodDeclaration().getReturnTypeElement().getType().getPresentableText();
                if (!returnType.contains("LinkedHashMap")
                        && !returnType.contains("ConcurrentHashMap")
                        && (returnType.contains("HashMap<Long")
                        || returnType.contains("HashMap<Integer"))) {
                    HachMapReturns.add(method.getMethodDeclaration().getReturnTypeElement());
                }

                //verify the parameters of the method
                for (PsiParameter param : method.getMethodDeclaration().getParameterList().getParameters()) {
                    String paramType = param.getTypeElement().getType().getPresentableText();
                    if (!paramType.contains("LinkedHashMap")
                            && !paramType.contains("ConcurrentHashMap")
                            && (paramType.contains("HashMap<Long")
                            || paramType.contains("HashMap<Integer"))) {
                        HachMapVariables.add(param);
                    }
                }

                //verify the fileds and the variables of the anonymous classes if their type is HashMap
                anonumousClasses = method.getAnonymousClassDeclarations();
                for (AnonymousClassDeclarationObject anonumousClass : anonumousClasses) {
                    System.out.println("the class is" + anonumousClass.getAnonymousClassDeclaration().getName());
                    //Verify the fields of the anonymous class
                    for (PsiField field : anonumousClass.getAnonymousClassDeclaration().getFields()) {
                        String Ftype = field.getTypeElement().getType().getPresentableText();
                        if (!Ftype.contains("LinkedHashMap")
                                && !Ftype.contains("ConcurrentHashMap")
                                && (Ftype.contains("HashMap<Long")
                                || Ftype.contains("HashMap<Integer"))) {
                            HachMapVariables.add(field);
                        }
                    }

                    //verify the variables of each method of the anonymous class
                    anonymousMethods = anonumousClass.getMethodList();

                    for (MethodObject anonymousMethod : anonymousMethods) {
                        variableList = anonymousMethod.getLocalVariableDeclarations();
                        for (LocalVariableDeclarationObject localVariableDeclarationObject : variableList) {
                            variableType = localVariableDeclarationObject.getVariableDeclaration().getTypeElement().getType().getPresentableText();
                            if (!variableType.contains("LinkedHashMap") && !variableType.contains("ConcurrentHashMap")) {
                                if (variableType.contains("HashMap<Long") || variableType.contains("HashMap<Integer")) {
                                    HachMapVariables.add(localVariableDeclarationObject.getVariableDeclaration());
                                }
                            }
                        }
                        //get the for statements to remove entryset()
                        for (PsiStatement s : anonymousMethod.getMethodDeclaration().getBody().getStatements()) {
                            if (s.getText().startsWith("for")) {
                                if ((s.getText().contains("Map.Entry<Integer") || s.getText().contains("Map.Entry<Long")) && s.getText().contains(".entrySet()")) {
                                    ForStatements.add(s);
                                }
                            }
                        }
                    }
                }
            }

            HashMapToSparseArray(HachMapVariables, HachMapReturns, ForStatements, idsClass, myProject);
        }
    }

    private void getHashMap(ArrayList<PsiVariable> hachMapVariables, String variableType, PsiVariable var) {
        if (!variableType.contains("LinkedHashMap")
                && !variableType.contains("ConcurrentHashMap")
                && (variableType.contains("HashMap<Long")
                || variableType.contains("HashMap<Integer"))
                //if the variable is an instance of hashmap
                || (var.getInitializer() != null && !var.getInitializer().getText().contains("new LinkedHashMap")
                && !var.getInitializer().getText().contains("new ConcurrentHashMap")
                && (var.getInitializer().getText().contains("new HashMap<Long")
                || var.getInitializer().getText().contains("new HashMap<Integer")
                || var.getInitializer().getText().contains("new HashMap")))
                //if the variable is cast to hashmap
                || (var.getInitializer() != null && !var.getInitializer().getText().contains("LinkedHashMap")
                && !var.getInitializer().getText().contains("ConcurrentHashMap")
                && (var.getInitializer().getText().contains("(HashMap<Long")
                || var.getInitializer().getText().contains("(HashMap<Integer")
                || var.getInitializer().getText().contains("(HashMap")))) {
            hachMapVariables.add(var);
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
