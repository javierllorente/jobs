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
import java.util.List;

/**
 *
 * @author javier
 */
public class OBSPrjMetaConfig extends OBSMetaConfig {
    private List<OBSRepository> repositories;

    public OBSPrjMetaConfig() {
        repositories = new ArrayList<>();
    }

    public List<OBSRepository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<OBSRepository> repositories) {
        this.repositories = repositories;
    }
    
    public void addRepository(OBSRepository repository) {
        repositories.add(repository);
    }
}
