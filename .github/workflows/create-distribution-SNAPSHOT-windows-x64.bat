@echo off
REM VRL distribution script (creates app bundles and installers).
REM (c) 2020 by Michael Hoffer <info@michaelhoffer.de>
REM This script can easily be adapted for other applications as well.

REM App distribution configuration:
REM - specify app name, jar file and desired app type (msi, app-bundle, etc.)
REM - since jpackage is still in preview, we download a recent JDK installation that
REM   is used locally to build the application package
SET APP_NAME="SpaceFX"
SET APP_JAR="SpaceFX-1.0-SNAPSHOT.jar"
SET APP_TYPE="msi"
SET JPACKAGE_JVM="https://download.java.net/java/GA/jdk15/779bf45e88a44cbd9ea6621d33e33db1/36/GPL/openjdk-15_windows-x64_bin.zip"
REM App version is passed as argument by github action workflow
SET APP_VERSION=%1

REM Please don't change the rest of the script. If something doesn't work
REM then create an issue where we can discuss potential changes.
set DIR="%~dp0"
echo Building distribution in dir: %DIR%
cd %DIR%

REM Check for 7zip and curl which are used to download and unpack the local Java distribution.
for %%X in (7z.exe) do (set FOUND7Z=%%~$PATH:X)
for %%X in (curl.exe) do (set FOUNDCURL=%%~$PATH:X)
if not defined FOUND7Z (
  echo "ERROR: please make sure that 7Zip is installed and on the path."
)
if not defined FOUNDCURL (
  echo "ERROR: please make sure that Curl is installed and on the path."
)

REM Do not create the local JDK if it already exists
if exist ".tmp-runtime\jdk-15\" (
    echo "> jdk 15 for package generation already downloaded"
) else (
REM If it doesn't exist, however, download and unpack the local JDK
REM and create runtime image with the runtime JDK
REM (JAVA_HOME is expected to be set correctly)    
    mkdir .tmp-runtime\
    cd .tmp-runtime
    echo "> downloading jdk 15"
    curl -o jdk15.zip %JPACKAGE_JVM%
    echo "> unpacking jdk 15"
    7z x jdk14.zip
    echo "> creating runtime image"
    "%JAVA_HOME%\bin\jlink" -p "%JAVA_HOME%\jmods" ^
        --add-modules java.desktop ^
        --strip-debug ^
        --no-header-files ^
        --no-man-pages ^
        --strip-native-commands ^
        --vm=server ^
        --compress=2 ^
        --output runtime
)

REM Change to distribution directory (script location) where
REM we will create the final application package
cd %DIR%

REM Finally, invoke the jpackage CLI program from the local JDK that
REM has been downloaded earlier.
set JPKG_HOME=.tmp-runtime\jdk-15\
set JPKG_EXECUTABLE=%JPKG_HOME%\bin\jpackage
%JPKG_EXECUTABLE% --input ..\..\build\libs\ ^
    --name %APP_NAME% ^
    --main-jar %APP_JAR% ^
    --type %APP_TYPE% ^
    --runtime-image .tmp-runtime\runtime ^
    --app-version 0.0.0 ^
    --win-per-user-install ^
    --win-menu ^
    --win-menu-group SpaceFX

REM Rename the application MSI
move SpaceFX-0.0.0.msi SpaceFX-%APP_VERSION%-windows-x64.msi

REM Show final MSI package
dir *.msi
