package Corrector;

import Corrector.Refactoring.HSSRefactoringUtility;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ICorrectorTest {

    ICorrector corrector;

    @Test
    void getCodeSmellName() {
        corrector=new HSSRefactoringUtility();
        assertEquals("HSS",corrector.getCodeSmellName());
    }
}