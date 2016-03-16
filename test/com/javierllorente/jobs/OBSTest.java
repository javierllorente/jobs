/*
 * Copyright (C) 2016 Javier Llorente <javier@opensuse.org>
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
package com.javierllorente.jobs;

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
        obs.setUsername("yourusername");
        obs.setPassword("yourpassword");
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
     * Test of getBuild method, of class OBS.
     */
    @Test
    public void testGetBuild() throws Exception {
        System.out.println("getBuild");
        String project = "openSUSE:Factory";
        String repository = "standard";
        String architecture = "x86_64";
        String buildName = "kernel-source";
        OBSBuild result = obs.getBuild(project, repository, architecture, buildName);
        assertNotNull(result);
    }

    /**
     * Test of getRequests method, of class OBS.
     */
    @Test
    public void testGetRequests() throws Exception {
        System.out.println("getRequests");
        ArrayList<OBSRequest> result = obs.getRequests();
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
     * Test of getProjectMetadata method, of class OBS.
     */
    @Test
    public void testGetProjectMetadata() throws Exception {
        System.out.println("getProjectMetadata");
        ArrayList<String> result = obs.getProjectMetadata("KDE:Extra");
        assertNotNull(result);
    }
    
}