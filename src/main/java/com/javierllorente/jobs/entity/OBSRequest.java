/*
 * Copyright (C) 2015-2025 Javier Llorente <javier@opensuse.org>
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
@XmlRootElement(name = "request")
@XmlAccessorType(XmlAccessType.FIELD)
public class OBSRequest {
    
    @XmlAttribute
    private String id;
    
    @XmlAttribute
    private String creator;
    
    private OBSAction action;
    
    private OBSState state;
    
    @XmlElement(name = "review")
    private List<OBSReview> reviews;
   
    OBSHistory history;

    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public OBSAction getAction() {
        return action;
    }

    public void setAction(OBSAction action) {
        this.action = action;
    }

    public OBSState getState() {
        return state;
    }

    public void setState(OBSState state) {
        this.state = state;
    }

    public List<OBSReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<OBSReview> reviews) {
        this.reviews = reviews;
    }

    public OBSHistory getHistory() {
        return history;
    }

    public void setHistory(OBSHistory history) {
        this.history = history;
    }    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
