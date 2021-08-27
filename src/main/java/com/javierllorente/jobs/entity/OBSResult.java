/*
 * Copyright (C) 2015-2021 Javier Llorente <javier@opensuse.org>
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
import java.util.List;

/**
 *
 * @author javier
 */
public class OBSResult {
    private String project;
    private String repository;
    private String arch;
    private String code;
    private String state;
    private final List<OBSStatus> statusList;

    public OBSResult() {
        statusList = new ArrayList<>();
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

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public OBSStatus getStatus() {
        return statusList.get(0);
    }
    
    public List<OBSStatus> getStatusList() {
        return statusList;
    }

    public void addStatus(OBSStatus status) {
        statusList.add(status);
    }    
}
