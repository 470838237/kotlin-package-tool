@echo off
::chcp 936  gbk
::65001 utf-8
chcp 936
setlocal enabledelayedexpansion
set ROOT_PATH=%~dp0
set GRADLEW_CMD=%ROOT_PATH%../gradlew.bat
echo %GRADLEW_CMD%
call %GRADLEW_CMD% -p %ROOT_PATH%  assemble
set allparam=
set cppath=
:param
set str=%1
if "%str%"=="" (
    goto end
)

set allparam=%allparam% %str%

shift /0
goto param
:end

if not defined allparam (
    echo not define allparam
    goto :eof
 )
if not "%allparam%"=="" ( echo %allparam% >nul ) else echo no params

set mainpath=%ROOT_PATH%build\libs\PackageTool.jar
for /f %%i in ( libpath.txt ) do (
    set cppath=%%i
)
java -classpath %cppath% com.honor.packageTool.Main %allparam%











