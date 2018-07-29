package server.http.response;

import server.http.Resource;
import server.configuration.MimeTypes;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class OKResponse extends Response {
	private InputStream is;
	private String tempFile = "tempFile";
	private Path path;
	
	public OKResponse(Resource resource) {
		super(resource);
		super.code = 200;
		super.reasonPhrase = "OK";
		super.sendBody = false;
	}
	
	public OKResponse(Resource resource, boolean flag) {
		this(resource);
		super.sendBody = flag;
	}
	
	public OKResponse(Resource resource, boolean flag, MimeTypes mimeTypes) {
		this(resource, flag);
		super.mimeTypes = mimeTypes;
	}
	
	public OKResponse(Resource resource, boolean flag, MimeTypes mimeTypes, InputStream is) {
		this(resource, flag, mimeTypes);
		this.is = is;
		path = Paths.get(tempFile);
		try {
			Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void send(OutputStream out) {
		sendHeaders(out);
		
		if (super.sendBody) {
			if (is != null) {
				sendScriptOutput(out);
			} else {
				sendBody(out);
			}
		}
	}
	
	@Override
	void sendContentHeaders(PrintWriter writer) {
		String contentType = getContentType();
		contentType = is != null ? "text/html" : contentType;
		writer.write("Content-type: " + contentType + "\r\n");
		
		int contentLength = getContentLength();
		contentLength = is != null ? (int) path.toFile().length() : contentLength;
		writer.write("Content-length: " + contentLength + "\r\n");
	}
	
	private void sendScriptOutput(OutputStream out) {
		if (is != null) {
			try {
				Files.copy(path, out);
				return;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				path.toFile().delete();
			}
		}
	}
}
