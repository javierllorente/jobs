/*
 * Copyright (C) 2025 Javier Llorente <javier@opensuse.org>
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
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 *
 * @author javier
 */
@XmlRootElement(name = "action")
@XmlAccessorType(XmlAccessType.FIELD)
public class OBSAction {
    
    @XmlAttribute
    private String type;
    
    private OBSSource source;
    private OBSTarget target;
    
    @XmlElementWrapper(name = "options")
    private List<String> sourceUpdate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OBSSource getSource() {
        return source;
    }

    public void setSource(OBSSource source) {
        this.source = source;
    }

    public OBSTarget getTarget() {
        return target;
    }

    public void setTarget(OBSTarget target) {
        this.target = target;
    }

    public List<String> getSourceUpdate() {
        return sourceUpdate;
    }

    public void setSourceUpdate(List<String> sourceUpdate) {
        this.sourceUpdate = sourceUpdate;
    }
    
}
