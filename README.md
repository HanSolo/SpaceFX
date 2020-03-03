## SpaceFX
A simple tiny space game written in JavaFX.

The "dev" branch contains new developments that might come to the game later on. The "simpleversion" branch is like the name says more simple and should also run on
embedded devices etc. 
The "master" branch now contains some more fun things like enemies attacking in waves, a level boss that needs more hits to kill, enemies that drop bombs and an
enemy boss that also fires rockets.
This "mobile" branch is like the name says for mobile devices, it contains exactly the same code that is used in the "master" branch but needs some adjustments.
At the moment you cannot use sound on the mobile platform so this has to be disabled and other things.
Be aware that the "mobile" branch is using maven where the other branches use gradle, this will change in the future and is only temporary.

Donations are welcome at [Paypal](https://paypal.me/hans0l0)

### Desktop and Mobile
![Desktop](https://raw.githubusercontent.com/HanSolo/SpaceFX/master/SpaceFX_Desktop.jpg)
![iOS](https://raw.githubusercontent.com/HanSolo/SpaceFX/master/SpaceFX_iOS.jpg)
![Android](https://raw.githubusercontent.com/HanSolo/SpaceFX/master/SpaceFX_Android.jpg)


### Verify SpaceFX on desktop

Before building SpaceFX for a mobile device, it is convenient to test it first on desktop.

You can test with JDK 11 (hotspot):

```
mvn javafx:run
```

and you can build a native image and run on desktop:

```
export JAVA_HOME=$GRAALVM_HOME

mvn clean client:build

mvn client:run
```

### Run SpaceFX on iOS (you need OS X to compile it)
To compile/run SpaceFX on an iOS device you will need JDK 11 and you should follow the instructions from [gluon](https://github.com/gluonhq/client-samples)

Your device must be registered for development. See [iOS deployment instructions](https://docs.gluonhq.com/client/#_ios_deployment).
Make sure that your device is connected to your computer.

If your setup is correct you can build the iOS version by using the following commands on the console:
```
export JAVA_HOME=$GRAALVM_HOME

mvn clean -Pios client:build

mvn -Pios client:run
```

### Run SpaceFX on Android (you need Linux to compile it)
To compile/run SpaceFX on an Android device you will need JDK 11 and you should follow the instructions from [gluon](https://github.com/gluonhq/client-samples)

Your device must be enabled as a developer device.
Make sure that your device is connected to your computer.

If your setup is correct you can build the Android version by using the following commands on the console:
```
export JAVA_HOME=$GRAALVM_HOME

mvn clean -Pandroid client:build

mvn -Pandroid client:run
```
