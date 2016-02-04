/*******************************************************************************
 * Copyright 2015 Kaito Ii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package sbmlplugin.image;

import ij.ImagePlus;
import ij.gui.MessageDialog;
import ij.io.FileInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

@SuppressWarnings("serial")
public class ImageExplorer extends JFrame implements ActionListener{

	private HashMap<String, Integer> hashDomainTypes;
	private HashMap<String, Integer> hashSampledValues;
	private HashMap<String,ImagePlus> hashDomFile;
	private FileInfo compoInfo;
	private JScrollPane scroll; 
	
	public ImageExplorer(){
		super("DomainType Namer");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);	
		setResizable(false);
		setBounds(100,100,500,240);
		setLocationByPlatform(true);
		setLocationRelativeTo(null);
	}
	
	public ImageExplorer(HashMap<String, Integer> hashDomainTypes, HashMap<String, Integer> hashSampledValues){
		this();
		this.hashDomainTypes = hashDomainTypes;
		this.hashSampledValues = hashSampledValues;
		
		ImageTable table = new ImageTable();
		
		//scrollbar
		scroll = new JScrollPane(table);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setName("table");
		
		//button
		JButton ok = new JButton("OK"), plus = new JButton("+"), minus = new JButton("-");
		ok.addActionListener(this); plus.addActionListener(this); minus.addActionListener(this);
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.LINE_AXIS));
		p2.add(plus); p2.add(minus);
		p2.add(Box.createRigidArea(new Dimension(250, 0))); p2.add(ok);
		
		//set components 
		getContentPane().add(p2, BorderLayout.PAGE_END);
		getContentPane().add(scroll, BorderLayout.CENTER);	
			
		setVisible(true);	
	}

	//sets the datatable to the domaintype and return it
	public HashMap<String, Integer> getDomainTypes(){	
		int dimension = 3;
		for(Entry<String, ImagePlus> e : hashDomFile.entrySet()){
			if(e.getValue().getSlice() == 1) dimension = 2;
 			hashDomainTypes.put( e.getKey().toString(), dimension);	
		}
		hashDomainTypes.put("Extracellular", dimension);
		return hashDomainTypes;
	}
	
	//sets the datatable to the sampledvalue and return it
	public HashMap<String, Integer> getSampledValues(ImageTable table){
		int pixel = 255;
		int interval = 255 / hashDomFile.size();
		for(int i = 0 ; i < table.getRowCount() ; i++){
			String s = (String) table.getValueAt(i, 0);
			if(hashDomFile.containsKey(s)){
				hashSampledValues.put(s, pixel);
				pixel -= interval;
			}
		}
		hashSampledValues.put("Extracellular", 0);
		System.out.println(hashSampledValues.toString());
		return hashSampledValues;
	}

	public static void main(String[] args){	
		HashMap<String, Integer> hashDomainTypes = new HashMap<String, Integer>();
		HashMap<String, Integer> hashSampledValues = new HashMap<String, Integer>();
		new ImageExplorer(hashDomainTypes, hashSampledValues);
	}
	
	public HashMap<String, ImagePlus> getDomFile(){
		return hashDomFile;
	}
	
	public FileInfo getFileInfo(){
		return compoInfo;
	}
	
	@Override
	public  void actionPerformed(ActionEvent e) {		
		String input = e.getActionCommand();
		JViewport viewport = scroll.getViewport();
		ImageTable table = (ImageTable) viewport.getView();
		System.out.println(input);
		if(input == "+")
			table.addRow();
		
		else if(input == "-")
			table.delRow();
	
		else if(input == "OK" && table.getImgNum() > 0){
			hashDomFile = table.getHashDomFile();
			hashDomainTypes = getDomainTypes();			
			hashSampledValues = getSampledValues(table);
			setVisible(false);		
			dispose();
		} else
			new MessageDialog(new Frame(), "Error", "No Image");
			
	}

}