package proj02.view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import proj02.model.Job;
import proj02.model.Pippin;
import proj02.model.Program;

public class DataViewPanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Pippin model;
	Job job;
	private JScrollPane scroller;
	private JTextField[] dataDecimal;
	private int[] prevValue;
	private int upper = -1;

	public DataViewPanel(Pippin model, Job job) {
		super();
		this.model = model;
		model.getMemory();
		this.upper = job.getDataSize();
		this.job=job;
		prevValue=new int[upper];

		JPanel innerPanel = new JPanel();
		JPanel numPanel = new JPanel();
		JPanel decimalPanel = new JPanel();
		setLayout(new BorderLayout());
		int db=job.getDataMemoryBase();
		Border border = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				"Data Memory View [" + db + "-" + (db+upper) + "]",
				TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
		setBorder(border);
		innerPanel.setLayout(new BorderLayout());
		numPanel.setLayout(new GridLayout(0,1));
		decimalPanel.setLayout(new GridLayout(0,1));
		innerPanel.add(numPanel, BorderLayout.LINE_START);
		innerPanel.add(decimalPanel, BorderLayout.CENTER);
		dataDecimal = new JTextField[upper];
		for(int i = 0; i < upper; i++) {
			numPanel.add(new JLabel(String.format("[%3d] %3d.", i+db, i)));
			dataDecimal[i] = new JTextField(10);
			dataDecimal[i].setHorizontalAlignment(JTextField.RIGHT);
			decimalPanel.add(dataDecimal[i]);
			prevValue[i]=0;
		}
		scroller = new JScrollPane(innerPanel);
		add(scroller);
	}

	public void clear() {
		// Assumes data memory is cleared independently
		for(int i=0; i<upper; i++) {
			if (!dataDecimal[i].getText().equals("")) {
				dataDecimal[i].setText("");
				dataDecimal[i].setBackground(Color.WHITE);
				prevValue[i]=-1;
			} else break;
		}
	}

	public void load(Job job) {
		this.job=job;
		for(int i=0;i<upper; i++) {
			int val=job.getData(i);
			prevValue[i]=val;
			if (val==0) dataDecimal[i].setText("0");
			else dataDecimal[i].setText("" + val);
		}
	}

	public void update() {
		int firstChange=-1;
		for(int i = 0; i < upper; i++) {
			int newVal=job.getData(i);
			if (newVal==prevValue[i]) {
				dataDecimal[i].setBackground(Color.WHITE);
			} else {
				if (newVal==0) dataDecimal[i].setText("0");
				else dataDecimal[i].setText("" + newVal);
				dataDecimal[i].setBackground(Color.YELLOW);
				prevValue[i]=newVal;
				if (firstChange==-1) firstChange=i;
			}
		}

		if(scroller != null && model != null) {
			JScrollBar bar= scroller.getVerticalScrollBar();
			if (firstChange!=-1) {
				Rectangle bounds = dataDecimal[firstChange].getBounds();
				bar.setValue(Math.max(0, bounds.y - 15*bounds.height));
			}
		}
	}

	public static void main(String[] args) {
		Pippin model = new Pippin();
		Program gcdPgm = new Program("gcd");
		Job j = new Job("gcdJob", model, gcdPgm, 5);
		DataViewPanel panel = new DataViewPanel(model, j);
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 700);
		frame.setLocationRelativeTo(null);
		frame.add(panel);
		frame.setVisible(true);
		model.addJob(j);
		panel.load(j);
	}
}
