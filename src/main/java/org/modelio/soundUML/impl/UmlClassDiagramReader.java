package org.modelio.soundUML.impl;


import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.metamodel.uml.infrastructure.Dependency;
import org.modelio.metamodel.uml.infrastructure.Element;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
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
			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Class", null, userMessage, MessageDialog.INFORMATION,
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
			String parsedAttributeType = parseType(unparsedAttributeType);
			
			//TODO: Dizer se o attributo é privado ou público
			
			
			String userMessage = "Attribute " + attributeName + " from class " + attributeClass + " of the type " + parsedAttributeType;

			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Attribute", null, userMessage, MessageDialog.INFORMATION,
					new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue"}, 0);
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/02attribute.wav", userMessage); 
			int result = dialog.open();
		}
		
		//03: Operation/method
		if(mObj instanceof Operation) {
			//A que classe pertence
			String operationClass = mObj.getCompositionOwner().getName();
			//nome da operacao
			String operationName = mObj.getName();
			//Parametros da operacao
			 //EList<Parameter> parameterList = ((Operation) mObj).getIO();
			
			//TODO: Tratar dos parametros da operação e se o método é publico ou privado

			
			//O que retorna
			String returnType = ((Operation) mObj).getReturn().getType().toString();
			String parsedAttributeType = parseType(returnType);

			//Provavelmente vai ter de ser um FOR consoante o nº de parametros que tivermos
			String userMessage = "Operation " + operationName + " from class " + operationClass + " with parameters " + " and returns the type " + parsedAttributeType;

			
			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Operation/Method", null, userMessage, MessageDialog.INFORMATION,
					new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue"}, 0);
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/03operation.wav", userMessage); 
			int result = dialog.open();
			
		}
	
		
		//04 Association 
		//Agregation e composition também?
		if(mObj instanceof AssociationEnd) {
			//De onde liga 
			String associationFrom = mObj.getCompositionOwner().getName();

			//Para onde liga
			//String associationFrom = mObj.getCompositionOwner().getName();

			MessageDialog.openInformation(null, "Info", mObj.getName() + " " + mObj.getMClass());

			//TODO: Resolver esta parte (não sei como)
			
			//String associationTo = ((AssociationEnd) mObj).getTarget().getName();
					
			//Roles (Ex. ser dean)
			
			//Association Type
			EList<LinkEnd> links = ((AssociationEnd) mObj).getOccurence();
			
			//Cardinalidades
			for (LinkEnd link : links) { //Não está a entrar aqui
				String test = link.getMultiplicityMax();
				String test2 = link.getMultiplicityMin();
				MessageDialog.openInformation(null, "Info", "Multiplicity Max: " + test + " Multiplicity Min: " + test2);
			}
			
			String userMessage = "Relationship from class " + associationFrom + " to class " ;
			
			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Relationship", null, userMessage, MessageDialog.INFORMATION,
					new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue"}, 0);
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/04association.wav", userMessage); 
			int result = dialog.open();
			
			
		}
		
		
		//05 Generalization/Inheritance
		if(mObj instanceof Generalization) {
			
			//superType é o pai
			String parentClassUnparsed = ((Generalization) mObj).getSuperType().toString();
			String parentClassParsed = parseType(parentClassUnparsed);
			
			//subType
			String childClassUnparsed = ((Generalization) mObj).getSubType().toString();
			String childClassParsed = parseType(childClassUnparsed);
			
			String userMessage = childClassParsed + " is a " + parentClassParsed;

			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Generalization/Inheritance", null, userMessage, MessageDialog.INFORMATION,
					new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue"}, 0);
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/05inheritance.wav", userMessage); 
			int result = dialog.open();
		}
		
		
		//06 Realization/Implementation
		if(mObj instanceof InterfaceRealization) {

		}
		
		//07 Dependency
		if(mObj instanceof Dependency) {
			
			//Elemento que depende (provavelmente é sempre uma classe?)
			String dependentElement = mObj.getCompositionOwner().getName();

			//Depende deste elemento
			ModelElement element = ((Dependency) mObj).getDependsOn();
			String dependsOn = element.getName();
						
			String userMessage = null;
			
			if(element instanceof Class) {
				userMessage = dependentElement + " depends on class " + dependsOn;
			}
			
			if(element instanceof Interface) {
				userMessage = dependentElement + " depends on interface " + dependsOn;
			}
			
			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Dependency", null, userMessage, MessageDialog.INFORMATION,
					new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue"}, 0);
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/07dependency.wav", userMessage); 
			int result = dialog.open();

		}
		
		//08 Aggregation
		
		//09 Composition
		
		//10 Class Association
		if(mObj instanceof ClassAssociation) {
			//Get Associated Class
			((ClassAssociation) mObj).getClassPart();
			
		}
		
		//11 Package
		if(mObj instanceof Package) {
			//MessageDialog.openInformation(null, "Info", "Entrei aqui");
		}

		

		
		


	}
	
	//When using method getType().toString(), we get a long string of characters 
	//this method parses what we want
	private String parseType(String unparsedType) {		
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