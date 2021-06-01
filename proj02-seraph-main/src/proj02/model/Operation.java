package proj02.model;

public enum Operation {
	NOP {
		public boolean isModeValid(Mode mode) {
			return mode==Mode.NOM;
		}
		public String execute(CPU cpu, Instruction inst) {
			return "";
		}
	},
	LOD {
		public boolean isModeValid(Mode mode) {
			return isModeIMMorDIR(mode);
		}
		public String execute(CPU cpu, Instruction inst) {
			cpu.setAccumulator(inst.fetchOperand(cpu));
			return " acc=" + cpu.getAccumulator();
		}
	},
	STO {
		public boolean isModeValid(Mode mode) {
			return (mode==Mode.DIR || mode==Mode.IND);
		}
		public String execute(CPU cpu, Instruction inst) {
			int target=inst.getArgument();
			if (inst.getMode()==Mode.IND) target = cpu.getData(target);
			cpu.setData(target,cpu.getAccumulator());
			return " data[" + target + "]=" + cpu.getAccumulator();
		}
	},
	ADD {
		public boolean isModeValid(Mode mode) {
			return isModeIMMorDIR(mode);
		}
		public String execute(CPU cpu, Instruction inst) {
			cpu.setAccumulator(inst.fetchOperand(cpu)+cpu.getAccumulator());
			return " acc=" + cpu.getAccumulator();
		}
	},
	SUB {
		public boolean isModeValid(Mode mode) {
			return isModeIMMorDIR(mode);
		}
		public String execute(CPU cpu, Instruction inst) {
			cpu.setAccumulator(cpu.getAccumulator()-inst.fetchOperand(cpu));
			return " acc=" + cpu.getAccumulator();
		}
	},
	MUL {
		public boolean isModeValid(Mode mode) {
			return isModeIMMorDIR(mode);
		}
		public String execute(CPU cpu, Instruction inst) {
			cpu.setAccumulator(cpu.getAccumulator()*inst.fetchOperand(cpu));
			return " acc=" + cpu.getAccumulator();
		}
	},
	DIV {
		public boolean isModeValid(Mode mode) {
			return isModeIMMorDIR(mode);
		}
		public String execute(CPU cpu, Instruction inst) {
			if (inst.fetchOperand(cpu)==0) {
				System.out.println("Attempted to Divide by Zero ignored");
				return " ERROR: divide by zero!";
			}
			cpu.setAccumulator(cpu.getAccumulator()/inst.fetchOperand(cpu));
			return " acc=" + cpu.getAccumulator();
		}
	},
	AND {
		public boolean isModeValid(Mode mode) {
			return isModeIMMorDIR(mode);
		}
		public String execute(CPU cpu, Instruction inst) {
			int newValue = ((inst.fetchOperand(cpu) != 0) && (cpu.getAccumulator() != 0) ? 1 : 0);
			cpu.setAccumulator(newValue);
			return " acc=" + cpu.getAccumulator();
		}
	},
	NOT {
		public boolean isModeValid(Mode mode) {
			return mode==Mode.NOM;
		}
		public String execute(CPU cpu, Instruction inst) {
			int newValue = ((cpu.getAccumulator() != 0) ? 0 : 1);
			cpu.setAccumulator(newValue);
			return " acc=" + cpu.getAccumulator();
		}
	},
	CML {
		public boolean isModeValid(Mode mode) {
			return (mode==Mode.DIR || mode==Mode.IND);
		}
		public String execute(CPU cpu, Instruction inst) {
			int newValue=(inst.fetchOperand(cpu)<0) ? 1 : 0;
			cpu.setAccumulator(newValue);
			return " acc=" + cpu.getAccumulator();
		}
	},
	CMZ {
		public boolean isModeValid(Mode mode) {
			return (mode==Mode.DIR || mode==Mode.IND);
		}
		public String execute(CPU cpu, Instruction inst) {
			int newValue=(inst.fetchOperand(cpu)==0) ? 1 : 0;
			cpu.setAccumulator(newValue);
			return " acc=" + cpu.getAccumulator();
		}
	},
	JMP {
		public boolean isModeValid(Mode mode) {
			return (mode==Mode.IMM || mode==Mode.DIR);
		}
		public String execute(CPU cpu, Instruction inst) {
			int currentIp=cpu.getInstructionPointer()-1;
			cpu.setInstructionPointer(currentIp + inst.fetchOperand(cpu));
			return " instructionPointer = " + cpu.getInstructionPointer();
		}
	},
	JMZ {
		public boolean isModeValid(Mode mode) {
			return (mode==Mode.IMM || mode==Mode.DIR);
		}

		public String execute(CPU cpu, Instruction inst) {
			if (cpu.getAccumulator()==0) {
				int currentIp=cpu.getInstructionPointer()-1;
				cpu.setInstructionPointer(currentIp + inst.fetchOperand(cpu));
			}
			return " instructionPointer = " + cpu.getInstructionPointer();
		}
	},
	HLT {
		public boolean isModeValid(Mode mode) {
			return mode==Mode.NOM;
		}
		public String execute(CPU cpu, Instruction inst) {
			cpu.setHalted(true);
			return " Program halted";
		}
	};

	public abstract boolean isModeValid(Mode mode);
	public abstract String execute(CPU cpu, Instruction inst);

	boolean isModeIMMorDIR(Mode mode) {
		if (mode==Mode.IMM || mode==Mode.DIR || mode==Mode.IND) return true;
		return false;
	}

}
