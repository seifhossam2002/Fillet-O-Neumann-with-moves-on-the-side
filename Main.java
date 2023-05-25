import java.util.Hashtable;

public class Main {
	static FileOfRegisters registerFile;
	static Register pc;
	static Memory memory;
	static PipelineRegisterFetchDecode pipelineRegisterFetchDecode;
	static PipelineRegisterDecodeExcute pipelineRegisterDecodeExcute;
	static PipelineRegisterExecuteMemory pipelineRegisterExecuteMemory;
	static PipelineRegisterMemoryWriteBack pipelineRegisterMemoryWriteBack;
	static boolean canEnterFetch = true;
	static boolean canEnterDecode = true;
	static boolean canEnterExecute = true;
	static boolean canEnterMemory = true;
	static boolean canEnterWriteBack = true;
	static boolean clear = false;
	static Hashtable<Integer, Boolean> isJEQ;

	// static int clockcycle = 1;

	public Main() {
		registerFile = new FileOfRegisters();
		pc = registerFile.get(registerFile.size() - 1);
		memory = new Memory();
		pipelineRegisterFetchDecode = new PipelineRegisterFetchDecode();
		pipelineRegisterDecodeExcute = new PipelineRegisterDecodeExcute();
		pipelineRegisterExecuteMemory = new PipelineRegisterExecuteMemory();
		pipelineRegisterMemoryWriteBack = new PipelineRegisterMemoryWriteBack();
		isJEQ = new Hashtable<>();
	}

	public static void start() {
		int totalNoOfInstructions = 7 + ((memory.counter - 1) * 2);
		System.out.println("Total number of instructions: " + totalNoOfInstructions);
		// fetch , write back always odd
		// memory always even
		int counterDecode = 0;
		int counterExecute = 0;
		for (int clockcycle = 1; clockcycle <= totalNoOfInstructions; clockcycle++) {
			if (clockcycle > 6 && clockcycle % 2 == 1 && canEnterWriteBack) {// odd
				writeBack();
			}

			if (clockcycle > 5 && clockcycle % 2 == 0 && canEnterMemory) {// even
				memory();
				canEnterWriteBack = true;
			}

			if (clockcycle > 3 && canEnterExecute) {// odd
				if (counterExecute == 1) {
					executeHelper();
					counterExecute = 0;
					canEnterMemory = true;
				} else
					counterExecute++;
			}

			if (clockcycle > 1 && canEnterDecode) {// odd
				if (counterDecode == 1) {
					decodeHelper();
					counterDecode = 0;
					canEnterExecute = true;
				} else
					counterDecode++;
			}

			if (clockcycle % 2 == 1) {// odd
				fetch();
				canEnterDecode = true;
			}

			// print(clockcycle);

			if (clear) {
				clear = false;
				// clockcycle ++;
				canEnterDecode = false;
				canEnterExecute = false;
				canEnterMemory = false;
				canEnterWriteBack = false;
				counterDecode = 0;
				counterExecute = 0;
				totalNoOfInstructions += 2;
				isJEQ = new Hashtable<>();
			}
		}
		// printAllRegistersAndMemory();
	}

	private static void printAllRegistersAndMemory() {
		System.out.println("Registers: ");
		for (int i = 0; i < registerFile.size() - 1; i++) {
			System.out.println("R" + i + ": " + registerFile.get(i).getValue());
		}
		System.out.println("Memory: ");
		for (int i = 0; i < memory.counter; i++) {
			System.out.println(i + ": " + memory.memory[i]);
		}
	}

	private static void print(int clockcycle) {
		System.out.println("Clock cycle: " + clockcycle);
		System.out.println("Fetch/Decode: " + pipelineRegisterFetchDecode.getInstructionLine() + " "
				+ pipelineRegisterFetchDecode.getPc());
		System.out.print("Decode/Execute: " + pipelineRegisterDecodeExcute.getOpcode() + " " +
				pipelineRegisterDecodeExcute.getR1() + " " + pipelineRegisterDecodeExcute.getR2() + " "
				+ pipelineRegisterDecodeExcute.getR3()
				+ " " + pipelineRegisterDecodeExcute.getShamt() + " " + pipelineRegisterDecodeExcute.getImm() + " " +
				pipelineRegisterDecodeExcute.getAddress());
		System.out.println();
		System.out.println("Execute/Memory: PC " + pipelineRegisterExecuteMemory.getPc() + " R1 "
				+ pipelineRegisterExecuteMemory.getR1()
				+ " Address " + pipelineRegisterExecuteMemory.getAddress() + " opcode "
				+ pipelineRegisterExecuteMemory.getOpcode());
		System.out.println("Memory/WriteBack: R1" + pipelineRegisterMemoryWriteBack.getR1() + " isStore "
				+ pipelineRegisterMemoryWriteBack.isStore());
		System.out.println("PC: " + pc.getValue());
		System.out.println("--------------------------------------------------");
	}

	public static void fetch() {
		int instruction = 0;

		// Complete the fetch() body...
		if(pipelineRegisterExecuteMemory.isBranchTaken()){
			pc.setValue(pipelineRegisterExecuteMemory.getPc());
			pipelineRegisterExecuteMemory.setBranchTaken(false);
			System.out.println("current pc"+pipelineRegisterExecuteMemory.getPc());
		}
		instruction = memory.memory[pc.getValue()];
		pipelineRegisterFetchDecode.setInstructionLine(instruction);
		pipelineRegisterFetchDecode.setPc(pc.getValue() + 1);
		pc.setValue(pc.getValue() + 1);
		if (pc.getValue() > memory.counter)
			return;
	}

	public static int decode() {
		// clockcycle++;
		// decodeHelper();
		return pipelineRegisterFetchDecode.getInstructionLine();
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
		int pc = pipelineRegisterFetchDecode.getPc();

		// Complete the decode() body...
		int instructiontmp;
		int mask;
		mask = 0b11110000000000000000000000000000;
		instructiontmp = instruction & mask;
		opcode = instructiontmp >>> 28;
		boolean isJump = false;
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
			pipelineRegisterDecodeExcute.setShamt(shamt);
			pipelineRegisterDecodeExcute.setImm(imm);
		}
		pipelineRegisterDecodeExcute.setAddress(address);
		pipelineRegisterDecodeExcute.setPc(pc);
		// finishDecode = true;
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
		int pc = pipelineRegisterDecodeExcute.getPc();
		Object[] tmp = new Object[8];
		tmp[0] = opcode;
		tmp[1] = r1;
		tmp[2] = r2;
		tmp[3] = r3;
		tmp[4] = shamt;
		tmp[5] = imm;
		tmp[6] = address;
		tmp[7] = pc;
		return tmp;
	}

	public static void executeHelper() {
		int opcode = pipelineRegisterDecodeExcute.getOpcode();
		Register r1 = pipelineRegisterDecodeExcute.getR1();
		Register r2 = pipelineRegisterDecodeExcute.getR2();
		Register r3 = pipelineRegisterDecodeExcute.getR3();
		int shamt = pipelineRegisterDecodeExcute.getShamt();
		int imm = pipelineRegisterDecodeExcute.getImm();
		int address = pipelineRegisterDecodeExcute.getAddress();
		int myPc = pipelineRegisterDecodeExcute.getPc();
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
					myPc += 1 + address;
					pc.setValue(myPc + 1 + address);
					isJEQ.put(4, true);
					pipelineRegisterExecuteMemory.setBranchTaken(true);
				} else
					isJEQ.put(4, false);
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
				int temp = (myPc & 0b1111000000000000000000000000000) >> 28;
				String tmp = temp + "" + address;
				myPc = Integer.parseInt(tmp);
				pipelineRegisterExecuteMemory.setBranchTaken(true);
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
		pipelineRegisterExecuteMemory.setPc(myPc);
		// finishExecute = true;
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
			r1.setValue(memory.memory[address]);
			pipelineRegisterMemoryWriteBack.setR1Value(r1.getValue());
			pipelineRegisterMemoryWriteBack.setStore(false);
		} else if (isStore) {
			memory.memory[address] = r1.getValue();
			pipelineRegisterMemoryWriteBack.setR1Value(r1.getValue());
			pipelineRegisterMemoryWriteBack.setStore(true);
		}
		if (r1 == null || (isJEQ.size() != 0 && isJEQ.get(4))) {
			pipelineRegisterDecodeExcute = new PipelineRegisterDecodeExcute();
			pipelineRegisterExecuteMemory = new PipelineRegisterExecuteMemory();
			clear = true;
			pipelineRegisterMemoryWriteBack.setStore(false);
		} else {
			if (isJEQ.size() != 0) {
				isJEQ = new Hashtable<>();
				pipelineRegisterMemoryWriteBack.setStore(false);
			}
		}
		// finishMemory = true;
	}

	public static void writeBack() {
		Register r1 = pipelineRegisterMemoryWriteBack.getR1();
		boolean isStore = pipelineRegisterMemoryWriteBack.isStore();
		if (r1 == null) {
			return;
		}
		if (!isStore) {
			int index = registerFile.indexOf(r1);
			registerFile.get(index).setValue(r1.getValue());
		}
		// finishWriteBack = true;
	}

	public static void main(String[] args) {
		Main main = new Main();
		// Set the value of the registers
		// registerFile.setValue(1, 5);
		registerFile.setValue(7, 3);
		registerFile.setValue(8, 4);
		registerFile.setValue(2, 5);
		registerFile.setValue(3, 6);
		registerFile.setValue(5, 7);
		registerFile.setValue(6, 8);

		start();

		System.out.println(registerFile.get(1).getValue());
		System.out.println(registerFile.get(9).getValue());
		System.out.println(registerFile.get(4).getValue());
		// System.out.println(registerFile.get(10).getValue());
		// System.out.println(registerFile.get(13).getValue());
		// System.out.println(registerFile.get(14).getValue());
		// System.out.println(registerFile.get(17).getValue());
		// System.out.println(registerFile.get(19).getValue());
		// System.out.println(registerFile.get(21).getValue());
		// System.out.println(registerFile.get(23).getValue());
	}

}
