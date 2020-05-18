package UserInterface.Components;

import Corrector.Refactoring.RAMRefactoringUtility;
import UserInterface.Actions.RefactorRAM;

public class RAMSettingsScreen extends SettingsScreen {

    public RAMSettingsScreen(RefactorRAM action){
        correctionUtility= new RAMRefactoringUtility();
        this.action=action;
    }

}
