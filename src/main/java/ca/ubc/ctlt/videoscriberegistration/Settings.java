package ca.ubc.ctlt.videoscriberegistration;

import java.util.HashMap;
import java.util.Map;

import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManagerFactory;

import com.google.gson.Gson;
import com.spvsoftwareproducts.blackboard.utils.B2Context;

public class Settings
{
	public final static String MODE_SETTING = "mode_setting";
	public final static String INSTRUCTIONS_SETTING = "instructions_setting";
	public final static String PROD_USERNAME_SETTING = "prod_username_setting";
	public final static String PROD_PASSWORD_SETTING = "prod_password_setting";
	public final static String DEV_USERNAME_SETTING = "dev_username_setting";
	public final static String DEV_PASSWORD_SETTING = "dev_password_setting";
	
	public final static String PROD_MODE = "prod";
	public final static String DEV_MODE = "dev";
	
	private B2Context settings;
	
	/**
	 * Local class for converting settings to JSON and back 
	 */
	class Config
	{
		public String mode = "";
		public String instructions = "";
		public Map<String, String> prod = new HashMap<String, String>();
		public Map<String, String> dev = new HashMap<String, String>();
	}

	public Settings()
	{
		Context ctx = ContextManagerFactory.getInstance().getContext();
		settings = new B2Context(ctx.getRequest());
	}
	
	/**
	 * There are dev and prod API servers, need to switch between them as configured.
	 * @return The base url that we add the API path to.
	 */
	public String getApiBaseUrl()
	{
		if (isProdMode()) return "https://my.sparkol.com";
		return "http://my.sparkol-dev.co.uk/";
	}
	/**
	 * Get the username appropriate for the mode selected.
	 * @return
	 */
	public String getUsername()
	{
		if (isProdMode()) return getProdUsername();
		return getDevUsername();
	}
	/**
	 * Get the password appropriate for the mode selected
	 * @return
	 */
	public String getPassword()
	{
		if (isProdMode()) return getProdPassword();
		return getDevPassword();
	}
	/**
	 * Instructions for how to use the VideoScribe registration module
	 * @return
	 */
	public String getInstructions()
	{
		return settings.getSetting(INSTRUCTIONS_SETTING,
			"<p>Please fill in the form below and then click 'Register' to create your VideoScribe account. " +
			"Note that you don't have to put down your real name, but the email must be valid since you will" +
			" get additional instructions by email.</p>");
	}
	/**
	 * Converts the settings to JSON representation.
	 * @return
	 */
	public String toJson()
	{
		Config config = new Config();
		config.mode = getMode();
		config.instructions = getInstructions();
		config.prod.put("username", getProdUsername());
		config.prod.put("password", getProdPassword());
		config.dev.put("username", getDevUsername());
		config.dev.put("password", getDevPassword());
		
		Gson gson = new Gson();
		return gson.toJson(config);
	}
	
	public void updateFromJson(String json) throws InvalidOption
	{
		Gson gson = new Gson();
		Config config = gson.fromJson(json, Config.class);

		if (!config.mode.equals(PROD_MODE) && !config.mode.equals(DEV_MODE)) 
		{
			throw new InvalidOption("Operating mode selected is invalid!");
		}
		settings.setSetting(MODE_SETTING, config.mode);
		settings.setSetting(INSTRUCTIONS_SETTING, config.instructions);
		settings.setSetting(PROD_USERNAME_SETTING, config.prod.get("username"));
		settings.setSetting(PROD_PASSWORD_SETTING, config.prod.get("password"));
		settings.setSetting(DEV_USERNAME_SETTING, config.dev.get("username"));
		settings.setSetting(DEV_PASSWORD_SETTING, config.dev.get("password"));
		settings.persistSettings();
	}
	
	/**
	 * @return True if we're in production mode, false otherwise. If no values stored, defaults to dev mode.
	 */
	private boolean isProdMode()
	{
		if (getMode().equals(PROD_MODE)) return true;
		return false;
	}
	// boilerplate for getting each setting
	private String getMode()
	{
		return settings.getSetting(MODE_SETTING, DEV_MODE);
	}
	private String getProdUsername()
	{
		return settings.getSetting(PROD_USERNAME_SETTING, "");
	}
	private String getProdPassword()
	{
		return settings.getSetting(PROD_PASSWORD_SETTING, "");
	}
	private String getDevUsername()
	{
		return settings.getSetting(DEV_USERNAME_SETTING, "");
	}
	private String getDevPassword()
	{
		return settings.getSetting(DEV_PASSWORD_SETTING, "");
	}


}
