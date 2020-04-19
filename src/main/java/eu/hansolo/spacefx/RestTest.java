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

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static eu.hansolo.spacefx.Config.ADB_POST_SCORE_URL;
import static eu.hansolo.spacefx.Config.ADB_TOP_TEN_URL;


public class RestTest {


    public static void main(String[] args) {
        PropertyManager propertyManager = PropertyManager.INSTANCE;
        RestManager     restManager     = RestManager.INSTANCE;

        // Get Top-Ten
        String adbGetUrl  = propertyManager.getString(ADB_TOP_TEN_URL);
        try {
            String response = restManager.getFromAdb(adbGetUrl);
            System.out.println("Get response: " + response);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.out.println("Error getting top-ten from adb: " + e.getMessage());
        }

        // Post a score
        String adbPostUrl = propertyManager.getString(ADB_POST_SCORE_URL);
        StringBuilder json = new StringBuilder().append("{")
                                                .append("\"username\":").append("\"hansolo\"").append(",")
                                                .append("\"score\":").append(691203).append(",")
                                                .append("\"startTime\":").append("\"2020-04-19T14:45:23.151Z\"").append(",")
                                                .append("\"endTime\":").append("\"2020-04-19T14:53:28.972Z\"").append(",")
                                                .append("\"buttonLog\":[")
                                                .append("{")
                                                .append("\"button\":").append("\"a\"").append(",")
                                                .append("\"currentScore\":").append("12345").append(",")
                                                .append("\"buttonState\":").append("d").append(",")
                                                .append("\"milisec_since_start\":").append("23789")
                                                .append("},")
                                                .append("{")
                                                .append("\"button\":").append("\"b\"").append(",")
                                                .append("\"currentScore\":").append("3456").append(",")
                                                .append("\"buttonState\":").append("u").append(",")
                                                .append("\"milisec_since_start\":").append("21789")
                                                .append("}")
                                                .append("]")
                                                .append("}");

        try {
            int response = restManager.postToAdb(adbPostUrl, json.toString());
            System.out.println("Post status code: " + response);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            System.out.println("Error posting to adb: " + e.getMessage());
        }
    }
}
