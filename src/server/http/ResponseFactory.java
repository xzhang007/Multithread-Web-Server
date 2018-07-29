package server.http;

import server.http.helper.HttpDateFormat;
import server.http.response.*;
import server.configuration.MimeTypes;
import server.configuration.HttpdConf;
import server.http.helper.AuthorityHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ResponseFactory {
	private static final int BUFFERLENGTH = 250;
	private static final Pattern PATTERN = Pattern.compile(".*[?](.*)");
	
	static Response getResponse(int code, Resource resource) {
		if (code == 400) {
			return new BadRequestResponse(resource);
		}
		if (code == 500) {
			return new InternalServerErrorResponse(resource);
		}
		return null;
	}
	
	static Response getResponse(Request request, Resource resource, MimeTypes mimeTypes, Set<String> htaccessFiles, HttpdConf config) {
		Response response = null;
		try {
			String absolutePath = resource.getAbsolutePath();
			// access
			response = AuthorityHelper.checkAuthority(request, resource, absolutePath, htaccessFiles, config);
			if (response != null) {
				return response;
			}
			
			System.out.println("Received Request Message: Verb: " + request.getVerb() + " Absolute Path: " + absolutePath);
			File file = new File(absolutePath);
			if (file.exists() || request.getVerb().equals("PUT")) {
				if (resource.isScript()) {
					InputStream is = executeScript(resource, request);
					response = new OKResponse(resource, true, mimeTypes, is);
					return response;
				} else {
					response = doVerb(file, request, resource, mimeTypes);
					return response;
				}
			} else {
				response = new NotFoundResponse(resource);
				return response;
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	private static Response doVerb(File file, Request request, Resource resource, MimeTypes mimeTypes) {
		Response response = null;
		String verb = request.getVerb();
		switch (verb) {
			case "PUT":
				createOrModifyFile(file, request);
				response = new CreatedResponse(resource);
			break;
			case "DELETE":
				if (deleteFile(file)) {
					response = new NoContentResponse(resource);
				}
			break;
			case "POST":  // same as "GET"
				response = new OKResponse(resource, true, mimeTypes);
			break;
			case "HEAD":
				response = new HeadOKResponse(resource, mimeTypes);
			break;
			case "GET":
				if (request.getHeaders().containsKey("If-Modified-Since")) {
					response = checkIfModified(file, request) ? new OKResponse(resource, true) : new NotModifiedResponse(resource);
				} else {
					response = new OKResponse(resource, true, mimeTypes);
				}
			break;
		}
		
		return response;
	}
	
	private static void createOrModifyFile(File file, Request request) {
		FileOutputStream fos = null;
		InputStream body = null;
		
		try {
			fos = new FileOutputStream(file);
			byte[] buffer = new byte[BUFFERLENGTH];
			body = request.getBody();
			
			int temp = 0;
			while (true) {
				if ((temp != 0 && temp < BUFFERLENGTH) || temp == -1 || body.available() == 0) {  // important
					break;
				}
				temp = body.read(buffer, 0, buffer.length);  // cannot read more
				
				/*if (temp == -1) {
					break;
				}*/
				
				fos.write(buffer, 0, temp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				//body.close(); // Don't close, otherwise socket closed too
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean deleteFile(File file) {
		boolean flag = false;
		try {
			flag = file.delete();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	private static boolean checkIfModified(File file, Request request) {
		long lastModified = file.lastModified();
		Date lastModifiedDate = new Date(lastModified);
		
		String ifModified = request.getHeaders().get("If-Modified-Since");
		Date ifModifiedDate = HttpDateFormat.getDate(ifModified);
		
		return lastModifiedDate.after(ifModifiedDate);
	}
	
	private static InputStream executeScript(Resource resource, Request request) {
		ProcessBuilder pb = new ProcessBuilder(resource.getAbsolutePath());
		setEnvironment(pb.environment(), request);
		
		Process p = null;
		InputStream is = null;
		try {
			p = pb.start();
			is = p.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			/*try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}
		return is;
	}
	
	private static void setEnvironment(Map<String, String> env, Request request) {
		Map<String, String> headers = request.getHeaders();
		if (headers != null && !headers.isEmpty()) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				env.put("HTTP_" + entry.getKey().toUpperCase(), entry.getValue());
			}
		}
		if (request.getUri().matches(".*[?].*")) {
			String queryString = getQueryString(request);
			env.put("QUERY_STRING", queryString);
		}
		env.put("SERVER_PROTOCOL", request.getHttpVersion());
	}
	
	private static String getQueryString(Request request) {
		String queryString = null;
		Matcher matcher = PATTERN.matcher(request.getUri());
		if (matcher.matches()) {
			queryString = matcher.group(1);
		}
		return queryString;
	}
}
