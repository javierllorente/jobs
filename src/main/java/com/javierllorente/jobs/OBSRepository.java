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
package com.javierllorente.jobs;

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
