/*
 * Copyright (C) 2020 Javier Llorente <javier@opensuse.org>
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
