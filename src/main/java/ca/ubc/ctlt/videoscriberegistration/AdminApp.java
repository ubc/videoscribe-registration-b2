package ca.ubc.ctlt.videoscriberegistration;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import blackboard.data.user.User;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.user.UserDbLoader;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManagerFactory;


// add admin to the path, the complete path would now be /api/admin/<path>
@Path("admin")
// we expect to return JSON content, so set the content-type header to JSON
@Produces(MediaType.APPLICATION_JSON)
public class AdminApp extends Application
{
	private final static Logger logger = LoggerFactory.getLogger(AdminApp.class);
	
	/**
	 * Retrieves user information on the currently logged in user.
	 * 
	 * @return JSON formatted user information.
	 */
	@GET
	public String getDefaultUserInfo()
	{
		// The Blackboard Context object is how you access information about the request that we're serving.
		// This is the same Context accessible with the bbNG page tags.
		Context ctx = ContextManagerFactory.getInstance().getContext();
		User user = ctx.getUser(); // this is the logged in user who initiated this request
		return getUserInfoByUsername(user.getUserName());
	}
	
	/**
	 * Given a username, retrieve the user id, name, email, and student id of that user.
	 * @param username - the username to search to for
	 * @return JSON formatted string containing the user's id, username, name, email and student id.
	 */
	@GET
	@Path("{username}")
	public String getUserInfoByUsername(@PathParam("username") String username)
	{
		User user;
		try
		{
			// loading objects using the Building Block API often requires a *DbLoader object, 
			// getting an instance of the correct *DbLoader object follows this pattern
			UserDbLoader userloader;
			userloader = UserDbLoader.Default.getInstance();
			user = userloader.loadByUserName(username);
		} catch (KeyNotFoundException e)
		{
			throw new NotFoundException("User not found."); // returns a http 404 error
		} catch (PersistenceException e)
		{
			logger.debug("Unable to load user loader.", e);
			throw new InternalServerErrorException("Unable to load user."); // returns a http 500 error
		}
		
		HashMap<String,String> userInfo = new HashMap<String,String>();
		userInfo.put("id", user.getId().toExternalString());
		userInfo.put("username", user.getUserName());
		userInfo.put("name", user.getGivenName() + " " + user.getFamilyName());
		userInfo.put("email", user.getEmailAddress());
		userInfo.put("studentid", user.getStudentId());
		
		// Gson is a Google library for converting Java objects into JSON strings and
		// vice versa. I use Gson cause it's simple, but Jersey has actual integration
		// with other JSON libraries if needed.
		Gson gson = new Gson();
		
		return gson.toJson(userInfo);
	}
}
