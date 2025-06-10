#!/bin/bash

JAVA_VERSION=24
MAIN_JAR="SpaceFX-1.0.0.jar"
APP_VERSION=1.0.0

echo "java home: $JAVA_HOME"
echo "project version: $PROJECT_VERSION"
echo "app version: $APP_VERSION"
echo "main JAR file: $MAIN_JAR"

# ------ SETUP DIRECTORIES AND FILES ----------------------------------------
# Remove previously generated java runtime and installers. Copy all required
# jar files into the input/libs folder.

rm -rfd ./build/java-runtime/
rm -rfd build/installer/

mkdir -p build/installer/input/libs/

cp build/libs/* build/installer/input/libs/
cp build/libs/${MAIN_JAR} build/installer/input/libs/

# ------ REQUIRED MODULES ---------------------------------------------------
# Use jlink to detect all modules that are required to run the application.
# Starting point for the jdep analysis is the set of jars being used by the
# application.

echo "detecting required modules"
detected_modules=`$JAVA_HOME/bin/jdeps \
  --multi-release ${JAVA_VERSION} \
  --ignore-missing-deps \
  --print-module-deps \
  --class-path "build/installer/input/libs/*" \
    build/classes/java/main/eu/hansolo/spacefx/SpaceFX.class`
echo "detected modules: ${detected_modules}"


# ------ MANUAL MODULES -----------------------------------------------------
# jdk.crypto.ec has to be added manually bound via --bind-services or
# otherwise HTTPS does not work.
#
# See: https://bugs.openjdk.java.net/browse/JDK-8221674
#
# In addition we need jdk.localedata if the application is localized.
# This can be reduced to the actually needed locales via a jlink paramter,
# e.g., --include-locales=en,de.

manual_modules=jdk.crypto.ec,jdk.localedata
echo "manual modules: ${manual_modules}"

# ------ RUNTIME IMAGE ------------------------------------------------------
# Use the jlink tool to create a runtime image for our application. We are
# doing this is a separate step instead of letting jlink do the work as part
# of the jpackage tool. This approach allows for finer configuration and also
# works with dependencies that are not fully modularized, yet.

echo "creating java runtime image"
$JAVA_HOME/bin/jlink \
  --no-header-files \
  --no-man-pages  \
  --compress=2  \
  --strip-debug \
  --add-modules "${detected_modules},${manual_modules}" \
  --include-locales=en,de \
  --output build/java-runtime

# ------ PACKAGING ----------------------------------------------------------
# A loop iterates over the various packaging types supported by jpackage. In
# the end we will find all packages inside the build/installer directory.

for type in "deb" "rpm"
do
  echo "Creating installer of type ... $type"

  $JAVA_HOME/bin/jpackage \
  --type $type \
  --dest build/installer \
  --input build/installer/input/libs \
  --name SpaceFX \
  --main-class eu.hansolo.spacefx.Launcher \
  --main-jar ${MAIN_JAR} \
  --java-options -Xmx2048m \
  --java-options '--enable-preview' \
  --java-options '-Djdk.gtk.verbose=true' \
  --java-options '-Djdk.gtk.version=3' \
  --runtime-image build/java-runtime \
  --icon src/main/resources/eu/hansolo/spacefx/icon.png \
  --linux-shortcut \
  --linux-menu-group "SpaceFX" \  
  --app-version ${APP_VERSION} \
  --vendor "Gerrit Grunwald" \
  --copyright "Copyright © 2025 Gerrit Grunwald" \
  --description "An Arcade like game written in JavaFx" \

done