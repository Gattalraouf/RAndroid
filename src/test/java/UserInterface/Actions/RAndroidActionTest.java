package UserInterface.Actions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RAndroidActionTest {

    RAndroidAction action=new RAndroidAction();

    @Test
    void getScreen() {
        assertEquals(action.screen,action.getScreen());
    }

    @Test
    void getMyProject() {
        assertEquals(RAndroidAction.myProject,action.getMyProject());
    }

}