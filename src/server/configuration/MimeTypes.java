package server.configuration;

import server.exception.ServerConfigNotFoundException;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

import java.util.regex.Matcher;

public class MimeTypes extends ConfigurationReader {
	private Map<String, String> types;
	private Pattern pattern = Pattern.compile("(\\S+/\\S+)\\s+(.*)");
	
	public MimeTypes(String fileName)  throws ServerConfigNotFoundException {
		super(fileName);
		types = new HashMap<String, String>();
	}
	
	@Override
	public void load() {
		while (super.hasMoreLines()) {
			String line = super.nextLine();
			
			getTypes(line);
		}
		
		super.closeReaders();
	}
	
	private void getTypes(String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.matches()) {
			String type = matcher.group(1);
			String[] extensions = matcher.group(2).split("\\s");
			for (String ext : extensions) {
				types.put(ext, type);
			}
		}
	}
	
	public String lookup(String extension) {
		String type = null;
		if (types.containsKey(extension)) {
			type =  types.get(extension);
		}
		return type;
	}
	
	public void test() {
		for (Map.Entry<String, String> entry : types.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
}
