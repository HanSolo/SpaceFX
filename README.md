## SpaceFX
A simple tiny space game written in JavaFX.

Donations are welcome at [Paypal](https://paypal.me/hans0l0)

### Overview
![Overview](https://raw.githubusercontent.com/HanSolo/SpaceFX/master/SpaceFX.png)

### Youtube video
I've recorded a little [video](https://www.youtube.com/watch?v=unNnAICtHwY) that shows the game in action.


### Run SpaceFX in the browser using jpro
To run SpaceFX in the browser you will need to set the used JDK to 11 in
the build.gradle file. In the future you will also be able to use JDK 13 etc.
More info on how to run a JavaFX application in the browser can be found
here [jpro](https://www.jpro.one/?page=docs/current/1.1/).


### Building a native package
If you would like to build a native package you have to set the used JDK to 13
in the build.gradle file. After that you have to build the jar file and if that was
successful you can run the build_app.sh/build_app.bat script to create the
native app.
To build a native package you will need the early access release of the 
jpackage tool. Please find more info [here](https://github.com/dlemmermann/JPackageScriptFX).