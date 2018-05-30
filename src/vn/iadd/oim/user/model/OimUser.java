package vn.iadd.oim.user.model;

import oracle.iam.identity.usermgmt.vo.User;

public class OimUser extends User {

	/**
	 * Serial version
	 */
	private static final long serialVersionUID = 1L;

	public OimUser() {
		this(null);
	}
	
	public OimUser(String entityId) {
		super(entityId);
	}
	
	public User toParent() {
		return this;
	}
}
