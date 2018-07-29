package server.http;

import server.configuration.HttpdConf;

import java.io.File;
import java.util.Map;

public class Resource {
	private String uri;
	private HttpdConf config;
	private Map<String, String> scriptAliases;
	private Map<String, String> aliases;
	private String documentRoot;
	
	public Resource(String uri, HttpdConf config) {
		this.uri = uri;
		this.config = config;
		scriptAliases = config.getScriptAliases();
		aliases = config.getAliases();
		documentRoot = config.getDocumentRoot();
	}
	
	public boolean isScript() {
		if (scriptAliases != null && scriptAliases.containsKey(uri)) {
			return true;
		}
		if (scriptAliases != null) {
			for (Map.Entry<String, String> entry : scriptAliases.entrySet()) {
				String key = entry.getKey();
				if (uri.matches(".*" + key + ".*")) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isAlias() {
		if (aliases != null && aliases.size() > 0) {
			for (Map.Entry<String, String> entry : aliases.entrySet()) {
				if (uri.matches(".*" + entry.getKey() + ".*")) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public String getAbsolutePath() {
		if (isScript()) {
			return getAbsolutePath(scriptAliases);
		}
		if (isAlias()) {
			return getAbsolutePath(aliases);
		}
		if (!uri.regionMatches(0, documentRoot, 0, documentRoot.length())) {
			uri = resolvePath();
		}
		if (isDir()) {
			uri += config.getDirectoryIndex();
		}
		
		return uri;
	}
	
	private String resolvePath() {
		if (documentRoot.substring(documentRoot.length() - 1).equals("/") && uri.charAt(0) == '/') {
			return documentRoot.substring(0, documentRoot.length() - 1) + uri;
		}
		return documentRoot + uri;
	}
	
	private boolean isDir() {
		File file = new File(uri);
		return file.isDirectory();
	}
	
	private String getAbsolutePath(Map<String, String> map) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			if (uri.matches(".*" + key + ".*")) {
				return entry.getValue() + uri.substring(key.length(), uri.length());
			}
		}
		return null;
	}
}
