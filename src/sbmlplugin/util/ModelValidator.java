package sbmlplugin.util;

import ij.IJ;

import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLError;
import org.sbml.jsbml.SBMLErrorLog;
import org.sbml.jsbml.SBMLReader;


// TODO: Auto-generated Javadoc
/**
 * The Class ModelValidator.
 */
public class ModelValidator {

	/** The document. */
	private SBMLDocument document;
	
	/** The model. */
	private Model model;

	
	/**
	 * Instantiates a new model validator.
	 *
	 * @param document the document
	 */
	public ModelValidator(SBMLDocument document){
		this.document = document;
		this.model = document.getModel();	
	}
	
	/**
	 * Check model version.
	 *
	 * @return true, if successful
	 */
	private boolean checkModelVersion(){
		if(model.getLevel() != PluginConstants.SBMLLEVEL  ||  model.getVersion() != PluginConstants.SBMLVERSION) 
			IJ.log("model is not level 3 version 1");
		
		return model.getLevel() == PluginConstants.SBMLLEVEL  &&  model.getVersion() == PluginConstants.SBMLVERSION;
	}
	
	/**
	 * Check extension.
	 *
	 * @return true, if successful
	 */
	private boolean checkExtension(){
		
		if(!document.getPackageRequired("spatial"))
			IJ.log("model missing extension spatial");
		
//		if(!document.getPackageRequired("req"))
//			IJ.log("model missing extension req");
		
		return document.getPackageRequired("spatial"); //&& document.getPackageRequired("req");
	}

	/**
	 * Validate.
	 */
	public void validate(){
		boolean hasRequiredAttribute = checkModelVersion() && checkExtension();
		
		if(!hasRequiredAttribute)
			return;
		
		document.checkConsistency();
		SBMLErrorLog errorLog = document.getListOfErrors();
		List<SBMLError> errorList = errorLog.getValidationErrors();
		for (SBMLError e : errorList) {
			IJ.log(e.getLine() + " " + e.getMessage());
		}
	}
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SBMLDocument d;
		try {
			d = SBMLReader.read("sampledfield_3d.xml");
			ModelValidator mv = new ModelValidator(d);
			mv.validate();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
