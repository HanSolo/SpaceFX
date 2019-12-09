## SpaceFX
A simple tiny space game written in JavaFX.

Donations are welcome at [Paypal](https://paypal.me/hans0l0)

### Overview
![Overview](https://raw.githubusercontent.com/HanSolo/SpaceFX/master/SpaceFX.png)

### Youtube video
I've recorded a little [video](https://www.youtube.com/watch?v=An1BdUGC6D4) that shows the game in action.


### Run SpaceFX in the browser using jpro
To run SpaceFX in the browser you will need to set the used JDK to 11 in
the build.gradle file. In the future you will also be able to use JDK 13 etc.
More info on how to run a JavaFX application in the browser can be found
here [jpro](https://www.jpro.one/?page=docs/current/1.1/).


### Building a native package
If you would like to build a native package you should have at least JDK 13 installed
on your machine. Make sure you run on Java13 and do a `gradle clean build jar` on the 
command line and execute the build app script e.g. `bash build_app.sh`. If everything 
worked ok you will find the native app in the folder `/build/installer`.
To build a native package you will need the early access release of the 
jpackage tool. Please find more info [here](https://github.com/dlemmermann/JPackageScriptFX).