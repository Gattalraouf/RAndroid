package UserInterface.Components;

import Corrector.Recommanding.UIORefactoringUtility;
import UserInterface.Actions.RefactorUIO;


public class UIOSettingsScreen extends SettingsScreen {

    public UIOSettingsScreen(RefactorUIO action){
        correctionUtility= new UIORefactoringUtility();
        this.action=action;
    }

}