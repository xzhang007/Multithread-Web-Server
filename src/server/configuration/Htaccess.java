package server.configuration;

import server.exception.ServerConfigNotFoundException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Htaccess extends ConfigurationReader {
	private static final Pattern PATTERN1 = Pattern.compile("\\w+ \"(.*)\"");
	private static final Pattern PATTERN2 = Pattern.compile("\\w+ (.*)");
	private Htpassword userFile;
	private String authType;
	private String authName;
	private String require;
	  
	
	public Htaccess(String fileName) throws ServerConfigNotFoundException {
		super(fileName); 
	}
	
	@Override
	public void load() {
		while (super.hasMoreLines()) {
			String line = super.nextLine();
			parseLine(line);
		}		
		super.closeReaders();
		
	}
	
	protected void parseLine(String line) {
	    if (line.startsWith("AuthUserFile")) {
	    	Matcher matcher = PATTERN1.matcher(line);
	    	if (matcher.matches()) {
	    		String str = matcher.group(1);
	    		try {
	    			getHtpassword(str);
	    		} catch (ServerConfigNotFoundException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    } else if (line.startsWith("AuthType")) {
	    	Matcher matcher = PATTERN2.matcher(line);
	    	if (matcher.matches()) {
	    		authType = matcher.group(1);
	    	}
	    	if (authType.equals("Basic")) {} // do nothing
	    } else if (line.startsWith("AuthName")) {
	    	Matcher matcher = PATTERN1.matcher(line);
	    	if (matcher.matches()) {
	    		authName = matcher.group(1);
	    	}
	    } else if (line.startsWith("Require")) {
	    	Matcher matcher = PATTERN2.matcher(line);
	    	if (matcher.matches()) {
	    		require = matcher.group(1);
	    	}
	    }
	  }

	private void getHtpassword(String path)  throws ServerConfigNotFoundException {
		userFile = new Htpassword(path);
	}
	
	public boolean isAuthorized(String authInfo) {
		if (userFile.isAuthorized(authInfo)) {
			if (require.equals("valid-user")) {
				return true;
			}
			String validUserName = require.substring(5);
			String inputUserName = userFile.getUserNameAndPwd(authInfo)[0];
			if (validUserName.equals(inputUserName)) {
				return true;
			}
		}
		return false;
	}
	
	
	public String getAuthName() {
		return authName;
	}
}
