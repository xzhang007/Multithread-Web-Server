package server.http;

import server.exception.BadRequest;

import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Set;
import java.util.HashSet;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;


public class Request {
	private String uri;
	private InputStream body;
	private String verb;
	private String httpVersion;
	private Map<String, String> headers;
	private static final Set<String> METHODS = new HashSet<>();
	private InputStream inputStreamFromClient;
	private static final Charset UTF8_CHARSET = Charset.forName("UTF8");
	private static final byte SPACE = 32;
	private static final byte CARRIAGERETURN = 13;
	private static final byte LINEFEED = 10;
	
	
	static {
		METHODS.add("GET");
		METHODS.add("HEAD");
		METHODS.add("POST");
		METHODS.add("PUT");
		METHODS.add("DELETE");
		METHODS.add("TRACE");
		METHODS.add("OPTIONS");
		METHODS.add("CONNECT");
		METHODS.add("PATCH");
	}
	
	public Request(String test) {
		convertStringToInputStream(test);
		headers = new HashMap<String, String>();
	}
	
	public Request(InputStream client) {
	    inputStreamFromClient = client;
		headers = new HashMap<String, String>();
	}
	
	public void parse() throws BadRequest {
		parseVerb();
		parseUri();
		parseHttpVersion();
		parseHeaders();
	}
	
	private void convertStringToInputStream(String test) {
		byte[] byteArray = test.getBytes(UTF8_CHARSET);
		inputStreamFromClient = new ByteArrayInputStream(byteArray);
	}
	
	private void parseVerb() throws BadRequest {
		verb = parseNextToken(SPACE, "");
		if (!METHODS.contains(verb)) {
			throw new BadRequest("BadeRequest: HTTP method is not correct"); 
		}
	}
	
	private void parseUri() throws BadRequest {
		uri = parseNextToken(SPACE, "");
		if (uri.equals("")) {
			throw new BadRequest("BadRequest: The request line is not correct"); 
		}
	}
	
	private void parseHttpVersion() throws BadRequest {
		httpVersion = parseNextToken(CARRIAGERETURN, "");
		
		byte[] smallBuffer = new byte[1];
		try {
			inputStreamFromClient.read(smallBuffer, 0, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (smallBuffer[0] != LINEFEED || httpVersion.equals("")) {
			throw new BadRequest("BadRequest: The request line is not correct");
		}
		
		if (!httpVersion.equals("HTTP/1.0") && 
				!httpVersion.equals("HTTP/1.1") && 
				!httpVersion.equals("HTTP/2")) {
				throw new BadRequest("BadRequest: HTTP Version is not correct");
			}
	}
	
	private void parseHeaders() throws BadRequest {
		int length = 0;
		byte[] smallBuffer = new byte[1];
		String[] headerLines = null;
		while (length != -1) {
			StringBuilder stringBuilder = new StringBuilder();
			try {
				for (int i = 0; i < 2; i++) {
					inputStreamFromClient.read(smallBuffer, 0, 1);
					stringBuilder.append(new String(smallBuffer, UTF8_CHARSET));
				}
				if (stringBuilder.toString().equals("\r\n")) {
					body = inputStreamFromClient;
					return;
				}
				
				headerLines = parseNextToken(CARRIAGERETURN, stringBuilder.toString()).split("\\: ");
				inputStreamFromClient.read(smallBuffer, 0, 1);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			if (length == -1 || smallBuffer[0] != LINEFEED) {
				throw new BadRequest("BadRequest: The header line is not correct");
			}
			if (headerLines.length != 2) {
				throw new BadRequest("BadRequest: The header line is not correct");
			}
			headers.put(headerLines[0], headerLines[1]);
		}
	}
	
	public String getUri() {
		return uri;
	}
	
	public InputStream getBody() {
		return body;
	}
	
	public String getVerb() {
		return verb;
	}
	
	public String getHttpVersion() {
		return httpVersion;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public String getRequestLine() {
		return verb + " " + uri + " " + httpVersion; 
	}
	
	public void closeReaders() {
		
	}
	
	private String parseNextToken(int charactor, String start) throws BadRequest {
		int length = 0;
		byte[] smallBuffer = new byte[1];
		StringBuilder stringBuilder = new StringBuilder(start);
		try {
			do {
				length = inputStreamFromClient.read(smallBuffer, 0, 1);
				if (smallBuffer[0] != charactor) {
					stringBuilder.append(new String(smallBuffer, UTF8_CHARSET));
				}
			} while (length != -1 && smallBuffer[0] != charactor);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		if (length == -1) {
			throw new BadRequest("BadRequest: The request line is not correct"); 
		}
		
		return stringBuilder.toString();
	}
}
