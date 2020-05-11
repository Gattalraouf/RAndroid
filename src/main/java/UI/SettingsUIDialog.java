package UI;

import Actions.*;
import com.intellij.openapi.project.Project;
import ide.refactoring.CodeSmells.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        buttonLoadFile.addActionListener(e -> {
            SelectedPath.setText(onFileChoose());
            if (!SelectedPath.getText().equals("")) {
                buttonOK.setEnabled(true);
            } else buttonOK.setEnabled(false);
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
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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
        filePath = SelectedPath.getText();
        Project myProject;
        if (Title.getText().contains("IOD")) {
            myProject = RefactorIOD.myProject;
            IODRefactoringUtility iod = new IODRefactoringUtility();
            iod.onRefactorIOD(filePath, Title.getText(), myProject);
        } else if (Title.getText().contains("UIO")) {
            myProject = RefactorUIO.myProject;
            UIORefactoringUtility uio = new UIORefactoringUtility();
            uio.onRefactorUIO(filePath, Title.getText(), myProject);
        } else if (Title.getText().contains("RAM")) {
            myProject = RefactorRAM.myProject;
            RAMRefactoringUtility ram = new RAMRefactoringUtility();
            ram.onRefactorRAM(filePath, myProject);
        } else if (Title.getText().contains("HSS")) {
            myProject = RefactorHSS.myProject;
            HSSRefactoringUtility hss = new HSSRefactoringUtility();
            hss.onRefactorHSS(filePath, Title.getText(), myProject);
        } else if (Title.getText().contains("IDS")) {
            myProject = RefactorIDS.myProject;
            IDSRefactoringUtility ids = new IDSRefactoringUtility();
            ids.onRefactorIDS(filePath, myProject);
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        filePath = "";
        dispose();
    }

}
