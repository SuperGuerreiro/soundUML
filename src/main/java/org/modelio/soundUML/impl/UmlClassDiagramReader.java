package org.modelio.soundUML.impl;


import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.metamodel.uml.statik.*;


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
		//Class
		if (mObj instanceof Class) {
			//Nome da classe
			String className = mObj.getName();
			
			//TODO: Ver se a class é abstract
			
			String userMessage = "Class with name " + className;
			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info", null, userMessage, MessageDialog.INFORMATION,
					new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue"}, 0);
			
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/01class.wav", userMessage); 
			int result = dialog.open(); //Deixar esta variável result para depois fazer o "End Reading"
		
				
			//Ler os attributes desta classe e as suas relações -> Faz sentido ser aqui
			// Iterar os child objects deste nó (cuidado que pode causar ciclos)
			List<? extends MObject> compChldrn = mObj.getCompositionChildren();
			for (MObject c : compChldrn) {
				readObject(c);
			}
		}

		if (mObj instanceof Attribute) {
			//A que classe pertence
			String attributeClass = mObj.getCompositionOwner().getName();
			//O nome do atributo
			String attributeName = mObj.getName();
			//O tipo do atributo
			String unparsedAttributeType = ((Attribute) mObj).getType().toString();
			String parsedAttributeType = parseAttType(unparsedAttributeType);
		
			String userMessage = "Attribute " + attributeName + " from class " + attributeClass + " of the type " + parsedAttributeType;

			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info", null, userMessage, MessageDialog.INFORMATION,
					new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue"}, 0);
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/02attribute.wav", userMessage); 
			int result = dialog.open();
		}
		
		//Operation/method
	
		//Association
		if(mObj instanceof AssociationEnd) {
			//De onde liga 
			//Para onde liga
			((AssociationEnd) mObj).getTarget();
			//Cardinalidades
			//Roles
		}
		
		//Generalization/Inheritance
		if(mObj instanceof Generalization) {
			//superType é o pai
			((Generalization) mObj).getSuperType();
			
			//subType
			((Generalization) mObj).getSubType();
			
		}
		
		//Inheritance
		
		//Realization/Implementation
		
		//Dependency
		
		//Aggregation
		
		//Composition
		
		//Class Association
		if(mObj instanceof ClassAssociation) {
			//Get Associated Class
			((ClassAssociation) mObj).getClassPart();
			
		}
		
		//Package
		if(mObj instanceof Package) {

		}

		

		
		


	}
	
	private String parseAttType(String unparsedType) {		
		return unparsedType.substring(1, unparsedType.indexOf("'", 1));
		
	}
/* 
    //Useful for debugging in eclipse
	//Show an information dialog box.
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
		dialog.setStrings("/org/modelio/soundUML/sounds/01class.wav", "aaaa"); // Set the file path and text to be read
		int result = dialog.open();
		System.out.println("aaaa");


		System.out.println(result);
	}

*/
}


