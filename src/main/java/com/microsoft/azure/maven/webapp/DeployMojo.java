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

import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.azure.maven.webapp.handlers.DeployHandler;
import com.microsoft.azure.maven.webapp.handlers.PrivateDockerHubDeployHandler;
import com.microsoft.azure.maven.webapp.handlers.PrivateDockerRegistryDeployHandler;
import com.microsoft.azure.maven.webapp.handlers.PublicDockerHubDeployHandler;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import static com.microsoft.azure.maven.webapp.Constants.*;

/**
 * Goal which deploy specified docker image to a Linux web app in Azure.
 */
@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY)
public class DeployMojo extends WebAppAbstractMojo {

    public void execute() throws MojoExecutionException {
        final WebApp app = azure.webApps().getByResourceGroup(resourceGroup, appName);
        if (app == null) {
            getLog().info(WEBAPP_NOT_FOUND);
        }

        getLog().info(WEBAPP_DEPLOY_START + appName + APOSTROPHE);

        final DeployHandler handler = getDeployHandler();
        if (handler == null) {
            getLog().warn(DEPLOY_HANDLER_NOT_FOUND + "\n" + DEPLOY_SKIPPED);
            return;
        }

        /**
         * Invoke DeployHandler
         */
        handler.validate(app);
        handler.deploy(app);

        getLog().info(new StringBuilder()
                .append(WEBAPP_DEPLOY_SUCCESS)
                .append(appName));
    }

    /**
     * Get DeployHandler based on configuration.
     * @return A new DeployHandler instance or null.
     */
    DeployHandler getDeployHandler() {
        if (containerSetting == null || containerSetting.isEmpty()) {
            getLog().info(CONTAINER_SETTING_NOT_FOUND);
            return null;
        }
        // Public Docker Hub image
        if (Utils.isStringEmpty(containerSetting.serverId)) {
            return new PublicDockerHubDeployHandler(this);
        }
        // Private Docker Hub image
        if (containerSetting.dockerRegistryUrl == null) {
            return new PrivateDockerHubDeployHandler(this);
        }
        // Private Docker registry image
        return new PrivateDockerRegistryDeployHandler(this);
    }
}
