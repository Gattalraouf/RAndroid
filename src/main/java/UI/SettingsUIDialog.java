package UI;

import Actions.RefactorIOD;
import Utils.CSVReadingManager;
import Utils.JDeodorantFacade;
import com.google.common.collect.Iterables;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.HelpID;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.PrepareFailedException;
import com.intellij.util.SmartList;
import core.ast.ASTReader;
import core.ast.ClassObject;
import core.ast.decomposition.cfg.ASTSlice;
import core.ast.decomposition.cfg.ASTSliceGroup;
import core.ast.decomposition.cfg.PDGNode;
import core.ast.decomposition.cfg.PDGSliceUnion;
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
        onRefactor(filePath);
        dispose();
    }

    private void onRefactor(String filePath) {
        ArrayList<String[]> file;
        file = CSVReadingManager.ReadFile(filePath);
        PsiClass innerClass;
        Set<ASTSliceGroup> candidates;
        PsiMethod[] methods;
        ClassObject c;
        ASTReader astReader;

        for (String[] target : Iterables.skip(file, 1)) {
            candidates = new HashSet<>();
            innerClass = getTargetClass(target);
            methods = innerClass.findMethodsByName(getTargetMethodName(target), false);
            astReader = new ASTReader(new ProjectInfo(RefactorIOD.myProject), innerClass);
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

    private PsiClass getTargetClass(String[] target) {
        PsiClass innerClass;
        String[] targetDetails = target[1].split("#", 0);
        String[] InnerClass = targetDetails[1].split("\\$", 0);
        String[] targetClass = InnerClass[0].split("\\.", 0);
        PsiFile[] targetClassFile = FilenameIndex.getFilesByName(RefactorIOD.myProject, targetClass[targetClass.length - 1] + ".java", GlobalSearchScope.allScope(RefactorIOD.myProject));
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


    private void onCancel() {
        // add your code here if necessary
        filePath = "";
        dispose();
    }

    /**
     * Checks that the slice can be extracted into a separate method without compilation errors.
     */
    private boolean canBeExtracted(ASTSlice slice) {
        SmartList<PsiStatement> statementsToExtract = getStatementsToExtract(slice);

        MyExtractMethodProcessor processor = new MyExtractMethodProcessor(RefactorIOD.myProject,
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


}
