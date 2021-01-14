@echo off
setlocal enabledelayedexpansion
set ROOT_PATH=%~dp0
set libspath=%ROOT_PATH%build\libs\PackageTool.jar
set librariesPath=%ROOT_PATH%../.idea/libraries
echo %libspath%>libpath.txt
for /f "delims=" %%i in ("%cd%") do set folder=%%~ni
set moduleimlpath=%ROOT_PATH%../.idea/modules/%folder%/%folder%.iml
echo %moduleimlpath%
set moduleimlpath=%moduleimlpath:/=\%
if exist %moduleimlpath% (
    type %moduleimlpath% | findstr "<orderEntry\ type=\"library\"" > tmp.txt
    for /f tokens^=5^ delims^=^" %%i in (tmp.txt) do (
        set item=%%i
        set item=!item::=_!
        set item=!item: =_!
        set item=!item:-=_!
        set item=!item:.=_!
        set item=!item:@=_!
        set item=%librariesPath%\!item!.xml
        set item=!item:/=\!
        if exist !item! (echo !item!>>temp.txt)
      )
      del tmp.txt
)

for /f %%i in (temp.txt) do (
    set flag=0
    echo %%i
    for /f "delims=" %%j in (%%i) do (
        echo "%%j"|findstr "</CLASSES>" > nul
        if  !errorlevel!==0 (
            set flag=1
        ) else (
            if !flag!==0 (
                set tmp=%%j
                set tmp=!tmp:.jar=.jar$!
                set tmp=!tmp:"=!
                for /f "delims=$ tokens=3" %%k in ("!tmp!") do (
                    echo "%%k"|findstr "\/>" >nul
                    if not !errorlevel!==0 (
                        set libspath=!libspath!;C:%HOMEPATH%%%k
                        set libspath=!libspath:/=\!
                    )
                )
            )
        )
    )
)
del temp.txt
echo %libspath% > libpath.txt
pause