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

import com.microsoft.azure.management.appservice.OperatingSystem;
import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.azure.maven.webapp.DeployMojo;
import org.apache.maven.plugin.MojoExecutionException;

import static com.microsoft.azure.maven.webapp.Constants.CONTAINER_NOT_SUPPORTED;

abstract class ContainerDeployHandler implements DeployHandler {

    protected final DeployMojo mojo;

    public ContainerDeployHandler(final DeployMojo mojo) {
        this.mojo = mojo;
    }

    public void validate(WebApp app) throws MojoExecutionException {
        // Skip validation for app to be created.
        if (app == null) {
            return;
        }

        if (app.operatingSystem() != OperatingSystem.LINUX) {
            throw new MojoExecutionException(CONTAINER_NOT_SUPPORTED);
        }
    }

    public abstract void deploy(WebApp app) throws MojoExecutionException;
}
