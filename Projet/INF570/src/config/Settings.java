package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;



public class Settings {
	private double version;
	private String sharePath;
	private String configPath="bin/config/conf";
	

	public double getVersion() {
		return version;
	}

	public String getSharePath() {
		return sharePath;
	}

	public boolean load(){
		File fconf = new File(configPath);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fconf));
			String currentLine=reader.readLine();
			while(currentLine!=null){
				String[] tab  = currentLine.split(" ");
				handle(tab);
				currentLine=reader.readLine();
			}
			
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
		return true;	
	}
	
	public boolean save(){
		File fconf = new File(configPath);		
		
		fconf.delete();
		
		
		try {
			PrintWriter write = new PrintWriter(new FileWriter(fconf));
			
			write.println("version: "+version);
			write.println("sharePath: "+sharePath);
			
		
			write.flush();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
		return true;	
	}
	
	private void handle(String[] value){//le premier élément de value est le nom du paramètre
		if(value[0].equals("version:"))version=Double.parseDouble(value[1]);
		else if(value[0].equals("sharePath:"))sharePath = value[1];
	}
	
}
