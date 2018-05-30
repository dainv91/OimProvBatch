package vn.iadd.oim.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.iam.identity.exception.NoSuchUserException;
import oracle.iam.identity.exception.UserDeleteException;
import oracle.iam.identity.exception.UserDisableException;
import oracle.iam.identity.exception.UserEnableException;
import oracle.iam.identity.exception.UserManagerException;
import oracle.iam.identity.exception.ValidationFailedException;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.api.UserManagerConstants;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.authz.exception.AccessDeniedException;
import vn.iadd.excel.IExcelModel;
import vn.iadd.oim.user.model.OimExcelUser;
import vn.iadd.util.Logger;

public class UserHelper {
	
	
	private OimHelper helper = new OimHelper();
	
	private UserManager userManager;

	private boolean needChangePass;
	
	public UserHelper() {
		this(false);
	}
	
	public UserHelper(boolean needChangePass) {
		this.needChangePass = needChangePass;
	}
	
	public static List<OimExcelUser> convert(List<IExcelModel> lst) {
		List<OimExcelUser> result = new ArrayList<>();
		if (lst == null || lst.isEmpty()) {
			return result;
		}
		for (IExcelModel row : lst) {
			result.add((OimExcelUser) row);
		}
		return result;
	}

	public static void copy(OimExcelUser excelRow, User user, boolean isCreate) {
		if (user == null) {
			return;
		}
		if (excelRow == null) {
			return;
		}
		
		user.setAttribute("First Name", excelRow.getFirstName());
		user.setAttribute("Middle Name", excelRow.getMiddleName());
		user.setAttribute("Last Name", excelRow.getLastName());

		user.setAttribute("Email", excelRow.getEmail());
		// user.setAttribute("Manager", excelRow.getManager());
		// user.setAttribute("Xellerate Type", excelRow.getUserType());
		// //user.setAttribute("Xellerate Type","End-User");
		user.setAttribute("Role", excelRow.getUserType()); // User Type
		user.setAttribute("act_key", excelRow.getOrganization().longValue());
		user.setAttribute("Display Name", excelRow.getDisplayName());
		user.setAttribute("User Login", excelRow.getUserLogin());
		
		if (isCreate) {
			//oracle.iam.identity.utils.Constants.PASSWORD;
			user.setAttribute("usr_password", excelRow.getPassword());	
		}
	}
	
	public static User convert(OimExcelUser excelRow) {
		User user = new User(null);
		copy(excelRow, user, true);
		return user;
	}

	public boolean[] createOrUpdate(List<OimExcelUser> rows) {
		if (rows == null || rows.isEmpty()) {
			return null;
		}
		boolean[] result = new boolean[rows.size()];
		for (int i = 0; i < result.length; i++) {
			OimExcelUser row = rows.get(i);
			if (Boolean.TRUE.equals(row.getNeedDelete())) {
				result[i] = delete(row.getUserLogin());
				continue;
			}
			if (row.getNeedEnable() != null) {
				result[i] = toggleUserStatus(row.getUserLogin(), row.getNeedEnable());
				continue;
			}
			
			result[i] = createOrUpdate(row);
		}
		return result;
	}

	public boolean createOrUpdate(OimExcelUser excelRow) {
		boolean created = false;
		if (excelRow == null) {
			return created;
		}
		boolean needDelete = false;
		if (excelRow.getNeedDelete() != null) {
			needDelete = excelRow.getNeedDelete().booleanValue();
		}
		if (needDelete) {
			return false;
		}
		
		User oimUser = search(excelRow.getUserLogin());
		boolean existed = false;
		if (oimUser != null) {
			// Update
			existed = true;
		}
		
		try {
			if (existed) {
				// Update
				copy(excelRow, oimUser, false);
				getUserManager().modify(oimUser);
				if (needChangePass) {
					boolean passwordChanged = changePass(excelRow, oimUser);
					Logger.log(this, String.format("User {%s}, password changed: {%s}", oimUser.getLogin(), passwordChanged));	
				}
			} else {
				// Create
				oimUser = convert(excelRow);
				getUserManager().create(oimUser);
			}
			created = true;
		} catch (Exception e) {
			Logger.log(this, String.format("createOrUpdate {%s}: {%s}", oimUser.getLogin(), e.getMessage()));
		}
		return created;
	}
	
	public boolean toggleUserStatus(String userId, Boolean enable) {
		if (enable == null) {
			return true;
		}
		boolean success = false;
		boolean isUserLogin = true;
		try {
			if (enable.booleanValue()) {
				// Enable
				getUserManager().enable(userId, isUserLogin);
			} else {
				// Disable
				getUserManager().disable(userId, isUserLogin);
			}
			success = true;
		} catch (ValidationFailedException | NoSuchUserException | AccessDeniedException | UserDisableException | UserEnableException e) {
			//e.printStackTrace();
			Logger.log(this, String.format("toggleUserStatus {%s}: %s", userId, e.getMessage()));
		}
		return success;
	}
	
	public boolean delete(String userId) {
		boolean success = false;
		boolean isUserLogin = true;
		try {
			//UserManagerResult ret = getUserManager().delete(userId, isUserLogin);
			getUserManager().delete(userId, isUserLogin);
			//Logger.log(this, ret.getStatus());
			success = true;
		} catch (ValidationFailedException | UserDeleteException | NoSuchUserException | AccessDeniedException e) {
			//e.printStackTrace();
			Logger.log(this, String.format("delete {%s}: %s", userId, e.getMessage()));
		}
		return success;
	}
	
	private boolean changePass(OimExcelUser excelRow, User oimUser) {
		boolean success = false;
		if (excelRow.getPassword() == null) {
			return success;
		}
		boolean isUserLogin = true;
		boolean sendNotification = false;
		try {
			getUserManager().changePassword(oimUser.getLogin(), excelRow.getPassword().toCharArray(), isUserLogin, sendNotification);
			success = true;
		} catch (UserManagerException | AccessDeniedException e) {
			// Skip password error
			//e.printStackTrace();
		}
		return success;
	}
	
	private User search(String userLogin) {
		boolean userLoginUsed = true; // True for searching by User Login; False for searching by USR_KEY
		Set<String> attrsToFetch = new HashSet<String>();
		//attrsToFetch.add(UserManagerConstants.AttributeName.USER_KEY.getId());
		attrsToFetch.add(UserManagerConstants.AttributeName.USER_LOGIN.getId());
		try {
			User info = getUserManager().getDetails(userLogin, attrsToFetch, userLoginUsed);
			//return info;
			return new User(info.getEntityId());
		} catch (Exception e) {
			//e.printStackTrace();
			Logger.log(this, String.format("search {%s}: %s", userLogin, e.getMessage()));
		}
		return null;
	}
	
	private UserManager getUserManager() {
		if (userManager == null) {
			userManager = helper.getService(UserManager.class);
		}
		return userManager;
	}
}
