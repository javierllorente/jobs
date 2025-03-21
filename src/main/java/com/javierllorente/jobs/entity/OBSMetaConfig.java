/*
 * Copyright (C) 2020-2025 Javier Llorente <javier@opensuse.org>
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlElements;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author javier
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OBSMetaConfig {
    
    @XmlAttribute
    protected String name;
    
    private String title;
    private String description;
    
    @XmlElement(name = "person")
    private List<OBSPerson> persons;
    
    @XmlElement(name = "group")
    private List<OBSGroup> groups;
    
    @XmlElements(value = {
        @XmlElement(name = "enable", nillable = true, type = OBSEnable.class),
        @XmlElement(name = "disable", nillable = true, type = OBSDisable.class)
    })
    @XmlElementWrapper(name = "lock")
    private List<OBSEnable> locks;
    
    @XmlElements(value = {
        @XmlElement(name = "enable", nillable = true, type = OBSEnable.class),
        @XmlElement(name = "disable", nillable = true, type = OBSDisable.class)
    })
    @XmlElementWrapper(name = "build")
    private List<OBSEnable> builds;
    
    @XmlElements(value = {
        @XmlElement(name = "enable", nillable = true, type = OBSEnable.class),
        @XmlElement(name = "disable", nillable = true, type = OBSDisable.class)
    })
    @XmlElementWrapper(name = "publish")
    private List<OBSEnable> publishes;
    
    @XmlElements(value = {
        @XmlElement(name = "enable", nillable = true, type = OBSEnable.class),
        @XmlElement(name = "disable", nillable = true, type = OBSDisable.class)
    })
    @XmlElementWrapper(name = "debuginfo")
    private List<OBSEnable> debugInfos;
    
    @XmlElements(value = {
        @XmlElement(name = "enable", nillable = true, type = OBSEnable.class),
        @XmlElement(name = "disable", nillable = true, type = OBSDisable.class)
    })
    @XmlElementWrapper(name = "useforbuild")
    private List<OBSEnable> useForBuilds;
    
    @XmlElement(name = "repository")
    private List<OBSRepository> repositories;

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

    public List<OBSPerson> getPersons() {
        return persons;
    }

    public void setPersons(List<OBSPerson> persons) {
        this.persons = persons;
    }

    public List<OBSGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<OBSGroup> groups) {
        this.groups = groups;
    }

    public List<OBSEnable> getLocks() {
        return locks;
    }

    public void setLocks(List<OBSEnable> locks) {
        this.locks = locks;
    }

    public List<OBSEnable> getBuilds() {
        return builds;
    }

    public void setBuilds(List<OBSEnable> builds) {
        this.builds = builds;
    }

    public List<OBSEnable> getPublishes() {
        return publishes;
    }

    public void setPublishes(List<OBSEnable> publishes) {
        this.publishes = publishes;
    }

    public List<OBSEnable> getDebugInfos() {
        return debugInfos;
    }

    public void setDebugInfos(List<OBSEnable> debugInfos) {
        this.debugInfos = debugInfos;
    }

    public List<OBSEnable> getUseForBuilds() {
        return useForBuilds;
    }

    public void setUseForBuilds(List<OBSEnable> useForBuilds) {
        this.useForBuilds = useForBuilds;
    }

    public List<OBSRepository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<OBSRepository> repositories) {
        this.repositories = repositories;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            return name.equals(((OBSMetaConfig) obj).getName());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(name);
        return hash;
    }    
    
    @Override
    public String toString() {
        return name;
    }
    
}
