public class PipelineRegisterExecuteMemory {
    private int pc;
    private Register r1;
    private int address;
    private int opcode;

    public PipelineRegisterExecuteMemory() {
        super();
        this.r1 = null;
        pc = 0;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public Register getR1() {
        return r1;
    }

    public void setR1(Register r1) {
        this.r1 = r1;
    }

    public void setR1Value(int value) {
        this.r1.setValue(value);
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }
}
