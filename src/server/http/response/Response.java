package server.http.response;

import server.http.Resource;
import server.http.helper.HttpDateFormat;
import server.configuration.MimeTypes;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.PrintWriter;

public abstract class Response {
	int code;
	String reasonPhrase;
	Resource resource;
	boolean sendBody;
	int contentLength;
	MimeTypes mimeTypes;
	
	public Response(Resource resource) {
		this.resource = resource;
	}
	
	public Response(Resource resource, MimeTypes mimeTypes) {
		this(resource);
		this.mimeTypes = mimeTypes;
	}
	
	public void send(OutputStream out) {
		sendHeaders(out);
		
		if (sendBody) {
			sendBody(out);
		}
	}
	
	public int getCode() {
		return code;
	}
	
	public int getContentLength() {
		if (resource == null) {
			return 0;
		}
		String absolutePath = resource.getAbsolutePath();
		File file = new File(absolutePath);
		return (int) file.length();
	}
	
	void sendHeaders(OutputStream out) {
		PrintWriter writer = new PrintWriter(out, true);
		sendResponseLine(writer);
		sendDefaultHeaders(writer);
		if (sendBody) {
			sendContentHeaders(writer);
		}
		writer.write("\r\n");
		writer.flush();  // important
	}
	
	void sendResponseLine(PrintWriter writer) {
		writer.write("HTTP/1.1 " + code + " " + reasonPhrase + "\r\n");
	}
	
	void sendDefaultHeaders(PrintWriter writer) {
		writer.write("Server: Web Server Project\r\n");
		writer.write("Date: " + HttpDateFormat.getCurrentDate() + "\r\n");
		writer.write("Connection: Closed\r\n");
	}
	
	void sendContentHeaders(PrintWriter writer) {
		writer.write("Content-type: " + getContentType() + "\r\n");
		writer.write("Content-length: " + getContentLength() + "\r\n");
	}
	
	void sendBody(OutputStream out) {
		String absolutePath = resource.getAbsolutePath();
		Path path = Paths.get(absolutePath);
		try {
			Files.copy(path, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	String getExtension() {
		if (resource == null) {
			return null;
		}
		String extension = null;
		String absolutePath = resource.getAbsolutePath();
		String[] strArr = absolutePath.split("\\.");
		extension = strArr[strArr.length - 1];
		return extension;
	}
	
	String getContentType() {
		if (mimeTypes == null) {
			return null;
		}
		String extension = getExtension();
		
		return mimeTypes.lookup(extension);
	}
}
