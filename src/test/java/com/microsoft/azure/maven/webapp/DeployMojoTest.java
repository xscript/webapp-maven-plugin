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
import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.azure.management.appservice.WebApps;
import com.microsoft.azure.maven.webapp.handlers.DeployHandler;
import com.microsoft.azure.maven.webapp.handlers.PrivateDockerHubDeployHandler;
import com.microsoft.azure.maven.webapp.handlers.PrivateDockerRegistryDeployHandler;
import com.microsoft.azure.maven.webapp.handlers.PublicDockerHubDeployHandler;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeployMojoTest {
    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    @Mock
    Azure azure;

    @Mock
    WebApps webApps;

    @Mock
    WebApp app;

    @Mock
    DeployHandler deployHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(azure.webApps()).thenReturn(webApps);
    }

    @Test
    public void testExecuteWithDeploy() throws Exception {
        final DeployMojo mojo = getMojoFromPom("/pom-public-docker-hub.xml");
        assertNotNull(mojo);

        final DeployMojo mojoSpy = spy(mojo);
        doReturn(azure).when(mojoSpy).internalGetAzureClient();
        doReturn(deployHandler).when(mojoSpy).getDeployHandler();

        when(webApps.getByResourceGroup(any(String.class), any(String.class))).thenReturn(app);

        mojoSpy.execute();

        verify(deployHandler, times(1)).validate(app);
        verify(deployHandler, times(1)).deploy(app);
    }

    @Test
    public void testExecuteWithDeploySkipped() throws Exception {
        final DeployMojo mojo = getMojoFromPom("/pom-no-container-setting.xml");
        assertNotNull(mojo);

        mojo.execute();
    }

    @Test
    public void testNoDeployHandlerFound() throws Exception {
        final DeployMojo mojo = getMojoFromPom("/pom-no-container-setting.xml");
        assertNotNull(mojo);

        assertNull(mojo.getDeployHandler());
    }

    @Test
    public void testDeployWithPublicDockerImage() throws Exception {
        final DeployMojo mojo = getMojoFromPom("/pom-public-docker-hub.xml");
        assertNotNull(mojo);

        assertThat(mojo.getDeployHandler(), instanceOf(PublicDockerHubDeployHandler.class));
    }

    @Test
    public void testDeployWebAppWithPrivateDockerImage() throws Exception {
        final DeployMojo mojo = getMojoFromPom("/pom-private-docker-hub.xml");
        assertNotNull(mojo);

        assertThat(mojo.getDeployHandler(), instanceOf(PrivateDockerHubDeployHandler.class));
    }

    @Test
    public void testDeployWebAppWithPrivateRegistryImage() throws Exception {
        final DeployMojo mojo = getMojoFromPom("/pom-private-docker-registry.xml");
        assertNotNull(mojo);

        assertThat(mojo.getDeployHandler(), instanceOf(PrivateDockerRegistryDeployHandler.class));
    }

    private DeployMojo getMojoFromPom(String filename) throws Exception {
        final File pom = new File(DeployMojoTest.class.getResource(filename).toURI());
        return (DeployMojo) rule.lookupMojo("deploy", pom);
    }
}
