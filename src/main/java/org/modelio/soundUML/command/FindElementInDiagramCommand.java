package org.modelio.soundUML.command;

import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.configuration.IModuleUserConfiguration;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.diagrams.AbstractDiagram;
import org.modelio.metamodel.uml.infrastructure.Element;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.soundUML.impl.UmlClassDiagramReader;
import org.modelio.vcore.model.CompositionGetter;
import org.modelio.vcore.smkernel.mapi.MObject;

/**
 * Implementation of the IModuleContextualCommand interface. <br>
 * The module contextual commands are displayed in the contextual menu and in
 * the specific toolbar of each module property page. <br>
 * The developer may inherit the DefaultModuleContextualCommand class which
 * contains a default standard contextual command implementation.
 *
 */
public class FindElementInDiagramCommand extends DefaultModuleCommandHandler {

	/**
	 * Constructor.
	 */
	public FindElementInDiagramCommand() {
		super();
	}

	/**
	 * @see org.modelio.api.module.commands.DefaultModuleContextualCommand#accept(java.util.List,
	 *      org.modelio.api.module.IModule)
	 */
	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		// Check that there is only one selected element
		return selectedElements.size() == 1;
	}

	/**
	 * @see org.modelio.api.module.commands.DefaultModuleContextualCommand#actionPerformed(java.util.List,
	 *      org.modelio.api.module.IModule)
	 */
	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		boolean elementFound = false;
		ILogService logService = module.getModuleContext().getLogService();
		logService.info("FindElementInDiagramCommand - actionPerformed(...)");

		IModelingSession session = module.getModuleContext().getModelingSession();
		List<MObject> root = session.getModel().getModelRoots();
		IModuleUserConfiguration configuration = module.getModuleContext().getConfiguration();

		ModelElement modelelt = (ModelElement) selectedElements.get(0);

		// Pede o input ao user
		String inputName = JOptionPane.showInputDialog(null, "Insert the name of the element you are searching for:");

		// Dá-me os diagramas UML existentes neste projecto
		Collection<AbstractDiagram> elements = session.findByClass(AbstractDiagram.class);

		// Iterar todos os diagramas existentes (posso ter dentro deste projecto um
		// class diagram, um state machine, etc...)
		for (AbstractDiagram d : elements) {
			// Vai buscar os elementos representados neste diagrama
			EList<Element> diagramElements = d.getRepresented();
			// Iterar esses elementos deste diagrama e encontra aquele com o nome igual ao
			// que passámos como input
			for (Element e : diagramElements) {

				if (e.getName().equalsIgnoreCase(inputName)) {
					elementFound = true;

					if (e.getCompositionOwner() != null) {
						MessageDialog.openInformation(null, "Info", "Owner: " + e.getCompositionOwner());
					}

					// Iterar os child objects deste nó (cuidado que pode causar ciclos)
					// CompositionGetter.getAllChildren((Collection<? extends MObject>) e); -> pode
					// evitar ciclos
					List<? extends MObject> compChldrn = e.getCompositionChildren();
					for (MObject c : compChldrn) {
						MessageDialog.openInformation(null, "Info", c.getName() + " " + c.getMClass());

						UmlClassDiagramReader uml = new UmlClassDiagramReader();
						uml.readObject(c);
						// TODO: Ver se consigo retornar info extra relativamente a este element
						// Provavelmente a solução será fazer uma especie de "parser" onde vou ter de
						// ver se MObject é instanceof e passar os atributos disso
					}
					break;
				}
			}

		}

		if (!elementFound) {
			MessageDialog.openInformation(null, "No Element was found",
					"Sorry, the element with the name " + inputName + " was not found.");
		}

		// HashSet rawResults = new HashSet();
		// rawResults.addAll(session.findByClass(AbstractDiagram.class));

		// module.getModuleContext().getModelioServices().getDiagramService().getDiagramHandle(d).getDiagramGraphics(element)

	}
	

}



