package vn.iadd.oim.user.test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import vn.iadd.excel.IExcelModel;
import vn.iadd.excel.IExcelReader;
import vn.iadd.excel.impl.ExcelReaderImpl;
import vn.iadd.oim.helper.UserHelper;
import vn.iadd.oim.user.model.OimExcelUser;
import vn.iadd.oim.util.ConfigUtils;
import vn.iadd.util.Logger;

public class UserBatch {

	static final String file = ConfigUtils.getConfig("EXCEL_FILE_PATH");
	
	static void log(String msg) {
		Logger.log(UserBatch.class, msg);
	}
	
	public static void main(String[] args) {
		int rowHeader = Integer.valueOf(ConfigUtils.getConfig("C_ROW_HEADER"));
		boolean needChangePass = Boolean.valueOf(ConfigUtils.getConfig("USR_NEED_CHANGE_PASS"));
		
		UserHelper helper = new UserHelper(needChangePass);
		
		IExcelReader reader = new ExcelReaderImpl(rowHeader, new OimExcelUser());
		Consumer<List<IExcelModel>> onDone = lstObj -> {
			tryClose(reader);
			List<OimExcelUser> lst = UserHelper.convert(lstObj);
			boolean[] result = helper.createOrUpdate(lst);
			log(Arrays.toString(result));
		};
		reader.readAsync(file, onDone);
	}
	
	static void tryClose(AutoCloseable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
