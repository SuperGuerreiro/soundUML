package org.modelio.soundUML.impl;

import org.eclipse.jface.dialogs.MessageDialog;
import org.modelio.metamodel.uml.statik.Attribute;
import org.modelio.vcore.smkernel.mapi.MObject;


public class UmlClassDiagramReader {

	private MObject mObject;

	public UmlClassDiagramReader(MObject mObject) {
		this.mObject = mObject;
	}

	public void readObject(MObject mObj) {

		if (mObj instanceof Attribute) {
			String test = ((Attribute) mObj).getType().toString();
			TextToSpeech.speak(test);
			MessageDialog.openInformation(null, "Info", test);

			//TODO meter este dialog a fazer algo em cada uma das opções
			MessageDialog dialog = new MessageDialog(null, "My Title", null, "My message", MessageDialog.ERROR,
					new String[] { "Play Sound", "Read Message", "Continue" }, 0);
			int result = dialog.open();
			System.out.println(result);

		}

	}

}