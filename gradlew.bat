@echo off
SETLOCAL

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.

set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

set GRADLE_HOME=%APP_HOME%\gradle\wrapper
set GRADLE_WRAPPER_JAR=%GRADLE_HOME%\gradle-wrapper.jar
set GRADLE_WRAPPER_PROPERTIES=%GRADLE_HOME%\gradle-wrapper.properties

if exist "%GRADLE_WRAPPER_JAR%" (
  java -classpath "%GRADLE_WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
) else (
  echo ERROR: Gradle wrapper JAR not found at "%GRADLE_WRAPPER_JAR%"
  exit /b 1
)
ENDLOCAL
