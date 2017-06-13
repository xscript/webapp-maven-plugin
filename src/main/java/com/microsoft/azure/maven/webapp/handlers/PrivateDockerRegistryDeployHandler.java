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

package com.microsoft.azure.maven.webapp.handlers;

import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.azure.maven.webapp.ContainerSetting;
import com.microsoft.azure.maven.webapp.DeployMojo;
import com.microsoft.azure.maven.webapp.Utils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Server;

/**
 *
 */
public class PrivateDockerRegistryDeployHandler extends ContainerDeployHandler {
    /**
     * Constructor
     * @param mojo
     */
    public PrivateDockerRegistryDeployHandler(DeployMojo mojo) {
        super(mojo);
    }

    /**
     *
     * @param app
     * @throws MojoExecutionException
     */
    @Override
    public void validate(WebApp app) throws MojoExecutionException {
        super.validate(app);
    }

    /**
     *
     * @param app
     * @throws MojoExecutionException
     */
    @Override
    public void deploy(WebApp app) throws MojoExecutionException {
        final ContainerSetting containerSetting = mojo.getContainerSetting();
        final Server server = mojo.getServer(containerSetting.serverId);
        if (server == null) {
            throw new MojoExecutionException(String.format("ServerId=%s not found.", containerSetting.serverId));
        }

        if (app == null) {
            Utils.defineApp(mojo)
                    .withPrivateRegistryImage(containerSetting.imageName, containerSetting.dockerRegistryUrl.toString())
                    .withCredentials(server.getUsername(), server.getPassword())
                    .withStartUpCommand(containerSetting.startUpFile)
                    .withAppSettings(mojo.getAppSettings())
                    .create();
        } else {
            app.update()
                    .withPrivateRegistryImage(containerSetting.imageName, containerSetting.dockerRegistryUrl.toString())
                    .withCredentials(server.getUsername(), server.getPassword())
                    .withStartUpCommand(containerSetting.startUpFile)
                    .withAppSettings(mojo.getAppSettings())
                    .apply();
        }
    }
}
