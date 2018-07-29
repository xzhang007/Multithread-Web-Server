package server.configuration;

import server.exception.ServerConfigNotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

public abstract class ConfigurationReader {
    private File file;
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private String line;
    
    public ConfigurationReader(String fileName) throws ServerConfigNotFoundException {
    	try {
    		file = new File(fileName);
    		fileReader = new FileReader(file);
    		bufferedReader = new BufferedReader(fileReader);
    	} catch (NullPointerException e) {
    		e.printStackTrace();
    		closeReaders();
    	} catch (FileNotFoundException e) {
    		closeReaders();
    		throw new ServerConfigNotFoundException();
    	}
    }
    
    public boolean hasMoreLines() {
    	try {
    		line = bufferedReader.readLine();
    		if (line == null) {
    			return false;
    		}
    		while (line.equals("") || line.regionMatches(0, "#", 0, 1)) {
    			line = bufferedReader.readLine();
    			if (line == null) {
    				return false;
    			}
    		}
    		return true; 
    	} catch (IOException e) {
    		e.printStackTrace();
    		closeReaders();
    	}
    	
    	return true;
    }
    
    public String nextLine() {
    	return line;
    }
    
    abstract public void load();
    
    void closeReaders() {
    	try {
    		bufferedReader.close();
    		fileReader.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}
