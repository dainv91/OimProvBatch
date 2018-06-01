package vn.iadd.oim.user.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import oracle.iam.provisioning.vo.ChildTableRecord;
import vn.iadd.excel.IExcelReader;
import vn.iadd.excel.impl.ExcelReaderImpl;
import vn.iadd.oim.helper.AccountHelper;
import vn.iadd.oim.util.ConfigUtils;
import vn.iadd.util.Logger;

public class AccountBatch {

	static final String file = ConfigUtils.getConfig("EXCEL_FILE_PATH");

	static void log(String msg) {
		Logger.log(UserBatch.class, msg);
	}

	public static void main(String[] args) {
		//testProv();
		//testFunc();
		provBatch(args);
	}
	
	static void provBatch(String[] args) {
		int rowHeader = Integer.valueOf(ConfigUtils.getConfig("C_ROW_HEADER"));
		IExcelReader reader = new ExcelReaderImpl(rowHeader, null);
		Consumer<List<Map<String, Object>>> onDone = lstObj -> {
			// Read child form
			Consumer<List<Map<String, Object>>> onChildDone = lstChild -> {
				vn.iadd.oim.util.ObjectUtil.tryClose(reader);
				
			};
			String sheetChildForm = ConfigUtils.getConfig("EX_SHEET_NAME_CHILD");
			((ExcelReaderImpl) reader).setSheetIndexOrNameToRead(sheetChildForm);
			reader.readToMapAsync(file, onChildDone);
		};
		reader.readToMapAsync(file, onDone);
	}
	
	static void testProv() {
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
	
	static void testFunc() {
		Map<String, String> colObj = new HashMap<>();
		BiConsumer<Map<String, String>, Object> colEvent = (col, value) -> {
			log("BI_COL_EVT:" + value);
			col.put(value.toString(), null);
		};
		List<Map<String, String>> rowObj = new ArrayList<>();
		BiConsumer<List<Map<String, String>>, Map<String, String>> rowEvent= (row, value) -> {
			log("BI_ROW_EVT:" + value);
			Map<String, String> tmp = new HashMap<>();
			tmp.putAll(value);
			row.add(tmp);
			value.clear();
		};
		log("Final====");
		testFuncChild(file, colObj, colEvent, rowObj, rowEvent);
		log(Arrays.toString(rowObj.toArray()));
	}
	
	static <C1, R1>void testFuncChild(String file, C1 colObj, BiConsumer<C1, Object> colEvent, R1 rowObj, BiConsumer<R1, C1> rowEvent)  {
		for (int i=0; i<2; i++) {
			//log("Row: " + i);
			
			for (int j =0; j< 3; j++) {
				//log("Col: " + j);
				colEvent.accept(colObj, i+"<--Col-" + j);
			}
			rowEvent.accept(rowObj, colObj);
		}
	}
}
