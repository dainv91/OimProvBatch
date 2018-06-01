package vn.iadd.oim.user.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import oracle.iam.provisioning.vo.ChildTableRecord;
import vn.iadd.oim.helper.AccountHelper;
import vn.iadd.oim.util.ConfigUtils;
import vn.iadd.util.Logger;

public class AccountBatch {

	static final String file = ConfigUtils.getConfig("EXCEL_FILE_PATH");

	static void log(String msg) {
		Logger.log(UserBatch.class, msg);
	}

	public static void main(String[] args) {
		//int rowHeader = Integer.valueOf(ConfigUtils.getConfig("C_ROW_HEADER"));
		//boolean needChangePass = Boolean.valueOf(ConfigUtils.getConfig("USR_NEED_CHANGE_PASS"));
		
		AccountHelper helper = new AccountHelper();
		/*
		IExcelReader reader = new ExcelReaderImpl(rowHeader, new OimExcelUser());
		Consumer<List<IExcelModel>> onDone = lstObj -> {
			vn.iadd.oim.util.ObjectUtil.tryClose(reader);
			List<OimExcelUser> lst = UserHelper.convert(lstObj);
			boolean[] result = helper.createOrUpdate(lst);
			log(Arrays.toString(result));
		};
		reader.readAsync(file, onDone);*/
		String userLogin = "DAINV0_0";
		String appInstName = "BPMBService";
		Map<String, Object> parent = new HashMap<>();
		parent.put("Login", "DAINV0_0");
		parent.put("Email", "DAINV0_0@gmail.com");
		Map<String, ArrayList<ChildTableRecord>> child = new HashMap<>();
		helper.provisionResourceAccountToUser(userLogin, appInstName, parent, child);
	}
}
