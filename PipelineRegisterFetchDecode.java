public class PipelineRegisterFetchDecode {
    private int pc;
    private int InstructionLine;

    public PipelineRegisterFetchDecode(){
        pc = 0;
        InstructionLine = 0;
        
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getInstructionLine() {
        return InstructionLine;
    }

    public void setInstructionLine(int instructionLine) {
        InstructionLine = instructionLine;
    }

    
}
