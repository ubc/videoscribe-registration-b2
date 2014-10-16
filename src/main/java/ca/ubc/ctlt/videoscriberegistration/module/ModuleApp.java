package ca.ubc.ctlt.videoscriberegistration.module;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Path("module")
@Produces(MediaType.APPLICATION_JSON)
public class ModuleApp extends Application
{
	private final static Logger logger = LoggerFactory.getLogger(ModuleApp.class);

	@GET
	public String loadCourses()
	{
		// Map courses according to the user's role
		HashMap<String, Integer> ret = new HashMap<String, Integer>();
		ret.put("Test", 1);
		
		Gson gson = new Gson();
		// serialising generic types require specifying the type information since .getClass() doesn't have those
		//Type type = new TypeToken<HashMap<String, List<CourseJson>>>(){}.getType();
		return gson.toJson(ret);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String registerVideoScribe(String message)
	{
		Gson gson = new Gson();
		// parse params
		Map<String, String> params = new HashMap<String, String>();
		params = gson.fromJson(message, params.getClass());
		// create REST client
		Client client = ClientBuilder.newClient();
		// basic authentication defaults to pre-emptive mode, meaning that credentials are always sent
		HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic("username", "password");
		// should probably use a ClientConfig for client configuration later
		client.register(authFeature);
		WebTarget target = client.target("http://my2.sparkol-dev.co.uk").path("registrations/signup-with-licence");
		// prepare parameters for the VideoScribe API
		Map<String, String> vsApiParams = new HashMap<String, String>();
		vsApiParams.put("firstname", params.get("firstname"));
		vsApiParams.put("surname", params.get("lastname"));
		vsApiParams.put("email", params.get("email"));
		vsApiParams.put("password", params.get("password"));
		
		Response resp = target.request(MediaType.APPLICATION_JSON_TYPE).
				post(Entity.entity(gson.toJson(vsApiParams), MediaType.APPLICATION_JSON));
		
		logger.debug("Hello World!!!!");
		logger.debug(Integer.toString(resp.getStatus()));
		logger.debug(resp.readEntity(String.class));
		return "";
	}

}
