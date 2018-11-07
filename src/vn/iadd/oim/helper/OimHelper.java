package vn.iadd.oim.helper;

import java.util.Hashtable;

import javax.security.auth.login.LoginException;

import oracle.iam.platform.OIMClient;
import vn.iadd.oim.util.ConfigUtils;
import vn.iadd.util.Logger;

/**
 * OimHelper
 * @author DaiNV
 * @since 20180530
 *
 */
public class OimHelper implements AutoCloseable {

	private String oimAuthwlPath;
	private String oimAppServerType;
	private String oimFactoryInitType;

	private String oimUrl, oimUser, oimPass;

	private OIMClient oimClient;
	
	/**
	 * Default constructor
	 */
	public OimHelper() {
		this(ConfigUtils.getConfig("OIM_AUTHWL_PATH"), ConfigUtils.getConfig("OIM_APPSERVER_TYPE"),
				ConfigUtils.getConfig("OIM_FACTORY_INITIAL_TYPE"), ConfigUtils.getConfig("OIM_PROVIDER_URL"),
				ConfigUtils.getConfig("OIM_ADMIN_USER"), ConfigUtils.getConfig("OIM_ADMIN_PASS"));
	}

	/**
	 * Constructor 
	 * @param url String
	 * @param user String
	 * @param pass String
	 */
	public OimHelper(String url, String user, String pass) {
		this(ConfigUtils.getConfig("OIM_AUTHWL_PATH"), ConfigUtils.getConfig("OIM_APPSERVER_TYPE"),
				ConfigUtils.getConfig("OIM_FACTORY_INITIAL_TYPE"), url, user, pass);
	}

	/**
	 * Constructor with all parameter
	 * 
	 * @param authwlPath
	 * @param appServerType
	 * @param factoryInitType
	 * @param url
	 * @param user
	 * @param pass
	 * 
	 * @author DaiNV
	 * @since 20180411
	 */
	public OimHelper(String authwlPath, String appServerType, String factoryInitType, String url, String user,
			String pass) {
		oimAuthwlPath = authwlPath;
		oimAppServerType = appServerType;
		oimFactoryInitType = factoryInitType;
		oimUrl = url;
		oimUser = user;
		oimPass = pass;
		init();
	}

	/**
	 * Init
	 */
	public void init() {
		try {
			initialize0();
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public <T> T getService(Class<T> clazz) {
		if (oimClient == null) {
			return null;
		}
		return oimClient.getService(clazz);
	}
	
	private void initialize0() throws LoginException {
		// Set system properties required for OIMClient
		// Logger.log("Begin OIM client Login ");

		System.setProperty("java.security.auth.login.config", oimAuthwlPath);
		System.setProperty("APPSERVER_TYPE", oimAppServerType);

		// Create an instance of OIMClient with OIM environment information
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, oimFactoryInitType);
		env.put(OIMClient.JAVA_NAMING_PROVIDER_URL, oimUrl);

		// Establish an OIM Client
		oimClient = new OIMClient(env);

		try {
			Logger.log(this, "Logging OIM.");
			// Login to OIM with System Administrator Credentials
			oimClient.login(oimUser, oimPass.toCharArray());
			Logger.log("Logging OIM successful.");
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws Exception {
		oimClient.logout();
	}
}
