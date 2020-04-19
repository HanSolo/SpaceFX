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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static eu.hansolo.spacefx.Config.ADB_GET_TIMEOUT;
import static eu.hansolo.spacefx.Config.ADB_POST_TIMEOUT;


public enum RestManager {
    INSTANCE;

    private final HttpClient httpClient;


    RestManager() {
        httpClient = HttpClient.newBuilder()
                               .version(HttpClient.Version.HTTP_2)
                               .build();
    }


    public String getFromAdb(final String url) throws InterruptedException, ExecutionException, TimeoutException {
        HttpRequest request = HttpRequest.newBuilder()
                                         .GET()
                                         .uri(URI.create(url))
                                         .timeout(Duration.ofSeconds(ADB_GET_TIMEOUT))
                                         .setHeader("User-Agent", "SpaceFX")
                                         .build();

        CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        return response.thenApply(HttpResponse::body).get(ADB_GET_TIMEOUT, TimeUnit.SECONDS);
    }

    public int postToAdb(final String url, final String json) throws InterruptedException, TimeoutException, ExecutionException {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(url))
                                         .timeout(Duration.ofSeconds(ADB_POST_TIMEOUT))
                                         .setHeader("User-Agent", "SpaceFX")
                                         .header("Content-Type", "application/json")
                                         .POST(HttpRequest.BodyPublishers.ofString(json))
                                         .build();

        CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request, BodyHandlers.ofString());

        return response.thenApply(HttpResponse::statusCode).get(ADB_POST_TIMEOUT, TimeUnit.SECONDS);
    }
}
