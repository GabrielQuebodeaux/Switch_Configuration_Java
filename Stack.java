package app.controller;
import java.util.ArrayList;

public class Stack {
	private String hostname;
	private String ipAddress;
	private ArrayList<Switch> switches;
	
	public Stack(String hostname, String ipAddress, ArrayList<Switch> switches) {
		this.hostname = hostname;
		this.ipAddress = ipAddress;
		this.switches = switches;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public ArrayList<String> getConfiguration() {
		String hostnameCommand = String.format("hostname %s\n\n", hostname);
		String vlanOneCommand = String.format("interface vlan 1\nip address %s/16\nexit\n\n", ipAddress);
		int index = ipAddress.indexOf('.', ipAddress.indexOf('.') + 1);
		String ipRouteCommand = String.format("ip route 0.0.0.0/0 %s.0.1\n\n", ipAddress.substring(0, index));
		String[] temp = {ipRouteCommand, vlanOneCommand, hostnameCommand};
		ArrayList<String> commands = this.getCommands();
		for(int i = 0; i < temp.length; i++) {
			commands.add(0, temp[i]);
		}
		return commands;
	}
	
	private ArrayList<String> getCommands() {
		ArrayList<ArrayList<PortGroup>> sorted = this.sort();
		PortGroup allPorts = sorted.get(0).get(0);
		ArrayList<PortGroup> vlanGroups = sorted.get(1);
		ArrayList<PortGroup> descriptionGroups = sorted.get(2);
		ArrayList<String> commands = allPorts.getConfiguration();
		for(int i = 0; i < vlanGroups.size(); i++) {
			PortGroup group = vlanGroups.get(i);
			ArrayList<String> groupConfiguration = group.getConfiguration();
			for(int k = 0; k < groupConfiguration.size(); k++) {
				String command = groupConfiguration.get(k);
				commands.add(command);
			}
		}
		for(int i = 0; i < descriptionGroups.size(); i++) {
			PortGroup group = descriptionGroups.get(i);
			ArrayList<String> groupConfiguration = group.getConfiguration();
			for(int k = 0; k < groupConfiguration.size(); k++) {
				String command = groupConfiguration.get(k);
				commands.add(command);
			}
		}
		commands.add("vsf split-detect mgmt\n");
		commands.add(String.format("vsf secondary-member %d\n\n", switches.size()));
		return commands;
	}
	
	private ArrayList<ArrayList<PortGroup>> sort() {
		ArrayList<PortGroup> vlanGroups = new ArrayList<>();
		ArrayList<PortGroup> descriptionGroups = new ArrayList<>();
		ArrayList<String> descriptions = new ArrayList<>();
		ArrayList<Integer> vlans = new ArrayList<>();
		ArrayList<PortGroup> allPorts = new ArrayList<>();
		PortGroup all = new PortGroup(-1, "");
		for(int i = 0; i < switches.size(); i++) {
			Switch s = switches.get(i);
			for(int k = 0; k < s.getPorts().size(); k++) {
				Port port = s.getPorts().get(k);
				int vlanAccess = port.getVlanAccess();
				String description = port.getDescription();
				if(vlanAccess != -1) {
					if(vlans.indexOf(vlanAccess) != -1) {
						vlanGroups.get(vlans.indexOf(vlanAccess)).add(port);
					}
					else {
						PortGroup group = new PortGroup(vlanAccess, "");
						group.add(port);
						vlanGroups.add(group);
						vlans.add(vlanAccess);
						
					}
				}
				if(description != "") {
					if(descriptions.indexOf(description) != -1) {
						descriptionGroups.get(descriptions.indexOf(description)).add(port);
					}
					else {
						PortGroup group = new PortGroup(-1, description);
						group.add(port);
						descriptionGroups.add(group);
						descriptions.add(description);
					}
				}
				all.add(port);
			}
		}
		allPorts.add(all);
		ArrayList<ArrayList<PortGroup>> sorted = new ArrayList<>();
		sorted.add(allPorts);
		sorted.add(vlanGroups);
		sorted.add(descriptionGroups);
		return sorted;
	}
}
