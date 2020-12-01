package ui.util;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class TooltipHelper {

	/**
	 * Adds the given text as a tootltip to the given control, so that the user can
	 * read it if the text does not fit in the entry.
	 * 
	 * @param control The {@link Control} to add a {@link Tooltip} to.
	 * @param text    The text to add as a Tooltip.
	 */
	public static void addTooltipToLabel(Control control, String text) {
		Tooltip t = new Tooltip(text);
		t.setShowDelay(Duration.millis(250));
		control.setTooltip(t);
	}

	/**
	 * Adds the text of the given label as a tootltip to the label, so that the user
	 * can read it if the text does not fit in the entry.
	 * 
	 * @param label The label to add a {@link Tooltip} to.
	 */
	public static void addTooltipToLabel(Label label) {
		addTooltipToLabel(label, label.getText());
	}

}
