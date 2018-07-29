package server.http.response;

import server.http.Resource;

public class BadRequestResponse extends Response {
	
	public BadRequestResponse(Resource resource) {
		super(resource);
		super.code = 400;
		super.reasonPhrase = "Bad Request";
		super.sendBody = false;
	}
}
