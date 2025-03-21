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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 *
 * @author javier
 */
@XmlRootElement(name = "directory")
@XmlAccessorType(XmlAccessType.FIELD)
public class OBSDirectory {
    
    @XmlAttribute
    private String name;
    
    @XmlAttribute
    private String rev;
    
    @XmlAttribute
    private String vrev;
    
    @XmlAttribute
    private String srcmd5;
    
    private OBSLinkInfo linkInfo;
    
    @XmlElement(name = "entry")
    private List<OBSEntry> entries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public String getVrev() {
        return vrev;
    }

    public void setVrev(String vrev) {
        this.vrev = vrev;
    }

    public String getSrcmd5() {
        return srcmd5;
    }

    public void setSrcmd5(String srcmd5) {
        this.srcmd5 = srcmd5;
    }

    public OBSLinkInfo getLinkInfo() {
        return linkInfo;
    }

    public void setLinkInfo(OBSLinkInfo linkInfo) {
        this.linkInfo = linkInfo;
    }
    
    public List<OBSEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<OBSEntry> entries) {
        this.entries = entries;
    }
    
}
