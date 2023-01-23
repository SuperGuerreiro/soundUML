package org.modelio.soundUML.impl;

import java.io.InputStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/*
 * Class that extends the MessageDialog Class
 * Basically allows us to play sound and the TTS through the dialog box
 * For this, it was necessary to override the method buttonPressed in order for the dialog box to remain on the screen
 */
public class MessageDialogExtended extends MessageDialog {

	private String audioFilePath; // File path to the audio to be played
	private String textToSpeech; // The text to be read by the TTS

	public MessageDialogExtended(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
			int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
				defaultIndex);

	}

	// Need to call this method in order to set the strings for the audioFilePath
	// and the Text to be read by the TTS
	public void setStrings(String filePath, String tts) {
		audioFilePath = filePath;
		textToSpeech = tts;
	}

	/*
	 * This method is needed so that the user can play the sound or hear the TTS as
	 * many times as they want
	 * 
	 * Known bug: you can press the button several times in a row (TODO)
	 * TODO: Fazer um button para acabar logo a leitura
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		super.getButton(buttonId).setEnabled(false);

		switch (buttonId) {
		case 0: //play the sound related to this element
			InputStream inputStream = getClass().getResourceAsStream(audioFilePath);
			SimpleAudioPlayer player = new SimpleAudioPlayer();
			player.play(inputStream);
			break;
		case 1: //play the textToSpeech
			TextToSpeech.speak(textToSpeech);
			break;
		case 2: //this resets the buttons -> workaround for the bug of being able to press the button unlimited times
			super.getButton(0).setEnabled(true);
			super.getButton(1).setEnabled(true);
			super.getButton(2).setEnabled(true);
			super.getButton(3).setEnabled(true);
			break;
		case 3:
			super.buttonPressed(buttonId);
			break;
		}
		
	}
	
}
