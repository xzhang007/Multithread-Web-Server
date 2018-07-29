package server.http.helper;

import server.configuration.Htaccess;
import server.configuration.HttpdConf;
import server.http.Request;
import server.http.Resource;
import server.http.response.ForbiddenResponse;
import server.http.response.InternalServerErrorResponse;
import server.http.response.Response;
import server.http.response.UnauthorizedResponse;
import server.exception.ServerConfigNotFoundException;

import java.util.Set;

public class AuthorityHelper {
	
	public static Response checkAuthority(Request request, Resource resource, String absolutePath, Set<String> htaccessFiles, HttpdConf config) {
		Response response = null;
		String accessPath = HtaccessHelper.getHtaccessPath(absolutePath, htaccessFiles, config);
		if (accessPath != null) {
			try {
				Htaccess htaccess = new Htaccess(accessPath);
				htaccess.load();
				if (!request.getHeaders().containsKey("Authorization")) {
					response = new UnauthorizedResponse(resource, htaccess.getAuthName());
					return response;
				} else {
					response = checkUserNameAndPassword(request, resource, htaccess);
				}
			} catch (ServerConfigNotFoundException e) {
				response = new InternalServerErrorResponse(resource);
			}
		}
		return response;
	}
	
	private static Response checkUserNameAndPassword(Request request, Resource resource, Htaccess htaccess) {
		Response response = null;
		String authInfo = request.getHeaders().get("Authorization");
		if (!htaccess.isAuthorized(authInfo)) {
			response = new ForbiddenResponse(resource);
			return response;
		}
		return response;
	}
}
