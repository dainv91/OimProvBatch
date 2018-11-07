package vn.iadd.oim.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import oracle.iam.identity.exception.NoSuchUserException;
import oracle.iam.identity.exception.UserLookupException;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.api.UserManagerConstants;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.authopss.exception.AccessDeniedException;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;
import oracle.iam.provisioning.api.ProvisioningConstants;
import oracle.iam.provisioning.exception.ApplicationInstanceNotFoundException;
import oracle.iam.provisioning.exception.GenericAppInstanceServiceException;
import oracle.iam.provisioning.exception.GenericProvisioningException;
import oracle.iam.provisioning.exception.UserNotFoundException;
import oracle.iam.provisioning.vo.Account;
import vn.iadd.oim.util.ProvisioningUtils;
import vn.iadd.util.Logger;

/**
 * AccountHelper
 * @author DaiNV
 * @since 20180601
 */
public class AccountHelper implements AutoCloseable {
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
	
	@Override
	public void close() throws Exception {
		if (helper != null) {
			helper.close();
		}
	}
	
	public void provResourceAccountToUserWithChildLabel(String userLogin, String appInstName,
			Map<String, Object> parent, Map<String, List<Map<String, Object>>> child) {
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
	
	/**
     * Get the user's usr_key
     * @param userLogin OIM.User Login      (USR_LOGIN)
     * @return value of usr_key
     * @throws NoSuchUserException
     * @throws UserLookupException
     */
    public String getUserKeyByUserLogin(String userLogin) throws NoSuchUserException, UserLookupException
    {
        boolean userLoginUsed = true;
        HashSet<String> attrsToFetch = new HashSet<String>();
        attrsToFetch.add(UserManagerConstants.AttributeName.USER_KEY.getId());
        attrsToFetch.add(UserManagerConstants.AttributeName.USER_LOGIN.getId());
        User user = getUserManager().getDetails(userLogin, attrsToFetch, userLoginUsed);
        return user.getEntityId();
    }
	
	public static void main(String[] args) throws Exception {
		AccountHelper helper = new AccountHelper();
		oracle.iam.provisioning.api.ProvisioningService c = helper.helper.getService(oracle.iam.provisioning.api.ProvisioningService.class);
		System.out.println(c);
		//SearchCriteria criteria = new SearchCriteria("User Login", "dainv", SearchCriteria.Operator.EQUAL);
		final String userKey = helper.getUserKeyByUserLogin("dainv1_1");
		final String resourceObj = "BPMB service User";
		SearchCriteria criteria =  new SearchCriteria(ProvisioningConstants.AccountSearchAttribute.OBJ_NAME.getId(), resourceObj, SearchCriteria.Operator.EQUAL);
        //List<Account> accounts = provOps.getAccountsProvisionedToUser(userKey, criteria , configParams , populateAccountData);
		
		boolean populateAccountData = true;
        HashMap<String,Object> configParams = new HashMap<String,Object>();
        //SearchCriteria search = new SearchCriteria(ProvisioningConstants.AccountSearchAttribute.ACCOUNT_STATUS.getId(), ProvisioningConstants.ObjectStatus.PROVISIONED.getId(), SearchCriteria.Operator.EQUAL);
		List<Account> lst = c.getAccountsProvisionedToUser(userKey, criteria , configParams , populateAccountData);
		
		System.out.println(lst.size());
		for (Account obj: lst) {
			System.out.println(obj + "--" +obj.getAccountID() + "--" + obj.getAccountStatus());
			long accId = Long.parseLong(obj.getAccountID()); 
			c.revoke(accId);
		}
		helper.close();
	}
}
