package UserInterface.Components;

import Corrector.Refactoring.IDSRefactoringUtility;
import UserInterface.Actions.RefactorIDS;

public class IDSSettingsScreen extends SettingsScreen {

    public IDSSettingsScreen(){
        correctionUtility= new IDSRefactoringUtility();
        action=new RefactorIDS();
    }

}