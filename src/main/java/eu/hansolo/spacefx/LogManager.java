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
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static eu.hansolo.spacefx.Config.LOG_FILE_NAME;


public enum LogManager {
    INSTANCE;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private        final Logger            logger;
    private              Formatter         formatter;
    private              FileHandler       logFile;


    // ******************** Constructors **************************************
    LogManager() {
        logger = Logger.getLogger(LogManager.class.getName());

        formatter = new Formatter() {
            @Override public String format(LogRecord record) {
                StringBuilder format = new StringBuilder(1000);
                format.append(DTF.format(LocalDateTime.now()));
                format.append(' ');
                format.append(record.getLevel());
                format.append(' ');
                format.append(formatMessage(record));
                format.append('\n');
                return format.toString();
            }
        };

        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        try {
            String jarFolder;
            try {
                jarFolder = new File(SpaceFX.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            } catch (URISyntaxException e) {
                jarFolder = "./";
            }
            File file = new File(jarFolder, LOG_FILE_NAME);

            if (file.exists()) { file.delete(); }
            logFile = new FileHandler(String.join("/", jarFolder,LOG_FILE_NAME));
            logFile.setFormatter(formatter);
            logger.addHandler(logFile);
            logger.setLevel(Level.INFO);
        } catch (IOException ex) {
            System.out.println("Error initializing logger: " + ex);
        }
    }


    // ******************** Methods *******************************************
    public boolean clearLogFile() {
        boolean response;
        try {
            logger.removeHandler(logFile);
            logFile.close();

            PrintWriter writer = new PrintWriter(LOG_FILE_NAME);
            writer.print("");
            writer.close();

            logFile = new FileHandler(LOG_FILE_NAME);
            logFile.setFormatter(formatter);
            logger.addHandler(logFile);
            logger.setLevel(Level.INFO);
            logInfo(LogManager.class, "LogFile reset");
            response = true;
        } catch(Exception ex){
            response = false;
        }
        return response;
    }

    public void logSevere(final Class CLASS, final String logMessage) {
        logger.severe(CLASS.getName() + ": " + logMessage);
    }

    public void logWarning(final Class CLASS, final String logMessage) {
        logger.warning(CLASS.getName() + ": " + logMessage);
    }

    public void logInfo(final Class CLASS, final String logMessage) {
        logger.info(CLASS.getName() + ": " + logMessage);
    }
}
