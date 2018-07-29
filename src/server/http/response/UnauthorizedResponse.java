package server.http.response;

import server.http.Resource;

import java.io.OutputStream;
import java.io.PrintWriter;

public class UnauthorizedResponse extends Response {
	private String authName;
	
	public UnauthorizedResponse(Resource resource) {
		super(resource);
		super.code = 401;
		super.reasonPhrase = "Unauthorized";
		super.sendBody = false;
	}
	
	public UnauthorizedResponse(Resource resource, String authName) {
		this(resource);
		this.authName = authName;
	}
	
	@Override
	void sendHeaders(OutputStream out) {
		PrintWriter writer = new PrintWriter(out, true);
		sendResponseLine(writer);
		sendDefaultHeaders(writer);
		sendAuthenticateHeader(writer);
		
		writer.write("\r\n");
		writer.flush();  // important
	}
	
	private void sendAuthenticateHeader(PrintWriter writer) {
		writer.write("WWW-Authenticate: Basic realm=" + authName + "\r\n");
	}
}
