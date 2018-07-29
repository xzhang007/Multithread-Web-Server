package server.http;

import server.configuration.MimeTypes;
import server.configuration.HttpdConf;
import server.http.response.Response;
import server.exception.BadRequest;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.net.InetSocketAddress;

public class Worker extends Thread {
	private Socket clientSocket;
	private HttpdConf config;
	private MimeTypes mimeTypes;
	private Request request;
	private Resource resource;
	private Response response;
	private Logger logger;
	private Set<String> htaccessFiles;
	private String clientIPAddress;
	
	@SuppressWarnings("static-access")
	public Worker(Socket clientSocket, HttpdConf config, MimeTypes mimeTypes, Logger logger, Set<String> htaccessFiles) {
		this.clientSocket = clientSocket;
		this.config = config;
		this.mimeTypes = mimeTypes;
		this.logger = logger;
		this.htaccessFiles = htaccessFiles;
		try {
			clientIPAddress = ((InetSocketAddress) clientSocket.getRemoteSocketAddress()).getAddress().getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			 InputStream inFromClient = receiveRequest(clientSocket);
			 parseRequest(inFromClient);
			 createResource();
			 response = ResponseFactory.getResponse(request, resource, mimeTypes, htaccessFiles, config);
			 if (response == null) {
				System.out.println("222");
			 }
			 sendResponse(response, resource);
			 logger.write(request, response, clientIPAddress);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (BadRequest badRequest) {
			dealWithBadRequest(badRequest);
		} catch (Exception e) {
			dealWithException(e);
			//e.printStackTrace();
		} finally {
			socketClose();
		}
	}
	
	private InputStream receiveRequest(Socket clientSocket) throws IOException {
		InputStream inFromClient = clientSocket.getInputStream();
		return inFromClient;
	}
	
	private void parseRequest(InputStream inFromClient) throws BadRequest {
		request = new Request(inFromClient);
		request.parse();
	}
	
	private void createResource() {
		resource = new Resource(request.getUri(), config);
	}
	
	private void dealWithBadRequest(BadRequest badRequest) {
		resource = null;
		response = ResponseFactory.getResponse(400, resource);
		try {
			sendResponse(response, resource);
			logger.write(request, response, clientIPAddress);
			logger.write(badRequest, clientIPAddress);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private void dealWithException(Exception e) {
		resource = null;
		response = ResponseFactory.getResponse(500, resource);
		try {
			sendResponse(response, resource);
			logger.write(request, response, clientIPAddress);
			logger.write(e, clientIPAddress);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private void sendResponse(Response response, Resource resource) throws IOException {
		OutputStream out = clientSocket.getOutputStream();
		response.send(out);
	}
	
	private void socketClose() {
		try {
			clientSocket.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
