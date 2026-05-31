#!/bin/sh
# Maven Wrapper Script
MAVEN_WRAPPER_JAR="$( cd "$( dirname "$0" )" && pwd )/.mvn/wrapper/maven-wrapper.jar"
if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
  echo "Downloading Maven Wrapper..."
  mkdir -p "$(dirname "$MAVEN_WRAPPER_JAR")"
  curl -sL "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" -o "$MAVEN_WRAPPER_JAR"
fi
exec java -jar "$MAVEN_WRAPPER_JAR" "$@"
