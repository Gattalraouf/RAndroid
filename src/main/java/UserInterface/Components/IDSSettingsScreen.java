package UserInterface.Components;

import Corrector.Refactoring.IDSRefactoringUtility;
import UserInterface.Actions.RefactorIDS;

public class IDSSettingsScreen extends SettingsScreen {

    public IDSSettingsScreen(RefactorIDS action){
        correctionUtility= new IDSRefactoringUtility();
        this.action=action;
    }

}