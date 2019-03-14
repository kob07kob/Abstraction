ECHO OFF
for /R %%i in (./src/svcomp/myown/*.cfa) do (
echo src/svcomp/myown/%%~ni.cfa 
abstractionTool.jar -src src/svcomp/myown/%%~ni.cfa -absType Color
abstractionTool.jar -src src/svcomp/myown/%%~ni.cfa -absType Color -wt SimpleWt
abstractionTool.jar -src src/svcomp/myown/%%~ni.cfa -wt NoWt
abstractionTool.jar -src src/svcomp/myown/%%~ni.cfa -wt SimpleWt
)
PAUSE