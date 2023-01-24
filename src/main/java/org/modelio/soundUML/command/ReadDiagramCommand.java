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
import org.modelio.metamodel.mda.Project;
import org.modelio.metamodel.uml.infrastructure.Element;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.soundUML.impl.UmlClassDiagramReader;
import org.modelio.vcore.model.CompositionGetter;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.metamodel.uml.statik.Class;

/**
 * Implementation of the IModuleContextualCommand interface. <br>
 * The module contextual commands are displayed in the contextual menu and in
 * the specific toolbar of each module property page. <br>
 * The developer may inherit the DefaultModuleContextualCommand class which
 * contains a default standard contextual command implementation.
 *
 */
public class ReadDiagramCommand extends DefaultModuleCommandHandler {

	/**
	 * Constructor.
	 */
	public ReadDiagramCommand() {
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
		ILogService logService = module.getModuleContext().getLogService();
		logService.info("FindElementInDiagramCommand - actionPerformed(...)");

		IModelingSession session = module.getModuleContext().getModelingSession();
		List<MObject> root = session.getModel().getModelRoots();
		IModuleUserConfiguration configuration = module.getModuleContext().getConfiguration();

		ModelElement modelelt = (ModelElement) selectedElements.get(0);

		// Dá-me os diagramas UML existentes neste projecto
		Collection<AbstractDiagram> elements = session.findByClass(AbstractDiagram.class);

		// Iterar todos os diagramas existentes (posso ter dentro deste projecto um
		// class diagram, um state machine, etc...)
		for (AbstractDiagram d : elements) {
			// Vai buscar os elementos representados neste diagrama
			EList<Element> diagramElements = d.getRepresented();
			// Iterar os elementos deste diagrama
			for (Element e : diagramElements) {

				/*
				 * Esta verificação assegura que só estamos a ler numa primeira fase, 
				   os elementos mais de topo na hierarquia (À partida são classes e packages)
				   A restante iteração é feita dentro do método readObject(e)
				 */
				if (e.getCompositionOwner().getCompositionOwner() != null && e.getCompositionOwner().getCompositionOwner() instanceof Project ) {
					UmlClassDiagramReader uml = new UmlClassDiagramReader(e);
					uml.readObject(e);
				}

				/*
				 * //Já não sei se isto é necessário, porque estamos a iterar os filhos dentro
				 * da class UmlClassDiagramReader // pelo sim pelo não, vou deixar comentado //
				 * Iterar os child objects deste nó (cuidado que pode causar ciclos) //
				 * CompositionGetter.getAllChildren((Collection<? extends MObject>) e); -> pode
				 * evitar ciclos List<? extends MObject> compChldrn =
				 * e.getCompositionChildren(); for (MObject c : compChldrn) {
				 * //MessageDialog.openInformation(null, "Info", c.getName() + " " +
				 * c.getMClass());
				 * 
				 * UmlClassDiagramReader uml1 = new UmlClassDiagramReader(c);
				 * uml1.readObject(c); // TODO: Ver se consigo retornar info extra relativamente
				 * a este element // Provavelmente a solução será fazer uma especie de "parser"
				 * onde vou ter de // ver se MObject é instanceof e passar os atributos disso }
				 */

			}

		}

	}

}
