package server.http.response;

import server.http.Resource;

public class CreatedResponse extends Response {
	
	public CreatedResponse(Resource resource) {
		super(resource);
		super.code = 201;
		super.reasonPhrase = "Created";
		super.sendBody = false;
	}
}
