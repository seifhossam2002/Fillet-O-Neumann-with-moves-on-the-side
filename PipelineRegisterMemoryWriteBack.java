public class PipelineRegisterMemoryWriteBack {
    private Register r1;
    
    public PipelineRegisterMemoryWriteBack(){
        this.r1 = null;
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
}
