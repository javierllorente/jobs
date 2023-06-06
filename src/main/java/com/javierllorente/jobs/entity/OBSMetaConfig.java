/*
 * Copyright (C) 2020 Javier Llorente <javier@opensuse.org>
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
package com.javierllorente.jobs.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author javier
 */
public class OBSMetaConfig {
    private String name;
    private String title;
    private String description;
    private Map<String, List<String>> persons;
    private Map<String, List<String>> groups;
    
    private Map<String, Boolean> buildFlag;
    private Map<String, Boolean> publishFlag;
    private Map<String, Boolean> useForBuildFlag;
    private Map<String, Boolean> debugInfoFlag;

    public OBSMetaConfig() {
        persons = new HashMap<>();
        groups = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, List<String>> getPersons() {
        return persons;
    }
    
    public void setPersons(Map<String, List<String>> persons) {
        this.persons = persons;
    }
    
    public List<String> getRoles(String userId) {
        return persons.get(userId);
    }
    
    public void putPerson(String userId, String role) {
        List roles = persons.get(userId);
        if (roles == null) {
            roles = new ArrayList();
        }
        roles.add(role);
        persons.put(userId, roles);
    }

    public Map<String, List<String>> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, List<String>> groups) {
        this.groups = groups;
    }
    
    public void putGroup(String groupId, String role) {
        List roles = groups.get(groupId);
        if (roles == null) {
            roles = new ArrayList();
        }
        roles.add(role);
        groups.put(groupId, roles);
    }

    public Map<String, Boolean> getBuildFlag() {
        return buildFlag;
    }

    public void setBuildFlag(Map<String, Boolean> buildFlag) {
        this.buildFlag = buildFlag;
    }

    public Map<String, Boolean> getPublishFlag() {
        return publishFlag;
    }

    public void setPublishFlag(Map<String, Boolean> publishFlag) {
        this.publishFlag = publishFlag;
    }

    public Map<String, Boolean> getUseForBuildFlag() {
        return useForBuildFlag;
    }

    public void setUseForBuildFlag(Map<String, Boolean> useForBuildFlag) {
        this.useForBuildFlag = useForBuildFlag;
    }

    public Map<String, Boolean> getDebugInfoFlag() {
        return debugInfoFlag;
    }

    public void setDebugInfoFlag(Map<String, Boolean> debugInfoFlag) {
        this.debugInfoFlag = debugInfoFlag;
    }
    
}
