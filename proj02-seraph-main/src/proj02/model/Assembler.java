package proj02.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

public class Assembler {

	private int indexInPasmFile;
	private Program p;
	private int dataLocationIndex;
	private Map<String, Integer> variableMap; //Variable name, dataLocationIndex
	private SortedMap<Integer, String> errors;

	//Labels
	private Map<String, Integer> unresolvedJumpCalls; //Label, programIndex
	private Map<String, Integer> finishedLabelIndex;
	private int programIndex;


	public boolean assemble(String name, SortedMap<Integer, String> errors) {

		File filePath = new File("src/proj02/pasm/" + name + ".pasm");
		Scanner s;
		// Opening the .pasm file
		try {
			s = new Scanner(filePath);
		} catch (Exception e) {
			String errorOutput = "E: File " + filePath + " not found";
			// System.out.println(errorOutput); //For Debugging
			errors.put(-1, errorOutput);
			return false;
		}

		// Essentially a mini Constructor that gets run when the method is.
		// Done in the method so these get reset every time but are totally accessible
		// at all times due to heavy use otherwise
		// Could theoretically be static but this is also a viable, if non-traditional
		// method
		indexInPasmFile = 0;
		dataLocationIndex = 0;
		this.errors = errors;
		p = new Program();
		p.setName(name);
		variableMap = new HashMap<>();

		unresolvedJumpCalls = new HashMap<>();
		finishedLabelIndex = new HashMap<>();
		programIndex = 0;

		boolean passedDataLine = false;

		// While loop to read file
		while (s.hasNext()) {
			boolean lineFinished = false;
			String[] tokens = null;
			++indexInPasmFile;
			String line = s.nextLine(); // Get next line
			line = line.replaceFirst("#.*", ""); // Remove comments
			line = line.trim(); // Remove white spaces

			// Check if line is blank
			if (line.equals("")) {
				lineFinished = true;
			} else {
				tokens = line.split("\\s+"); // Split up line
				++programIndex;
			}

			// Check if it is the data line
			if (line.equals("---data---") && !lineFinished) {
				if (line.equals("---data---") && passedDataLine) {
					errors.put(indexInPasmFile,
							"E: Illegal Second data delimiter - the remainder of the file is ignored");
					s.close();
					return false;
				} else {
					passedDataLine = true;
					lineFinished = true;
				}
			}
			//Means this is a data line and must be handled as such
			else if(!lineFinished && passedDataLine) {
				handleDataLine(tokens);
				lineFinished = true;

			}

			// Check if the line is a label by itself

			if(isLabel(line) && !lineFinished) {
				handleLabel(line);
				lineFinished = true;
			}

			if(!lineFinished && cursoryOpCheck(tokens)) {
				if(!lineFinished && tokens.length > 2) { //Checking for operations on the same line as labels
					String label = tokens[0];
					if(isLabel(label)){
						handleLabel(label);
						String[] nextInstruction = new String[tokens.length-1];
						for(int i = 0; i < nextInstruction.length; ++i) {
							nextInstruction[i] = tokens[i+1];
						}
						++programIndex;
						handleInstructionLine(nextInstruction);
						lineFinished = true;
					}
				}

				// Call for a normal instructionLine
				if (!lineFinished) {
					handleInstructionLine(tokens);
					lineFinished = true;
				}
			}
		}

		//Adds all unresolved Labels to the error Map
		if(!unresolvedJumpCalls.isEmpty()) {
			for(Map.Entry<String, Integer> entry: unresolvedJumpCalls.entrySet()) {
				errors.put(entry.getValue(), "E: Line number label: " + entry.getKey() + " never defined.");
			}
		}
		// Write the executable if no errors
		if (errors.isEmpty()) {
			p.writeObject();
			s.close();
			return true;
		}

		//Prints out the errors and closes the scanner if there are any errors
		errors.toString();
		s.close();
		return false;
	}



	public void handleDataLine(String [] tokens) {

		int location = 0;
		int value = 0;
		if(tokens.length != 3) {
			errors.put(indexInPasmFile, "E: invalid data line: " + printStringArr(tokens));
			return;
		}
		//Check if First char is a valid integer or symbol
		String firstToken = tokens[0];
		String secondToken = tokens[1];
		String thirdToken = tokens[2];
		try {
			int temp = Integer.parseInt(firstToken);
			location = temp;
		}catch(Exception e) {
			if(variableMap.containsKey(firstToken)) {
				location = variableMap.get(firstToken);
			}
			else {
				errors.put(indexInPasmFile, "E: loc non-numeric in data line: " + firstToken);
				return;
			}
		}

		//Second token
		if(!secondToken.equals("=")) {
			errors.put(indexInPasmFile, "E: middle token not = in data line: " + secondToken);
		}

		//Third token
		try {
			int temp = Integer.parseInt(thirdToken);
			value = temp;
		}catch(Exception e) {
			errors.put(indexInPasmFile, "E: val non-numeric in data line: " + thirdToken);
		}

		p.addInit(location, value);
	}

	//Run into an actual label, not a JMP operation
	private void handleLabel(String label) {

		label = label.substring(0, label.length()-1);
		if(unresolvedJumpCalls.containsKey(label)) {
			int lineOfJumpCall = unresolvedJumpCalls.get(label);
			int lineOfLabel = programIndex;
			p.setArgAtLine(lineOfJumpCall-1, lineOfLabel - lineOfJumpCall);
			unresolvedJumpCalls.remove(label);
		}

		finishedLabelIndex.put(label, programIndex);
		--programIndex;
	}

	public void handleJump(String[] tokens) {

		if(finishedLabelIndex.containsKey(tokens[1])) {
			int tempLabelIndex = finishedLabelIndex.get(tokens[1]);
			p.add(new Instruction(tokens[0], "IMM", tempLabelIndex - programIndex));
		}
		else {
			p.add(new Instruction(tokens[0], "IMM", 0));
			unresolvedJumpCalls.put(tokens[1], programIndex);
		}

	}

	public void handleInstructionLine(String[] tokens) {
		int lineLength = tokens.length;
		if (tokens.length == 1 || checkIfIsNoArgInstruction(tokens[0])) {
			handleOneWordInstructionLine(tokens);

		} else if (lineLength == 2) {
			if(handleAtSymbolLine(tokens)) return; //Handled already in checker 

			else handleTwoWordInstructionLine(tokens);

		} else {
			errors.put(indexInPasmFile, "E: Illegal opcode: " + printStringArr(tokens));

		}

	}

	//Quick check to make sure nothing is horrendously wrong with the operator
	public boolean cursoryOpCheck(String[]  tokens) {
		String op = tokens[0];
		switch(op) {
		case "LOD":
		case "ADD":
		case "SUB":
		case "MUL":
		case "DIV":
		case "AND":
		case "JMP":
		case "JMZ":
		case "STO":
		case "CML":
		case "CMZ":
			try {
				String arg = tokens[1];
				return true;
			}catch(Exception nullPointerException) {
				errors.put(indexInPasmFile, "E: Missing argument specification");
				return false;
			}
		case "NOP":
		case "NOT":
		case "HLT":
			return true;


		default:
			errors.put(indexInPasmFile, "Unrecognized Operator: " + op);
			return false;
		}
	}

	// Handles lines with two words
	public void handleTwoWordInstructionLine(String[] tokens) {
		String operation = tokens[0];
		switch (operation) {
		case "LOD":
		case "ADD":
		case "SUB":
		case "MUL":
		case "DIV":
		case "AND":



			if(handleAtSymbolLine(tokens)) {
				break; //Handling is done in checker
			}else {
				p.add(new Instruction(operation, determineMode(tokens[1]), determineArg(tokens[1])));
				break;
			}

			//Always IMM
		case "JMP":
		case "JMZ":
			// Handle jump cases
			if(tokens[1].substring(0, 1).equals("@")) {
				errors.put(indexInPasmFile, "E: Invalid argument specification: " + tokens[1]);
				break;
			}
			handleJump(tokens);
			break;

			// Does not support IMM mode
		case "STO":
		case "CML":
		case "CMZ":
			if(handleAtSymbolLine(tokens)) {
				break; //Handling is done in checker
			}
			else {
				String currentMode = determineMode(tokens[1]);
				if (currentMode.equals("IMM")) {
					errors.put(indexInPasmFile, "Mode may not be IMM for operation: " + operation);
					break;
				} else {
					p.add(new Instruction(operation, "DIR", determineArg(tokens[1])));
					break;
				}
			}

		default:
			errors.put(indexInPasmFile, "Unrecognized Operator: " + operation);
		}
	}

	public void handleOneWordInstructionLine(String[] tokens) {
		if(tokens.length>1) {
			String badTokens = "";
			for(int i = 1; i<tokens.length;++i) {
				badTokens += " " + tokens[i] + ",";
			}
			errors.put(indexInPasmFile, "W: Extra tokens on no-argument instruction ignored:" + badTokens);
		}
		String firstToken = tokens[0];
		if (isLabel(firstToken)) {
			handleLabel(firstToken);
		} else {
			switch (firstToken) {
			case "NOP":
			case "NOT":
			case "HLT":
				p.add(new Instruction(firstToken, "NOM", 0));
				break;

			default:
				errors.put(indexInPasmFile, "Operation " + firstToken + " not recognized");
			}
		}
	}

	public String determineMode(String value) {
		try {
			Integer.parseInt(value);
			return "IMM";
		} catch (Exception e) {
			return "DIR";
		}
	}

	public boolean handleAtSymbolLine(String[] tokens){
		String value = tokens[1];
		if(value.length() > 2) {

			if(value.substring(0, 2).equals("@@")) { //@@nnn

				String temp = value.substring(2, value.length()-1);
				try {
					int numericArgValue = Integer.parseInt(temp);
					p.add(new Instruction(tokens[0], "IND", numericArgValue));
					return true;
				}catch(Exception e) {
					errors.put(indexInPasmFile, "E: Invalid argument specification: " + value);
					return false;
				}
			}
		}
		else if(value.length() >1) {
			if(value.substring(0, 1).equals("@")) {   // @nnn
				String temp = value.substring(1);
				if(handleAtSymbolLabel(tokens)) {return true;  //handled in checker
				}else {
					p.add(new Instruction(tokens[0], "DIR", determineArg(temp)));
					return true;
				}
			}
		}
		return false;
	}

	public boolean handleAtSymbolLabel(String[] tokens){
		String value = tokens[1].substring(1);
		try {
			int numericValue = Integer.parseInt(value);
			return false;
		}catch(Exception e) {
			p.add(new Instruction(tokens[0], "IND", determineArg(value)));
			return true;
		}
	}

	public int determineArg(String value) {

		try {
			int temp = Integer.parseInt(value);
			return temp;
		} catch (Exception e) {
			if(variableMap.containsKey(value)) {
				return variableMap.get(value); 
			}
			else {
				variableMap.put(value, dataLocationIndex);
				++dataLocationIndex;
				return variableMap.get(value);
			}
		}
	}

	public boolean checkIfIsNoArgInstruction(String token) {
		switch(token) {
		case "NOP":
		case "NOT":
		case "HLT":
			return true;
		default:
			return false;
		}
	}

	public boolean isLabel(String token) {
		if (token.endsWith(":")) {
			return true;
		}
		return false;
	}

	public String printStringArr(String[] str) {
		String total = "";
		for(String s: str) {
			total += s + " ";
		}
		return total;
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Invoke with arguments for each .pasm file to convert to .pexe");
		}
		Assembler asm = new Assembler();
		for (String arg : args) {
			SortedMap<Integer, String> errors = new TreeMap<>();
			asm.assemble(arg, errors);
			for (int ln : errors.keySet()) {
				System.out.println(String.format("%3d. : %s", ln, errors.get(ln)));
			}
			if (errors.isEmpty()) {
				System.out.println("Program " + arg + " assembled with no errors or warnings.");
			}
		}
	}

}
