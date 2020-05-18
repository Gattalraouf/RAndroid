package UserInterface.Components;

import Corrector.Refactoring.IODRefactoringUtility;
import UserInterface.Actions.RefactorIOD;



public class IODSettingsScreen extends SettingsScreen {

    public IODSettingsScreen(){
        correctionUtility= new IODRefactoringUtility();
        action=new RefactorIOD();
    }

}