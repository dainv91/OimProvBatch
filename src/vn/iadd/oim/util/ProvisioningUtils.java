package vn.iadd.oim.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import oracle.iam.provisioning.vo.ChildTableRecord.ACTION;
import oracle.iam.provisioning.vo.FormField;
import oracle.iam.provisioning.vo.FormInfo;
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
	
	/**
	 * Get map from name
	 * @param form FormInfo
	 * @return Map<String, String> UD_XXX - label
	 */
	private static Map<String, String> getMapFormName(FormInfo form) {
		Map<String, String> map = new HashMap<>();
		if (form == null) {
			return map;
		}
		List<FormField> fields = form.getFormFields();
        for (FormField f : fields) {
        	map.put(f.getLabel(), f.getName());
        }
		return map;
	}
	
	/**
	 * Get map form data from label
	 * @param mapFormName Map<String, String> Map Label - form name
	 * @param mapLabel Map<String, Object> Map label - data
	 * @return
	 */
	private static Map<String, Object> getMapFormDataFromLabel(Map<String, String> mapFormName, Map<String, Object> mapLabel) {
		Map<String, Object> map = new HashMap<>();
        for (String label: mapLabel.keySet()) {
        	if (!mapFormName.containsKey(label)) {
        		log("Invalid form label: " + label);
        		continue;
        	}
        	map.put(mapFormName.get(label), mapLabel.get(label));
        }
        return map;
	}
	
	/**
	 * Get child data in form
	 * @param childs List<FormInfo>
	 * @param childDataLabel
	 * @return
	 */
	private static Map<String, ArrayList<ChildTableRecord>> getChildData(List<FormInfo> childs, Map<String, List<Map<String, Object>>> childDataLabel) {
		Map<String, ArrayList<ChildTableRecord>> result = new HashMap<>();
		if (childs == null || childs.isEmpty()) {
			return result;
		}
		if (childDataLabel == null || childDataLabel.isEmpty()) {
			return result;
		}
		for (FormInfo f: childs) {
			Map<String, String> mapChildFormName = getMapFormName(f);
			
			String fName = f.getName();
			ArrayList<ChildTableRecord> childData = new ArrayList<>();
			for (String childUd: childDataLabel.keySet()) {
				if (!fName.equalsIgnoreCase(childUd)) {
					continue;
				}
				List<Map<String, Object>> rows = childDataLabel.get(childUd);
				for (Map<String, Object> row: rows) {
					Map<String, Object> childDataRecord = getMapFormDataFromLabel(mapChildFormName, row);
					
					for (String fieldName: childDataRecord.keySet()) {
						Object value = childDataRecord.get(fieldName);
						ChildTableRecord record = new ChildTableRecord();
						Map<String, Object> mapRecord = new HashMap<>();
						mapRecord.put(fieldName, value);
						record.setAction(ACTION.Add);
						record.setChildData(mapRecord);
						childData.add(record);
					}
				}
			}
			result.put(fName, childData);
		}
		return result;
	}
	
	public static void provisionResourceAccountToUser(OimHelper oimHelper, String userLogin, String appInstName,
			Map<String, Object> parent, Map<String, List<Map<String, Object>>> childDataLabel)
			throws AccessDeniedException, UserNotFoundException, ApplicationInstanceNotFoundException,
			GenericProvisioningException, GenericAppInstanceServiceException, NoSuchUserException, UserLookupException,
			oracle.iam.platform.authz.exception.AccessDeniedException {
		
			// Get OIM User searching by User Login (USR.USR_LOGIN)
			boolean isUserLogin = true; // True for searching by User Login; False for searching by USR_KEY
			Set<String> retAttrs = null; // Return attributes; Null implies returning every attributes on user
			User user = oimHelper.getService(UserManager.class).getDetails(userLogin, retAttrs, isUserLogin); // Get OIM User
			
			log("User: " + user);

			// Get application instance by name (APP_INSTANCE.APP_INSTANCE_NAME)
			ApplicationInstance appInst = oimHelper.getService(ApplicationInstanceService.class)
					.findApplicationInstanceByName(appInstName);
			log("AppInstance: " + appInst);
			
			List<FormInfo> childs = appInst.getChildForms();
			
			Map<String, String> mapFormName  = getMapFormName(appInst.getAccountForm());
	        Map<String, Object> parentData = getMapFormDataFromLabel(mapFormName, parent);
			// Get information required provisioning resource account
			String usrKey = user.getId(); // Get usr_key of OIM User
			Long resourceFormKey = appInst.getAccountForm().getFormKey(); // Get Process Form Key (SDK_KEY)
			String udTablePrimaryKey = null;

			Map<String, ArrayList<ChildTableRecord>> child = getChildData(childs, childDataLabel);
			
			// Construct-Stage Resource Account
			AccountData accountData = new AccountData(String.valueOf(resourceFormKey), udTablePrimaryKey, parentData);
			accountData.setChildData(child);
			Account resAccount = new Account(appInst, accountData);

			// Provision resource account to user
			Long accountId = oimHelper.getService(ProvisioningService.class).provision(usrKey, resAccount); // Account Key = OIU_KEY
			//oimHelper.getService(ProvisioningService.class).getAccountsProvisionedToUser(arg0)
			log("Provisioned: " + accountId + ", userLogin: " + userLogin);
	}
}