package org.modelio.soundUML.impl;


import org.eclipse.jface.dialogs.MessageDialog;

import org.modelio.metamodel.uml.statik.Attribute;
import org.modelio.vcore.smkernel.mapi.MObject;


/*
 * Class that reads each element (mObject) of the UML Class Diagrams, differentiating them through their respective attributes
 * Each element has it's own unique sound and attributes
 */
public class UmlClassDiagramReader {

	private MObject mObject;

	public UmlClassDiagramReader(MObject mObject) {
		this.mObject = mObject;
	}

	public void readObject(MObject mObj) {

		if (mObj instanceof Attribute) {
			String test = ((Attribute) mObj).getType().toString();
			// TextToSpeech.speak(test);

			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info", null, test, MessageDialog.INFORMATION,
					new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue" }, 0);
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/duolingo.wav", test); 
			int result = dialog.open();
				
		}

	}

	
/* Useful for debugging in eclipse
	/**
	 * Show an information dialog box.
	 
	public static void showInformation(final String title, final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(null, title, message);
			}
		});
	}

	public static void main(String[] args) {
		showInformation("aaaa", "aaaa");

		MessageDialogExtended dialog = new MessageDialogExtended(null, "Info", null, "My message TEST A", MessageDialog.INFORMATION,
				new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue" }, 0);
		dialog.setStrings("/org/modelio/soundUML/sounds/duolingo.wav", "aaaa"); // Set the file path and text to be read
		int result = dialog.open();
		System.out.println("aaaa");


		System.out.println(result);
	}
*/
}


