public class Main {
	static FileOfRegisters registerFile;
	static Register pc;
	static Memory memory;
	static PipelineRegisterFetchDecode pipelineRegisterFetchDecode;
	static PipelineRegisterDecodeExcute pipelineRegisterDecodeExcute;
	static PipelineRegisterExecuteMemory pipelineRegisterExecuteMemory;
	static PipelineRegisterMemoryWriteBack pipelineRegisterMemoryWriteBack;
	static boolean isJump;
	static boolean finishDecode = false;
	static boolean finishExecute = false;
	static boolean finishMemory = false;
	static boolean finishWriteBack = false;
	static int clockcycle = 1;
	static boolean fetch = false;

	public Main() {
		registerFile = new FileOfRegisters();
		pc = registerFile.get(registerFile.size() - 1);
		memory = new Memory();
		pipelineRegisterFetchDecode = new PipelineRegisterFetchDecode();
		pipelineRegisterDecodeExcute = new PipelineRegisterDecodeExcute();
		pipelineRegisterExecuteMemory = new PipelineRegisterExecuteMemory();
		pipelineRegisterMemoryWriteBack = new PipelineRegisterMemoryWriteBack();
		isJump = false;
	}

	public static void start() {
		while (true) {
			if (pc.getValue() > memory.counter)
				break;

			if (clockcycle % 2 == 1) {//odd
				fetch();
			}
			if (finishDecode) {
				execute();
				finishDecode = false;
			}
			if (finishExecute) {
				memory();
				finishExecute = false;
			}
			if (finishMemory) {
				writeBack();
				finishMemory = false;
			}
			if (finishWriteBack) {
				fetch();
				finishWriteBack = false;
			}
			clockcycle++;
		}
	}

	public static void fetch() {
		int instruction = 0;

		// Complete the fetch() body...
		instruction = memory.memory[pc.getValue()];
		pipelineRegisterFetchDecode.setInstructionLine(instruction);
		pipelineRegisterFetchDecode.setPc(pc.getValue() + 1);
		pc.setValue(pc.getValue() + 1);
		if (pc.getValue() > memory.counter)
			return;
		// decode();

		// Complete the fetch() body...
	}

	public static void decode() {
		clockcycle++;
		decodeHelper();
	}

	public static void decodeHelper() {
		int instruction = pipelineRegisterFetchDecode.getInstructionLine();
		int opcode = -1; // bits31:28
		int r1 = -1; // bits27:23
		int r2 = -1; // bit22:18
		int r3 = -1; // bits17:13
		int shamt = -1; // bits12:0
		int imm = -1; // bits17:0
		int address = -1; // bits27:0

		// Complete the decode() body...
		int instructiontmp;
		int mask;
		mask = 0b11110000000000000000000000000000;
		instructiontmp = instruction & mask;
		opcode = instructiontmp >>> 28;

		switch (opcode) {
			case 0:
			case 1:
			case 2:
			case 5:
			case 8:
			case 9:
				// ---------------------------------------------
				mask = 0b00001111100000000000000000000000;
				instructiontmp = instruction & mask;
				r1 = instructiontmp >>> 23;
				// ---------------------------------------------
				mask = 0b00000000011111000000000000000000;
				instructiontmp = instruction & mask;
				r2 = instructiontmp >>> 18;
				// ---------------------------------------------
				mask = 0b00000000000000111110000000000000;
				instructiontmp = instruction & mask;
				r3 = instructiontmp >>> 13;
				// ---------------------------------------------
				mask = 0b00000000000000000001111111111111;
				instructiontmp = instruction & mask;
				shamt = instructiontmp;
				// ---------------------------------------------
				break;

			case 3:
			case 4:
			case 6:
			case 10:
			case 11:
				mask = 0b00001111100000000000000000000000;
				instructiontmp = instruction & mask;
				r1 = instructiontmp >>> 23;
				// ---------------------------------------------
				mask = 0b00000000011111000000000000000000;
				instructiontmp = instruction & mask;
				r2 = instructiontmp >>> 18;
				// ---------------------------------------------
				mask = 0b00000000000000111111111111111111;
				imm = instruction & mask;
				break;

			case 7:
				mask = 0b00001111111111111111111111111111;
				address = instruction & mask;
				isJump = true;
				break;

		}
		// Instruction inst = new Instruction(opcode, r1, r2, r3, shamt, imm, address);
		// pipelineRegisterDecodeExcute.add(inst);
		// execute(inst);
		pipelineRegisterDecodeExcute.setOpcode(opcode);
		if (!isJump) {
			Register r1tmp = registerFile.get(r1);
			Register r2tmp = registerFile.get(r2);
			Register r3tmp = registerFile.get(r3);
			pipelineRegisterDecodeExcute.setR1(r1tmp);
			pipelineRegisterDecodeExcute.setR2(r2tmp);
			pipelineRegisterDecodeExcute.setR3(r3tmp);
		}
		pipelineRegisterDecodeExcute.setShamt(shamt);
		pipelineRegisterDecodeExcute.setImm(imm);
		pipelineRegisterDecodeExcute.setAddress(address);
		finishDecode = true;
		clockcycle++;
		// execute();
	}

	public static void execute() {
		clockcycle++;
		executeHelper();
	}

	public static void executeHelper() {
		int opcode = pipelineRegisterDecodeExcute.getOpcode();
		Register r1 = pipelineRegisterDecodeExcute.getR1();
		Register r2 = pipelineRegisterDecodeExcute.getR2();
		Register r3 = pipelineRegisterDecodeExcute.getR3();
		int shamt = pipelineRegisterDecodeExcute.getShamt();
		int imm = pipelineRegisterDecodeExcute.getImm();
		int address = pipelineRegisterDecodeExcute.getAddress();
		pipelineRegisterExecuteMemory.setR1(r1);

		switch (opcode) {
			case 0:
				// add
				pipelineRegisterExecuteMemory.setR1Value(r2.getValue() + r3.getValue());
				// pipelineRegisterDecodeExcute.setR1(registerFile.get(r1).getValue());
				break;
			case 1:
				// sub
				pipelineRegisterExecuteMemory.setR1Value(r2.getValue() - r3.getValue());
				// pipelineRegisterDecodeExcute.setR1(registerFile.get(r1).getValue());
				break;
			case 2:
				// mul
				pipelineRegisterExecuteMemory.setR1Value(r2.getValue() * r3.getValue());
				// pipelineRegisterDecodeExcute.setR1(registerFile.get(r1).getValue());
				break;
			case 3:
				// MOVI
				pipelineRegisterExecuteMemory.setR1Value(imm);
				// pipelineRegisterDecodeExcute.get(index).setR1(registerFile.get(r1).getValue());
				break;
			case 4:
				// JEQ
				if (r1.getValue() == r2.getValue()) {
					pipelineRegisterExecuteMemory.setPc(pc.getValue() + 1 + address);
					isJump = true;
					// pc.setValue(pc.getValue() + 1 + address);
				}
				break;
			case 5:
				// and
				pipelineRegisterExecuteMemory.setR1Value(r2.getValue() & r3.getValue());
				// pipelineRegisterDecodeExcute.get(index).setR1(registerFile.get(r1).getValue());
				break;
			case 6:
				// xori
				pipelineRegisterExecuteMemory.setR1Value(r2.getValue() ^ imm);
				// pipelineRegisterDecodeExcute.get(index).setR1(registerFile.get(r1).getValue());
				break;
			case 7:
				// JMP
				int temp = (pc.getValue() & 0b1111000000000000000000000000000) >> 28;
				String tmp = temp + "" + address;
				pipelineRegisterExecuteMemory.setPc(Integer.parseInt(tmp));
				break;
			case 8:
				// LSL
				pipelineRegisterExecuteMemory.setR1Value(r2.getValue() << shamt);
				// pipelineRegisterDecodeExcute.get(index).setR1(registerFile.get(r1).getValue());
				break;

			case 9:
				// LSR
				pipelineRegisterExecuteMemory.setR1Value(r2.getValue() >>> shamt);
				// pipelineRegisterDecodeExcute.get(index).setR1(registerFile.get(r1).getValue());
				break;

			case 10:
				// MOVR
				// registerFile.get(r1).setValue(memory.memory[registerFile.get(r2).getValue()+
				// imm]);
				address = r2.getValue() + imm;
				pipelineRegisterExecuteMemory.setAddress(address);
				break;
			case 11:
				// MOVM
				address = r2.getValue() + imm;
				pipelineRegisterExecuteMemory.setAddress(address);
				break;

		}
		pipelineRegisterExecuteMemory.setOpcode(opcode);
		clockcycle++;
		// pipelineRegisterExecuteMemory.add(pipelineRegisterDecodeExcute.get(index));
		// memory();
	}

	public static void memory() {
		// int index = pipelineRegisterExecuteMemory.indexOf(inst);
		int opcode = pipelineRegisterExecuteMemory.getOpcode();
		Register r1 = pipelineRegisterExecuteMemory.getR1();
		int address = pipelineRegisterExecuteMemory.getAddress();
		boolean isLoad = false;
		boolean isStore = false;
		pipelineRegisterMemoryWriteBack.setR1(r1);
		switch (opcode) {
			case 10:
				isLoad = true;
				break;
			case 11:
				isStore = true;
				break;
		}
		if (isLoad) {
			// int index = registerFile.indexOf(r1);
			// registerFile.get(index).setValue(address);
			r1.setValue(memory.memory[address + 1024]);
			pipelineRegisterMemoryWriteBack.setR1Value(r1.getValue());
		} else if (isStore) {
			memory.memory[address + 1024] = r1.getValue();
			pipelineRegisterMemoryWriteBack.setR1Value(-1);
		}
		// pipelineRegisterMemoryWriteBack.add(pipelineRegisterExecuteMemory.get(index));
		if (isJump) {
			isJump = false;
			fetch();
		}
		// writeBack();
		clockcycle++;
	}

	public static void writeBack() {
		// int index = pipelineRegisterExecuteMemory.indexOf(inst);
		Register r1 = pipelineRegisterMemoryWriteBack.getR1();
		if (r1.getValue() != -1) {
			int index = registerFile.indexOf(r1);
			registerFile.get(index).setValue(r1.getValue());
		}
		// fetch();
		clockcycle++;
	}

	public static void main(String[] args) {
		Main main = new Main();
		registerFile.get(2).setValue(4);
		registerFile.get(3).setValue(1);
		registerFile.get(5).setValue(8);
		registerFile.get(6).setValue(1);
		int totalNoOfInstructions = 7 + ((memory.counter - 1) * 2);
		int countOfInstructions = memory.counter;
		for (int i = 1; i <= totalNoOfInstructions; i++) {
			if (i % 2 != 0) {
				fetch();
			}

			if (!finishDecode) {
				decode();
			}

		}

		System.out.println(registerFile.get(1).getValue());
		System.out.println(registerFile.get(4).getValue());
	}

}
