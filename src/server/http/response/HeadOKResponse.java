package server.http.response;

import server.http.Resource;
import server.configuration.MimeTypes;

import java.io.OutputStream;
import java.io.PrintWriter;

public class HeadOKResponse extends Response {
	
	public HeadOKResponse(Resource resource) {
		super(resource);
		super.code = 200;
		super.reasonPhrase = "OK";
		super.sendBody = false;
	}
	
	public HeadOKResponse(Resource resource, MimeTypes mimeTypes) {
		this(resource);
		this.mimeTypes = mimeTypes;
	}
	
	@Override
	void sendHeaders(OutputStream out) {
		PrintWriter writer = new PrintWriter(out, true);
		sendResponseLine(writer);
		sendDefaultHeaders(writer);
		sendContentHeaders(writer);
		
		writer.write("\r\n");
		writer.flush();  // important
	}
}
