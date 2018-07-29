package server.http.response;

import server.http.Resource;

public class NotFoundResponse extends Response {
	
	public NotFoundResponse(Resource resource) {
		super(resource);
		super.code = 404;
		super.reasonPhrase = "Not Found";
		super.sendBody = false;
	}
}
