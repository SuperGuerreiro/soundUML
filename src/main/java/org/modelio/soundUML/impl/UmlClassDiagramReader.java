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
import org.modelio.metamodel.uml.infrastructure.properties.LocalPropertyTable;
import org.modelio.metamodel.uml.infrastructure.properties.PropertyTable;
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
	
		
		//04 Association, aggregation e composition
		//According to what we concluded in the diagrammatic reading experiment, we are only reading the "outgoing" relationships of UML classes,
		//Instead of repeating the same relationships
		if(mObj instanceof AssociationEnd) {
			String userMessage = null;
			
			//Class from where the association is connecting 
			String associationFrom = mObj.getCompositionOwner().getName();
			//Value that allows me to know what type of relationship (association, aggregation or composition) this end of the arrow has
			int relationshipValue = ((AssociationEnd) mObj).getAggregation().getValue();
			
			AssociationEnd oppositeEnd = ((AssociationEnd) mObj).getOpposite();
			//Value for the opposite end of the arrow
			int oppositeEndValue = oppositeEnd.getAggregation().getValue();
			
			//Multiplicities
			String multiplicityMinFrom = ((AssociationEnd) mObj).getMultiplicityMin();
			String multiplicityMaxFrom = ((AssociationEnd) mObj).getMultiplicityMax();
			String multiplicityMinOpposite = oppositeEnd.getMultiplicityMin();
			String multiplicityMaxOpposite = oppositeEnd.getMultiplicityMax();
			
			//Parsed strings with multiplicity, ready to be read
			String multiplicityFrom = parseMultiplicity(multiplicityMinFrom, multiplicityMaxFrom);
			String multiplicityOpposite = parseMultiplicity(multiplicityMinOpposite, multiplicityMaxOpposite);
			
			//Association
			if(relationshipValue == 0 && oppositeEndValue == 0) {
				
				//Ver se é navegável só me faz falta quando já sei que estamos a lidar com association
				boolean isNavigable = ((AssociationEnd) mObj).isNavigable(); 
				
				/* Penso que se isNavigable for true, temos sempre uma classe Target 
				 * Este tipo de relationship só aparece uma vez no diagrama (do lado da associationFrom)
				 * ao contrário das outras que aparecem em ambos os associationEnd
				*/
				if(isNavigable) {
					//Obter o target para onde vamos
					//In this case, we have to get the owner this way. If not, the program crashes
					String associationTo = ((AssociationEnd) mObj).getTarget().getName();
										
					//Role -> it is the name of the associationEnd
					String role = oppositeEnd.getName();
					if(!role.isEmpty()) {
						userMessage = "Association, where " + multiplicityOpposite + " class " +  associationFrom + ", with the role " 
								+ role + ", accesses " + multiplicityFrom + " class " + associationTo;
						
					} else {
						//Não tem role
						userMessage = "Association, where " + multiplicityOpposite + " class " +  associationFrom + " accesses " 
								+ multiplicityFrom + " class " + associationTo;
					}
					
				} else {
					//se não é navegável, é só associação normal, sem pontas
					//Class that "owns" the other side of the arrow
					String associationTo = oppositeEnd.getOwner().getName();
					
					//Possible TODO: Provavelmente aqui deveria existir role também, mas não é urgente
					
					userMessage = "Association between " + multiplicityOpposite + " class " + associationFrom 
							+ " and " + multiplicityFrom + " class " + associationTo;
					
				}
				
				MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Association", null, userMessage, MessageDialog.INFORMATION,
						new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue"}, 0);
				// Set the file path and text to be read
				dialog.setStrings("/org/modelio/soundUML/sounds/04association.wav", userMessage); 
				int result = dialog.open();
				
			}

			//Aggregation
			if(relationshipValue == 0 && oppositeEndValue == 1) {				
				//Class that "owns" the other side of the arrow
				String associationTo = oppositeEnd.getOwner().getName();
				
				userMessage = "Aggregation, where " + multiplicityOpposite + " class " + associationFrom 
						+ " is part of " + multiplicityFrom + " class " + associationTo;

				
				MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Aggregation", null, userMessage, MessageDialog.INFORMATION,
						new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue"}, 0);
				// Set the file path and text to be read
				dialog.setStrings("/org/modelio/soundUML/sounds/08aggregation.wav", userMessage); 
				int result = dialog.open();

				
			}
			
			
			//Composition
			if(relationshipValue == 0 && oppositeEndValue == 2) {
				//Class that "owns" the other side of the arrow
				String associationTo = oppositeEnd.getOwner().getName();
				
				userMessage = "Composition, where " +  multiplicityFrom + " class " + 
						associationTo  + " is composed by " +  multiplicityOpposite + " class " + associationFrom;
				
				
				MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Aggregation", null, userMessage, MessageDialog.INFORMATION,
						new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue"}, 0);
				// Set the file path and text to be read
				dialog.setStrings("/org/modelio/soundUML/sounds/09composition.wav", userMessage); 
				int result = dialog.open();
				
			}

			
			
		
			
			/*Poderá ser preciso para as class associations, mas ver depois
			//Ler se tem uma class association ou nao
			 //Iterar os child objects deste nó (cuidado que pode causar ciclos)
			List<? extends MObject> compChldrn = mObj.getCompositionChildren();
			for (MObject c : compChldrn) {
				readObject(c);
			}
			*/
			
		}
		
		if(mObj instanceof Association) {
			
			List<? extends MObject> compChldrn = mObj.getCompositionChildren();
			for (MObject child : compChldrn) {
				/*
				 * 10 Class Association
				 * association class is a class that is part of an association relationship between two other classes
				 * An association class provide additional information about the association relationship between the two other classes. 
				 */
				// In Modelio, a ClassAssociation belongs to an Association, this means this has to stay within an association
				if(child instanceof ClassAssociation){
					String parentRelation = child.getCompositionOwner().getName();
								
					//Get Associated Class
					String associatedClass = ((ClassAssociation) child).getClassPart().getName();
					
					
					String userMessage = "An association class named " + associatedClass + " between " + parentRelation;

					
					MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Class Association", null, userMessage, MessageDialog.INFORMATION,
							new String[] { "Play Sound", "Read Message", "Reset Buttons", "Continue"}, 0);
					// Set the file path and text to be read
					dialog.setStrings("/org/modelio/soundUML/sounds/07dependency.wav", userMessage); 
					int result = dialog.open();

				} else
					continue;
				
			}
			
		}
		
		
		//05 Generalization/Inheritance
		if(mObj instanceof Generalization) {
			
			//superType é o pai
			String parentClassUnparsed = ((Generalization) mObj).getSuperType().toString();
			String parentClassParsed = parseType(parentClassUnparsed);
			
			//subType
			String childClassUnparsed = ((Generalization) mObj).getSubType().toString();
			String childClassParsed = parseType(childClassUnparsed);
			
			String userMessage = childClassParsed + " is a " + parentClassParsed + " and extends it";

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
	

	/*Method to parse multiplicity from one AssociationEnd,
	 * Returns the string with the multiplicity, ready to be read by the TTS
	 */
	private String parseMultiplicity(String min, String max){		
		//a to a --> a
		if(min.equalsIgnoreCase(max)) {
			return min;
		}
		
		// a to * --> a to many
		if(max.equals("*")) {
			return (min + " or more");
		}
				
		//a to b
		return min + " to " + max;

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