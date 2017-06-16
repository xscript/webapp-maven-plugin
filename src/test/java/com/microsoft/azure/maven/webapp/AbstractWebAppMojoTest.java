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
import com.microsoft.azure.management.appservice.PricingTier;
import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.azure.management.appservice.WebApps;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.settings.Settings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static com.microsoft.azure.maven.webapp.Constants.FAIL_TO_INIT_AZURE;
import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractWebAppMojoTest {
    @Mock
    Azure azure;

    @Mock
    WebApps apps;

    @Mock
    WebApp app;

    @InjectMocks
    private AbstractWebAppMojo mojo = new AbstractWebAppMojo() {
        @Override
        public void execute() throws MojoExecutionException, MojoFailureException {

        }
    };

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAzureClient() throws Exception {
        try {
            mojo.getAzureClient();
        } catch (Exception e) {
            assertEquals(FAIL_TO_INIT_AZURE, e.getMessage());
        }

        ReflectionTestUtils.setField(mojo, "azure", azure);
        assertEquals(azure, mojo.getAzureClient());
    }

    @Test
    public void testGetWebApp() {
        assertNull(mojo.getWebApp());

        ReflectionTestUtils.setField(mojo, "azure", azure);
        when(azure.webApps()).thenReturn(apps);
        when(apps.getByResourceGroup(null, null)).thenReturn(app);

        assertEquals(app, mojo.getWebApp());
    }

    @Test
    public void testGetMavenSettings() {
        assertNull(mojo.getSettings());
        final Settings settings = mock(Settings.class);
        ReflectionTestUtils.setField(mojo, "settings", settings);
        assertEquals(settings, mojo.getSettings());
    }

    @Test
    public void testGetWebAppProperties() {
        assertNull(mojo.getResourceGroup());
        ReflectionTestUtils.setField(mojo, "resourceGroup", "ResourceGroupName");
        assertEquals("ResourceGroupName", mojo.getResourceGroup());

        assertNull(mojo.getAppName());
        ReflectionTestUtils.setField(mojo, "appName", "WebAppName");
        assertEquals("WebAppName", mojo.getAppName());

        assertNull(mojo.getRegion());
        ReflectionTestUtils.setField(mojo, "region", "Region");
        assertEquals("Region", mojo.getRegion());

        assertEquals(PricingTier.STANDARD_S1, mojo.getPricingTier());
    }

    @Test
    public void testGetContainerSetting() {
        assertNull(mojo.getContainerSetting());

        final ContainerSetting container = new ContainerSetting();
        ReflectionTestUtils.setField(mojo, "containerSetting", container);
        assertNotNull(mojo.getContainerSetting());
        assertTrue(mojo.getContainerSetting().isEmpty());
    }

    @Test
    public void testGetAppSetting() {
        assertNull(mojo.getAppSettings());

        final Map map = new HashMap();
        map.put("PORT", "80");
        ReflectionTestUtils.setField(mojo, "appSettings", map);

        assertNotNull(mojo.getAppSettings());
        assertEquals(1, mojo.getAppSettings().size());
        assertNotNull(mojo.getAppSettings().get("PORT"));
    }
}
