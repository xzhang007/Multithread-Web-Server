package server.configuration;

import server.exception.ServerConfigNotFoundException;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

import java.util.regex.Matcher;

public class HttpdConf extends ConfigurationReader {
    private Map<String, String> aliases;
    private Map<String, String> scriptAliases;
    private String serverRoot;
    private String documentRoot;
    private int portToListen;
    private String logFile;
    private String accessFileName;
    private String directoryIndex;
    private Pattern pattern1 = Pattern.compile("\\w+ \"(.*)\"");
    private Pattern pattern2 = Pattern.compile("\\w+ (\\/.*\\/) \"(.*)\"");
    private Pattern pattern3 = Pattern.compile("\\w+ (\\d+)");
    private Pattern pattern4 = Pattern.compile("\\w+ (.*)");
    private static final String DEFAULT_ACCESSFILENAME = ".htaccess";
    private static final String DEFAULT_DIRECTORYINDEX = "index.html";
    
    public HttpdConf(String fileName)  throws ServerConfigNotFoundException {
        super(fileName);
        aliases = new HashMap<String, String>();
        scriptAliases = new HashMap<String, String>();
    }
    
    @Override
    public void load() {
        while (super.hasMoreLines()) {
            String line = super.nextLine();
        	
            getConfigureParameter(line);
        }
        
        super.closeReaders();
    }
    
    private void getConfigureParameter(String line) {
    	if (line.regionMatches(0, "ServerRoot", 0, 10)) {
            Matcher matcher = pattern1.matcher(line);
            if (matcher.matches()) {
                serverRoot = matcher.group(1);
            }
    	} else if (line.regionMatches(0, "DocumentRoot", 0, 12)) {
    		Matcher matcher = pattern1.matcher(line);
    		if (matcher.matches()) {
    			documentRoot = matcher.group(1);
    		}
    	} else if (line.regionMatches(0, "Listen", 0, 6)) {
    		Matcher matcher = pattern3.matcher(line);
    		if (matcher.matches()) {
    			portToListen = Integer.parseInt(matcher.group(1));
    		}
    	} else if (line.regionMatches(0, "LogFile", 0, 7)) {
    		Matcher matcher = pattern1.matcher(line);
    		if (matcher.matches()) {
    			logFile = matcher.group(1);
    		}
    	} else if (line.regionMatches(0, "ScriptAlias", 0, 11)) {
    		Matcher matcher = pattern2.matcher(line);
    		if (matcher.matches()) {
    			scriptAliases.put(matcher.group(1), matcher.group(2));
    		}
    	} else if (line.regionMatches(0, "Alias", 0, 5)) {
    		Matcher matcher = pattern2.matcher(line);
    		if (matcher.matches()) {
    			aliases.put(matcher.group(1), matcher.group(2));
    		}
    	} else if (line.regionMatches(0, "AccessFileName", 0, 14)) {
    		Matcher matcher = pattern4.matcher(line);
    		if (matcher.matches()) {
    			String str = matcher.group(1);
    			if (str != null) {
    				accessFileName = str.matches("\".*\"") ? str.substring(1, str.length() - 1) : str;
    			}
    		}
    	} else if (line.regionMatches(0, "DirectoryIndex", 0, 14)) {
    		Matcher matcher = pattern4.matcher(line);
    		if (matcher.matches()) {
    			String str = matcher.group(1);
    			if (str != null) {
    				directoryIndex = str.matches("\".*\"") ? str.substring(1, str.length() - 1) : str;
    			}
    		}
    	}
    }
    
    public Map<String, String> getAliases() {
    	return aliases;
    }
    
    public Map<String, String> getScriptAliases() {
    	return scriptAliases;
    }
    
    public int getPort() {
    	return portToListen;
    }
    
    public String getDocumentRoot() {
    	return documentRoot;
    }
    
    public String getLogFile() {
    	return logFile;
    }
    
    public String getAccessFileName() {
    	if (accessFileName == null) {
    		return DEFAULT_ACCESSFILENAME;
    	}
    	return accessFileName;
    }
    
    public String getDirectoryIndex() {
    	if (directoryIndex == null) {
    		return DEFAULT_DIRECTORYINDEX;
    	}
    	return directoryIndex;
    }
    
    void test() {
    	System.out.println("ServerRoot " + serverRoot);
    	System.out.println("DocumentRoot " + documentRoot);
    	System.out.println("Listen " + portToListen);
    	System.out.println("LogFile " + logFile);
    	System.out.println("ScriptAliases: ");
    	for (Map.Entry<String, String> entry : scriptAliases.entrySet()) {
    		System.out.println(entry.getKey() + " " + entry.getValue());
    	}
    	System.out.println("Aliases: ");
    	for (Map.Entry<String, String> entry : aliases.entrySet()) {
    		System.out.println(entry.getKey() + " " + entry.getValue());
    	}
    	System.out.println("AccessFileName " + getAccessFileName());
    	System.out.println("DirectoryIndex " + getDirectoryIndex());
    }
 }
