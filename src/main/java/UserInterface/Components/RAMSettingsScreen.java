package UserInterface.Components;

import Corrector.Refactoring.RAMRefactoringUtility;
import UserInterface.Actions.RefactorRAM;

public class RAMSettingsScreen extends SettingsScreen {

    public RAMSettingsScreen(){
        correctionUtility= new RAMRefactoringUtility();
        action=new RefactorRAM();
    }

}
