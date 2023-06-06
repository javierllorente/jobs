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
import java.util.List;

/**
 *
 * @author javier
 */
public class OBSRepository {
    private String name;
    private String project;
    private String repository;
    private List<String> archs;

    public OBSRepository() {
        archs = new ArrayList<>();
    }

    public OBSRepository(String name, String project, String repository, 
            List<String> archs) {
        this.name = name;
        this.project = project;
        this.repository = repository;
        this.archs = archs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public List<String> getArchs() {
        return archs;
    }

    public void setArchs(List<String> archs) {
        this.archs = archs;
    }
    
    public void addArch(String arch) {
        archs.add(arch);
    }
}
