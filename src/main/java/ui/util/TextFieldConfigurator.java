package ui.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * Helper class that provides some methods to configure {@link TextField}s.
 *
 */
public class TextFieldConfigurator {

	/**
	 * Turns a given TextField into a numeric one, so that it will only accept
	 * numbers as input.
	 * 
	 * @param textField {@link TextField} to make numeric.
	 * @param maxValue  The maximum value that can be entered.
	 */
	public static void configureNumericTextField(TextField textField, int maxValue) {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue == "")
					return;
				if (!newValue.matches("\\d*")) {
					textField.setText(newValue.replaceAll("[^\\d]", ""));
				}
				if (oldValue.equals("0") && newValue.startsWith("0")) {
					textField.setText(newValue.replace("0", ""));
				}
				try {
					int input = Integer.parseInt(newValue);
					if (input >= maxValue) {
						textField.setText(maxValue + "");
					} else if (input <= 0) {
						textField.setText(1 + "");
					}
				} catch (NumberFormatException e) {
					// intended behavior
				}
			}
		});
	}

	/**
	 * Prevents the user from entering whitespaces, by replacing them by
	 * underscores.
	 * 
	 * @param textField {@link TextField} to configure.
	 */
	public static void configureUrlTextField(TextField textField) {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.contains(" ")) {
					textField.setText(newValue.replace(' ', '_'));
				}
			}
		});
	}

}
