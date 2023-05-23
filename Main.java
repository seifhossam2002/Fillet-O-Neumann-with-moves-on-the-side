public class Main {
	static FileOfRegisters registerFile;
	static Register pc;
	static Memory memory;
	static PipelineRegisterFetchDecode pipelineRegisterFetchDecode;
	static PipelineRegisterDecodeExcute pipelineRegisterDecodeExcute;
	static PipelineRegisterExecuteMemory pipelineRegisterExecuteMemory;
	static PipelineRegisterMemoryWriteBack pipelineRegisterMemoryWriteBack;
	static boolean isJump;
	static boolean finishFetch;
	static boolean finishDecode;
	static boolean finishExecute;
	static boolean finishMemory;
	static boolean finishWriteBack;

	// static int clockcycle = 1;

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
		int totalNoOfInstructions = 7 + ((memory.counter - 1) * 2);
		System.out.println("Total number of instructions: " + totalNoOfInstructions);
		// fetch , write back always odd
		// memory always even
		boolean enteredfetch = false;
		int uppperfordecode = (memory.counter * 2) + 1;
		int uppperforexecute = (memory.counter * 2) + 3;
		int returnfromdecode = 0;
		Object[] returnfromexecute = new Object[7];
		for (int clockcycle = 1; clockcycle <= totalNoOfInstructions; clockcycle++) {
			if (clockcycle > 3 && clockcycle % 2 == 0 && clockcycle < uppperforexecute) {// even
				returnfromexecute = execute();
			}
			if (clockcycle > 1 && clockcycle % 2 == 0 && finishFetch && clockcycle < uppperfordecode) {// even
				returnfromdecode = decode();
			}
			if (clockcycle % 2 == 1) {// odd
				fetch();
				enteredfetch = true;
			} else {
				enteredfetch = false;
			}

			if (clockcycle > 1 && clockcycle % 2 == 1 && finishFetch && clockcycle <= uppperfordecode) {// odd
				decodeHelper(returnfromdecode);
			}
			if (enteredfetch) {
				finishFetch = true;
			}

			if (clockcycle > 3 && clockcycle % 2 == 1 && clockcycle <= uppperforexecute) {// odd
				executeHelper(returnfromexecute);
			}
			if (clockcycle > 5 && clockcycle % 2 == 0) {// even
				memory();
			}
			if (clockcycle > 6 && clockcycle % 2 == 1) {// odd
				writeBack();
			}
		}

		// while (true) {
		// if (pc.getValue() > memory.counter)
		// break;

		// if (clockcycle % 2 == 1) {//odd
		// fetch();
		// }
		// }
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

	}

	public static int decode() {
		// clockcycle++;
		// decodeHelper();
		return pipelineRegisterFetchDecode.getInstructionLine();
	}

	public static void decodeHelper(int instruction) {

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
			if (r2 != -1) {
				Register r2tmp = registerFile.get(r2);
				pipelineRegisterDecodeExcute.setR2(r2tmp);
			}

			if (r3 != -1) {
				Register r3tmp = registerFile.get(r3);
				pipelineRegisterDecodeExcute.setR3(r3tmp);
			}
			pipelineRegisterDecodeExcute.setR1(r1tmp);

			
		}
		pipelineRegisterDecodeExcute.setShamt(shamt);
		pipelineRegisterDecodeExcute.setImm(imm);
		pipelineRegisterDecodeExcute.setAddress(address);
		finishDecode = true;
		// clockcycle++;
		// execute();
	}

	public static Object[] execute() {
		// clockcycle++;
		// executeHelper();
		int opcode = pipelineRegisterDecodeExcute.getOpcode();
		Register r1 = pipelineRegisterDecodeExcute.getR1();
		Register r2 = pipelineRegisterDecodeExcute.getR2();
		Register r3 = pipelineRegisterDecodeExcute.getR3();
		int shamt = pipelineRegisterDecodeExcute.getShamt();
		int imm = pipelineRegisterDecodeExcute.getImm();
		int address = pipelineRegisterDecodeExcute.getAddress();
		Object[] tmp = new Object[7];
		tmp[0] = opcode;
		tmp[1] = r1;
		tmp[2] = r2;
		tmp[3] = r3;
		tmp[4] = shamt;
		tmp[5] = imm;
		tmp[6] = address;
		return tmp;
	}

	public static void executeHelper(Object[] tmpp) {
		int opcode = (int) tmpp[0];
		Register r1 = (Register) tmpp[1];
		Register r2 = (Register) tmpp[2];
		Register r3 = (Register) tmpp[3];
		int shamt = (int) tmpp[4];
		int imm = (int) tmpp[5];
		int address = (int) tmpp[6];
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
		// clockcycle++;
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
		// clockcycle++;
	}

	public static void writeBack() {
		// int index = pipelineRegisterExecuteMemory.indexOf(inst);
		Register r1 = pipelineRegisterMemoryWriteBack.getR1();
		if (r1.getValue() != -1) {
			int index = registerFile.indexOf(r1);
			registerFile.get(index).setValue(r1.getValue());
		}
		// fetch();
		// clockcycle++;
	}

	public static void main(String[] args) {
		Main main = new Main();
		// registerFile.get(2).setValue(4);
		// registerFile.get(3).setValue(1);
		// registerFile.get(5).setValue(8);
		// registerFile.get(6).setValue(1);
		// registerFile.get(7).setValue(2);
		// registerFile.get(8).setValue(3);
		// registerFile.get(9).setValue(4);
		// registerFile.get(10).setValue(5);
		// registerFile.get(11).setValue(6);
		// registerFile.get(12).setValue(7);
		// registerFile.get(13).setValue(8);
		// registerFile.get(14).setValue(9);
		// registerFile.get(15).setValue(10);
		// registerFile.get(16).setValue(11);
		// registerFile.get(17).setValue(12);
		// registerFile.get(18).setValue(13);
		// registerFile.get(19).setValue(14);
		// registerFile.get(20).setValue(15);
		// registerFile.get(21).setValue(16);
		start();

		// System.out.println(registerFile.get(1).getValue());
		// System.out.println(registerFile.get(4).getValue());
		System.out.println(registerFile.get(8).getValue());
		// System.out.println(registerFile.get(10).getValue());
		// System.out.println(registerFile.get(13).getValue());
		// System.out.println(registerFile.get(16).getValue());
		// System.out.println(registerFile.get(19).getValue());
	}

}
