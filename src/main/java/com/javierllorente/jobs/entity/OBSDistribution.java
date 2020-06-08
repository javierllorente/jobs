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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author javier
 */
public class OBSDistribution {
    
    private String vendor;
    private String version;
    private String id;
    private String name;
    private String project;
    private String repoName;
    private String repository;
    private URL link;
    private List<URL> icons;
    private List<String> archs;

    public OBSDistribution() {
        icons = new ArrayList<>();
        archs = new ArrayList<>();
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public List<URL> getIcons() {
        return icons;
    }

    public void setIcons(List<URL> icons) {
        this.icons = icons;
    }
    
    public void addIcon(URL icon) {
        icons.add(icon);
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
