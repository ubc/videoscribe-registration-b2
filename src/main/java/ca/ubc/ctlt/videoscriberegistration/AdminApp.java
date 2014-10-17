package ca.ubc.ctlt.videoscriberegistration;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blackboard.data.user.User;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManagerFactory;

// add admin to the path, the complete path would now be /api/admin/<path>
@Path("admin")
// we expect to return JSON content, so set the content-type header to JSON
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
		ensureAccess();

		// The Blackboard Context object is how you access information about the request that we're serving.
		// This is the same Context accessible with the bbNG page tags.
		Settings settings = new Settings();
		return settings.toJson();
	}
	
	/**
	 * Given a username, retrieve the user id, name, email, and student id of that user.
	 * @param username - the username to search to for
	 * @return JSON formatted string containing the user's id, username, name, email and student id.
	 */
	@POST
	public String getUserInfoByUsername(String json)
	{
		ensureAccess();

		Settings settings = new Settings();
		try
		{
			settings.updateFromJson(json);
		} catch (InvalidOption e)
		{
			logger.error("Unable to update settings.", e);
			throw new BadRequestException("Invalid operation mode selected!");
		}
		return settings.toJson();
	}
	
	/**
	 * Only system admins can access these APIs
	 */
	private void ensureAccess()
	{
		Context ctx = ContextManagerFactory.getInstance().getContext();
		if (ctx.getSession().isAuthenticated())
		{
			User user = ctx.getUser();
			if (!user.getSystemRole().equals(User.SystemRole.SYSTEM_ADMIN))
			{
				throw new ForbiddenException();
			}
		}
		else
		{
			throw new ForbiddenException();
		}
	}
}
