# SpaceFX
A simple tiny space game written in JavaFX.

Original source and more information at: https://github.com/HanSolo/SpaceFX

## This fork...

This fork uses [Bach](https://github.com/sormuras/bach) as its build tool.

### Build

- Install JDK 16 or later
- `java --module-path .bach/bin --module com.github.sormuras.bach build`

### Run via Custom Runtime Image

- Linux and Mac OS

```shell script
.bach/workspace/images/bin/spacefx
```

- Windows

```shell script
.bach\workspace\image\bin\spacefx[.bat]
```
