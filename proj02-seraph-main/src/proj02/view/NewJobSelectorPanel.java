package proj02.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import proj02.model.Pippin;

public class NewJobSelectorPanel extends JPanel {


	private JFrame frame;
	private GridLayout layout;
	private PippinGUI parentGUI;
	private String[] listOfPexeFiles;
	private HashMap<String, String> fileDescriptions;
	private HashMap<String, JTextArea> textAreaMap;


	public NewJobSelectorPanel(PippinGUI parent, JFrame frame) {

		this.parentGUI = parent;
		this.frame = frame;
		loadJob();
		setUpPanel();
		frame.setVisible(true);

	}

	public void loadJob() {
		//Gets executable names
		File ldir = new File(Pippin.PEXEPATH);
		List<String> programs = new ArrayList<String>();
		for(String file : ldir.list()) {
			if (file.endsWith(".pexe")) programs.add(file.replace(".pexe", ""));
		}
		String[] progList = programs.toArray(new String[0]);
		this.listOfPexeFiles = progList;
	}

	public void setUpPanel() {

		layout = new GridLayout(0,2);
		this.setLayout(layout);

		this.fileDescriptions = new HashMap<String, String>(); //File name, description

		//Get Previously saved descriptions
		try {
			File savedDescriptions = new File("src/proj02/view/saveFile.txt");

			if(savedDescriptions.createNewFile()) {
				System.out.println("New description file created");
				initializeNewSaveFile(savedDescriptions);
				readDescriptionFile(savedDescriptions);
				this.textAreaMap = populateGUIDescriptions(savedDescriptions);
			}
			else {
				System.out.println("Description file already exists");
				readDescriptionFile(savedDescriptions);
				this.textAreaMap = populateGUIDescriptions(savedDescriptions);
			}
			JButton cancelButton = new JButton("Save and Exit");
			cancelButton.addActionListener(e -> saveAndClose(savedDescriptions));
			this.add(cancelButton);
		}
		catch(Exception e) {}


	}

	private void initializeNewSaveFile(File saveFile) {
		try {
			FileWriter editor = new FileWriter(saveFile);
			for(int i = 0; i < this.listOfPexeFiles.length; ++i) {
				editor.write("-" + this.listOfPexeFiles[i] + "\n\n");
			}
			editor.close();
		}catch(Exception e) {
			System.out.println("New file failed to initialize");
		}
	}

	public void saveAndClose(File saveFile) {
		saveChanges(saveFile, textAreaMap);
		this.frame.dispose();
	}

	public void saveChanges(File saveFile, HashMap<String, JTextArea> textAreaMap) {

		try {
			FileWriter editor = new FileWriter(saveFile);

			for(Map.Entry<String, JTextArea> entry : textAreaMap.entrySet()) {

				editor.write("-" + entry.getKey() + "\n");
				editor.write(entry.getValue().getText() + "\n");

			}
			editor.close();
		}catch(Exception e) { System.out.println("Error saving description changes");}
	}

	public HashMap<String, JTextArea> populateGUIDescriptions(File saveFile) {

		HashMap<String, JTextArea> textBoxMap = new HashMap<>();

		Border blackline = BorderFactory.createLineBorder(Color.black);

		for(Map.Entry<String, String> entry : this.fileDescriptions.entrySet()) {

			JButton programButton = new JButton(entry.getKey());
			programButton.addActionListener(e -> pressProgramButton(programButton.getText(), saveFile));
			this.add(programButton);
			JTextArea desc = new JTextArea();
			desc.setText(entry.getValue());
			desc.setBorder(blackline);
			this.add(desc);
			textBoxMap.put(entry.getKey(), desc);
		}

		//Check for present exe files with no description saved
		if(fileDescriptions.size() < listOfPexeFiles.length) {

			for(String fileName: listOfPexeFiles) {

				if(!fileDescriptions.containsKey(fileName)) {
					JButton programButton = new JButton(fileName);
					programButton.addActionListener(e -> pressProgramButton(programButton.getText(), saveFile));
					this.add(programButton);
					JTextArea desc = new JTextArea();
					this.add(desc);
					textBoxMap.put(fileName, desc);
				}

			}
		}
		return textBoxMap;
	}

	private void pressProgramButton(String programName, File saveFile) {

		saveChanges(saveFile, textAreaMap);
		parentGUI.loadJob(programName);
	}

	public void readDescriptionFile(File textFile) {

		try {
			Scanner reader = new Scanner(textFile);
			boolean foundHeader = false;
			String programName = "";
			String description = "";

			while(reader.hasNextLine()) {
				String line = reader.nextLine();
				if(!line.trim().equals("")) {
					if(line.substring(0, 1).equals("-")){ //Looks for marker
						
						if(foundHeader) {//Check if already reading a description
							this.fileDescriptions.put(programName, description);
							description = "";
						}
						programName = line.replace("-", ""); 

						List<String> tempList = new ArrayList<String>();
						tempList = List.of(listOfPexeFiles); 
						if(tempList.contains(programName)) { //Converts to list and then checks if the executable is present

							foundHeader = true;

						}
						else {
							foundHeader = false; //If not it gets set to false and the program ignores non-relevant descriptions
						}
					}
					else if(foundHeader) {
						description += line;
					}
				}
			}

			this.fileDescriptions.put(programName, description);
			reader.close();
		}catch(Exception e) {
			System.out.println("Error Reading description file");
		}

	}
}

