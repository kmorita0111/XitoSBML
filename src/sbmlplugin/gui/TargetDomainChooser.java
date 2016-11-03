package sbmlplugin.gui;

import ij.IJ;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.sbml.libsbml.Geometry;
import org.sbml.libsbml.ListOfDomainTypes;
import org.sbml.libsbml.Model;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLReader;
import org.sbml.libsbml.SpatialModelPlugin;


// TODO: Auto-generated Javadoc
/**
 * Spatial SBML Plugin for ImageJ.
 *
 * @author Kaito Ii <ii@fun.bio.keio.ac.jp>
 * @author Akira Funahashi <funa@bio.keio.ac.jp>
 * Date Created: Aug 30, 2015
 */
@SuppressWarnings("serial")
public class TargetDomainChooser extends JFrame implements ActionListener{
	static {
		try {
			System.loadLibrary("sbmlj");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/** The target domain. */
	private String targetDomain = null;
	
	/** The compartment list. */
	private List<String> compartmentList = new ArrayList<String>();
	
	/** The exclude dom. */
	private final String[] excludeDom = {"Extracellular","Cytosol"};
	
	/**
	 * Instantiates a new target domain chooser.
	 */
	TargetDomainChooser() {
		super("Target Domain Chooser");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);	
		setSize(250, 70);
		setResizable(false);
		setLocationByPlatform(true);
		setLocationRelativeTo(null);
	}
	
	/**
	 * Instantiates a new target domain chooser.
	 *
	 * @param model the model
	 */
	public TargetDomainChooser(Model model){
		this();
		createCompartmentList(model);
		initComponent();
	}
	
	/**
	 * Creates the compartment list.
	 *
	 * @param model the model
	 */
	private void createCompartmentList(Model model){
		SpatialModelPlugin spatialplugin = (SpatialModelPlugin) model.getPlugin("spatial");
		Geometry geometry = spatialplugin.getGeometry();
		ListOfDomainTypes lodt = geometry.getListOfDomainTypes();
		for(int i = 0 ; i < lodt.size() ; i++){
			String dom = lodt.get(i).getId();
			if(!Arrays.asList(excludeDom).contains(dom) && dom.contains("membrane"))
				compartmentList.add(lodt.get(i).getId());
		}			
	
		if(compartmentList.isEmpty()){
			IJ.error("No target domain found");
			setTargetDomain("");
		}
	}
	
	/** The compartment box. */
	private JComboBox compartmentBox;
	
	/** The ok button. */
	private JButton okButton;
	
	/**
	 * Inits the component.
	 */
	private void initComponent(){
		JLabel label = new JLabel(" Select one domain :");
		JPanel panel = new JPanel();
		
		compartmentBox = new JComboBox(compartmentList.toArray());
		String title = "DomainType List";
		compartmentBox.setName(title);
		compartmentBox.setRenderer(new ComboBoxRenderer(title));
		compartmentBox.setSelectedIndex(-1);
	
		okButton = new JButton("OK");
		okButton.setName("ok");
		okButton.addActionListener(this);
		
		
		panel.add(compartmentBox, BorderLayout.LINE_START);
		panel.add(okButton, BorderLayout.PAGE_END);
		getContentPane().add(label, BorderLayout.NORTH);
		getContentPane().add(panel, BorderLayout.CENTER);
		setVisible(true);
	}

	/**
	 * Gets the target domain.
	 *
	 * @return the target domain
	 */
	public String getTargetDomain() {
		return targetDomain;
	}

	/**
	 * Sets the target domain.
	 *
	 * @param targetDomain the new target domain
	 */
	private void setTargetDomain(String targetDomain) {
		this.targetDomain = targetDomain;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == okButton && compartmentBox.getSelectedIndex() >= 0){ 
			setTargetDomain((String) compartmentBox.getSelectedItem());
			dispose();
		} else{
			IJ.error("Must select one domain type.");
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		SBMLReader reader = new SBMLReader();
		SBMLDocument d = reader.readSBML("mem_diff.xml");
		TargetDomainChooser tdc = new TargetDomainChooser(d.getModel());	
	
		while(tdc.getTargetDomain() == null){
			synchronized (d){
				
			}
		}
	}
}
