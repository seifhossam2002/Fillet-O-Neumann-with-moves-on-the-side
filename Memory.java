import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

public class Memory {
    int[] memory;
    boolean flagimm = false;
    boolean flagshamt = false;
    int counter = 0;
    ArrayList<potentialHazard> hazards = new ArrayList<potentialHazard>();

    public Memory() {
        memory = new int[2048];
        try {
            readassembltfile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readassembltfile() throws FileNotFoundException {
        BufferedReader reader;
        int counter = 0;

        try {
            reader = new BufferedReader(new FileReader("config.txt"));
            String line = reader.readLine();

            while (line != null) {
                if (counter == 1024) {
                    break;
                }
                String[] parts = line.split(" ");
                if (parts.length == 4) {
                    String opcode = parts[0];
                    String r1 = parts[1];
                    String r2 = parts[2];
                    String r3 = parts[3];

                    opcode = changetobinary(opcode);
                    r1 = changetobinary(r1);
                    r2 = changetobinary(r2);
                    String instruction = "";
                    boolean hazardImm = false;
                    boolean hazardShamt = false;
                    if (this.flagimm == true) {
                        hazardImm = true;
                        this.flagimm = false;
                        int imm = Integer.parseInt(r3);
                        String imm2 = Integer.toBinaryString(imm);
                        int size = 18 - imm2.length();
                        for (int i = 0; i < size; i++) {
                            imm2 = "0" + imm2;
                        }
                        r3 = imm2;
                        instruction = opcode + r1 + r2 + r3;

                    } else if (flagshamt) {
                        hazardShamt = true;
                        flagshamt = false;
                        int imm = Integer.parseInt(r3);
                        String imm2 = Integer.toBinaryString(imm);
                        int size = 18 - imm2.length();
                        for (int i = 0; i < size; i++) {
                            imm2 = "0" + imm2;
                        }
                        r3 = imm2;
                        instruction = opcode + r1 + r2 + r3;
                    } else {
                        r3 = changetobinary(r3);
                        instruction = opcode + r1 + r2 + r3 + "0000000000000";
                    }
                    BigInteger bigInteger = new BigInteger(instruction, 2);
                    // memory[counter] = bigInteger.intValue();
                    if(hazardImm || hazardShamt){
                        hazardImm = false;
                        hazardShamt = false;
                        potentialHazard hazard = new potentialHazard(r1,r2, "null");   //zyada
                        counter = ay7aga(hazard, counter, bigInteger.intValue()); //zyada
                    }
                    else{
                        potentialHazard hazard = new potentialHazard(r1,r2, r3);   //zyada
                        counter = ay7aga(hazard, counter, bigInteger.intValue()); //zyada
                    }
                    counter++;
                } else if (parts.length == 3) {
                    String opcode = parts[0];
                    String r1 = parts[1];
                    String imm = parts[2];
                    opcode = changetobinary(opcode);
                    r1 = changetobinary(r1);

                    int imm2 = Integer.parseInt(imm);
                    String imm3 = Integer.toBinaryString(imm2);
                    int size = 18 - imm3.length();
                    for (int i = 0; i < size; i++) {
                        imm3 = "0" + imm3;
                    }
                    imm = imm3;
                    String instruction = opcode + r1 + "00000" + imm;
                    BigInteger bigInteger = new BigInteger(instruction, 2);
                    // memory[counter] = bigInteger.intValue();
                    potentialHazard hazard = new potentialHazard(r1,"null", "null");
                    counter = ay7aga(hazard, counter, bigInteger.intValue()); //zyada
                    counter++;

                } else if (parts.length == 2) {
                    String opcode = parts[0];
                    String address = parts[1];
                    opcode = changetobinary(opcode);
                    int address2 = Integer.parseInt(address);
                    String address3 = Integer.toBinaryString(address2);
                    int size = 28 - address3.length();
                    for (int i = 0; i < size; i++) {
                        address3 = "0" + address3;
                    }
                    address = address3;
                    String instruction = opcode + address;
                    BigInteger bigInteger = new BigInteger(instruction, 2);
                    // int instruction2 = Integer.parseInt(instruction, 2);
                    memory[counter] = bigInteger.intValue();
                    counter++;
                }
                line = reader.readLine();
            }
            reader.close();
            this.counter = counter;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File does not exist.");

        }

    }

    public int ay7aga(potentialHazard hazard, int counter, int instruction) {
        if (hazards.size() == 0) {
            hazards.add(hazard);
            memory[counter] = instruction;
        } else {
            boolean flag = false;
            for (int i = hazards.size() - 1; i >= Math.max(0, hazards.size() - 3); i--) {
                if (hazards.get(i).r1.equals(hazard.r2) || hazards.get(i).r1.equals(hazard.r3)) {
                    memory[counter] = 0;
                    counter++;
                    memory[counter] = 0;
                    counter++;
                    memory[counter] = instruction;
                    hazards.add(hazard);
                    hazards.add(hazard);
                    flag = true;
                }
            }
            if (!flag) {
                hazards.add(hazard);
                memory[counter] = instruction;
            }
        }
        return counter;
    }
    

    public String changetobinary(String assembly) {
        // ADD,SUB,MUL,MOVI,JEQ,AND,XORI,JMP,LSL,LSR,MOVR,MOVM
        if (assembly.equals("ADD")) {
            return "0000";
        }
        if (assembly.equals("SUB")) {
            return "0001";
        }
        if (assembly.equals("MUL")) {
            return "0010";
        }
        if (assembly.equals("MOVI")) {
            return "0011";
        }
        if (assembly.equals("JEQ")) {
            this.flagimm = true;
            return "0100";
        }
        if (assembly.equals("AND")) {
            return "0101";
        }
        if (assembly.equals("XORI")) {
            this.flagimm = true;
            return "0110";
        }
        if (assembly.equals("JMP")) {
            return "0111";
        }
        if (assembly.equals("LSL")) {
            flagshamt = true;
            return "1000";
        }
        if (assembly.equals("LSR")) {
            flagshamt = true;
            return "1001";
        }
        if (assembly.equals("MOVR")) {
            this.flagimm = true;
            return "1010";
        }
        if (assembly.equals("MOVM")) {
            this.flagimm = true;
            return "1011";
        }

        // R0 to R31 in 5 bits
        if (assembly.equals("R0")) {
            return "00000";
        }
        if (assembly.equals("R1")) {
            return "00001";
        }
        if (assembly.equals("R2")) {
            return "00010";
        }
        if (assembly.equals("R3")) {
            return "00011";
        }
        if (assembly.equals("R4")) {
            return "00100";
        }
        if (assembly.equals("R5")) {
            return "00101";
        }
        if (assembly.equals("R6")) {
            return "00110";
        }
        if (assembly.equals("R7")) {
            return "00111";
        }
        if (assembly.equals("R8")) {
            return "01000";
        }
        if (assembly.equals("R9")) {
            return "01001";
        }
        if (assembly.equals("R10")) {
            return "01010";
        }
        if (assembly.equals("R11")) {
            return "01011";
        }
        if (assembly.equals("R12")) {
            return "01100";
        }
        if (assembly.equals("R13")) {
            return "01101";
        }
        if (assembly.equals("R14")) {
            return "01110";
        }
        if (assembly.equals("R15")) {
            return "01111";
        }
        if (assembly.equals("R16")) {
            return "10000";
        }
        if (assembly.equals("R17")) {
            return "10001";
        }
        if (assembly.equals("R18")) {
            return "10010";
        }
        if (assembly.equals("R19")) {
            return "10011";
        }
        if (assembly.equals("R20")) {
            return "10100";
        }
        if (assembly.equals("R21")) {
            return "10101";
        }
        if (assembly.equals("R22")) {
            return "10110";
        }
        if (assembly.equals("R23")) {
            return "10111";
        }
        if (assembly.equals("R24")) {
            return "11000";
        }
        if (assembly.equals("R25")) {
            return "11001";
        }
        if (assembly.equals("R26")) {
            return "11010";
        }
        if (assembly.equals("R27")) {
            return "11011";
        }
        if (assembly.equals("R28")) {
            return "11100";
        }
        if (assembly.equals("R29")) {
            return "11101";
        }
        if (assembly.equals("R30")) {
            return "11110";
        }
        if (assembly.equals("R31")) {
            return "11111";
        } else {
            return null;
        }

    }

    public void printMemory() {
        for (int i = 0; i < counter; i++) {
            System.out.println("memory[ " + i + " ] :" + memory[i]);
        }
    }

    public static void main(String[] args) {
        Memory memory = new Memory();
        memory.printMemory();
    }
}
