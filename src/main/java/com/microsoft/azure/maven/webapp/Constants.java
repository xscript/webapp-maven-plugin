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

/**
 * Constants class.
 */
public class Constants {
    public static final String USER_AGENT_MAVEN = "maven/";
    public static final String USER_AGENT_PLUGIN_NAME_AND_VERSION = "webapp-maven-plugin/0.1.0";
    public static final String APOSTROPHE = "...";

    // Messages
    public static final String WEBAPP_DEPLOY_START = "Start deploying to Web App ";
    public static final String WEBAPP_DEPLOY_SUCCESS = "Successfully deployed to Web App ";
    public static final String DEPLOY_SKIPPED = "Skip deployment.";

    // Error messages
    public static final String FAIL_TO_INIT_AZURE = "Fail to initialize Azure client object.";
    public static final String CONTAINER_SETTING_NOT_FOUND = "No configuration for containerSetting found.";
    public static final String DEPLOY_HANDLER_NOT_FOUND = "Not able to handle deployment for such configuration.";
    public static final String WEBAPP_NOT_FOUND = "Web App not found. A new one will be created.";
}
