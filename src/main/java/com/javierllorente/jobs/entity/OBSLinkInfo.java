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
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author javier
 */
@XmlRootElement(name = "linkinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class OBSLinkInfo extends OBSObject {
    
    @XmlAttribute
    private String srcmd5;
            
    @XmlAttribute
    private String baserev;
    
    @XmlAttribute
    private String xsrcmd5;
    
    @XmlAttribute
    private String lsrcmd5;

    public String getSrcmd5() {
        return srcmd5;
    }

    public void setSrcmd5(String srcmd5) {
        this.srcmd5 = srcmd5;
    }

    public String getBaserev() {
        return baserev;
    }

    public void setBaserev(String baserev) {
        this.baserev = baserev;
    }

    public String getXsrcmd5() {
        return xsrcmd5;
    }

    public void setXsrcmd5(String xsrcmd5) {
        this.xsrcmd5 = xsrcmd5;
    }

    public String getLsrcmd5() {
        return lsrcmd5;
    }

    public void setLsrcmd5(String lsrcmd5) {
        this.lsrcmd5 = lsrcmd5;
    }    
    
}
