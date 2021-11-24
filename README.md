## SpaceFX 

<br>
A simple tiny space game written in JavaFX.

The "dev" branch contains new developments that might come to the game later on. The "simpleversion" branch is like the name says more simple and should also run on
embedded devices etc. 
The "master" branch now contains some more fun things like enemies attacking in waves, a level boss that needs more hits to kill, enemies that drop bombs and an
enemy boss that also fires rockets.


If you want to see/play it in a browser you can check it out at the [jpro demo page](https://demos.jpro.one/spacefx.html)


Donations are welcome at [Paypal](https://paypal.me/hans0l0)

### Desktop and Mobile
<p>Desktop<br><img src="https://raw.githubusercontent.com/HanSolo/SpaceFX/master/SpaceFX_Desktop.jpg" width=50%></img></p>
<p>iOS<br><img src="https://raw.githubusercontent.com/HanSolo/SpaceFX/master/SpaceFX_iOS.jpg" width=50%></img></p>
<p>Android<br><img src="https://raw.githubusercontent.com/HanSolo/SpaceFX/master/SpaceFX_Android.jpg" width=50%></img></p>
<p>Web<br><img src="https://raw.githubusercontent.com/HanSolo/SpaceFX/master/SpaceFX_Web.jpg" width=50%></img></p>

### Youtube video
I've recorded a little [video](https://youtu.be/Kc0lv3R5VG0) that shows the game in action.


### Requirements for building a native package
If you would like to build a native package you should have at least JDK 13 installed
on your machine. Make sure you run on Java13 and do a `gradle clean build jar` on the 
command line and execute the build app script e.g. `bash build_app.sh`. If everything 
worked ok you will find the native app in the folder `/build/installer`.
To build a native package you will need the early access release of the 
jpackage tool. Please find more info [here](https://github.com/dlemmermann/JPackageScriptFX).


### master branch
This branch is using gradle for the build and it needs JDK 17 with OpenJFX 17.0.0.1.
On my machines I use [Zulu](https://cdn.azul.com/zulu/bin/) for the JDK 17 installation and
[OpenJFX](https://openjfx.io/) for the JavaFX installation.
You should be able to fork the branch and open the build.gradle file in your favourite IDE as a project to run it from the code.
To compile it you need to make sure you are on JDK13 and OpenJFX is installed, then execute the following on the command line.

OS X:
```
cd /PATH/TO/SpaceFX

./gradlew clean build

bash ./build_app.sh
```

Windows:
```
cd \PATH\TO\SpaceFX

.\gradlew.bat clean build

.\build_app.bat
```

Linux:
```
cd /PATH/TO/SpaceFX

./gradlew clean build

bash ./build_app_linux.sh
```


After that you will find the runnable jar file in
```
/PATH/TO/SpaceFX/build/libs
```
and the bundle created by the jpackage tool from JDK17 in
```
/PATH/TO/SpaceFX/build/installer
```

### mobile branch
This branch uses maven for the build and it needs at least JDK 11. In principle you simply have to follow the instructions
over at [github](https://github.com/gluonhq/client-samples) from Gluon to make it run on iOS and Android.
If you have the setup your system as mentioned on the github page with the Gluon examples you can build/deploy SpaceFX to your
device as follows. To build/run it on iOS you need to run it on a Mac and to build/run it on Android you need to run it on Linux.
First of all make sure you are on JDK11 and that you have installed all things described on github.

iOS:
Make sure your iPhone is registered as a developer device at [apple](https://developer.apple.com/) and is plugged into your Mac.
```
export JAVA_HOME=$GRAALVM_HOME

cd /PATH/TO/SpaceFX

mvn clean -Pios client:build

mvn -Pios client:run
```

The iOS spacefx.app file can be found at 
```
/PATH/TO/SpaceFX/target/client/arm64-ios/
```


OSX:
```
export JAVA_HOME=$GRAALVM_HOME

cd /PATH/TO/SpaceFX

mvn clean client:build
```

The OSX spacefx binary can be found at
```
/PATH/TO/SpaceFX/target/client/x86_64-darwin
```


Android:
Make sure your Android phone is a developer phone and is plugged into your Linux machine.
```
export JAVA_HOME=$GRAALVM_HOME

cd /PATH/TO/SpaceFX

mvn clean -Pandroid client:build

mvn -Pandroid client:run
```

The Android spacefx.apk file can be found at
```
/PATH/TO/SpaceFX/target/client/aarch64-android/gvm/apk/bin
```


Linux:
```
export JAVA_HOME=$GRAALVM_HOME

cd /PATH/TO/SpaceFX

mvn clean client:build
```

The Linux binary can be found at
```
/PATH/TO/SpaceFX/target/x86_64-linux
```

Keep in mind that at the moment there is no support for sound when using the GraalVM/Substrate combo.

###Attention:
Because we use different build tools for the master and mobile branch at the moment it can lead to problems
in your favourite IDE when switching branches. To avoid those problems just keep the following steps in mind:

When switching from the master to the mobile branch you should follow these simple steps:
- remove the project from your IDE
- close your IDE
- switch branch from master to mobile
- remove files/folders like .gradle, .idea, /build and /logs
- start your IDE
- open the pom.xml file as a project

When switching from mobile to the master branch you should follow these simple steps:
- remove the project from your IDE
- close your IDE
- switch branch from mobile to master
- start your IDE
- open the build.gradle file as a project


### Run SpaceFX in the browser using jpro
To run SpaceFX in the browser you will need to set the used JDK to 11 in
the build.gradle file. In the future you will also be able to use JDK 13 etc.
More info on how to run a JavaFX application in the browser can be found
here [jpro](https://www.jpro.one/?page=docs/current/1.1/).

To make SpaceFX run in your browser you need to be on the master branch and execute the following steps:
Open the build.gradle file in an editor and comment out the line:
```
//mainClassName = "$moduleName/eu.hansolo.spacefx.SpaceFX"
```
Now enable the line:
```
mainClassName = "eu.hansolo.spacefx.SpaceFX"
```

On the command line execute:
```
cd /PATH/TO/SpaceFX

./gradlew jproRun
```

Open a browser and go to ```localhost:8080```
