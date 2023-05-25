public class Register {
	private String name;
	private int value;

	public Register(String name, int value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		if (!this.name.equals("R0"))
			this.value = value;
	}

	@Override
	public String toString() {
		return "Register [name=" + name + ", value=" + value + "]";
	}
}
