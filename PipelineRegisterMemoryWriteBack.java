public class PipelineRegisterMemoryWriteBack {
    private Register r1;
    private boolean isStore;

    public PipelineRegisterMemoryWriteBack() {
        this.r1 = null;
        isStore = false;
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

    public boolean isStore() {
        return isStore;
    }

    public void setStore(boolean isStore) {
        this.isStore = isStore;
    }
}
