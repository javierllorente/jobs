/*
 * Copyright (C) 2016-2020 Javier Llorente <javier@opensuse.org>
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
package com.javierllorente.jobs.test;

import com.javierllorente.jobs.OBS;
import com.javierllorente.jobs.entity.OBSPrjMetaConfig;
import com.javierllorente.jobs.entity.OBSRequest;
import com.javierllorente.jobs.entity.OBSStatus;
import java.net.URL;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author javier
 */
public class OBSTest {
    
    private static OBS obs;
    final static String API_URL = "https://api.opensuse.org/";
    
    public OBSTest() {
    }    
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        obs = new OBS();
        URL apiUrl = new URL(API_URL);
        obs.setApiUrl(apiUrl);
        obs.setUsername("");
        obs.setPassword("");
        obs.authenticate();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getBuildStatus method, of class OBS.
     */
    @Test
    public void testGetBuildStatus() throws Exception {
        System.out.println("getBuild");
        String project = "openSUSE:Factory";
        String repository = "standard";
        String architecture = "x86_64";
        String build = "kernel-source";
        OBSStatus status = obs.getBuildStatus(project, repository, architecture, build);
        assertNotNull(status);
    }

    /**
     * Test of getIncomingRequests method, of class OBS.
     */
    @Test
    public void testGetIncomingRequests() throws Exception {
        System.out.println("getRequests");
        List<OBSRequest> result = obs.getIncomingRequests();
        assertNotNull(result);
    }

    /**
     * Test of getProjectList method, of class OBS.
     */
    @Test
    public void testGetProjectList() throws Exception {
        System.out.println("getProjectList");
        List<String> result = obs.getProjectList(true);
        assertNotNull(result);
    }

    /**
     * Test of getPackageList method, of class OBS.
     */
    @Test
    public void testGetPackageList() throws Exception {
        System.out.println("getPackageList");
        String projectName = "KDE:Extra";
        List<String> result = obs.getPackages(projectName);
        assertNotNull(result);

    }

    /**
     * Test of getProjectMetaConfig method, of class OBS.
     */
    @Test
    public void testGetProjectMetaConfig() throws Exception {
        System.out.println("getProjectMetaConfig");
        OBSPrjMetaConfig prjMetaConfig = obs.getProjectMetaConfig("KDE:Extra");
        assertFalse(prjMetaConfig.getName().isEmpty());
    }
    
}
