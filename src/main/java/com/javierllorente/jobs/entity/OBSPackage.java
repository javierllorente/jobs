/*
 * Copyright (C) 2024-2025 Javier Llorente <javier@opensuse.org>
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

import com.javierllorente.jobs.adapters.OBSProjectAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.Objects;

/**
 *
 * @author javier
 */
@XmlRootElement(name = "package")
@XmlAccessorType(XmlAccessType.FIELD)
public class OBSPackage extends OBSMetaConfig {
        
    @XmlAttribute
    @XmlJavaTypeAdapter(OBSProjectAdapter.class)
    private OBSProject project;
    
    @XmlElement(name = "url")
    private URI uri;

    public OBSProject getProject() {
        return project;
    }

    public void setProject(OBSProject project) {
        this.project = project;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OBSPackage) {
            return super.equals(obj) && project.equals(((OBSPackage) obj).getProject());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(project + name);
        return hash;
    }
    
    @Override
    public String toString() {
        return project + "/" + name;
    }
    
}
