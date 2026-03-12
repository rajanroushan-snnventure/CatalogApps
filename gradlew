#!/bin/sh
# Gradle wrapper script for Unix/macOS/Linux
# On Windows use gradlew.bat instead.

APP_HOME="$(cd "$(dirname "$0")" && pwd)"
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

exec java \
  -Dorg.gradle.appname="$(basename "$0")" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain "$@"
