/*
 * Copyright (C) 2016-2025 Javier Llorente <javier@opensuse.org>
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
import com.javierllorente.jobs.entity.OBSEntry;
import com.javierllorente.jobs.entity.OBSProject;
import com.javierllorente.jobs.entity.OBSStatus;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author javier
 */
public class OBSTest {

    private static OBS obs;
    final static String API_URI = "https://api.opensuse.org/";

    public OBSTest() {
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
        obs = new OBS();
        URI apiUri = new URI(API_URI);
        obs.setApiUri(apiUri);
        obs.setUsername("");
        obs.setPassword("");
        obs.authenticate();
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getBuildStatus method, of class OBS.
     */
    @Test
    public void testGetBuildStatus() {
        String project = "openSUSE:Factory";
        String repository = "standard";
        String architecture = "x86_64";
        String build = "kernel-source";
        OBSStatus status = obs.getBuildStatus(project, repository, architecture, build);
        assertNotNull(status.getCode());
    }

    /**
     * Test of getProjectList method, of class OBS.
     */
    @Test
    public void testGetProjectList() {
        List<OBSEntry> result = obs.getProjects().getEntries();
        assertFalse(result.isEmpty());
    }

    /**
     * Test of getPackageList method, of class OBS.
     */
    @Test
    public void testGetPackageList() {
        String projectName = "KDE:Extra";
        List<OBSEntry> result = obs.getPackages(projectName).getEntries();
        assertFalse(result.isEmpty());

    }

    /**
     * Test of getProjectMetaConfig method, of class OBS.
     */
    @Test
    public void testGetProjectMetaConfig() {
        OBSProject prjMetaConfig = obs.getProjectMetaConfig("KDE:Extra");
        assertFalse(prjMetaConfig.getName().isEmpty());
    }
}
