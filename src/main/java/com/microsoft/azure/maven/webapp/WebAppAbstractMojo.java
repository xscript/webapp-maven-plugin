/*
 *  MIT License
 *
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE
 */

package com.microsoft.azure.maven.webapp;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.Azure.Authenticated;
import com.microsoft.rest.LogLevel;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

import java.io.File;
import java.util.Map;

/**
 * Base abstract class for shared configurations and operations.
 */
public abstract class WebAppAbstractMojo extends AbstractMojo {
    private static final String USER_AGENT_MAVEN = "maven";
    private static final String USER_AGENT_PLUGIN_NAME_AND_VERSION = "webapp-maven-plugin/0.1.0";

    /**
     * The system settings for Maven. This is the instance resulting from
     * merging global and user-level settings files.
     */
    @Component
    private Settings settings;

    @Parameter(property = "authFile")
    private File authFile;

    @Parameter(property = "subscriptionId")
    private String subscriptionId;

    @Parameter(property = "resourceGroup", required = true)
    protected String resourceGroup;

    @Parameter(property = "region")
    protected String region;

    @Parameter(property = "appName", required = true)
    protected String appName;

    @Parameter(property = "container")
    protected Container container;

    @Parameter(property = "appSettings")
    protected Map appSettings;

    protected Azure azure;

    public Azure getAzureClient() throws MojoExecutionException {
        if (azure != null) {
            return azure;
        }
        return getAzureClient();
    }

    public String getResourceGroup() {
        return resourceGroup;
    }

    public String getAppName() {
        return appName;
    }

    public Container getContainer() {
        return container;
    }

    public Map getAppSettings() {
        return appSettings;
    }

    public void execute() throws MojoExecutionException {
        /* Common Mojo logic such as log in */
        azure = internalGetAzureClient();
    }

    protected Azure internalGetAzureClient() throws MojoExecutionException {
        final LogLevel logLevel = getLog().isDebugEnabled() ? LogLevel.BODY_AND_HEADERS : LogLevel.NONE;

        try {
            final Authenticated authenticated = Azure.configure()
                    .withLogLevel(logLevel)
                    .withUserAgent(USER_AGENT_MAVEN)
                    .withUserAgent(USER_AGENT_PLUGIN_NAME_AND_VERSION)
                    .authenticate(authFile);
            return Utils.isStringEmpty(subscriptionId) ?
                    authenticated.withDefaultSubscription() :
                    authenticated.withSubscription(subscriptionId);
        } catch (Exception e) {
            throw new MojoExecutionException("Fail to initialize Azure client object.", e);
        }
    }

    public Server getServer(String serverId) {
        if (settings != null && serverId != null) {
            final Server server = settings.getServer(serverId);
            if (server != null) {
                return server;
            }
        }
        return null;
    }
}
