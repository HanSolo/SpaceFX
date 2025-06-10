module spacefx {

    // Java
    // Java
    requires java.net.http;
    requires java.desktop;

    // Java-FX
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.media;
    //requires jpro.webapi;

    exports eu.hansolo.spacefx;
}