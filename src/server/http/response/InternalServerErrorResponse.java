package server.http.response;

import server.http.Resource;

public class InternalServerErrorResponse extends Response {
	
	public InternalServerErrorResponse(Resource resource) {
		super(resource);
		super.code = 500;
		super.reasonPhrase = "Internal Server Error";
		super.sendBody = false;
	}
}
