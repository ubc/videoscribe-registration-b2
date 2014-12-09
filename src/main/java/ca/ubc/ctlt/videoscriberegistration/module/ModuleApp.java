package ca.ubc.ctlt.videoscriberegistration.module;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.SslConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blackboard.data.user.User;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManagerFactory;
import ca.ubc.ctlt.videoscriberegistration.Settings;

import com.google.gson.Gson;

@Path("module")
@Produces(MediaType.APPLICATION_JSON)
public class ModuleApp extends Application
{
	private final static Logger logger = LoggerFactory.getLogger(ModuleApp.class);

	@GET
	public String getDefaultUserInfo()
	{
		ensureAccess();
		// The Blackboard Context object is how you access information about the request that we're serving.
		// This is the same Context accessible with the bbNG page tags.
		Context ctx = ContextManagerFactory.getInstance().getContext();
		User user = ctx.getUser(); // this is the logged in user who initiated this request
		Settings settings = new Settings();
		HashMap<String,String> info = new HashMap<String,String>();
		info.put("instructions", settings.getInstructions());
		info.put("firstname", user.getGivenName());
		info.put("lastname", user.getFamilyName());
		info.put("email", user.getEmailAddress());
		Gson gson = new Gson();
		return gson.toJson(info);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerVideoScribe(String message)
	{
		ensureAccess();
		Settings settings = new Settings();
		Gson gson = new Gson();
		// parse params
		Map<String, String> params = new HashMap<String, String>();
		params = gson.fromJson(message, params.getClass());
		// create REST client
		Client client = ClientBuilder.newClient();
		// basic authentication defaults to pre-emptive mode, meaning that credentials are always sent
		HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic(
				settings.getUsername(), settings.getPassword());
		// should probably use a ClientConfig for client configuration later
		client.register(authFeature);
		WebTarget target = client.target(settings.getApiBaseUrl()).path("api/registrations/signup-with-licence");
		logger.debug("VideoScribe API URL: " + target.toString());
		// prepare parameters for the VideoScribe API
		Map<String, String> vsApiParams = new HashMap<String, String>();
		vsApiParams.put("firstname", params.get("firstname"));
		vsApiParams.put("surname", params.get("lastname"));
		vsApiParams.put("email", params.get("email"));
		vsApiParams.put("password", params.get("password"));
		
		Response resp = target.request(MediaType.APPLICATION_JSON_TYPE).
				post(Entity.entity(gson.toJson(vsApiParams), MediaType.APPLICATION_JSON));
		
		if (resp.getStatus() >= 200 && resp.getStatus() < 300)
		{
			return Response.ok("{\"status\":\"success\"}").build();
		}
		else if (resp.getStatus() >= 400 && resp.getStatus() < 500)
		{
			String errorJson = resp.readEntity(String.class);
			logger.error("Videoscribe API returned " + resp.getStatus() + " with message:" + 
				errorJson);
			return Response.status(400).entity(
				"{\"status\":\"error\", \"desc\":"+ errorJson +"}").build();
		}
		else
		{
			logger.error("Videoscribe API returned " + resp.getStatus() + " with message:" + 
				resp.readEntity(String.class));
			throw new InternalServerErrorException();
		}
	}
	
	/**
	 * Ensure that users must be logged in.
	 */
	private void ensureAccess()
	{
		Context ctx = ContextManagerFactory.getInstance().getContext();
		if (!ctx.getSession().isAuthenticated())
		{
			throw new ForbiddenException();
		}
	}

}
