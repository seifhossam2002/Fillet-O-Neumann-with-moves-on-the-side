public class PipelineRegisterDecodeExcute {
    private int opcode;
    private Register r1;
    private Register r2;
    private Register r3;
    private int shamt;
    private int imm;
    private int address;

    public PipelineRegisterDecodeExcute() {
        this.opcode = -1;
        this.r1 = null;
        this.r2 = null;
        this.r3 = null;
        this.shamt = 0-1;
        this.imm = 0-1;
        this.address = 0-1;
    }

    public int getOpcode() {
        return opcode;
    }

    public Register getR1() {
        return r1;
    }

    public Register getR2() {
        return r2;
    }

    public Register getR3() {
        return r3;
    }

    public int getShamt() {
        return shamt;
    }

    public int getImm() {
        return imm;
    }

    public int getAddress() {
        return address;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public void setR1(Register r1) {
        this.r1 = r1;
    }

    public void setR2(Register r2) {
        this.r2 = r2;
    }

    public void setR3(Register r3) {
        this.r3 = r3;
    }

    public void setShamt(int shamt) {
        this.shamt = shamt;
    }

    public void setImm(int imm) {
        this.imm = imm;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}