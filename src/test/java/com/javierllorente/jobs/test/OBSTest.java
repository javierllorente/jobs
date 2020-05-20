/*
 * Copyright (C) 2016-2020 Javier Llorente <javier@opensuse.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.javierllorente.jobs.test;

import com.javierllorente.jobs.OBS;
import com.javierllorente.jobs.OBSPrjMetaConfig;
import com.javierllorente.jobs.OBSRequest;
import com.javierllorente.jobs.OBSStatus;
import java.net.URL;
import java.util.ArrayList;
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
        ArrayList<OBSRequest> result = obs.getIncomingRequests();
        assertNotNull(result);
    }

    /**
     * Test of getProjectList method, of class OBS.
     */
    @Test
    public void testGetProjectList() throws Exception {
        System.out.println("getProjectList");
        ArrayList<String> result = obs.getProjectList();
        assertNotNull(result);
    }

    /**
     * Test of getPackageList method, of class OBS.
     */
    @Test
    public void testGetPackageList() throws Exception {
        System.out.println("getPackageList");
        String projectName = "KDE:Extra";
        ArrayList<String> result = obs.getPackageList(projectName);
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
