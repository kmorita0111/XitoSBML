
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.sbml.libsbml.ListOfParameters;
import org.sbml.libsbml.ListOfSpecies;
import org.sbml.libsbml.Model;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLReader;


public class ParamAndSpecies extends JFrame implements ActionListener, FocusListener, WindowListener{
	  static {
		    System.loadLibrary("sbmlj");
		  }
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Model model;
	private ListOfParameters lop; 
	private ListOfSpecies los;
	private final int initRow = 1;
	private DefaultTableModel tableModel;
	private JTable table;
	private boolean wasExited = false;
	
	public ParamAndSpecies(){
		super("Parameter and Species Table");
		setDefaultCloseOperation(EXIT_ON_CLOSE);	
		setSize(480, 200);
		setResizable(false);

	}
	
	private final String[] header = {"Parameter","Species"};
	
	public ParamAndSpecies(Model model){
		this();
		this.model = model;
		this.lop = model.getListOfParameters();
		this.los = model.getListOfSpecies();
		Object[][] data = new Object[initRow][header.length];
		tableModel = new DefaultTableModel(data, header){
			private static final long serialVersionUID = 1L;
		
			public boolean isCellEditable(int row, int column){	
					return false;
			}
		
		};
				
		//table setting 
		table = new JTable(tableModel){
			private static final long serialVersionUID = 1L;
			@Override
			public Class<?> getColumnClass(int Column){
				switch (Column) {
				case 0:
				case 1:
					return String.class;
				default:
					return Boolean.class;
				}
			}
		};
		table.setBackground(new Color(169,169,169));
		table.getTableHeader().setReorderingAllowed(false);
		table.addFocusListener(this);
		
		//scrollbar
		JScrollPane scroll = new JScrollPane(table);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	
		//button
		JButton ok = new JButton("OK"), plus = new JButton("+"), minus = new JButton("-");
		ok.addActionListener(this); plus.addActionListener(this); minus.addActionListener(this);
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.LINE_AXIS));
		p2.add(plus); p2.add(minus);
		p2.add(Box.createRigidArea(new Dimension(200, 0))); p2.add(ok);
		
		//set components 
		getContentPane().add(p2, BorderLayout.PAGE_END);
		getContentPane().add(scroll, BorderLayout.CENTER);	

		setVisible(true);
	}
	
	private final int PARAMETER = 0;
	private final int SPECIES = 1;
	
	private void removeCell(int row, int column){
		String name = (String) table.getValueAt(row, column);
		if(column == PARAMETER){
			lop.remove(name);	
		}
			
		if(column == SPECIES){
			los.remove(name);
		}
		
		tableModel.setValueAt("", row, column);
		updateTable();
	}
	
	private void updateTable(){
		long pSize = lop.size();
		long sSize = los.size();
		long listSize = Math.max(pSize, sSize);
		
		while(listSize > table.getRowCount())
			tableModel.addRow(new Object[]{"",""});
		
		for(int i = 0 ; i < table.getRowCount() ; i++){
			if (i < pSize)table.setValueAt(lop.get(i).getId(), i, PARAMETER);
			else table.setValueAt("", i, PARAMETER);
			
			if (i < sSize)table.setValueAt(los.get(i).getId(), i, SPECIES);
			else table.setValueAt("", i, SPECIES);
		}
			

		validate();
	}
	
	public boolean wasExited(){
		return wasExited;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SBMLReader reader = new SBMLReader();
		SBMLDocument d = reader.readSBML("simple_mem_diffusion.xml");
		new ParamAndSpecies( d.getModel());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String input = e.getActionCommand();
		
		if(input.equals("OK")){
			wasExited = true;
			dispose();
			return;
		}
		
		if(input.contentEquals("+")){
			new Adder(model);
		}
		
		if(input.contentEquals("-")){
			int row = table.getSelectedRow();
			int column = table.getSelectedColumn();
			if( (row >= 0 || column >= 0) && table.getValueAt(row, column) != null)
				removeCell(row, column);
		}
	}

	@Override
	public void focusGained(FocusEvent f) {
		updateTable();
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		wasExited = true;
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
