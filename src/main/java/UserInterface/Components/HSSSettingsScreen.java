package UserInterface.Components;

import Corrector.Refactoring.HSSRefactoringUtility;
import UserInterface.Actions.RefactorHSS;


public class HSSSettingsScreen extends SettingsScreen {

    public HSSSettingsScreen(RefactorHSS action){
        this.action=action;
        correctionUtility= new HSSRefactoringUtility();
    }

}