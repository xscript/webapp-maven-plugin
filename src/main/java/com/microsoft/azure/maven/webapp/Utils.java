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

import com.microsoft.azure.management.appservice.WebApp.DefinitionStages.WithDockerContainerImage;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

/**
 * Utility class
 */
public final class Utils {
    /**
     * Check whether string is null or empty.
     *
     * @param str Input string.
     * @return Boolean. True means input is null or empty. False means input is a valid string.
     */
    public static boolean isStringEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * Get server credential from Maven settings by server Id.
     * @param settings Maven settings object.
     * @param serverId Server Id.
     * @return Server object if it exists in settings. Otherwise return null.
     */
    public static Server getServer(final Settings settings, String serverId) {
        if (settings == null || isStringEmpty(serverId)) {
            return null;
        }
        return settings.getServer(serverId);
    }

    /**
     * @param mojo
     * @return
     * @throws MojoExecutionException
     */
    public static WithDockerContainerImage defineApp(final AbstractWebAppMojo mojo) throws MojoExecutionException {
        final boolean isGroupExisted = mojo.getAzureClient()
                .resourceGroups()
                .checkExistence(mojo.getResourceGroup());

        if (isGroupExisted) {
            return mojo.getAzureClient().webApps()
                    .define(mojo.getAppName())
                    .withRegion(mojo.getRegion())
                    .withExistingResourceGroup(mojo.getResourceGroup())
                    .withNewLinuxPlan(mojo.getPricingTier());
        } else {
            return mojo.getAzureClient().webApps()
                    .define(mojo.getAppName())
                    .withRegion(mojo.getRegion())
                    .withNewResourceGroup(mojo.getResourceGroup())
                    .withNewLinuxPlan(mojo.getPricingTier());
        }
    }
}
