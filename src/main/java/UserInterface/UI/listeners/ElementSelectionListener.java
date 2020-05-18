package UserInterface.UI.listeners;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

@FunctionalInterface
public interface ElementSelectionListener extends TreeSelectionListener {
    void onSelect();

    default void valueChanged(TreeSelectionEvent var1) {
        onSelect();
    }
}
