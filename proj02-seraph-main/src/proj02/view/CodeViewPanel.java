package proj02.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import proj02.model.Instruction;
import proj02.model.Job;
import proj02.model.Memory;
import proj02.model.Pippin;
import proj02.model.Program;

public class CodeViewPanel extends JPanel implements MouseListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	Pippin model;
	Memory mem;
	Job job;
	JScrollPane scroller;
	JTextField[] codeText;
	private int lower = -1;
	private int upper = -1;
	int previousColor = -1;
	Map<Component,Integer> ipMap;
	JobViewPanel jobView;

	public CodeViewPanel(Pippin mdl,Job job,JobViewPanel jobView) {
		super();
		model=mdl;
		this.job=job;
		this.jobView = jobView;
		mem = model.getMemory();
		int pgmSize=job.getProgramSize();
		codeText = new JTextField[pgmSize];
		ipMap=new HashMap<Component,Integer>();

		JPanel innerPanel = new JPanel();
		JPanel numPanel = new JPanel();
		JPanel textPanel = new JPanel();
		setPreferredSize(new Dimension(220,150));
		setLayout(new BorderLayout());
		Border border = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				"Code Memory View",
				TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
		setBorder(border);
		innerPanel.setLayout(new BorderLayout());
		numPanel.setLayout(new GridLayout(0,1));
		textPanel.setLayout(new GridLayout(0,1));
		int codeStart = job.getCodeStart();
		for(int i = 0; i < pgmSize; i++) {
			numPanel.add(new JLabel(String.format("[%3d] %3d.", i+codeStart,i)));
			codeText[i] = new JTextField(10);
			codeText[i].addMouseListener(this);
			ipMap.put(codeText[i], i);
			textPanel.add(codeText[i]);
		}
		innerPanel.add(numPanel, BorderLayout.LINE_START);
		innerPanel.add(textPanel, BorderLayout.CENTER);

		scroller = new JScrollPane(innerPanel);
		add(scroller);
	}

	public void loadCode(Job job) {
		this.job = job;
		this.upper=job.getCodeStart()+job.getProgramSize();
		this.lower=job.getCodeStart();
		int offset = lower;

		for(int i = offset; i < upper; i++) {
			Instruction inst = new Instruction(mem.get(i));
			codeText[i-lower].setText(inst.toString());
		}
		previousColor = model.getCpu().getInstructionPointer();
		if (previousColor >= lower && previousColor < upper) {
			codeText[previousColor-lower].setBackground(Color.YELLOW);
		}
		update();
	}

	public void clearCode() {
		for(int i = lower; 	i < upper; i++) {
			codeText[i-lower].setText("");
		}
		if(previousColor >= lower && previousColor < upper) {
			codeText[previousColor-lower].setBackground(Color.WHITE);
		}
		previousColor = -1;
		update();
	}

	public void update() {
		if(previousColor >= lower && previousColor < upper) {
			codeText[previousColor-lower].setBackground(Color.WHITE);
		}
		previousColor = model.getCpu().getInstructionPointer();
		if(previousColor >= lower && previousColor < upper) {
			codeText[previousColor-lower].setBackground(Color.YELLOW);
		}
		if(scroller != null && model!= null) {
			JScrollBar bar= scroller.getVerticalScrollBar();
			int pc = model.getCpu().getInstructionPointer();
			if(pc >= lower && pc < upper /* && codeHex[pc] != null */) {
				Rectangle bounds = codeText[pc-lower].getBounds();
				bar.setValue(Math.max(0, bounds.y - 15*bounds.height));
			}
		}
	}

	public static void main(String[] args) {
		Pippin model = new Pippin();
		Program gcdPgm = new Program("gcd");
		Job j = new Job("gcdJob", model, gcdPgm, 100);
		CodeViewPanel panel = new CodeViewPanel(model,j,null);
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 700);
		frame.setLocationRelativeTo(null);
		frame.add(panel);
		frame.setVisible(true);
		model.addJob(j);
		panel.loadCode(j);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (job.isHalted() || ! job.isLoaded()) return;
		Component c = e.getComponent();
		int ip=ipMap.get(c)+lower;
		// System.out.println("Got a mouse clicked event in code for ip " + ip);
		if (jobView != null) jobView.toggleBreakpoint(ip);
	}

	@Override public void mousePressed(MouseEvent e) { }
	@Override public void mouseReleased(MouseEvent e) { }
	@Override public void mouseEntered(MouseEvent e) { }
	@Override public void mouseExited(MouseEvent e) { }

	public void setBreakPoint(int ip) {
		codeText[ip-lower].setBackground(Color.PINK);
	}

	public void clearBreakPoint(int ip) {
		codeText[ip-lower].setBackground(Color.WHITE);
	}

}
