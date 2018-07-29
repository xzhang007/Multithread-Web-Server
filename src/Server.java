
import server.configuration.HttpdConf;
import server.configuration.MimeTypes;
import server.http.Logger;
import server.http.Worker;
import server.exception.ServerConfigNotFoundException;

import java.net.*;
import java.io.*;
import java.util.Set;
import java.util.HashSet;

public class Server {
	private int port;
	private HttpdConf config;
	private MimeTypes mimeTypes;
	private ServerSocket serverSocket;
	private Logger logger;
	private Set<String> htaccessFiles;
	
	public Server() {
		try {
			loadConfig();
			loadMimeTypes();
			loadLogger();
		
			htaccessFiles = new HashSet<String>();
			traverseAllPathToFindHtaccess(config.getDocumentRoot());
			port = config.getPort();
		} catch (ServerConfigNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
	
	public void start() {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server Socket Created.");
			
			while (true) {
				try {
					System.out.println("Waiting for connection on port: " + config.getPort());
					
					Socket clientSocket = serverSocket.accept();
					System.out.println("Connection successful.");
					
					Thread workerThread = new Worker(clientSocket, config, mimeTypes, logger, htaccessFiles);
					workerThread.start();
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		} catch (IOException exception) {
			System.out.println("Could not listen on port: " + port);
			exception.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException exception) {
				System.out.println("Could not close port: " + port);
				exception.printStackTrace();
			}
		}
	}
	
	private void loadConfig() throws ServerConfigNotFoundException {
		config = new HttpdConf("conf/Httpd.conf");
		config.load();
	}
	
	private void loadMimeTypes() throws ServerConfigNotFoundException {
		mimeTypes = new MimeTypes("conf/mime.types");
		mimeTypes.load();
	}
	
	private void loadLogger() throws ServerConfigNotFoundException  {
		if (config.getLogFile() == null) {
			throw new ServerConfigNotFoundException();
		}
		logger = new Logger(config.getLogFile());
	}
	
	private void traverseAllPathToFindHtaccess(String absolutePath) {
		File file = new File(absolutePath);
		File[] list = file.listFiles();
		if (list != null) {
			for (File fil : list) {
				if (fil.isDirectory()) {
					traverseAllPathToFindHtaccess(fil.getAbsolutePath());
				} else {
					String name = fil.getName();
					if (name.equals(config.getAccessFileName())) {
						htaccessFiles.add(absolutePath);
					}
				}
			}
		}
	}
}
