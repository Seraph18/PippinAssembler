package proj02.view;

import javax.swing.Timer;

public class TimerControl {
	static final int TICK = 500;
	boolean autoStepOn = false;
	Timer timer;
	PippinGUI gui;

	public TimerControl(PippinGUI gm) {
		gui=gm;
	}

	/**
	 * @return the autoStepOn
	 */
	public boolean isAutoStepOn() {
		return autoStepOn;
	}

	/**
	 * @param autoStepOn the autoStepOn to set
	 */
	public void setAutoStep(boolean autoStepOn) {
		this.autoStepOn = autoStepOn;
	}

	public void toggleAutoStep() {
		autoStepOn=!autoStepOn;
	}

	void setPeriod(int period) {
		timer.setDelay(period);
	}

	public void start() {
		timer = new Timer(TICK, e-> {if(autoStepOn) gui.step();});
		timer.start();
	}

}
