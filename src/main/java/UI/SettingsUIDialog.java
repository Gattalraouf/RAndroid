package UI;

import Actions.RefactorHSS;
import Actions.RefactorIOD;
import Actions.RefactorRAM;
import Actions.RefactorUIO;
import Utils.CSVReadingManager;
import Utils.JDeodorantFacade;
import com.google.common.collect.Iterables;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.HelpID;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.PrepareFailedException;
import com.intellij.util.SmartList;
import core.ast.ASTReader;
import core.ast.ClassObject;
import core.ast.MethodInvocationObject;
import core.ast.MethodObject;
import core.ast.decomposition.MethodBodyObject;
import core.ast.decomposition.cfg.*;
import core.distance.ProjectInfo;
import ide.fus.collectors.IntelliJDeodorantCounterCollector;
import ide.refactoring.extractMethod.ExtractMethodCandidateGroup;
import ide.refactoring.extractMethod.MyExtractMethodProcessor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.*;
import java.util.*;

import static Utils.PsiUtils.isChild;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class SettingsUIDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField SelectedPath;
    private JButton buttonLoadFile;
    private JLabel Title;
    public String filePath = "";
    private Project myProject;

    public SettingsUIDialog(String title) {
        Title.setText(title);
        createComponent();
    }

    public void createComponent() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.setEnabled(false);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        buttonLoadFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SelectedPath.setText(onFileChoose());
                if (!SelectedPath.getText().equals("")) {
                    buttonOK.setEnabled(true);
                } else buttonOK.setEnabled(false);
            }
        });

        SelectedPath.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (!SelectedPath.getText().equals("")) buttonOK.setEnabled(true);
                else buttonOK.setEnabled(false);
            }
        });
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private String onFileChoose() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Select a CSV file");
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files only", "csv");
        jfc.addChoosableFileFilter(filter);

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return jfc.getSelectedFile().getPath();
        } else return "";
    }

    private void onOK() {
        // add your code here
        filePath = SelectedPath.getText();
        if (Title.getText().contains("IOD")) {
            this.myProject = RefactorIOD.myProject;
            onRefactorIOD(filePath);
        } else if (Title.getText().contains("UIO")) {
            this.myProject = RefactorUIO.myProject;
            onRefactorUIO(filePath);
        } else if (Title.getText().contains("RAM")) {
            this.myProject = RefactorRAM.myProject;
            onRefactorRAM(filePath);
        }else if (Title.getText().contains("HSS")) {
            this.myProject = RefactorHSS.myProject;
            onRefactorHSS(filePath);
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        filePath = "";
        dispose();
    }

    private void onRefactorHSS(String filePath) {
        ArrayList<String[]> file;
        file = CSVReadingManager.ReadFile(filePath);
        PsiClass innerClass;
        Set<ASTSliceGroup> candidates;
        PsiMethod[] methods;
        ClassObject c;
        MethodObject method;
        ASTReader astReader;


        for (String[] target : Iterables.skip(file, 1)) {
            //System.out.println("the first element is "+target[0]);
            innerClass = getPaprikaTargetClass(target);
            methods = innerClass.findMethodsByName(getTargetMethodName(target), false);
            astReader = new ASTReader(new ProjectInfo(myProject), innerClass);
            c = astReader.getSystemObject().getClassObject(innerClass.getQualifiedName());
            method = c.getMethodByName(methods[0].getName());
            RunServiceOnBackground(method,c);
        }
    }

    private void RunServiceOnBackground(MethodObject onStartCommand, ClassObject c) {
        PsiElementFactory elementFactory=PsiElementFactory.getInstance(myProject);
        PsiJavaFile psiFile = c.getPsiFile();

        WriteCommandAction.runWriteCommandAction(myProject, new Runnable() {
            @Override
            public void run() {

                PsiJavaToken LBrace= onStartCommand.getMethodDeclaration().getBody().getLBrace();
                PsiJavaToken RBrace=onStartCommand.getMethodDeclaration().getBody().getRBrace();

                //get The return Statement
                PsiReturnStatement returnStat=null;
                PsiStatement[] statements=onStartCommand.getMethodDeclaration().getBody().getStatements();
                for (int i = 0; i < statements.length; i++) {
                    if(statements[i] instanceof PsiReturnStatement){
                        returnStat= (PsiReturnStatement) statements[i];
                    }
                }

                //HandlerThread thread = new HandlerThread("RunCode");
                PsiTypeElement type=elementFactory.createTypeElementFromText("HandlerThread",psiFile);
                PsiElement elem= psiFile.addBefore(type, onStartCommand.getMethodDeclaration().getBody().getFirstBodyElement());
                PsiStatement stat=elementFactory.createStatementFromText("thread = new HandlerThread(\"RunCode\");",psiFile);
                elem= psiFile.addAfter(stat, elem);

                //thread.start();
                stat=elementFactory.createStatementFromText("thread.start();",psiFile);
                elem= psiFile.addAfter(stat,elem);

                //Runnable tr = new Runnable(){
                type=elementFactory.createTypeElementFromText("Runnable",psiFile);
                elem= psiFile.addAfter(type,elem );
                stat=elementFactory.createStatementFromText("r = new Runnable(){",psiFile);
                elem= psiFile.addAfter(stat, elem);

                //@Ovveride public void run() {
                PsiAnnotation annotation=elementFactory.createAnnotationFromText("@Override",psiFile);
                stat=elementFactory.createStatementFromText("public",psiFile);
                annotation.addAfter(stat,psiFile);
                type=elementFactory.createTypeElementFromText("void",psiFile);
                annotation.addAfter(type,psiFile);
                stat=elementFactory.createStatementFromText("run()",psiFile);
                annotation.addAfter(stat,psiFile);
                annotation.addAfter(LBrace,psiFile);
                psiFile.addAfter(annotation,elem);

                if(returnStat==null) {
                    returnStat= (PsiReturnStatement) onStartCommand.getMethodDeclaration().getBody().getLastBodyElement();
                }

                //close the braces of the Runnable and run
                elem =psiFile.addBefore(RBrace,returnStat);
                elem=psiFile.addAfter(RBrace,elem);
                stat=elementFactory.createStatementFromText(";",psiFile);
                elem=psiFile.addAfter(stat,elem);

                //Handler backgroundHandler = new Handler(thread.getLooper());
                type=elementFactory.createTypeElementFromText("Handler",psiFile);
                elem= psiFile.addAfter(type,elem );
                stat=elementFactory.createStatementFromText("backgroundHandler = new Handler(thread.getLooper());",psiFile);
                elem= psiFile.addAfter(stat, elem);

                //backgroundHandler.post(r);
                stat=elementFactory.createStatementFromText("backgroundHandler.post(r);",psiFile);
                psiFile.addAfter(stat,elem);
            }
        });

    }

    private void onRefactorRAM(String filePath) {

        ArrayList<ClassObject> ramClasses;
        ramClasses = getaDoctorTargetClass(filePath);
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(myProject);

        for (ClassObject ramClass : ramClasses) {
            PsiJavaFile file = ramClass.getPsiFile();
            for (MethodObject method : ramClass.getMethodList()) {
                for (Map.Entry<AbstractVariable, LinkedHashSet<MethodInvocationObject>> usedVariable : method.getMethodBody().getInvokedMethodsThroughLocalVariables().entrySet()) {
                    if (usedVariable.getKey().getType().equals("android.app.AlarmManager")) {
                        for (MethodInvocationObject invok : usedVariable.getValue()) {
                            if (invok.getMethodName().equals("setRepeating")) {
                                WriteCommandAction.runWriteCommandAction(myProject, new Runnable() {
                                    @Override
                                    public void run() {
                                        PsiExpression[] list = invok.getMethodInvocation().getArgumentList().getExpressions();
                                        PsiStatement callExpression = factory.createStatementFromText(
                                                usedVariable.getKey().getName() + ".setInexactRepeating ( " +
                                                        list[0].getText() + " , " + list[1].getText() + " , " + list[2].getText()
                                                        + " , " + list[3].getText() + " );", invok.getMethodInvocation());
                                        invok.getMethodInvocation().replace(callExpression);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

    }

    private ArrayList<ClassObject> getaDoctorTargetClass(String filePath) {
        ArrayList<String[]> file;
        ASTReader astReader;
        ArrayList<ClassObject> classes = new ArrayList<>();
        PsiClass ramClass;
        ClassObject c;

        file = CSVReadingManager.ReadFile(filePath);
        int i = -1;
        for (String[] target : file) {
            if (i == -1) {
                i = 0;
                for (String ram : target) {
                    if (ram.equals("RAM")) {
                        break;
                    }
                    i++;
                }
            } else {
                if (target[i].equals("1")) {
                    ramClass = JavaPsiFacade.getInstance(myProject).findClass(target[0], GlobalSearchScope.allScope(myProject));
                    astReader = new ASTReader(new ProjectInfo(myProject), ramClass);
                    c = astReader.getSystemObject().getClassObject(ramClass.getQualifiedName());
                    classes.add(c);
                }
            }
        }

        return classes;
    }

    private void onRefactorUIO(String filePath) {
        ArrayList<String[]> file;
        file = CSVReadingManager.ReadFile(filePath);
        PsiClass innerClass;
        Set<ASTSliceGroup> candidates;
        PsiMethod[] methods;
        ClassObject c;
        MethodObject method;
        ASTReader astReader;

        for (String[] target : Iterables.skip(file, 1)) {
            innerClass = getPaprikaTargetClass(target);
            methods = innerClass.findMethodsByName(getTargetMethodName(target), false);
            astReader = new ASTReader(new ProjectInfo(myProject), innerClass);
            c = astReader.getSystemObject().getClassObject(innerClass.getQualifiedName());
            method = c.getMethodByName(methods[0].getName());
            clipRect(method, c);
        }

    }

    private void clipRect(MethodObject onDraw, ClassObject c) {
        PsiJavaFile file = c.getPsiFile();
        int NRectDraw = 0, NClipRect = 0, Ntranslation = 0, NRotation = 0, NDrawPath = 0, NclipPath = 0;

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(myProject);
        MethodBodyObject body = onDraw.getMethodBody();
        Set<MethodInvocationObject> canvasInvokes = body.getInvokedMethodsThroughParameters().get(new PlainVariable(onDraw.getParameter(0).getVariableDeclaration()));
        for (MethodInvocationObject invocationObject : canvasInvokes) {
            if (invocationObject.getMethodName().equals("drawPath")) {
                NDrawPath++;
                if (NDrawPath != NclipPath)
                    WriteCommandAction.runWriteCommandAction(myProject, new Runnable() {
                        @Override
                        public void run() {
                            PsiStatement statement = factory.createStatementFromText(
                                    "canvas.clipPath(" + invocationObject.getMethodInvocation().getArgumentList()
                                            .getExpressions()[0].getText() + ");", file);
                            file.addBefore(statement, invocationObject.getMethodInvocation().getParent());
                        }
                    });
            } else if (!invocationObject.getMethodName().equals("drawColor") &&
                    !invocationObject.getMethodName().equals("drawARGB") &&
                    !invocationObject.getMethodName().equals("drawRGB") &&
                    !invocationObject.getMethodName().contains("Text") &&
                    !invocationObject.getMethodName().contains("Paint") &&
                    invocationObject.getMethodName().contains("draw")) {

                NRectDraw++;
            } else if (invocationObject.getMethodName().contains("clipRect")) {
                NClipRect++;
            } else if (invocationObject.getMethodName().contains("clipPath")) {
                NclipPath++;
            } else if (invocationObject.getMethodName().equals("translate")) {
                Ntranslation++;
            } else if (invocationObject.getMethodName().equals("rotate")) {
                NRotation++;
            }
        }

        String clip = "//TODO change the parameters of the used " + NClipRect + "canvas.clipRect(...) to cover the " +
                "visible drawings only on the screen.";

        String rect = "//TODO use canvas.clipRect() or canvas.clipOutRect() taking in consideration the "
                + NRectDraw + " RectF shapes you are drawing in order that you don't draw shapes over each other " +
                "without clipping the hidden parts.";

        String addit = "//You can visualize overdraw using color tinting on your device with the Debug GPU Overdraw " +
                "tool https://developer.android.com/tools/performance/debug-gpu-overdraw/index.html.";

        String translation = "//Pay Attention to " + Ntranslation + "x canvas.translate(...) and " + NRotation
                + "x canvas.rotate(...) that are used in your code.";


        if (NRotation != 0 || Ntranslation != 0) {
            WriteCommandAction.runWriteCommandAction(myProject, new Runnable() {
                @Override
                public void run() {
                    PsiComment comment = factory.createCommentFromText(translation, file);
                    file.addBefore(comment, onDraw.getMethodDeclaration().getFirstChild());
                }
            });
        }

        if (NRectDraw != 0 && NClipRect == 0) {
            WriteCommandAction.runWriteCommandAction(myProject, new Runnable() {
                @Override
                public void run() {
                    PsiComment comment = factory.createCommentFromText(addit, file);
                    file.addBefore(comment, onDraw.getMethodDeclaration().getFirstChild());
                    comment = factory.createCommentFromText(rect, file);
                    file.addBefore(comment, onDraw.getMethodDeclaration().getFirstChild());
                }
            });
        } else if (NRectDraw != 0) {
            WriteCommandAction.runWriteCommandAction(myProject, new Runnable() {
                @Override
                public void run() {
                    PsiComment comment = factory.createCommentFromText(addit, file);
                    file.addBefore(comment, onDraw.getMethodDeclaration().getFirstChild());
                    comment = factory.createCommentFromText(clip, file);
                    file.addBefore(comment, onDraw.getMethodDeclaration().getFirstChild());
                }
            });
        }

        for (MethodInvocationObject method : onDraw.getMethodBody().getInvokedMethodsThroughThisReference()) {
            if (method.getParameterList().contains("android.graphics.Canvas")) {
                clipRect(c.getMethodByName(method.getMethodName()), c);
            }
        }

    }

    private void onRefactorIOD(String filePath) {
        ArrayList<String[]> file;
        file = CSVReadingManager.ReadFile(filePath);
        PsiClass innerClass;
        Set<ASTSliceGroup> candidates;
        PsiMethod[] methods;
        ClassObject c;
        ASTReader astReader;

        for (String[] target : Iterables.skip(file, 1)) {
            candidates = new HashSet<>();
            innerClass = getPaprikaTargetClass(target);
            methods = innerClass.findMethodsByName(getTargetMethodName(target), false);
            astReader = new ASTReader(new ProjectInfo(myProject), innerClass);
            c = astReader.getSystemObject().getClassObject(innerClass.getQualifiedName());
            //Handle long Method case
            JDeodorantFacade.processMethod(candidates, c, c.getMethodByName(methods[0].getName()));
            extractMethod(candidates);
            candidates = new HashSet<>();

            //Handle object creation
            JDeodorantFacade.processMethodForInstances(candidates, c, c.getMethodByName(methods[0].getName()));
            extractMethod(candidates);

        }

    }

    private PsiClass getPaprikaTargetClass(String[] target) {
        PsiClass innerClass;
        String[] targetDetails = target[1].split("#", 0);
        if (Title.getText().contains("HSS")) {
            targetDetails = target[3].split("#", 0);
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

    private String getTargetMethodName(String[] target) {
        String[] targetDetails = target[1].split("#", 0);
        if (Title.getText().contains("HSS")) {
            targetDetails = target[3].split("#", 0);
        }
        return targetDetails[0];
    }

    private void extractMethod(Set<ASTSliceGroup> candidates) {
        final List<ExtractMethodCandidateGroup> extractMethodCandidateGroups = candidates.stream().filter(Objects::nonNull)
                .map(sliceGroup ->
                        sliceGroup.getCandidates().stream()
                                .filter(c -> canBeExtracted(c))
                                .collect(toSet()))
                .filter(set -> !set.isEmpty())
                .map(ExtractMethodCandidateGroup::new)
                .collect(toList());
        if (extractMethodCandidateGroups.size() != 0) {
            TransactionGuard.getInstance().submitTransactionAndWait(doExtract(extractMethodCandidateGroups.get(0).getCandidates().stream().findAny().get()));
        }
    }

    /**
     * Checks that the slice can be extracted into a separate method without compilation errors.
     */
    private boolean canBeExtracted(ASTSlice slice) {
        SmartList<PsiStatement> statementsToExtract = getStatementsToExtract(slice);

        MyExtractMethodProcessor processor = new MyExtractMethodProcessor(myProject,
                null, statementsToExtract.toArray(new PsiElement[0]), slice.getLocalVariableCriterion().getType(), "Refactoring", "", HelpID.EXTRACT_METHOD,
                slice.getSourceTypeDeclaration(), slice.getLocalVariableCriterion());

        processor.setOutputVariable();

        try {
            processor.setShowErrorDialogs(false);
            return processor.prepare();

        } catch (PrepareFailedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Collects statements that can be extracted into a separate method.
     */
    public SmartList<PsiStatement> getStatementsToExtract(ASTSlice slice) {
        Set<PDGNode> nodes = slice.getSliceNodes();
        SmartList<PsiStatement> statementsToExtract = new SmartList<>();

        for (PDGNode pdgNode : nodes) {
            boolean isNotChild = true;
            for (PDGNode node : nodes) {
                if (isChild(node.getASTStatement(), pdgNode.getASTStatement())) {
                    isNotChild = false;
                }
            }
            if (isNotChild) {
                statementsToExtract.add(pdgNode.getASTStatement());
            }
        }
        return statementsToExtract;
    }

    /**
     * Extracts statements into new method.
     *
     * @param slice computation slice.
     * @return callback to run when "Refactor" button is selected.
     */
    private Runnable doExtract(ASTSlice slice) {
        return () -> {
            Editor editor = FileEditorManager.getInstance(slice.getSourceMethodDeclaration().getProject()).getSelectedTextEditor();
            SmartList<PsiStatement> statementsToExtract = getStatementsToExtract(slice);

            MyExtractMethodProcessor processor = new MyExtractMethodProcessor(slice.getSourceMethodDeclaration().getProject(),
                    editor, statementsToExtract.toArray(new PsiElement[0]), slice.getLocalVariableCriterion().getType(),
                    "", "", HelpID.EXTRACT_METHOD,
                    slice.getSourceTypeDeclaration(), slice.getLocalVariableCriterion());

            processor.setOutputVariable();

            try {
                processor.setShowErrorDialogs(true);
                if (processor.prepare()) {
                    ExtractMethodHandler.invokeOnElements(slice.getSourceMethodDeclaration().getProject(), processor,
                            slice.getSourceMethodDeclaration().getContainingFile(), true);
                    if (editor != null && processor.getExtractedMethod() != null) {
                        IntelliJDeodorantCounterCollector.getInstance().extractMethodRefactoringApplied(editor.getProject(),
                                slice, processor.getExtractedMethod());
                    }
                }
            } catch (PrepareFailedException e) {
                e.printStackTrace();
            }
        };
    }

}
