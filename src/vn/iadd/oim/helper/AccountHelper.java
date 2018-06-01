package vn.iadd.oim.helper;

import java.util.ArrayList;
import java.util.Map;

import oracle.iam.identity.exception.NoSuchUserException;
import oracle.iam.identity.exception.UserLookupException;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.platform.authopss.exception.AccessDeniedException;
import oracle.iam.provisioning.exception.ApplicationInstanceNotFoundException;
import oracle.iam.provisioning.exception.GenericAppInstanceServiceException;
import oracle.iam.provisioning.exception.GenericProvisioningException;
import oracle.iam.provisioning.exception.UserNotFoundException;
import oracle.iam.provisioning.vo.ChildTableRecord;
import vn.iadd.oim.util.ProvisioningUtils;
import vn.iadd.util.Logger;

/**
 * AccountHelper
 * @author DaiNV
 * @since 20180601
 */
public class AccountHelper {
	/**
	 * OimHelper
	 */
	private OimHelper helper;
	
	/**
	 * Oracle UserManager
	 */
	private UserManager userManager;

	/**
	 * Default constructor
	 */
	public AccountHelper() {
		this(new OimHelper());
	}
	
	public AccountHelper(OimHelper oim) {
		this.helper = oim;
	}
	
	public void provisionResourceAccountToUser(String userLogin, String appInstName,
			Map<String, Object> parent, Map<String, ArrayList<ChildTableRecord>> child) {
		try {
			ProvisioningUtils.provisionResourceAccountToUser(helper, userLogin, appInstName, parent, child);
		} catch (NoSuchUserException | UserLookupException | AccessDeniedException
				| oracle.iam.platform.authz.exception.AccessDeniedException | UserNotFoundException
				| ApplicationInstanceNotFoundException | GenericProvisioningException
				| GenericAppInstanceServiceException e) {
			Logger.log(this, e.getMessage());
		}
	}
	
	public UserManager getUserManager() {
		if (userManager == null) {
			userManager = helper.getService(UserManager.class);
		}
		return userManager;
	}
}
