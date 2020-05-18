package UserInterface.Components;

import Corrector.Recommanding.UIORefactoringUtility;
import UserInterface.Actions.RefactorUIO;


public class UIOSettingsScreen extends SettingsScreen {

    public UIOSettingsScreen(){
        correctionUtility= new UIORefactoringUtility();
        action=new RefactorUIO();
    }

}