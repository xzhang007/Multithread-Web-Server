package server.http.response;

import server.http.Resource;

public class ForbiddenResponse extends Response {
	
	public ForbiddenResponse(Resource resource) {
		super(resource);
		super.code = 403;
		super.reasonPhrase = "Forbidden";
		super.sendBody = false;
	}
}
