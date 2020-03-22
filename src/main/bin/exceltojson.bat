@echo off
chcp 65001
::返回上一層目錄
cd..
::設置JAVA環境路徑
SET JAVA_PATH=%JAVA_HOME%
::設置應用環境路徑
SET EXCELTOJSON_HOME=%cd%
::設置應用啟動參數
SET STARTUP_MODE=RELEASE EXCELTOJSON
::設定應用啟動類
SET EXCELTOJSON_CLASS="com.tiny.exceltojson.ExcelApp"
::設置JVM環境參數
SET JAVA_OPTS=-Xms512M -Xmx512M -Xmn512M -XX:+UseG1GC -XX:MaxGCPauseMillis=200
SET JAVA_OPTS=%JAVA_OPTS% -Dapp.logPath=%EXCELTOJSON_HOME%
::設置依賴資源
SET CP=%EXCELTOJSON_HOME%\lib\*;%EXCELTOJSON_HOME%\config

::顯示訊息
echo %EXCELTOJSON_HOME% @ %STARTUP_MODE% start

::執行
start javaw %JAVA_OPTS% -cp %CP% %EXCELTOJSON_CLASS% %STARTUP_MODE%