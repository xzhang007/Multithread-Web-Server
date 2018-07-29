package server.http.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;
import java.text.ParseException;

public class HttpDateFormat {
	private static DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	private static DateFormat dateFormatForLogger = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss Z]");
	
	static {
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public static Date getDate(String dateString) {
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String getCurrentDate() {
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String getCurrentDateForLogger() {
		Date date = new Date();
		return dateFormatForLogger.format(date);
	}
}
