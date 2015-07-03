package sbmlplugin.sbmlplugin;

import ij.io.OpenDialog;
import ij.plugin.PlugIn;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFileChooser;

import org.sbml.libsbml.ReqExtension;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLNamespaces;
import org.sbml.libsbml.SBMLReader;
import org.sbml.libsbml.SpatialExtension;

import sbmlplugin.geometry.GeometryDatas;
import sbmlplugin.image.SpatialImage;
import sbmlplugin.util.PluginConstants;
import sbmlplugin.visual.Viewer;

/**
 * Spatial SBML Plugin for ImageJ
 * @author Kaito Ii <ii@fun.bio.keio.ac.jp>
 * @author Akira Funahashi <funa@bio.keio.ac.jp>
 * Date Created: Jun 17, 2015
 */
public class MainSBaseSpatial extends MainSpatial implements PlugIn{
	
	/* (non-Javadoc)
	 * @see ij.plugin.PlugIn#run(java.lang.String)
	 */
	@Override
	public void run(String arg) {
		document = getDocment();
		if(document == null || document.getModel() == null) return;
		model = document.getModel();
		checkLevelAndVersion();
		checkExtension();
		
		addParaAndSpecies();
		save();
		//ModelValidator  mv = new ModelValidator(model);
		//mv.checkValidation();
		showDomainStructure();
		GeometryDatas gData = new GeometryDatas(model);
		visualize(gData.getSpImgList());
	}

	protected void visualize(ArrayList<SpatialImage> spImgList){
		Iterator<SpatialImage> it = spImgList.iterator();
		Viewer viewer = new Viewer();
		while(it.hasNext()){
			viewer.view(it.next());
		}
	}
	
	private SBMLDocument getDocment(){
		JFileChooser chooser = new JFileChooser(OpenDialog.getLastDirectory());
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(false);
		int returnVal = chooser.showOpenDialog(null);
		
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return null;
		File f = chooser.getSelectedFile();
		SBMLReader reader = new SBMLReader();
		return reader.readSBMLFromFile(f.getAbsolutePath());
	}
	
	private void checkLevelAndVersion(){
		if(model.getLevel() == PluginConstants.LOWERSBMLLEVEL)			 			//level 2		change to latest level 2 version
			document.setLevelAndVersion(PluginConstants.LOWERSBMLLEVEL, PluginConstants.LOWERSBMLVERSION);
		else{}																	//level 3		if new verison comes up check

	}
	
	private void checkExtension(){
		if(model.getLevel() == 2) return ;
		
		SBMLNamespaces sbmlns = document.getSBMLNamespaces();
		if(!document.getPackageRequired("spatial")){			//check spatial
			document.setPackageRequired("spatial", true);
			sbmlns.addPackageNamespace("spatial", 1);
		}

		if(!document.getPackageRequired("req")){				//check req
			document.setPackageRequired("req", true);
			sbmlns.addPackageNamespace("req", 1);
		}
		
		// add extension if necessary
		if(!model.isPackageEnabled("spatial"))
			model.enablePackage(SpatialExtension.getXmlnsL3V1V1(), "spatial", true);

		if(!model.isPackageEnabled("req"))
			model.enablePackage(ReqExtension.getXmlnsL3V1V1(), "req", true);	
	}

}