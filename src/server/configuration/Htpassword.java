package server.configuration;

import server.exception.ServerConfigNotFoundException;

import java.util.Base64;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.HashMap;

public class Htpassword extends ConfigurationReader {
    private HashMap<String, String> passwords;

    public Htpassword(String filename)  throws ServerConfigNotFoundException {
    	super(filename);
    	System.out.println("Password file: " + filename);

    	this.passwords = new HashMap<String, String>();
    	this.load();
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
    	String[] tokens = line.split(":");

    	if (tokens.length == 2) {
    		passwords.put(tokens[0], tokens[1].replace("{SHA}", "").trim());
    	}
    }

    public boolean isAuthorized(String authInfo) {
    	// authInfo is provided in the header received from the client
    	// as a Base64 encoded string.
    	if (authInfo == null || !authInfo.startsWith("Basic")) {
    		return false;
    	}
	  
    	// The string is the key:value pair username:password
    	String[] tokens = getUserNameAndPwd(authInfo);

    	// TODO: implement this
    	return verifyPassword(tokens[0], tokens[1]);
    }

    private boolean verifyPassword(String username, String password) {
    	// encrypt the password, and compare it to the password stored
    	// in the password file (keyed by username)
    	String encryptedPassword = encryptClearPassword(password);
    	if (passwords.containsKey(username)) {
		   String storedPassword = passwords.get(username);
		   if (storedPassword.equals(encryptedPassword)) {
			   return true;
		   }
    	}
    	return false;
    }

    private String encryptClearPassword(String password) {
    	// Encrypt the cleartext password (that was decoded from the Base64 String
    	// provided by the client) using the SHA-1 encryption algorithm
    	try {
    		MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
    		byte[] result = mDigest.digest(password.getBytes());

    		return Base64.getEncoder().encodeToString(result);
    	} catch(Exception e) {
    		return "";
    	}
    }
  
    String[] getUserNameAndPwd(String authInfo) {
    	String base64Credentials = authInfo.substring("Basic".length()).trim();
		String credentials = null;
		try {
			credentials = new String(
			        Base64.getDecoder().decode(base64Credentials),
				    Charset.forName("UTF-8")
				    );
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		// The string is the key:value pair username:password
	    String[] tokens = credentials.split(":");
	    
	    return tokens;
    }
}
