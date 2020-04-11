package core.ast;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.javadoc.PsiDocMethodOrFieldRef;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import core.ast.util.ExpressionExtractor;
import core.ast.util.MethodDeclarationUtility;
import core.ast.util.StatementExtractor;


import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;
import java.util.stream.Collectors;

public class SystemObject {
    private static final String TYPE_STATE_CHECKING_INDICATOR_KEY = "type.state.checking.identification.indicator";

    private final List<ClassObject> classList;
    //Map that has as key the classname and as value
    //the position of className in the classNameList
    private final Map<String, Integer> classNameMap;
    private final Map<MethodInvocationObject, FieldInstructionObject> getterMap;
    private final Map<MethodInvocationObject, FieldInstructionObject> setterMap;
    private final Map<MethodInvocationObject, FieldInstructionObject> collectionAdderMap;
    private final Map<MethodInvocationObject, MethodInvocationObject> delegateMap;

    public SystemObject() {
        this.classList = new ArrayList<>();
        this.classNameMap = new HashMap<>();
        this.getterMap = new LinkedHashMap<>();
        this.setterMap = new LinkedHashMap<>();
        this.collectionAdderMap = new LinkedHashMap<>();
        this.delegateMap = new LinkedHashMap<>();
    }

    public void addClass(ClassObject c) {
        classNameMap.put(c.getName(), classList.size());
        classList.add(c);
    }

    public void addClasses(List<ClassObject> classObjects) {
        for (ClassObject classObject : classObjects)
            addClass(classObject);
    }

    public void replaceClass(ClassObject c) {
        int position = getPositionInClassList(c.getName());
        if (position != -1) {
            classList.set(position, c);
        } else {
            addClass(c);
        }
    }

    public void removeClass(ClassObject c) {
        int position = getPositionInClassList(c.getName());
        if (position != -1) {
            for (int i = position + 1; i < classList.size(); i++) {
                ClassObject classObject = classList.get(i);
                classNameMap.put(classObject.getName(), classNameMap.get(classObject.getName()) - 1);
            }
            classNameMap.remove(c.getName());
            classList.remove(c);
        }
    }

    public void addGetter(MethodInvocationObject methodInvocation, FieldInstructionObject fieldInstruction) {
        getterMap.put(methodInvocation, fieldInstruction);
    }

    public void addSetter(MethodInvocationObject methodInvocation, FieldInstructionObject fieldInstruction) {
        setterMap.put(methodInvocation, fieldInstruction);
    }

    public void addCollectionAdder(MethodInvocationObject methodInvocation, FieldInstructionObject fieldInstruction) {
        collectionAdderMap.put(methodInvocation, fieldInstruction);
    }

    public void addDelegate(MethodInvocationObject methodInvocation, MethodInvocationObject delegation) {
        delegateMap.put(methodInvocation, delegation);
    }

    public FieldInstructionObject containsGetter(MethodInvocationObject methodInvocation) {
        return getterMap.get(methodInvocation);
    }

    public FieldInstructionObject containsSetter(MethodInvocationObject methodInvocation) {
        return setterMap.get(methodInvocation);
    }

    public FieldInstructionObject containsCollectionAdder(MethodInvocationObject methodInvocation) {
        return collectionAdderMap.get(methodInvocation);
    }

    public MethodInvocationObject containsDelegate(MethodInvocationObject methodInvocation) {
        return delegateMap.get(methodInvocation);
    }

    public MethodObject getMethod(MethodInvocationObject mio) {
        ClassObject classObject = getClassObject(mio.getOriginClassName());
        if (classObject != null)
            return classObject.getMethod(mio);
        return null;
    }

    public MethodObject getMethod(SuperMethodInvocationObject smio) {
        ClassObject classObject = getClassObject(smio.getOriginClassName());
        if (classObject != null)
            return classObject.getMethod(smio);
        return null;
    }

    public boolean containsMethodInvocation(MethodInvocationObject methodInvocation, ClassObject excludedClass) {
        for (ClassObject classObject : classList) {
            if (!excludedClass.equals(classObject) && classObject.containsMethodInvocation(methodInvocation))
                return true;
        }
        return false;
    }

    public boolean containsFieldInstruction(FieldInstructionObject fieldInstruction, ClassObject excludedClass) {
        for (ClassObject classObject : classList) {
            if (!excludedClass.equals(classObject) && classObject.containsFieldInstruction(fieldInstruction))
                return true;
        }
        return false;
    }

    public boolean containsSuperMethodInvocation(SuperMethodInvocationObject superMethodInvocation) {
        for (ClassObject classObject : classList) {
            if (classObject.containsSuperMethodInvocation(superMethodInvocation))
                return true;
        }
        return false;
    }

    public ClassObject getClassObject(String className) {
        Integer pos = classNameMap.get(className);
        if (pos != null)
            return getClassObject(pos);
        else
            return null;
    }

    private ClassObject getClassObject(int pos) {
        return classList.get(pos);
    }

    public ListIterator<ClassObject> getClassListIterator() {
        return classList.listIterator();
    }

    public int getClassNumber() {
        return classList.size();
    }

    private int getPositionInClassList(String className) {
        Integer pos = classNameMap.get(className);
        if (pos != null)
            return pos;
        else
            return -1;
    }

    public Set<ClassObject> getClassObjects() {
        return new LinkedHashSet<>(classList);
    }

    public List<String> getClassNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < classList.size(); i++) {
            names.add(getClassObject(i).getName());
        }
        return names;
    }

    private boolean validType(PsiType type) {
        if (type instanceof PsiPrimitiveType) {
            return true;
        }
        if (!(type instanceof PsiClassType)) {
            return false;
        }
        PsiClass resolvedClass = ((PsiClassType) type).resolve();
        if (resolvedClass == null) {
            return false;
        }
        return resolvedClass.isEnum() || "java.lang.String".equals(resolvedClass.getQualifiedName());
    }

    private boolean nonEmptyIntersection(List<PsiField> staticFieldUnion, List<PsiField> staticFields) {
        for (PsiField simpleName1 : staticFields) {
            for (PsiField simpleName2 : staticFieldUnion) {
                if (simpleName1.equals(simpleName2))
                    return true;
            }
        }
        return false;
    }

    private List<PsiField> constructUnion(List<PsiField> staticFieldUnion, List<PsiField> staticFields) {
        List<PsiField> initialStaticFields = new ArrayList<>(staticFieldUnion);
        List<PsiField> staticFieldsToBeAdded = new ArrayList<>();
        for (PsiField simpleName1 : staticFields) {
            boolean isContained = false;
            for (PsiField simpleName2 : staticFieldUnion) {
                if (simpleName1.equals(simpleName2)) {
                    isContained = true;
                    break;
                }
            }
            if (!isContained)
                staticFieldsToBeAdded.add(simpleName1);
        }
        initialStaticFields.addAll(staticFieldsToBeAdded);
        return initialStaticFields;
    }

    private boolean allStaticFieldsWithinSystemBoundary(List<PsiField> staticFields) {
        for (PsiField staticField : staticFields) {
            PsiClass declaringClassTypeBinding = staticField.getContainingClass();
            if (declaringClassTypeBinding != null) {
                if (getPositionInClassList(declaringClassTypeBinding.getQualifiedName()) == -1 && !declaringClassTypeBinding.isEnum())
                    return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ClassObject classObject : classList) {
            sb.append(classObject.toString());
            sb.append("\n--------------------------------------------------------------------------------\n");
        }
        return sb.toString();
    }
}