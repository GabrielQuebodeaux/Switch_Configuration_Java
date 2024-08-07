package app.controller;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;
public class Translator {
	
	private static ArrayList<String> readFile() {
		ArrayList<String> oldConfiguration = new ArrayList<>();
		while(true) {
			try {
				System.out.print("Enter File Name: ");
				Scanner input = new Scanner(System.in);
				String fileName = input.nextLine();
				File oldConfigFile = new File(fileName);
				input.close();
				Scanner fileScanner = new Scanner(oldConfigFile);
				while(fileScanner.hasNextLine()) {
					oldConfiguration.add(fileScanner.nextLine());
				}
				fileScanner.close();
				break;
			} catch (FileNotFoundException e) {
				System.out.println("*FILE NOT FOUND*");
			}
		}
		return oldConfiguration;
			
	}
	
	public static void translate() {
		ArrayList<String> oldConfiguration = readFile();
		String hostname = "";
		String ipAddress = "";
		String location = "";
		int vlanAccess = -1;
		String description = "";
		ArrayList<Switch> switches = new ArrayList<>();
		Switch temp = new Switch(1);
		switches.add(temp);
		for(int i = 0; i < oldConfiguration.size(); i++) {
			String line = oldConfiguration.get(i);
			if(line.indexOf("interface GigabitEthernet") != -1) {
				location = line.substring(25, line.length()).replace("/0/", "/1/");
			}
			else if(line.indexOf("access vlan") != -1 && location != "") {
				vlanAccess = Integer.parseInt(line.substring(18, line.length() - 1));
			}
			else if(line.indexOf("description") != -1 && location != "") {
				description = line.substring(13, line.length());
			}
			else if(line.indexOf('#') != -1 && location != "") {
				Port port = new Port(location, vlanAccess, description);
				if(port.getBladeNumber() == switches.getLast().getBladeNumber()) {
					Switch s = switches.getLast();
					s.add(port);
					switches.set(switches.size() - 1, s);
				}
				else {
					Switch s = new Switch(port.getBladeNumber());
					s.add(port);
					switches.add(s);
				}
				
				location = "";
				vlanAccess = -1;
				description = "";
			}
			else if(line.indexOf("sysname") != -1) {
				hostname = line.substring(9, line.length());
				hostname = hostname.replace(' ', '_').replace('_', '-');
				if(hostname.substring(hostname.length() - 3) == "-") {
					hostname = hostname.substring(0, hostname.length() - 2);
					hostname += "0" + hostname.substring(hostname.length() - 2);
				}
			}
			else if(line.indexOf("ip address") != -1) {
				ipAddress = line.substring(12, line.indexOf("255") - 1);
			}
		}
		Stack stack = new Stack(hostname, ipAddress, switches);
		write(stack);
	}
	private static void write(Stack stack) {
		String hostname = stack.getHostname();
		File newConfig = new File(hostname + ".txt");
		ArrayList<String> config = stack.getConfiguration();
		try {
			newConfig.createNewFile();
			FileWriter fileWriter = new FileWriter(hostname + ".txt");
			for(int i = 0; i < config.size(); i++) {
				System.out.println(config.get(i));
			}
			fileWriter.close();
			
		}
		catch(IOException e) {
			System.out.println("An error occured");
		}
	}
}
