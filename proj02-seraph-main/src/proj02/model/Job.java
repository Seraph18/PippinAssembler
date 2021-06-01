package proj02.model;

public class Job {
	// CPU state of the current job
	// Note: Assumes there is a shared memory
	private int accumulator;
	private int instructionPointer;
	private int dataMemoryBase;
	private boolean halted;

	// Extra information about the current job
	private String name;
	private CPU cpu;
	private Program pgm;
	private int codeStart;
	private int dataSize;
	private boolean loaded=false;

	public Job(String name,Pippin model,Program pgm,int dataSize) {
		this.name=name;
		this.cpu=model.getCpu();
		this.pgm=pgm;
		this.dataSize=dataSize;
	}

	public void load(int codeStart) {
		if (loaded) {
			throw new PippinMemoryException("Job " + name + " cannot be loaded twice.");
		}
		this.codeStart = codeStart;
		pgm.load(cpu,codeStart);
		// Define the initial cpu state for when this gets loaded
		instructionPointer=codeStart;
		accumulator=0;
		dataMemoryBase=codeStart + pgm.size();
		halted=false;
		loaded=true;
	}

	public void reload() {
		if (!loaded) {
			throw new PippinMemoryException("Job " + name + " cannot be reloaded if not loaded.");
		}
		instructionPointer=codeStart;
		halted=false;
		accumulator=0;
		pgm.loadData(cpu.getMemory(), dataMemoryBase);
	}

	public void initData(int loc,int value) {
		if (!loaded) {
			throw new PippinMemoryException("Job " + name + " cannot initialize data before program is loaded");
		}
		if (loc>dataSize) {
			throw new PippinMemoryException("Job " + name + " cannot initialize data memory larger than data size");
		}
		cpu.getMemory().set(dataMemoryBase+loc, value);
	}

	public int getData(int loc) {
		if (!loaded) {
			throw new PippinMemoryException("Job " + name + " cannot retrieve data before program is loaded");
		}
		if (loc>dataSize) {
			throw new PippinMemoryException("Job " + name + " cannot retrieve data memory larger than data size");
		}
		return cpu.getMemory().get(dataMemoryBase+loc);
	}

	public void swapOut() {
		if (!loaded) {
			throw new PippinMemoryException("Job " + name + " cannot be swapped out before it is loaded");
		}
		accumulator=cpu.getAccumulator();
		instructionPointer=cpu.getInstructionPointer();
		dataMemoryBase = cpu.getDataMemoryBase();
		halted = cpu.isHalted();
		cpu.setCurrentJob(null);
		Trace.message("Job " + name + " swapped out.");
	}

	public void swapIn() {
		if (!loaded) {
			throw new PippinMemoryException("Job " + name + " cannot be swapped in before it is loaded");
		}
		cpu.setAccumulator(accumulator);
		cpu.setInstructionPointer(instructionPointer);
		cpu.setDataMemoryBase(dataMemoryBase);
		cpu.setHalted(halted);
		cpu.setCurrentJob(this);
		Trace.message("Job " + name + " swapped in");
	}

	/**
	 * @return the dataSize
	 */
	public int getDataSize() { return dataSize; }

	public int getProgramSize() { return pgm.size(); }

	public int getJobSize() {
		return pgm.size() + dataSize;
	}

	/**
	 * @return the codeStart
	 */
	public int getCodeStart() {
		if (!loaded) throw new PippinMemoryException("Job " + name + " cannot get the code start before the job is loaded.");
		return codeStart;
	}

	public int getJobEndMemoryLoc() {
		if (!loaded) throw new PippinMemoryException("Job " + name + " cannot get the code start before the job is loaded.");
		return codeStart + pgm.size() + dataSize;
	}

	/**
	 * @return the accumulator
	 */
	public int getAccumulator() { return accumulator; }

	/**
	 * @return the name
	 */
	public String getName() { return name; }

	public boolean isHalted() { return halted; }

	/**
	 * @return the loaded
	 */
	public boolean isLoaded() { return loaded; }

	/**
	 * @param loaded the loaded to set
	 */
	public void setLoaded(boolean loaded) { this.loaded = loaded; }

	/**
	 * @return the dataMemoryBase
	 */
	public int getDataMemoryBase() { return dataMemoryBase; }

	public String getProgramName() { return pgm.getName(); }



}
