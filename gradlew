#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

app_name="Gradle"
app_command="gradlew"

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || {
        echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH." >&2
        exit 1
    }
fi

# Attempt to set APP_HOME
PRG="$0"
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    expr "$link" : '/.*' > /dev/null || link=`dirname "$PRG"`/"$link"
    PRG="$link"
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

EXEC="exec"
if [ "$(uname)" = "CYGWIN" ] \vert{}\vert{} [ "$(uname)" = "MINGW"* ] ; then
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`
    CYGWIN=true
    if [ -n "$JAVA_HOME" ] ; then
        JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
    fi
fi

exec "$JAVACMD" \
    -Dorg.gradle.appname="$app_command" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain "$@"
