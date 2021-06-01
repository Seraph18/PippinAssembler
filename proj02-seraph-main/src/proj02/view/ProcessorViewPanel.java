package proj02.view;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import proj02.model.CPU;
import proj02.model.Pippin;

public class ProcessorViewPanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	CPU cpu;
	// JTextField job = new JTextField();
	JTextField acc = new JTextField();
	JTextField ip = new JTextField();
	JTextField base = new JTextField();
	/**
	 * @param cpu
	 */
	public ProcessorViewPanel(CPU cpu) {
		super(new GridLayout(1,0));
		this.cpu = cpu;

		add(new JLabel("CPU->", JLabel.LEFT));
		// add(new JLabel("Job:",JLabel.RIGHT));
		// add(job);
		add(new JLabel("Accumulator: ", JLabel.RIGHT));
		add(acc);
		add(new JLabel("Instruction Pointer: ", JLabel.RIGHT));
		add(ip);
		add(new JLabel("Data Memory Base: ", JLabel.RIGHT));
		add(base);
	}

	public void update() {
		if (cpu!= null) {
			// job.setText("" + cpu.getCurrentJob().getId());
			acc.setText("" + cpu.getAccumulator());
			ip.setText("" + cpu.getInstructionPointer());
			base.setText("" + cpu.getDataMemoryBase());
		}
	}

	public static void main(String[] args) { // Test to make sure class works
		Pippin model = new Pippin();
		ProcessorViewPanel panel = new ProcessorViewPanel(model.getCpu());
		JFrame frame = new JFrame("TEST Processor View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 60);
		frame.setLocationRelativeTo(null);
		frame.add(panel);
		frame.setVisible(true);
		panel.update();
	}

}
