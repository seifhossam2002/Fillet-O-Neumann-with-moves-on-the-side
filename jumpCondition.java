public class jumpCondition {
    private int ogAdress = 0;
    private int destinaton = 0;
    private boolean done;
    private String opcode;
    private int address;

    public jumpCondition(int ogAdress, int destinaton,String opcode,int address) {
        this.ogAdress = ogAdress;
        this.destinaton = destinaton;
        done = false;
        this.opcode = opcode;
        this.address = address;
    }

    public int getOgAdress() {
        return ogAdress;
    }

    public void setOgAdress(int ogAdress) {
        this.ogAdress = ogAdress;
    }

    public int getDestinaton() {
        return destinaton;
    }

    public void setDestinaton(int destinaton) {
        this.destinaton = destinaton;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone() {
        done = true;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}
