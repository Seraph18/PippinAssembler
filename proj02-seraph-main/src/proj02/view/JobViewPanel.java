package proj02.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import proj02.model.CPU;
import proj02.model.Job;
import proj02.model.Memory;
import proj02.model.Pippin;
import proj02.model.Program;


public class JobViewPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private PippinGUI gui;
	private Pippin model;
	private CPU cpu;
	private Memory mem;
	private Job job;

	private ProcessorViewPanel cpuView;
	private JFrame frame;
	private CodeViewPanel codeViewPanel;
	private DataViewPanel dataViewPanel;
	JButton reloadButton;
	JButton clearButton;
	JTextField jobName;
	Map<Integer,Integer> breakPoints;

	public JobViewPanel(PippinGUI gui, Job job) {
		super();
		this.gui = gui;
		this.model = gui.getModel();
		this.job = job;
		this.cpu = model.getCpu();
		this.mem = model.getMemory();
		this.cpuView = gui.getProcessorPanel();
		this.breakPoints = new HashMap<Integer,Integer>();

		reloadButton = new JButton("Reload");
		reloadButton.setBackground(Color.WHITE);
		reloadButton.addActionListener(e -> reload());

		clearButton = new JButton("Remove");
		clearButton.setBackground(Color.WHITE);
		//clearButton.addActionListener(e -> clearJob());

		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));

		buttonPanel.add(reloadButton);
		buttonPanel.add(clearButton);

		JPanel jobNamePanel = new JPanel();
		jobNamePanel.add(new JLabel("Loaded Code "));
		jobName = new JTextField(20);
		jobName.setEditable(false);
		jobNamePanel.add(jobName);
		jobName.setText(job.getName());

		JPanel jobControl = new JPanel(new GridLayout(0,1));
		jobControl.add(buttonPanel);
		jobControl.add(jobNamePanel);

		codeViewPanel = new CodeViewPanel(model, job,this);
		dataViewPanel = new DataViewPanel(model, job);
		setLayout(new BorderLayout(1, 1));
		add(jobControl, BorderLayout.NORTH);
		add(codeViewPanel, BorderLayout.WEST);
		add(dataViewPanel, BorderLayout.CENTER);


		if(job.isLoaded()) {
			codeViewPanel.loadCode(job);
			dataViewPanel.load(job);
			jobName.setText(job.getProgramName());
		}
	}

	public void reload() {
		job.reload();
		job.swapIn();
		gui.update();
	}

	public void enableReloadClear(boolean en) {
		reloadButton.setEnabled(en);
		clearButton.setEnabled(en);
	}

	public void update() {
		codeViewPanel.update();
		dataViewPanel.update();
		cpuView.update();
		if (job.isLoaded() && !cpu.isHalted()) {
			reloadButton.setEnabled(true);
			clearButton.setEnabled(true);
		} else if (job.isLoaded()) {
			reloadButton.setEnabled(true);
			clearButton.setEnabled(true);
		} else {
			reloadButton.setEnabled(false);
			clearButton.setEnabled(false);
		}

	}



	public boolean toggleBreakpoint(int ip) {
		// Toggle break point
		if (breakPoints.containsKey(ip)) {
			mem.set(ip, breakPoints.get(ip));
			breakPoints.remove(ip);
			codeViewPanel.clearBreakPoint(ip);
			return false;

		} else {
			breakPoints.put(ip,mem.get(ip));
			mem.set(ip, -1); // Put an invalid instruction at that memory
			codeViewPanel.setBreakPoint(ip);
			return true;
		}
	}

	public boolean atBreakpoint() {
		int ip=cpu.getInstructionPointer();
		if (!breakPoints.containsKey(ip)) return false;
		JOptionPane.showMessageDialog(frame, "Breakpoint at " + ip + " reached", ""
				+ "Run time error", JOptionPane.OK_OPTION);
		mem.set(ip, breakPoints.get(ip));
		breakPoints.remove(ip);
		codeViewPanel.clearBreakPoint(ip);
		cpu.setHalted(false);
		return false;
	}

	public void resetBreakPoints() {
		for(int ip : breakPoints.keySet()) {
			codeViewPanel.setBreakPoint(ip);
		}
	}



	public static void main(String[] args) {
		Pippin model = new Pippin();
		Program gcdPgm = new Program("gcd");
		Job job = new Job("gcd",model,gcdPgm,10);
		model.addJob(job);
		job.swapIn();
		PippinGUI gui = new PippinGUI();
		gui.setModel(model);
		JobViewPanel panel = new JobViewPanel(gui,job);
		JFrame frame = new JFrame("TEST JOBVIEW");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 700);
		frame.setLocationRelativeTo(null);
		frame.add(panel);
		frame.setVisible(true);
		panel.update();
	}

	public Job getJob() { return job; }
}
