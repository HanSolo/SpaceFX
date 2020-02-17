/*
 * Copyright (c) 2020 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.spacefx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.UUID;

import static eu.hansolo.spacefx.Config.PROPERTIES_FILE_NAME;


public enum PropertyManager {
    INSTANCE;

    private Properties properties;


    // ******************** Constructors **************************************
    PropertyManager() {
        properties = readProperties();
    }


    // ******************** Methods *******************************************
    public Properties getProperties() {
        if (null == properties) { properties = readProperties(); }
        return properties;
    }

    public Object get(final String KEY) { return properties.getOrDefault(KEY, ""); }
    public void set(final String KEY, final String VALUE) {
        properties.setProperty(KEY, VALUE);
        try {
            properties.store(new FileOutputStream(String.join(File.separator, System.getProperty("user.dir"), PROPERTIES_FILE_NAME)), null);
        } catch (IOException exception) {
            LogManager.INSTANCE.logSevere(PropertyManager.class, "Error writing properties file: " + exception);
        }
    }

    public String getString(final String key) { return properties.getOrDefault(key, "").toString(); }

    public double getDouble(final String key) { return Double.parseDouble(properties.getOrDefault(key, "0").toString()); }

    public float getFloat(final String key) { return Float.parseFloat(properties.getOrDefault(key, "0").toString()); }

    public int getInt(final String key) { return Integer.parseInt(properties.getOrDefault(key, "0").toString()); }

    public long getLong(final String key) { return Long.parseLong(properties.getOrDefault(key, "0").toString()); }

    public void storeProperties() {
        try {
            final File propertiesFile = getPropertiesFile();
            properties.store(new FileOutputStream(propertiesFile), null);
        } catch (IOException ex) {
            LogManager.INSTANCE.logSevere(PropertyManager.class, "Error writing properties file: " + ex);
        }
    }


    // ******************** Properties ****************************************
    private File getPropertiesFile() {
        String jarFolder;
        try {
            jarFolder = new File(SpaceFX.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException ex) {
            jarFolder = "./";
        }
        return new File(jarFolder, PROPERTIES_FILE_NAME);
    }

    private Properties readProperties() {
        Properties properties = new Properties();
        try {
            File propertiesFile = getPropertiesFile();
            properties.load(new FileInputStream(propertiesFile));
            if (properties.isEmpty()) createProperties(properties);
        } catch (IOException ex) {
            createProperties(properties);
        }
        return properties;
    }

    private void createProperties(Properties properties) {
        properties.put("hallOfFame1", UUID.randomUUID().toString() + ",AA,0");
        properties.put("hallOfFame2", UUID.randomUUID().toString() + ",BB,0");
        properties.put("hallOfFame3", UUID.randomUUID().toString() + ",CC,0");
        storeProperties();
    }
}
