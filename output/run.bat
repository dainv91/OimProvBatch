@echo off

::java -cp .;*;..\libs/*;..\libs\ooxml-lib/*;..\libs\poi-lib/*;..\libs\oim/*;..\libs\oim\common_lib/*;..\libs\oim\ext/*;..\libs\oim\lib/* vn.iadd.oim.user.test.UserBatch

setlocal
call set_classpath.bat
set CLASSPATH=%CLASSPATH%;.;

%JAVA_HOME%\bin\java -cp %CLASSPATH% vn.iadd.oim.user.test.UserBatch