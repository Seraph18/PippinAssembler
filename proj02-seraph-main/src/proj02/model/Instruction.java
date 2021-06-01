package proj02.model;

/**
 * Model a single Pippin Instruction.
 *
 * A Pippin Instruction consists of:
 * <ol>
 * <li>An operation - the kind of operation that the CPU should perform
 * <li>A mode - How the instruction argument should be interpreted
 * <li>An argument - an integer value
 * </ol>
 *
 * @author cs140
 */
public class Instruction {

	// Fields
	private Operation opcode;
	private Mode mode;
	private int argument;

	/**
	 * Construct an instruction object using the opcode, mode, and argument parameters.
	 * @param opcode index into the opNames array for this operation
	 * @param mode index into the modeNames array to specify the mode for this instruction
	 * @param argument the value of the argument to be used.
	 */
	public Instruction(Operation opcode,Mode mode,int argument) {
		this.opcode = opcode;
		this.mode = mode;
		this.argument = argument;
	}

	/**
	 * Generate an Instruction object from the parameters.
	 * If the operation is not recognized, returns a NOP Instruction.
	 * @param opName operation name
	 * @param modeName mode name
	 * @param argument argument value
	 * @return a reference to an Instruction object
	 */
	public Instruction (String opName,String modeName,int argument) {
		this(findOpcode(opName),findMode(modeName),argument);
	}

	/**
	 * Generate an Instruction object from the parameters.
	 * If the operation is not recognized, returns an NOP Instruction
	 * @param binInstr binary encoded instruction
	 * @return a reference to an Instruction object
	 */
	public Instruction(int binInstr) {
		this(getEncodedOpcode(binInstr),getEncodedMode(binInstr),getEncodedArg(binInstr));
	}

	/**
	 * Store this instruction in the specified memory at the specified location.
	 *
	 * The store method will write over whatever used to be in memory with the
	 * binary encoded instruction.
	 *
	 * @param mem The memory in which to store the instruction
	 * @param loc the location (index or address) where the instruction will be stored.
	 */
	public void store(Memory mem,int loc) {
		mem.set(loc,encodeOpModeArg(opcode,mode,argument));
	}

	/**
	 * get the three character operation name (mnemonic) for this instruction.
	 * @return the operation name
	 */
	public String getOpName() {
		return opcode.name(); }

	/**
	 * get the opcode for this instruction
	 * @return opcode
	 */
	public Operation getOpcode() { return opcode; }

	/**
	 * get the three character mode mnemonic for this instruction.
	 * @return the mode name (mnemonic)
	 */
	public String getModeName() {
		return mode.name();
	}

	/**
	 * get the mode value from this instruction.
	 * @return the mode value
	 */
	public Mode getMode() { return mode; }

	/**
	 * get the argument value from this instruction.
	 * @return the argument value
	 */
	public int getArgument() { return argument; }

	/**
	 * @param argument the argument to set
	 */
	public void setArgument(int argument) { this.argument = argument; }

	/**
	 * Fetch the value of the operand, based on the mode, for this instruction
	 * @param cpu The Pippin CPU to use when resolving the operand
	 * @return the resolved operand.
	 */
	public int fetchOperand(CPU cpu) {
		// Note... override this method for special opcodes (like STO)
		if (mode==Mode.IMM) return argument;
		if (mode==Mode.DIR) return cpu.getData(argument);
		if (mode==Mode.IND) return cpu.getData(cpu.getData(argument));
		System.out.println("fetchOperand encounterred something unexpected.");
		return 0;
	}

	/**
	 * Determine if everything in this instruction is valid.
	 *
	 * Checks include:
	 * <ul>
	 * <li>Does the instruction have a valid op-code
	 * <li>Does the instruction have a valid mode, and is that mode consistent with the opcode
	 * </ul>
	 * @return true if this instruction is valid, false otherwise.
	 */
	public boolean isValid() {
		if (opcode==null) {
			System.out.println("Invalid instruction, Invalid opcode: " + this);
			return false;
		}
		if (!opcode.isModeValid(mode)) {
			System.out.println("Invalid instruction, mode: " + this);
			return false;
		}
		if (opcode==Operation.NOP && mode==Mode.NOM && argument!=0) return false;
		return true;
	}

	/**
	 * Determine if the mode for this instruction is valid for the current operation.
	 *
	 * Actual implementation is in the InstructionXXX sub-classes.
	 * @return true if the mode is valid, false otherwise
	 */
	public boolean isModeValid() {
		throw new UnsupportedOperationException("Dont know about modes for a general instruction");
	}

	/**
	 * Determine if this mode is either IMM or DIR.
	 * @return true if mode is either IMM or DIR, false otherwise
	 */
	public boolean isModeIMMorDIR() {
		if (mode==findMode("IMM")) return true;
		if (mode==findMode("DIR")) return true;
		return false;
	}

	/**
	 * execute this instruction.
	 *
	 * The actual implementation is in each InstructionXXX sub-class
	 * @param cpu A Pippin CPU to use when executing this instruction
	 */
	public void execute(CPU cpu) {
		Trace.message(this + opcode.execute(cpu,this));
	}

	/**
	 * Find the index of the parameter in the opNames array.
	 * @param name the operation name
	 * @return the index of the operation in the opNames array.
	 * Returns -1 if the operation is not in the opNames array.
	 */
	public static Operation findOpcode(String name) {
	   return Operation.valueOf(name);
	}

	/**
	 * Find the index of the parameter in the modeNames array.
	 * @param name the mode name
	 * @return the index of the mode in the modeNames array.
	 * Returns -1 if the mode is not in the modeNames array.
	 */
	public static Mode findMode(String name) {
		return Mode.valueOf(name);
	}

	/**
	 * Generate binary encoded instruction from the parameters.
	 * @param opcode index into the opNames array
	 * @param mode index into the modeNames array
	 * @param arg argument value
	 * @return binar encoded instruction
	 */
	public static int encodeOpModeArg(Operation opcode,Mode mode,int arg) {
		int opMode= (opcode.ordinal()*10 + mode.ordinal()) << 24;
		int binInst = opMode | (arg & 0xFFFFFF);
		return binInst;
	}

	/**
	 * Extract and return the opcode from the binary encoded instruction.
	 * @param binInstr binary encoded instruction
	 * @return index into the opNames array extracted from the parameter
	 */
	public static Operation getEncodedOpcode(int binInstr) {
		int opMode=(binInstr>>24) & 0xFF; // Get last 8 bits
		int opNum=opMode/10;
		if (opNum < Operation.values().length) return Operation.values()[opNum];
		return Operation.NOP;
	}

	/**
	 * Extract and return the mode from the binary encoded instruction.
	 * @param binInstr binary encoded instruction
	 * @return index into the modeNames array extracted from the parameter
	 */
	public static Mode getEncodedMode(int binInstr) {
		int opMode=(binInstr>>24) & 0xFF;
		int mode = opMode % 10; // Remainder operation discards the opcode
		if (mode < Mode.values().length) return Mode.values()[mode];
		return Mode.NOM;
	}

	/**
	 * Extract and return the argument from the binary encoded instruction.
	 * @param binInstr binary encoded instruction
	 * @return argument value extracted from the parameter
	 */
	public static int getEncodedArg(int binInstr) {
		int arg = binInstr<<8; // Shift off leftmost 8 bits
		arg = arg>>8; // And shift back, propagating sign bit
		return arg;
	}

	@Override
	public String toString() {
		return getOpName() + " mode:" + getModeName()
				+ ", arg:" + argument;
	}

	/**
	 * convert this instruction to an object code integer
	 * @return the encoded integer representation of this instruction
	 */
	public int toInt() {
		return encodeOpModeArg(opcode,mode,argument);
	}

}