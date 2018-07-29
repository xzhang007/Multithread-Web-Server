package server.http.response;

import server.http.Resource;

public class NotModifiedResponse extends Response {

	public NotModifiedResponse(Resource resource) {
		super(resource);
		super.code = 304;
		super.reasonPhrase = "Not Modified";
		super.sendBody = false;
	}
}
