package server.http.helper;

import server.configuration.HttpdConf;

import java.io.File;
import java.util.Set;

public class HtaccessHelper {
	
	public static String getHtaccessPath(String absolutePath, Set<String> htaccessFiles, HttpdConf config) {
		String[] pathArr = absolutePath.split("/");
		int startIndex = config.getDocumentRoot().split("/").length;
		String traversePath = config.getDocumentRoot();
		for (int i = startIndex; i < pathArr.length; i++) {
			if (htaccessFiles.contains(traversePath)) {
				String htaccessFileName = getHtaccessFileName(traversePath, config);
				return htaccessFileName;
			}
			traversePath += pathArr[i];
		}
		
		return null;
	}
	
	private static String getHtaccessFileName(String absolutePath, HttpdConf config) {
		File file = new File(absolutePath);
		File[] list = file.listFiles();
		if (list != null) {
			for (File fil : list) {
				String name = fil.getName();
				if (name.equals(config.getAccessFileName())) {
					if (absolutePath.matches(".*/")) {
						return absolutePath + name;
					}
					return absolutePath + "/" + name;
				}
			}
		}
		return null;
	}
}
