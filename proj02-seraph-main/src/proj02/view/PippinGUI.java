package proj02.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import proj02.model.Assembler;
import proj02.model.CPU;
import proj02.model.Instruction;
import proj02.model.Job;
import proj02.model.Memory;
import proj02.model.Pippin;
import proj02.model.PippinMemoryException;
import proj02.model.Program;

public class PippinGUI {

	private Pippin model;
	private JFrame frame;
	private ProcessorViewPanel processorPanel;
	private JTabbedPane jobsPane;
	private JButton loadButton;
	private JButton addAssemble;
	private JButton stepButton;
	private JButton runButton;
	private JButton executeButton;
	private int jobCount;
	private TimerControl stepControl;
	private JobViewPanel currentJobView;
	private CPU cpu;


	public void setModel(Pippin m) {
		model = m;
		cpu=model.getCpu();
		stepControl = new TimerControl(this);
	}



	/**
	 * @return the model
	 */
	public Pippin getModel() { return model; }



	/**
	 * @return the processorPanel
	 */
	public ProcessorViewPanel getProcessorPanel() { return processorPanel; }



	public void changeToJob(int i) {
		if (i>=model.getJobs().size()) return;
		if (i==0) return; // Don't change to the help panel
		model.getCpu().getCurrentJob().swapOut();
		model.getJobs().get(i).swapIn();
		processorPanel.update();
		currentJobView=(JobViewPanel)jobsPane.getComponentAt(i);
		currentJobView.update();
	}

	private void createAndShowGUI() {
		processorPanel = new ProcessorViewPanel(model.getCpu());
		frame = new JFrame("Pippin Simulator");

		jobsPane = new JTabbedPane();
		List<Job> jobs = model.getJobs();
		for (Job job : jobs) {
			jobsPane.add("Job " + job.getName(), new JobViewPanel(this, job));
		}
		JTextArea helpText = new JTextArea();
		helpText.setLineWrap(true);
		helpText.setEditable(false);
		helpText.append("\tThe Pippin Simulator\n\n"
				+ "To get started, use Load... or Assemble and Load... button to load a new job.\n\n"

				+ "The \"Assemble and\" button presents a list of .pasm files in the pasm subdirectory, and allows you to select one. "
				+ "That .pasm file will get assembled, and then loaded into a new job.\n\n"

				+ "The \"Load...\" button will present a list of .pexe files in the pexe subdirectory and allow you to select one. "
				+ "That .pexe file will get loaded into a new job.\n\n"

				+ "In both cases, you will also be asked to specify the data size required for the job. "
				+ "Most jobs need only a few data slots, but some jobs, like the ramTest job, need more.\n\n"

				+ "New jobs can be added as long as the CPU has memory to hold them. Select a tab to switch to another job.\n\n"

				+ "The right hand panel is the CPU control panel. It has three control buttons and a slider."
				+ "The control buttons are:\n"
				+ "\t- Step : to execute a single instruction in the current job.\n"
				+ "\t- Run/Pause : toggles the auto-run mode. When in auto-run mode (see below).\n"
				+ "\t- Execute : Runs the program until it stops, then updates the display.\n\n"

				+ "Auto-Run mode executes a single instruction, updates the display, then executes the next instruction. "
				+ "The speed of auto-run is managed by the slider. Move it higher to run faster, lower to run slower. "
				+ "Auto-Run mode continues until one of the following occurs:\n"
				+ "\t- The Run/Pause button is pressed again.\n"
				+ "\t- The program halts\n"
				+ "\t- The program hits an exception such as divide by zero or a memory error\n"
				+ "\t- The program reaches a breakpoint (see Breakpoints below).\n\n"

				+ "The Code Memory View panel shows the loaded program...\n"
				+ "\t- The instruction that will execute next is highlighted in yellow.\n"
				+ "\t- Click on an instruction to set a breakpoint (red). Before a breakpoint is executed, processing stops, a popup is presented, and the breakpoint is cleared.\n\n"

				+ "The Data Memory View shows the data memory...\n"
				+ "\t- Data which has changed since the last display are highlighted.\n\n"

				+ "The CPU-> information bar shows the current status of the CPU...\n"
				+ "\t- Job : Shows which job is currently running.\n"
				+ "\t- Accumulator : Current value of the accumulator register\n"
				+ "\t- Instruction Pointer : Current value of the instruction pointer register.\n"
				+ "\t- Memory Base : Current value of the memory base register.\n\n"
				+ "Click on the X button to exit (a confirmation popup will appear)."
				);
		helpText.setCaretPosition(0);
		JScrollPane scroller = new JScrollPane(helpText);
		jobsPane.add("Help",scroller);
		int helpIndex=jobsPane.indexOfTab("Help");
		jobsPane.setBackgroundAt(helpIndex,Color.DARK_GRAY);
		jobsPane.setForegroundAt(helpIndex,Color.WHITE);

		jobsPane.addChangeListener(e -> changeToJob(jobsPane.getSelectedIndex()));

		// JButton assembleButton = new JButton("Assemble...");
		// assembleButton.setBackground(Color.WHITE);
		// assembleButton.addActionListener(e -> filesMgr.assembleFile());

		// JButton exitButton = new JButton("Exit");
		// exitButton.setBackground(Color.WHITE);
		// exitButton.addActionListener(e -> exit());

		JPanel buttonPanel = new JPanel();
		// buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		// buttonPanel.add(Box.createVerticalGlue());
		// buttonPanel.add(assembleButton);
		// buttonPanel.add(Box.createRigidArea(new Dimension(10, 10)));
		// buttonPanel.add(exitButton);
		// buttonPanel.add(Box.createVerticalGlue());
		// Define Buttons
		loadButton = new JButton("Load...");
		// loadButton.setMinimumSize(new Dimension(100,0));
		loadButton.setPreferredSize(new Dimension(100,30));
		loadButton.setHorizontalAlignment(SwingConstants.RIGHT);
		addAssemble = new JButton("Assemble and");
		addAssemble.setBackground(Color.WHITE);
		loadButton.add(addAssemble);
		loadButton.setBackground(Color.WHITE);
		loadButton.addActionListener(e -> loadJob());
		addAssemble.addActionListener(e-> assembleAndLoadJob());
		buttonPanel.add(new JLabel("New Job: "));
		buttonPanel.add(addAssemble);
		buttonPanel.add(loadButton);
		


		stepButton = new JButton("Step");
		stepButton.setBackground(Color.WHITE);
		stepButton.addActionListener(e -> step());

		runButton = new JButton("Run/Pause");
		runButton.setBackground(Color.WHITE);
		runButton.addActionListener(e -> toggleAutoStep());
		stepControl.start();

		executeButton = new JButton("Execute");
		executeButton.setBackground(Color.WHITE);
		executeButton.addActionListener(e -> execute());

		JPanel actionButtonPanel = new JPanel(new GridLayout(0, 1));
		actionButtonPanel.add(stepButton);
		actionButtonPanel.add(runButton);
		actionButtonPanel.add(executeButton);

		JPanel actionPanel = new JPanel(new GridLayout(1, 0));
		actionPanel.add(actionButtonPanel);

		JSlider slider = new JSlider(SwingConstants.VERTICAL, 0, 1000,500);
		slider.addChangeListener(e -> stepControl.setPeriod(1000 - slider.getValue()));

		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("src/proj02/view/turtle.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Image dimg = img.getScaledInstance(40, 30, Image.SCALE_SMOOTH);
		JLabel turtleLabel = new JLabel();
		ImageIcon imageIcon = new ImageIcon(dimg);
		turtleLabel.setIcon(imageIcon);
		turtleLabel.setHorizontalAlignment(JLabel.CENTER);

		try {
			img = ImageIO.read(new File("src/proj02/view/rabbit.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		dimg = img.getScaledInstance(40, 30, Image.SCALE_SMOOTH);
		JLabel rabbitLabel = new JLabel();
		imageIcon = new ImageIcon(dimg);
		rabbitLabel.setIcon(imageIcon);
		rabbitLabel.setHorizontalAlignment(JLabel.CENTER);

		JPanel sliderPanel = new JPanel(new BorderLayout(1, 1));
		sliderPanel.add(turtleLabel, BorderLayout.SOUTH);
		sliderPanel.add(slider, BorderLayout.CENTER);
		sliderPanel.add(rabbitLabel, BorderLayout.NORTH);
		actionPanel.add(sliderPanel);

		//add(actionPanel, BorderLayout.SOUTH);

		frame.setSize(700, 600);
		frame.add(buttonPanel, BorderLayout.NORTH);
		frame.add(actionPanel,BorderLayout.EAST);
		frame.add(jobsPane, BorderLayout.CENTER);
		frame.add(processorPanel, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(WindowListenerFactory.windowClosingFactory(e -> exit()));
		frame.setLocationRelativeTo(null);
		processorPanel.update();
		// ((JobViewPanel)jobsPane.getComponentAt(0)).update();
		frame.setVisible(true);
		// scroller.getViewport().setViewPosition(new Point(0,0));
		update();
	}

	public void exit() { // method executed when user exits the program
		int decision = JOptionPane.showConfirmDialog(frame, "Do you really wish to exit?", "Confirmation",
				JOptionPane.YES_NO_OPTION);
		if (decision == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
	
	public void loadJob() {
		JFrame x = new JFrame("Select Job");
		x.setSize(500, 500);
		x.add(new NewJobSelectorPanel(this, x));
        x.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		update();
	}

	/*
	public void loadJob() {
		File ldir = new File(Pippin.PEXEPATH);
		List<String> programs = new ArrayList<String>();
		for(String file : ldir.list()) {
			if (file.endsWith(".pexe")) programs.add(file.replace(".pexe", ""));
		}
		String[] progList = programs.toArray(new String[0]);
		String program = (String) JOptionPane.showInputDialog(null, "Choose a program...",
		        "Choose a program:", JOptionPane.QUESTION_MESSAGE, null, // Use
		                                                                        // default
		                                                                        // icon
		        progList, // Array of choices
		        progList[0]); // Initial choice
		if (program != null) loadJob(program);
	}
	*/

	public void loadJob(String pgmName) {
		jobCount++;
		String jobName = "Job " + jobCount;
		Program pgm = new Program(pgmName);
		String csize = JOptionPane.showInputDialog("Enter Job Data Size...");
		int dsize = 10;
		if (csize != null) try {
			dsize=Integer.parseInt(csize);
		} catch (NumberFormatException e) {
			// Ignore invalid input
		}
 		Job job = new Job(jobName,model,pgm,dsize);
 		try {
 			model.addJob(job);
 		} catch (PippinMemoryException e) {
 			JOptionPane.showMessageDialog(frame, "Not enough memory to add job " + jobName,
 					"Run time error", JOptionPane.OK_OPTION);
 			return;
 		}
		job.swapIn();
		JobViewPanel jobPanel = new JobViewPanel(this, job);
		jobsPane.add(jobName, jobPanel);
		jobsPane.setSelectedIndex(jobCount);
		currentJobView = jobPanel;
		update();
	}

	public void assembleAndLoadJob() {
		File ldir = new File(Pippin.PASMPATH);
		List<String> programs = new ArrayList<String>();
		for(String file : ldir.list()) {
			if (file.endsWith(".pasm")) programs.add(file.replace(".pasm", ""));
		}
		String[] progList = programs.toArray(new String[0]);
		String pgm = (String) JOptionPane.showInputDialog(null, "Choose a program...",
		        "Choose a program to assemble:", JOptionPane.QUESTION_MESSAGE, null,
		        progList, // Array of choices
		        progList[0]); // Initial choice
		Assembler asm = new Assembler();

		if (pgm != null) {
			SortedMap<Integer,String> errors = new TreeMap<>();
			boolean assembled = asm.assemble(pgm, errors);
			if (!errors.isEmpty()) {
				StringBuilder sb = new StringBuilder(pgm + " has one or more errors\n");
				for(Integer key : errors.keySet()) {
					sb.append(String.format("%3d.", key));sb.append(errors.get(key)); sb.append("\n");
				}
				JOptionPane.showMessageDialog(
					null,
					sb.toString(),
					"Source code error(s)",
					JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(
						null, // null,
						"Program " + pgm + " was assembled to an executable with no errors",
						"Success",
						JOptionPane.INFORMATION_MESSAGE);
			}
			if (assembled) loadJob(pgm);
		}
	}

	
	
	
	public void step() {
		stepModel();
		update();
	}

	public boolean stepModel() {
		Memory mem = cpu.getMemory();
		if (cpu.isHalted()) return false;
		Instruction currentInstruction=new Instruction(mem.get(cpu.getInstructionPointer()));
		try {
			cpu.execute();
		} catch (PippinMemoryException e) {
			stepControl.setAutoStep(false);
			if (mem.get(cpu.getInstructionPointer())==-1) return currentJobView.atBreakpoint();
			else {
				JOptionPane.showMessageDialog(frame, "Pippin Memory Exception executing " + currentInstruction
				+ "\n" + "Exception message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
			}
			return false;
		} catch (NullPointerException e) {
			stepControl.setAutoStep(false);
			JOptionPane.showMessageDialog(frame, "NullPointerException from line " + currentInstruction + "\n"
					+ "Exception message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
			return false;
		} catch (IllegalArgumentException e) {
			stepControl.setAutoStep(false);
			JOptionPane.showMessageDialog(frame, "Program Error from line " + currentInstruction + "\n"
					+ "Exception message: " + e.getMessage(), "Run time error", JOptionPane.OK_OPTION);
			return false;
		}
		if (cpu.isHalted()) {
			stepControl.setAutoStep(false);
			return false;
		}
		return true;
	}

	public void execute() {
		while (stepModel()) { }
		update();
	}

	public void toggleAutoStep() {
		stepControl.toggleAutoStep();
		update();
	}

	public void update() {
			if (currentJobView==null) { // Nothing loaded
				stepButton.setEnabled(false);
				runButton.setEnabled(false);
				executeButton.setEnabled(false);
				return;
			}
			currentJobView.update();
			currentJobView.enableReloadClear(!stepControl.isAutoStepOn());
			if (stepControl.isAutoStepOn()) {
				stepButton.setEnabled(false);
				runButton.setEnabled(true);
				executeButton.setEnabled(false);
			} else if (!cpu.isHalted()) {
				stepButton.setEnabled(true);
				runButton.setEnabled(true);
				executeButton.setEnabled(true);
			} else {
				stepButton.setEnabled(false);
				runButton.setEnabled(false);
				executeButton.setEnabled(false);
			}
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(() -> {
			PippinGUI organizer = new PippinGUI();
			Pippin model = new Pippin();
			organizer.setModel(model);
			organizer.createAndShowGUI();
		});
	}

}
