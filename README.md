# OimProvBatch
This project CRUD batch OIM User using APIs.

## Getting started

Git clone the project on your local machine and add it to your workspace (I'm using Eclipse)

### Prerequisites

For running this, you will need
 - Java 1.8
 - ExcelReader (https://github.com/dainv91/ExcelReader)
 - Oracle Identity Management libraries(http://www.oracle.com/technetwork/middleware/id-mgmt/overview/index.html) (included in project)

## How to

1. Create class extends **BaseExcelModel** (for example class **OimExcelUser**). It should implements method **mapColumnWithIndex**.
```java
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
```
- And method **createFromFields**
```java
@Override
public IExcelModel createFromFields(Map<String, Field> fields) {
	OimExcelUser user = new OimExcelUser(fields);
	return user;
}
```

2. Create new instance of **IExcelReader**. It takes **rowHeader** and an instance of **IExcelModel** as parameters. For example **ExcelReaderImpl**
```java
IExcelReader reader = new ExcelReaderImpl(rowHeader, new OimExcelUser());
```
3. Now you can use these function of **IExcelReader**
```java
List<IExcelModel> read(String file);	
void readAsync(String file, Consumer<List<IExcelModel>> onDone);
```

*You can check sample in class* **UserBatch**
```java
UserHelper helper = new UserHelper(needChangePass);

IExcelReader reader = new ExcelReaderImpl(rowHeader, new OimExcelUser());
Consumer<List<IExcelModel>> onDone = lstObj -> {
	tryClose(reader);
	List<OimExcelUser> lst = UserHelper.convert(lstObj);
	boolean[] result = helper.createOrUpdate(lst);
	log(Arrays.toString(result));
};
reader.readAsync(file, onDone);
```
4. Update **OimProvBatch_20180529.properties**
**EXCEL_FILE_PATH**: Path of excel file. (Sample excel file: **output/oim_user_20180528.xlsx**)
**C_ROW_HEADER**: Row in excel file are header. Data will be read from **C_ROW_HEADER + 1**
**USR_NEED_CHANGE_PASS**: Update user password or not. Default is **BLANK** or **FALSE**
**OIM_PROVIDER_URL**: OIM 's server address. (Ex: t3://10.4.18.114:14000)
**OIM_ADMIN_USER**: Admin user (Ex: xelsysadm)
**OIM_ADMIN_PASS**: Admin password.
