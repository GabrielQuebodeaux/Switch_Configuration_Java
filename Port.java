package app.controller;

public class Port {
	private String location;
	private int vlanAccess;
	private String description;
	private int bladeNumber;
	private int portNumber;
	
	public Port(String location, int vlanAccess, String description) {
		this.location = location;
		this.vlanAccess = vlanAccess;
		this.description = description;
		bladeNumber = Integer.parseInt(location.substring(0, location.indexOf("/")));
		portNumber = Integer.parseInt(location.substring(location.indexOf("/1/") + 3));
	}

	public String getLocation() {
		return location;
	}
	
	public int getVlanAccess() {
		return vlanAccess;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getBladeNumber() {
		return bladeNumber;
	}
	
	public int getPortNumber() {
		return portNumber;
	}
}
