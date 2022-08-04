package org.modelio.soundUML.command;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.jface.dialogs.MessageDialog;
import org.modelio.api.modelio.IModelioServices;
import org.modelio.api.modelio.Modelio;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.picking.IPickingClient;
import org.modelio.api.modelio.picking.IPickingProvider;
import org.modelio.api.modelio.picking.IPickingService;
import org.modelio.api.modelio.picking.IPickingSession;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.configuration.IModuleUserConfiguration;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.diagrams.AbstractDiagram;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.soundUML.impl.SimpleAudioPlayer;
import org.modelio.vcore.smkernel.mapi.MObject;

/**
 * Implementation of the IModuleContextualCommand interface.
 * <br>The module contextual commands are displayed in the contextual menu and in the specific toolbar of each module property page.
 * <br>The developer may inherit the DefaultModuleContextualCommand class which contains a default standard contextual command implementation.
 *
 */
public class DescribeSelectedElementCommand extends DefaultModuleCommandHandler {
    /**
     * Constructor.
     */
    public DescribeSelectedElementCommand() {
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
        logService.info("DescribeSelectedElementCommand - actionPerformed(...)");

        IModelingSession session = module.getModuleContext().getModelingSession();
        List<MObject> root = session.getModel().getModelRoots();
        IModuleUserConfiguration configuration = module.getModuleContext().getConfiguration();

        ModelElement modelelt = (ModelElement)selectedElements.get(0);            
        
        //List<? extends MObject> elements = modelelt.getCompositionChildren();
       
        //Chama o resource para tocar o ficheiro de audio
        //Nao da para criar ficheiro através de um URI num ficheiro jar, mas dá para passar o input stream directamente e tocar
        InputStream inputStream = getClass().getResourceAsStream("/org/modelio/soundUML/sounds/duolingo.wav");
        SimpleAudioPlayer player = new SimpleAudioPlayer();
        player.play(inputStream);

        
        //Pede o input ao user
        
        String teste = JOptionPane.showInputDialog(null, "Introduz texto aqui:");
        MessageDialog.openInformation(null, "Teste string", teste);
        
        if(teste.equalsIgnoreCase("boas")) {
            MessageDialog.openInformation(null, "Entrei aqui", modelelt.getName());
        } else {
            MessageDialog.openInformation(null, "Nao entrei", modelelt.getName());
        }
        

        
        //Evento em que vou clickar num elemento do diagrama, espera pelo click do user
        
        //IPickingService pickingService = module.getModuleContext().getModelioServices().getPickingService();
        //IPickingClient pickingClient = null;
        //IPickingSession pickingSession = pickingService.startPickingSession(pickingClient);

   
        //será que ele já seleciona e nem preciso de fazer isto?
        
        //Após click, ler informação daquele elemento em concreto com TTS
        

    }
    
   

 }

