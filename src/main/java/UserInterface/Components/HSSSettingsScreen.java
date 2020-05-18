package UserInterface.Components;

import Corrector.Refactoring.HSSRefactoringUtility;
import UserInterface.Actions.RefactorHSS;


public class HSSSettingsScreen extends SettingsScreen {

    public HSSSettingsScreen(){
        correctionUtility= new HSSRefactoringUtility();
        action=new RefactorHSS();
    }

}