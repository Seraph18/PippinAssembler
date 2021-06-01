package proj02.model;

/**
 * Simulate a Pippin CPU.
 *
 * A Pippin CPU consists of:
 * <ul>
 * <li>a reference to a Memory object,
 * <li>three internal integer registers: an instructionPointer, an accumulator, and a dataMemoryBase,
 * <li>a single flag called "halted",
 * <li>the logic to execute Pippin instructions in an ALU to manipulate the registers, flags, and memory
 * </ul>
 * @author cs140
 */
public class CPU {

	private int accumulator;
	private int instructionPointer;
	private int dataMemoryBase;
	private Memory memory;
	private boolean halted;
	private Job currentJob;

	/**
	 * Constructor to initialize CPU
	 * @param memory the memory to be used by the CPU
	 */
	public CPU(Memory memory) {
		accumulator=0; // Not required, but included for clarity
		dataMemoryBase=0;
		instructionPointer=0;
		this.memory=memory;
		halted=true;
	}

	/**
	 * Run the current program by executing instructions until halted
	 */
	public void run() {
		while(!halted) execute();
	}

	/**
	 * Run the current program at most numInstructions instructions until halted
	 * @param numInstructions
	 */
	public void run(int numInstructions) {
		while(!halted && numInstructions>0) {
			execute();
			numInstructions--;
		}
	}

	/**
	 * Execute a single Pippin Instruction
	 */
	public void execute() {

		if (halted) return;

		// Fetch instruction from memory
		if (instructionPointer < currentJob.getCodeStart()) {
			throw new PippinMemoryException("InstructionPointer " + instructionPointer +
					" is lower than the code start location " + currentJob.getCodeStart());
		}
		if (instructionPointer > currentJob.getCodeStart() + currentJob.getProgramSize()) {
			throw new PippinMemoryException("InstructionPointer " + instructionPointer +
					" is greater than the code end location " + currentJob.getCodeStart() + currentJob.getProgramSize());
		}
		Instruction inst = new Instruction(memory.get(instructionPointer));
		instructionPointer++;

		if (inst.isValid()) inst.execute(this);
		else {
			halted=true;
			instructionPointer--;
			throw new PippinMemoryException("Invalid Instruction Encounterred");
		}
		if (halted) instructionPointer--; // reset to failing instruction
	}

	/**
	 * @return the dataMemoryBase
	 */
	public int getDataMemoryBase() { return dataMemoryBase; }

	/**
	 * Set the value of the dataMemoryBase register.
	 * @param dataMemoryBase new value
	 */
	public void setDataMemoryBase(int dataMemoryBase) {
		this.dataMemoryBase = dataMemoryBase;
	}

	/**
	 * Get the memory object this CPU is using.
	 * @return A reference to the Memory object this CPU is using
	 */
	public Memory getMemory() { return memory; }



	/**
	 * Get the instructionPointer register value
	 * @return the instructionPointer value
	 */
	public int getInstructionPointer() { return instructionPointer; }

	/**
	 * Set the instructionPointer register.
	 * @param instructionPointer new value
	 */
	public void setInstructionPointer(int instructionPointer) {
		this.instructionPointer = instructionPointer;
	}

	/**
	 * Get the value in the data part of the memory at the specified location.
	 * @param loc index into the data part of the memory (offset from the dataMemoryBase)
	 * @return the value in memory at the specified location
	 */
	public int getData(int loc) {
		if (loc >= currentJob.getDataSize()) {
			throw new PippinMemoryException("Illegal data address " + loc +
					" larger than data size " + currentJob.getDataSize());
		}
		return memory.get(loc+dataMemoryBase);
	}

	/**
	 * Set the value in the data part of the memory at the specified location.
	 * @param loc index into the data part of the memory (offset from the dataMemoryBase)
	 * @param value new value to write into the specified location in data memory
	 */
	public void setData(int loc,int value) {
		if (loc > currentJob.getDataSize()) {
			throw new PippinMemoryException("Illegal data address " + loc +
					" larger than data size " + currentJob.getDataSize());
		}
		memory.set(loc+dataMemoryBase, value);
	}


	/**
	 * Get the accumulator register value
	 * @return the accumulator value
	 */
	public int getAccumulator() { return accumulator; }

	/**
	 * Update the accumulator register value
	 * @param accumulator the accumulator to set
	 */
	public void setAccumulator(int accumulator) {
		this.accumulator = accumulator;
	}

	/**
	 * Set the halted flag to the specified value.
	 * @param halted new value for the halted flag
	 */
	public void setHalted(boolean halted) { this.halted = halted; }

	/**
	 * @return the halted flag
	 */
	public boolean isHalted() { return halted; }

	/**
	 * @param currentJob the currentJob to set
	 */
	public void setCurrentJob(Job currentJob) { this.currentJob = currentJob; }

	/**
	 * @return the currentJob
	 */
	public Job getCurrentJob() { return currentJob; }
}
