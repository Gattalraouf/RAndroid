package UserInterface.Components;

import Corrector.Refactoring.IODRefactoringUtility;
import UserInterface.Actions.RefactorIOD;



public class IODSettingsScreen extends SettingsScreen {

    public IODSettingsScreen(RefactorIOD action){
        correctionUtility= new IODRefactoringUtility();
        this.action=action;
    }

}