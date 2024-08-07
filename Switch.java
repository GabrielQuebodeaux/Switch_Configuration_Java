package app.controller;

public class Switch extends PortGroup {
	private int bladeNumber;
	
	public Switch(int bladeNumber) {
		super(-1, "");
		this.bladeNumber = bladeNumber;
	}
	
	public int getBladeNumber() {
		return bladeNumber;
	}
	
}
