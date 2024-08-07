package app.controller;
import java.util.ArrayList;
import java.util.Arrays;

public class PortGroup {
	private int vlanAccess;
	private String description;
	private ArrayList<Port> ports = new ArrayList<>();
	private ArrayList<String> vanillaCommands = new ArrayList<>();
	
	public PortGroup(int vlanAccess, String description) {
		this.vlanAccess = vlanAccess;
		this.description = description;
		vanillaCommands.add("");
		vanillaCommands.add("no shutdown\n");
		vanillaCommands.add("no routing\n");
		vanillaCommands.add("vlan trunk native 1\n");
		vanillaCommands.add("vlan trunk allowed 1,40,100,200,240\n");
	}
	
	public void add(Port port) {
		ports.add(port);
	}
	
	public ArrayList<String> getConfiguration() {
		if(vlanAccess == -1 & description == "") {
			vanillaCommands.set(0, this.getInterfaceCommand());
			return vanillaCommands;
		}
		ArrayList<String> commands = new ArrayList<>();
		commands.add(this.getInterfaceCommand());
		if(vlanAccess != -1) {
			commands.add(String.format("vlan access %d\n", vlanAccess));
		}
		if(description != "") {
			commands.add(String.format("description %s\n", description));
		}
		return commands;
	}
	
	private String getInterfaceCommand() {
		String interfaceCommand = "interface ";
		ArrayList<ArrayList<Port>> interfaceRanges = this.getInterfaceRanges();
		for(int i = 0; i < interfaceRanges.size(); i++) {
			ArrayList<Port> range = interfaceRanges.get(i);
			if(range.size() <= 2) {
				for(int k = 0; k < range.size(); k++) {
					interfaceCommand += String.format("%s,", range.get(k).getLocation());
				}
			}
			else {
				String start = range.get(0).getLocation();
				String end = range.get(range.size() - 1).getLocation();
				interfaceCommand += String.format("%s-%s,", start, end);
			}
		}
		interfaceCommand = interfaceCommand.substring(0, interfaceCommand.length() - 1) + "\n";
		return interfaceCommand;
	}
	
	private ArrayList<ArrayList<Port>> getInterfaceRanges() {
		ArrayList<ArrayList<Port>> interfaceRanges = new ArrayList<>();
		ArrayList<Port> range = new ArrayList<>();
		range.add(ports.get(0));
		interfaceRanges.add(range);
		for(int i = 1; i < ports.size(); i++) {
			Port currentPort = ports.get(i);
			Port previousPort = ports.get(i - 1);
			boolean onSameBlade = currentPort.getBladeNumber() == previousPort.getBladeNumber();
			boolean inSequence = onSameBlade && currentPort.getPortNumber() == previousPort.getPortNumber() + 1;
			if(inSequence) {
				interfaceRanges.get(interfaceRanges.size() - 1).add(currentPort);
			}
			else {
				range = new ArrayList<Port>(Arrays.asList(currentPort));
				interfaceRanges.add(range);
			}
		}
		return interfaceRanges;
	}
	
	public String toString() {
		String str = "";
		ArrayList<String> config = this.getConfiguration();
		for(int i = 0; i < config.size(); i++) {
			str += config.get(i);
		}
		return str;
	}
	
	public int getVlanAccess() {
		return vlanAccess;
	}
	
	public String getDescription() {
		return description;
	}
	
	public ArrayList<Port> getPorts() {
		return ports;
	}
}
