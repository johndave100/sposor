#!/usr/bin/env sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

APP_BASE_NAME=$(basename "$0")
APP_HOME=$(dirname "$0")

# Add default JVM options from GRADLE_OPTS if set
if [ -n "$GRADLE_OPTS" ]; then
  JVM_OPTS="$DEFAULT_JVM_OPTS $GRADLE_OPTS"
else
  JVM_OPTS="$DEFAULT_JVM_OPTS"
fi

GRADLE_WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ ! -f "$GRADLE_WRAPPER_JAR" ]; then
  echo "ERROR: Could not find Gradle wrapper jar at $GRADLE_WRAPPER_JAR"
  exit 1
fi

exec java $JVM_OPTS -classpath "$GRADLE_WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
