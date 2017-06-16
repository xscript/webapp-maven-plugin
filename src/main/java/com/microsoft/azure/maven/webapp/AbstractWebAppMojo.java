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
import com.microsoft.azure.management.appservice.PricingTier;
import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.rest.LogLevel;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;

import java.io.File;
import java.util.Map;

import static com.microsoft.azure.maven.webapp.Constants.*;

/**
 * Base abstract class for shared configurations and operations.
 */
public abstract class AbstractWebAppMojo extends AbstractMojo {
    /**
     * The system settings for Maven. This is the instance resulting from
     * merging global and user-level settings files.
     */
    @Component
    protected Settings settings;

    @Parameter(property = "authFile")
    protected File authFile;

    @Parameter(property = "subscriptionId")
    protected String subscriptionId;

    @Parameter(property = "resourceGroup", required = true)
    protected String resourceGroup;

    @Parameter(property = "region", defaultValue = "westus")
    protected String region;

    @Parameter(property = "appName", required = true)
    protected String appName;

    @Parameter(property = "containerSetting")
    protected ContainerSetting containerSetting;

    @Parameter(property = "appSettings")
    protected Map appSettings;

    protected Azure azure;

    public Azure getAzureClient() throws MojoExecutionException {
        if (azure != null) {
            return azure;
        }
        return internalGetAzureClient();
    }

    public WebApp getWebApp() {
        try {
            return getAzureClient().webApps().getByResourceGroup(resourceGroup, appName);
        } catch (Exception e) {
            return null;
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public String getResourceGroup() {
        return resourceGroup;
    }

    public String getAppName() {
        return appName;
    }

    public String getRegion() {
        return region;
    }

    public PricingTier getPricingTier() {
        return PricingTier.STANDARD_S1;
    }

    public ContainerSetting getContainerSetting() {
        return containerSetting;
    }

    public Map getAppSettings() {
        return appSettings;
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
            throw new MojoExecutionException(FAIL_TO_INIT_AZURE, e);
        }
    }
}
