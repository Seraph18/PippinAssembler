package proj02.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * Model a Pippin Program - a list of Pippin instructions.
 * @author cs140
 */
public class Program {

	private List<Instruction> program;
	private Map<Integer,Integer> initializations;
	private String name;

	/**
	 * Constructor - creates a new empty program (no instructions).
	 */
	public Program() {
		program=new ArrayList<Instruction>();
		initializations=new HashMap<Integer,Integer>();
		this.name = "?";
	}

	/**
	 * Constructor - creates a program from an object file
	 * @param objFileName
	 */
	public Program(String name) {
		this(); // Instantiate the array list
		this.name=name;
		String objName = Pippin.PEXEPATH + name + ".pexe";
		File objFile = new File(objName);
		try(Scanner objScan = new Scanner(objFile)) {
			while(objScan.hasNextInt()) {
				int instr = objScan.nextInt();
				add(new Instruction(instr));
			}
			objScan.nextLine(); // Get to the end of the last line
			if (objScan.hasNextLine() && objScan.nextLine().equals("---init---")) {
				while( objScan.hasNextLine() ) {
					objScan.findInLine("(\\d+)\\=(\\d+)");
					MatchResult result = objScan.match();
					int loc = Integer.parseInt(result.group(1));
					int val = Integer.parseInt(result.group(2));
					addInit(loc,val);
					objScan.nextLine();
				}
			}
			Trace.message("Program read from " + objName);
		} catch (FileNotFoundException e) {
			System.out.println("Object file " + objName + " not found.");
		}
	}

	/**
	 * @return the name
	 */
	public String getName() { return name; }

	/**
	 * @param name the name to set
	 */
	public void setName(String name) { this.name = name; }

	/**
	 * Adds a new instruction to the program.
	 * @param e an instruction
	 * @return true if instruction was added, false otherwise
	 */
	public boolean add(Instruction e) { return program.add(e); }

	public void addInit(int loc,int value) {
		initializations.put(loc, value);
	}

	public void setArgAtLine(int ln, int arg) {
		program.get(ln).setArgument(arg);
	}

	/**
	 * Remove all instructions from the program, leaving it empty.
	 */
	public void clear() {
		program.clear();
		initializations.clear();
		name="?";
	}

	/**
	 * Get the size of the program.
	 * @return number of instructions in the program
	 */
	public int size() { return program.size(); }

	/**
	 * Load the program into the CPU.
	 * <ul>
	 * <li>Store instructions into memory, starting at location 0
	 * <li>Set the CPU's instructionCounter to 0 (beginning of the program)
	 * <li>Set the CPU's dataMemoryBase to last instruction loaded + 1
	 * </ul>
	 * @param cpu CPU object in which to load this program
	 */
	public void load(CPU cpu) {
		load(cpu,0); // By default, load the program at location 0
	}

	/**
	 * Load the program into the CPU.
	 * <ul>
	 * <li>Store instructions into memory, starting at location specified in the parameter
	 * <li>Set the CPU's instructionCounter to 0 (beginning of the program)
	 * </ul>
	 * @param cpu CPU object in which to load this program
	 */
	public void load(CPU cpu,int instructionCounter) {
		cpu.setInstructionPointer(instructionCounter);
		Memory mem = cpu.getMemory();
		for(Instruction inst : program) {
			inst.store(mem,instructionCounter);
			instructionCounter ++;
		}
		loadData(mem,instructionCounter);
	}

	public void loadData(Memory mem,int dataMemoryBase) {
		for(int loc : initializations.keySet()) {
			mem.set(loc+dataMemoryBase, initializations.get(loc));
		}
	}

	public void writeObject() {
		String objFileName = Pippin.PEXEPATH + name + ".pexe";
		File objFile = new File(objFileName);
		try {
			objFile.createNewFile(); // Will create the file if it's not there
		} catch (IOException e1) {
			System.out.println("Unable to create output file: " + objFileName + " : " + e1.getMessage());
			return;
		}
		try(PrintStream objStream = new PrintStream(objFile)) {
			for(Instruction inst : program) {
				objStream.println(inst.toInt());
			}
			if (!initializations.isEmpty()) {
				objStream.println("---init---");
				for(int loc : initializations.keySet()) {
					objStream.println(loc + "=" + initializations.get(loc));
				}
			}
			Trace.message("Program Object Code written to " + objFileName);
		} catch (FileNotFoundException e) {
			System.out.println("Should never get here.");
			e.printStackTrace();
		}
	}

	public void print() {
		System.out.println("Program: " + name);
		for(Instruction inst : program) {
			System.out.println("  " + inst);
		}
		System.out.println(" Data Initializations:");
		for(int loc : initializations.keySet()) {
			System.out.println("   " + loc + " = " + initializations.get(loc));
		}
	}

	public static void main(String[] args) {

		// Save the GCD program
		Trace.startTrace();
		Program prog = new Program();
		prog.setName("gcd");
		prog.add(new Instruction("CMZ","DIR",1)); // b==0
		prog.add(new Instruction("NOT","NOM",0)); // Invert for JMZ
		prog.add(new Instruction("JMZ","IMM",17)); // if (b==0) goto finished = 18
		prog.add(new Instruction("LOD","DIR",0)); // acc=a
		prog.add(new Instruction("SUB","DIR",1)); // acc=a-b
		prog.add(new Instruction("STO","DIR",2)); // temp=a-b
		prog.add(new Instruction("CML","DIR",2)); // if ((a-b)<0) or b>a
		prog.add(new Instruction("NOT","NOM",0)); // Invert for JMZ
		prog.add(new Instruction("JMZ","IMM",7)); // if (b>a) goto end-of-if = 13
		prog.add(new Instruction("LOD","DIR",0)); // acc=a
		prog.add(new Instruction("STO","DIR",2)); // temp=acc=a
		prog.add(new Instruction("LOD","DIR",1)); // acc=b
		prog.add(new Instruction("STO","DIR",0)); // a=acc=b
		prog.add(new Instruction("LOD","DIR",2)); // acc=temp
		prog.add(new Instruction("STO","DIR",1)); // b=temp
		prog.add(new Instruction("LOD","DIR",1)); // acc=b
		prog.add(new Instruction("SUB","DIR",0)); // acc=b-a
		prog.add(new Instruction("STO","DIR",1)); // b=b-a
		prog.add(new Instruction("JMP","IMM",-18)); // goto start=0
		prog.add(new Instruction("HLT","NOM",0));
		prog.addInit(0, 10);
		prog.addInit(1, 45);

		prog.writeObject();
	}

}
