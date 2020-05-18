package Detctor.Analyzer;

import AdaptedJDeodorant.core.ast.AnonymousClassDeclarationObject;
import AdaptedJDeodorant.core.ast.ClassObject;
import AdaptedJDeodorant.core.ast.LocalVariableDeclarationObject;
import AdaptedJDeodorant.core.ast.MethodObject;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

public class IDSAnalyzer extends aDoctorAnalyzer {

    private ArrayList<ArrayList<PsiVariable>> HachMapVariables = new ArrayList<>();
    private ArrayList<ArrayList<PsiTypeElement>> HachMapReturns = new ArrayList<>();
    private ArrayList<ArrayList<PsiStatement>> ForStatements = new ArrayList<>();
    private ArrayList<ClassObject> idsClasses = new ArrayList<>();

    public ArrayList<ArrayList<PsiVariable>> getHachMapVariables() {
        return HachMapVariables;
    }

    public void setHachMapVariables(ArrayList<ArrayList<PsiVariable>> hachMapVariables) {
        HachMapVariables = hachMapVariables;
    }

    public ArrayList<ArrayList<PsiTypeElement>> getHachMapReturns() {
        return HachMapReturns;
    }

    public void setHachMapReturns(ArrayList<ArrayList<PsiTypeElement>> hachMapReturns) {
        HachMapReturns = hachMapReturns;
    }

    public ArrayList<ArrayList<PsiStatement>> getForStatements() {
        return ForStatements;
    }

    public void setForStatements(ArrayList<ArrayList<PsiStatement>> forStatements) {
        ForStatements = forStatements;
    }

    public ArrayList<ClassObject> getIdsClasses() {
        return idsClasses;
    }

    public void setIdsClasses(ArrayList<ClassObject> idsClasses) {
        this.idsClasses = idsClasses;
    }

    public IDSAnalyzer(String filePath, Project myProject) {
        super(filePath);
        codeSmell="IDS";
        getIDSCandidates(filePath, myProject);
    }

    private void getIDSCandidates(String filePath, Project myProject) {
        ArrayList<ClassObject> idsClasses;
        idsClasses = ((aDoctorAnalyzer)getTargetClass(null,filePath," ",myProject)).getClasses();

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

            this.ForStatements.add(ForStatements);
            this.HachMapReturns.add(HachMapReturns);
            this.HachMapVariables.add(HachMapVariables);
            this.idsClasses.add(idsClass);
            //HashMapToSparseArray(HachMapVariables, HachMapReturns, ForStatements, idsClass, myProject);
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

}
