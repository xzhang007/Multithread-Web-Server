package server.http.response;

import server.http.Resource;

public class NoContentResponse extends Response {
	
	public NoContentResponse(Resource resource) {
		super(resource);
		super.code = 204;
		super.reasonPhrase = "No Content";
		super.sendBody = false;
	}
}
