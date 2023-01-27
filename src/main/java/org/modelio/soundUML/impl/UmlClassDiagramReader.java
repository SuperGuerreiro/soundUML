package org.modelio.soundUML.impl;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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
	
	static final int END_READING_CODE = 3;
	
	static final String SOUND_PATH_CLASS = "/org/modelio/soundUML/sounds/01class.wav";
	static final String  SOUND_PATH_ATTRIBUTE = "/org/modelio/soundUML/sounds/02attribute.wav";
	
	/*
	 *ArrayList that stores the unique identifier of each class
	 *Keeps track of what classes have already been read
	 */	
	private ArrayList<String> classList = new ArrayList<String>();
	
	private ArrayList<Element> generalizationList = new ArrayList<Element>();
	
	private static String[] dialogOptions = new String[] { "Play Sound", "Read Message", "Reset Buttons", "End Reading", "Back", "Continue"};
	
	private LinkedList<MessageDialogExtended> messageDialogs = new LinkedList<MessageDialogExtended>();
	
	
	public UmlClassDiagramReader() {
		
		
	}
	

	public void readObject(MObject mObj) {
		
		//Class
		if (mObj instanceof Class) {
			String userMessage = "";
			
			//Object unique identifier.
			String uuid = mObj.getUuid();
			if(!classList.contains(uuid)) {
				//Nome da classe
				String className = mObj.getName();
				
				boolean isAbstract = ((Class) mObj).isIsAbstract();
				
				if(isAbstract) {
					userMessage = "Abstract class with name " + className;
				} else {
					userMessage = "Class with name " + className;
				}
				
				MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Class", null, userMessage, MessageDialog.INFORMATION,
						dialogOptions, 0);
				// Set the file path and text to be read
				dialog.setStrings(SOUND_PATH_CLASS, userMessage); 
				
				//Adds this specific messageDialog to a linked list that will then be shown to the people
				messageDialogs.add(dialog);
			
				
				//dialog.open(); //Deixar esta variável result para depois fazer o "End Reading"
				
			
				//Ler os attributes operações e relações desta classe -> Faz sentido ser aqui
				// Iterar os child objects deste nó (cuidado que pode causar ciclos)
				List<? extends MObject> compChldrn = mObj.getCompositionChildren();
				for (MObject c : compChldrn) {
					readObject(c);
				}
				
				//Finally, if it has any incoming generalization/inheritances, read it here:
				ArrayList<Generalization> gList = getInheritancesWithSameParent(uuid);
				for(Generalization g: gList) {
					readObject(g);
				}
				
				//Putting the class with this id into the list, ensures that the class is read only once
				//MessageDialog.openInformation(null, "Info", "classname: " + className + " contains? " + classList.contains(uuid));
				classList.add(uuid);
				//MessageDialog.openInformation(null, "Info", "Adicionei à lista com este id: " + uuid + " classname: " + className + " contains? " + classList.contains(uuid));
				
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
			
			//Visibility (private, public, etc)
			String attVisibilityUnparsed = ((Attribute) mObj).getVisibility().getName();
			String attVisibility = parseVisibility(attVisibilityUnparsed);
			
			String userMessage = attVisibility + " attribute " + attributeName + ", of the type " + parsedAttributeType 
					+ ", from class " + attributeClass;

			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Attribute", null, userMessage, MessageDialog.INFORMATION,
					dialogOptions, 0);
			// Set the file path and text to be read
			dialog.setStrings(SOUND_PATH_ATTRIBUTE, userMessage); 
			
			//Adds this specific messageDialog to a linked list that will then be shown to the people
			messageDialogs.add(dialog);
			
			//int result = dialog.open();
		}
		
		//03: Operation/method
		if(mObj instanceof Operation) {
			String userMessage = null;
			
			//A que classe pertence
			String operationClass = mObj.getCompositionOwner().getName();
			//nome da operacao
			String operationName = mObj.getName();
			
			//Visibility (private, public, etc)
			String opVisibilityUnparsed = ((Operation) mObj).getVisibility().getName();
			String opVisibility = parseVisibility(opVisibilityUnparsed);
			
			
			String returnMessage = "";
			//Verificar se o método retorna alguma coisa
			if(((Operation) mObj).getReturn() != null) {
				String returnType = ((Operation) mObj).getReturn().getType().toString();
				String parsedReturnType = parseType(returnType);
				
				returnMessage = "and returns the type " + parsedReturnType;
				
			} else {
				returnMessage = "and is void";
			}
			
			//Parametros da operacao
			 EList<Parameter> parameterList = ((Operation) mObj).getIO();
			 if(!parameterList.isEmpty()) {
				 String parameters = "";
				 //"parameter NOME, of the type TYPE"
				 for(Parameter p: parameterList) {
					 //MessageDialog.openInformation(null, "Info", "Parameter name: ?" + p.getName() + " type? " + p.getType().toString());
					 String unparsedParameterType = p.getType().toString();
					 String parsedParameterType = parseType(unparsedParameterType);
				
					 parameters = parameters + p.getName() + ", of the type " + parsedParameterType + "; ";
				 }
				 
				 if(parameterList.size() == 1) {
					 userMessage = opVisibility + " operation " + operationName + ", from class " + operationClass + ", with parameter " 
							 + parameters + returnMessage;
				 } else {
					 //Há varios parametros
					 userMessage = opVisibility + " operation " + operationName + ", from class " + operationClass + ", with " + parameterList.size()
					 		+ " parameters: " + parameters + returnMessage; 
				 }

			 } else {
				 //Não há parametros
				 userMessage = opVisibility + " operation " + operationName +  ", from class " + operationClass + ", with no parameters" 
						 + ", " + returnMessage;
			 }

 
			
			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Operation/Method", null, userMessage, MessageDialog.INFORMATION,
					dialogOptions, 0);
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/03operation.wav", userMessage); 
			
			//Adds this specific messageDialog to a linked list that will then be shown to the people
			messageDialogs.add(dialog);
			
			//int result = dialog.open();
			
		}
	
		
		//04 Association, 08 aggregation e 09 composition
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
			
			//Association (neither incoming nor outgoing)
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
					
					userMessage = "Association, between " + multiplicityOpposite + " class " + associationFrom 
							+ ", and " + multiplicityFrom + " class " + associationTo;
					
				}
				
				MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Association", null, userMessage, MessageDialog.INFORMATION,
						dialogOptions, 0);
				// Set the file path and text to be read
				dialog.setStrings("/org/modelio/soundUML/sounds/04association.wav", userMessage); 
				
				//Adds this specific messageDialog to a linked list that will then be shown to the people
				messageDialogs.add(dialog);
				
				//int result = dialog.open();
				
			}

			//Outgoing Aggregation
			if(relationshipValue == 0 && oppositeEndValue == 1) {				
				//Class that "owns" the other side of the arrow
				String associationTo = oppositeEnd.getOwner().getName();
				
				userMessage = "Aggregation, where " + multiplicityOpposite + " class " + associationFrom 
						+ ", is part of " + multiplicityFrom + " class " + associationTo;

				
				MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Aggregation", null, userMessage, MessageDialog.INFORMATION,
						dialogOptions, 0);
				// Set the file path and text to be read
				dialog.setStrings("/org/modelio/soundUML/sounds/08aggregation.wav", userMessage); 
				
				//Adds this specific messageDialog to a linked list that will then be shown to the people
				messageDialogs.add(dialog);
				
				//int result = dialog.open();

				
			}
			
			
			//Outgoing Composition
			if(relationshipValue == 0 && oppositeEndValue == 2) {
				//Class that "owns" the other side of the arrow
				String associationTo = oppositeEnd.getOwner().getName();
				
				userMessage = "Composition, where " +  multiplicityFrom + " class " + 
						associationTo  + ", is composed by " +  multiplicityOpposite + " class " + associationFrom;
				
				MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Composition", null, userMessage, MessageDialog.INFORMATION,
						dialogOptions, 0);
				// Set the file path and text to be read
				dialog.setStrings("/org/modelio/soundUML/sounds/09composition.wav", userMessage); 
				
				//Adds this specific messageDialog to a linked list that will then be shown to the people
				messageDialogs.add(dialog);
				
				//int result = dialog.open();
				
			}
			

			//Poderá ser preciso para as class associations, mas ver depois
			//Ler se tem uma class association ou nao
			 //Iterar os child objects deste nó (cuidado que pode causar ciclos)
			List<? extends MObject> compChldrn = mObj.getCompositionChildren();
			for (MObject c : compChldrn) {
				readClassAssociation(c);
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
			
			//String discriminator = ((Generalization) mObj).getDiscriminator();
			
			
			String userMessage = childClassParsed + " is a " + parentClassParsed + ", and extends its functionalities";

			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Generalization/Inheritance", null, userMessage, MessageDialog.INFORMATION,
					dialogOptions, 0);
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/05inheritance.wav", userMessage); 
			
			//Adds this specific messageDialog to a linked list that will then be shown to the people
			messageDialogs.add(dialog);
			
			//int result = dialog.open();
		}
		
		
		//06 Realization/Implementation
		//Only implements an interface
		if(mObj instanceof InterfaceRealization) {
			String realizationFrom = mObj.getCompositionOwner().getName();

			String interfaceName = ((InterfaceRealization) mObj).getImplemented().getName();
			
			String userMessage = "Class " + realizationFrom + " implements the interface " + interfaceName;
			
			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Realization/Implementation", null, userMessage, MessageDialog.INFORMATION,
					dialogOptions, 0);
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/06realization.wav", userMessage); 
			
			//Adds this specific messageDialog to a linked list that will then be shown to the people
			messageDialogs.add(dialog);
			
			//int result = dialog.open();
				
		}
		
		//07 Dependency
		if(mObj instanceof Dependency) {
			String userMessage = null;

			//Elemento que depende (provavelmente é sempre uma classe?)
			String dependentElement = mObj.getCompositionOwner().getName();

			//Depende deste elemento
			ModelElement element = ((Dependency) mObj).getDependsOn();
			String dependsOn = element.getName();
						
			
			if(element instanceof Class) {
				userMessage = "Class " + dependentElement + ", depends on class " + dependsOn;
			}
			
			if(element instanceof Interface) {
				userMessage = "Class " + dependentElement + ", depends on interface " + dependsOn;
			}
			
			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Dependency", null, userMessage, MessageDialog.INFORMATION,
					dialogOptions, 0);
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/07dependency.wav", userMessage); 
			
			//Adds this specific messageDialog to a linked list that will then be shown to the people
			messageDialogs.add(dialog);
			
			//int result = dialog.open();

		}
		
		
		//11 Package
		if(mObj instanceof Package) {
			String packageName = mObj.getCompositionOwner().getName();
			
			String userMessage = "Package " + packageName;
			
			MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Package", null, userMessage, MessageDialog.INFORMATION,
					dialogOptions, 0);
			// Set the file path and text to be read
			dialog.setStrings("/org/modelio/soundUML/sounds/11package.wav", userMessage); 
			
			//Adds this specific messageDialog to a linked list that will then be shown to the people
			messageDialogs.add(dialog);
			
			//int result = dialog.open();
		}

	

	}
	
	
	/*
	 * 10 Class Association
	 * association class is a class that is part of an association relationship between two other classes
	 * An association class provide additional information about the association relationship between the two other classes. 
	 * 
	 * In Modelio, a ClassAssociation belongs to an Association, this means this has to stay within an association
	 */
	private void readClassAssociation(MObject mObj) {
	
		if(mObj instanceof Association) {
			
			//Iterate the children of the association
			List<? extends MObject> compChldrn = mObj.getCompositionChildren();
			for (MObject child : compChldrn) {
				
				if(child instanceof ClassAssociation){
					Association ass = ((ClassAssociation) child).getAssociationPart();
					
					//Get Associated Class	
					Class associatedClass = ((ClassAssociation) child).getClassPart();
					String associatedClassName = associatedClass.getName();
					
					
					//Get all the ends of this association (I think they can only be two, but in the case they are n-ends, it's working)
					EList<AssociationEnd> classEnd = ass.getEnd();
					String classNames = "";
					int i = 0;
					for(AssociationEnd aEnd : classEnd) {
						String className = aEnd.getOwner().getName();
						
						if(i < classEnd.size()-1) {
							classNames = classNames + " class " + className + " and";
						}
						if(i == classEnd.size()-1) {
							classNames = classNames + " class " + className;

						}
						i++;
					}
						
					
					String userMessage = "An association class named " + associatedClassName + ", between" + classNames;

					
					MessageDialogExtended dialog = new MessageDialogExtended(null, "Info - Class Association", null, userMessage, MessageDialog.INFORMATION,
							dialogOptions, 0);
					// Set the file path and text to be read
					dialog.setStrings("/org/modelio/soundUML/sounds/10classAssociationA.wav", userMessage); 
					
					//Adds this specific messageDialog to a linked list that will then be shown to the people
					messageDialogs.add(dialog);
					
					//int result = dialog.open();
					
					//Reads the class directly, this way we ensure that the class and all its attributes, operations and relations
					// are read right after being shown the class association message
					readObject(associatedClass);

				} else
					continue;
				
			}
			
		}
		
		
	}
	
	/*
	 * Method that shows the message dialogs contained in a linked list to the users
	 * Allows for user navigation by iterating on this list and deals with each
	 * button pressed in the message dialog. 
	 */
	public void showMessageDialogs() {
		
		int i = 0;
		while(i < messageDialogs.size()) {
			MessageDialogExtended currentMessage = messageDialogs.get(i);
			if(i == 0) {
				//currentMessage.isFirstMessage();
			}
			int result = currentMessage.open();
			
			//End reading
			if(result == 3) {
				return;
			}
			
			//Back to the previous
			if(result == 4) {			
				i--;
			}
				
			//Continue reading
			if(result == 5) {
				i++;
			}
			
		}		

	}
	
	//When using method getType().toString(), we get a long string of characters 
	//this method parses what we want
	private String parseType(String unparsedType) {		
		return unparsedType.substring(1, unparsedType.indexOf("'", 1));
		
	}
	
	private String parseVisibility(String unparsedVisibility) {	
		if(unparsedVisibility.equalsIgnoreCase("PackageVisibility")) {
			return "Package";
		}
		
		if(unparsedVisibility.equalsIgnoreCase("VisibilityUndefined")) {
			return "Undefined";
		}
		
		return unparsedVisibility;
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
	
	//Iterar a lista toda para obter apenas os elementos que são inheritance
	//This method NEEDS to be called before readObject()
	public void getInheritanceElements(EList<Element> elementList) {
		
		for(Element e: elementList) {		
			if(e instanceof Generalization) {
				generalizationList.add(e);
			} else {
				continue;

			}
		}
		
	}
	
	//Returns all the inheritances with the same parent
	//This method is needed to read all the incoming inheritance relationships of a given class
	private ArrayList<Generalization> getInheritancesWithSameParent(String classUuid) {
		ArrayList<Generalization> res = new ArrayList<Generalization>();
		
		for(Element g: generalizationList) {
			NameSpace classNameSpace = ((Generalization) g).getSuperType();
			
			if(classNameSpace.getUuid().equalsIgnoreCase(classUuid)) {
				res.add((Generalization) g);
				
			}
		}
		
		return res;

	}
	

/*------------------------------------------------------------------------*/	

	
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
				dialogOptions, 0);
		dialog.setStrings(SOUND_PATH_CLASS, "aaaa"); // Set the file path and text to be read
		int result = dialog.open();
		//System.out.println(dialog.getReturnCode());


		System.out.println(result);
	}
*/

	
}