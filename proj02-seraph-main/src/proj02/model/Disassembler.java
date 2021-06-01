package proj02.model;

public class Disassembler {

	public void disassemble(String name) {
		Program prog = new Program(name);
		prog.print();
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Invoke with arguments for each .pexe file to disassemble");
		}
		Disassembler dasm = new Disassembler();
		for(String arg : args) {
			dasm.disassemble(arg);
		}

	}

}
