package vn.iadd.oim.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import oracle.iam.identity.exception.NoSuchUserException;
import oracle.iam.identity.exception.UserLookupException;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.authopss.exception.AccessDeniedException;
import oracle.iam.provisioning.api.ApplicationInstanceService;
import oracle.iam.provisioning.api.ProvisioningService;
import oracle.iam.provisioning.exception.ApplicationInstanceNotFoundException;
import oracle.iam.provisioning.exception.GenericAppInstanceServiceException;
import oracle.iam.provisioning.exception.GenericProvisioningException;
import oracle.iam.provisioning.exception.UserNotFoundException;
import oracle.iam.provisioning.vo.Account;
import oracle.iam.provisioning.vo.AccountData;
import oracle.iam.provisioning.vo.ApplicationInstance;
import oracle.iam.provisioning.vo.ChildTableRecord;
import vn.iadd.oim.helper.OimHelper;
import vn.iadd.util.Logger;

/**
 * ProvisioningUtils
 * 
 * @author DaiNV
 * @since 20180531
 */
public class ProvisioningUtils {

	static void log(Object msg) {
		if (msg == null) {
			return;
		}
		Logger.log(ProvisioningUtils.class, msg.toString());
	}
	
	public static void provisionResourceAccountToUser(OimHelper oimHelper, String userLogin, String appInstName,
			Map<String, Object> parent, Map<String, ArrayList<ChildTableRecord>> child)
			throws AccessDeniedException, UserNotFoundException, ApplicationInstanceNotFoundException,
			GenericProvisioningException, GenericAppInstanceServiceException, NoSuchUserException, UserLookupException,
			oracle.iam.platform.authz.exception.AccessDeniedException {
		// Get OIM User searching by User Login (USR.USR_LOGIN)
		boolean isUserLogin = true; // True for searching by User Login; False for searching by USR_KEY
		Set<String> retAttrs = null; // Return attributes; Null implies returning every attributes on user
		User user = oimHelper.getService(UserManager.class).getDetails(userLogin, retAttrs, isUserLogin); // Get OIM
																											// User
		// logger.log(ODLLevel.NOTIFICATION, "User: {0}", new Object[]{user});

		// Get application instance by name (APP_INSTANCE.APP_INSTANCE_NAME)
		ApplicationInstance appInst = oimHelper.getService(ApplicationInstanceService.class)
				.findApplicationInstanceByName(appInstName);
		// logger.log(ODLLevel.NOTIFICATION, "Application Instance: {0}", new
		// Object[]{appInst});

		// Get information required provisioning resource account
		String usrKey = user.getId(); // Get usr_key of OIM User
		Long resourceFormKey = appInst.getAccountForm().getFormKey(); // Get Process Form Key (SDK_KEY)
		// logger.log(ODLLevel.NOTIFICATION, "Resource Process Form Key: {0}", new
		// Object[]{resourceFormKey});
		String udTablePrimaryKey = null;

		// Construct-Stage Resource Account
		AccountData accountData = new AccountData(String.valueOf(resourceFormKey), udTablePrimaryKey, parent);
		accountData.setChildData(child);
		Account resAccount = new Account(appInst, accountData);

		// Provision resource account to user
		Long accountId = oimHelper.getService(ProvisioningService.class).provision(usrKey, resAccount); // Account Key = OIU_KEY
		// logger.log(ODLLevel.NOTIFICATION, "Provisioning Account Id: {0}", new
		// Object[]{accountId});
		log(accountId);
	}
}