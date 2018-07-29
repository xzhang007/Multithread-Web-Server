package server.http;

import server.http.response.Response;
import server.http.helper.HttpDateFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

public class Logger {
	private File file;
	
	public Logger(String fileName) {
		file = new File(fileName);
	}
	
	public synchronized void write(Request request, Response response, String clientIPAddress) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			
			bw.write(clientIPAddress + " - - ");
			System.out.print(clientIPAddress + " - - ");
			bw.write(HttpDateFormat.getCurrentDateForLogger() + " ");
			System.out.print(HttpDateFormat.getCurrentDateForLogger() + " ");
			bw.write(request.getRequestLine() + " ");
			System.out.print(request.getRequestLine() + " ");
			bw.write(response.getCode() + " " + response.getContentLength());
			System.out.println(response.getCode() + " " + response.getContentLength());
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void write(Exception e, String clientIPAddress) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			
			if (e != null && e.getMessage() != null) {
				bw.write(clientIPAddress + " - - ");
				System.out.print(clientIPAddress + " - - ");
				bw.write(HttpDateFormat.getCurrentDateForLogger() + " ");
				System.out.print(HttpDateFormat.getCurrentDateForLogger() + " ");
				bw.write(e.getMessage());
				System.out.println(e.getMessage());
			}
			bw.newLine();
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
}
