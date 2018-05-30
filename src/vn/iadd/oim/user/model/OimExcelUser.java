package vn.iadd.oim.user.model;

import java.lang.reflect.Field;
import java.util.Map;

import vn.iadd.excel.BaseExcelModel;
import vn.iadd.excel.IExcelModel;

public class OimExcelUser extends BaseExcelModel {

	/**
	 * Serial version
	 */
	private static final long serialVersionUID = 1L;

	private String firstName, middleName, lastName, email;
	private String manager, userType;
	private Double organization; // act_key
	private String displayName, userLogin, password;
	
	private Boolean needDelete, needEnable;
	
	public OimExcelUser() {
		init();
	}
	
	public OimExcelUser(Map<String, Field> fields) {
		super(fields);
		init();
	}
	
	private void init() {
		this.organization = 1D;
	}
	
	@Override
	public IExcelModel createFromFields(Map<String, Field> fields) {
		OimExcelUser user = new OimExcelUser(fields);
		return user;
	}

	@Override
	public void mapColumnWithIndex() {
		int pos = 1;
		addColumnIndex("No.", pos++);
		addColumnIndex("firstName", pos++);
		addColumnIndex("middleName", pos++);
		addColumnIndex("lastName", pos++);
		addColumnIndex("email", pos++);
		addColumnIndex("manager", pos++);
		addColumnIndex("userType", pos++);
		addColumnIndex("organization", pos++);
		addColumnIndex("displayName", pos++);
		addColumnIndex("userLogin", pos++);
		addColumnIndex("password", pos++);
		addColumnIndex("needDelete", pos++);
		addColumnIndex("needEnable", pos++);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Double getOrganization() {
		return organization;
	}

	public void setOrganization(Double organization) {
		this.organization = organization;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getNeedDelete() {
		return needDelete;
	}

	public void setNeedDelete(Boolean needDelete) {
		this.needDelete = needDelete;
	}

	public Boolean getNeedEnable() {
		return needEnable;
	}

	public void setNeedEnable(Boolean needEnable) {
		this.needEnable = needEnable;
	}
}
