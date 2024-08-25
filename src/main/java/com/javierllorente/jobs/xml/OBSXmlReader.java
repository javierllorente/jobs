/*
 * Copyright (C) 2015-2024 Javier Llorente <javier@opensuse.org>
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
package com.javierllorente.jobs.xml;

import com.javierllorente.jobs.entity.OBSAbout;
import com.javierllorente.jobs.entity.OBSDistribution;
import com.javierllorente.jobs.entity.OBSFile;
import com.javierllorente.jobs.entity.OBSLink;
import com.javierllorente.jobs.entity.OBSMetaConfig;
import com.javierllorente.jobs.entity.OBSPackage;
import com.javierllorente.jobs.entity.OBSPerson;
import com.javierllorente.jobs.entity.OBSPkgMetaConfig;
import com.javierllorente.jobs.entity.OBSPrjMetaConfig;
import com.javierllorente.jobs.entity.OBSRepository;
import com.javierllorente.jobs.entity.OBSRequest;
import com.javierllorente.jobs.entity.OBSResult;
import com.javierllorente.jobs.entity.OBSRevision;
import com.javierllorente.jobs.entity.OBSStatus;
import com.javierllorente.jobs.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author javier
 */
public class OBSXmlReader {

    private int requestCount;

    public OBSXmlReader() {
    }

    public OBSRequest parseCreateRequest(InputStream is) throws ParserConfigurationException,
            IOException, SAXException {
        NodeList nodeList = getNodeList(is);
        OBSRequest request = new OBSRequest();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseRequest(node, request);
        }
        return request;

    }

    public OBSStatus parseBranchPackage(String prj, String pkg, InputStream is) throws
            ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSStatus status = new OBSStatus();
        status.setProject(prj);
        status.setPkg(pkg);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseStatus(node, status);
        }

        return status;
    }

    public OBSStatus parseDeleteProject(String project, InputStream is) throws
            SAXException, IOException, ParserConfigurationException {
        NodeList nodeList = getNodeList(is);
        OBSStatus status = new OBSStatus();
        status.setProject(project);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseStatus(node, status);
        }

        return status;
    }

    public OBSStatus parseDeletePackage(String project, String pkg, InputStream is)
            throws SAXException, IOException, ParserConfigurationException {
        NodeList nodeList = getNodeList(is);
        OBSStatus status = new OBSStatus();
        status.setProject(project);
        status.setPkg(pkg);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseStatus(node, status);
        }

        return status;
    }

    public OBSStatus parseDeleteFile(String project, String pkg, String file,
            InputStream is) throws SAXException, IOException,
            ParserConfigurationException {
        NodeList nodeList = getNodeList(is);
        OBSStatus status = new OBSStatus();
        status.setProject(project);
        status.setPkg(pkg);
        status.setDetails(file);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseStatus(node, status);
        }

        return status;
    }

    public OBSStatus parseBuildStatus(InputStream is) throws SAXException,
            IOException, ParserConfigurationException {
        NodeList nodeList = getNodeList(is);
        OBSStatus status = new OBSStatus();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseStatus(node, status);
        }

        return status;
    }

    public OBSStatus parseChangeRequestState(InputStream is) throws SAXException,
            IOException, ParserConfigurationException {
        NodeList nodeList = getNodeList(is);
        OBSStatus status = new OBSStatus();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseStatus(node, status);
        }

        return status;
    }

    public List<OBSRequest> parseRequests(InputStream is) throws SAXException,
            IOException, ParserConfigurationException {
        
        OBSRequest request = null;        
        List<OBSRequest> requests = new ArrayList<>();
        NodeList nodeList = getNodeList(is);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            switch (node.getNodeName()) {
                case "collection":
                    String matches = getAttributeValue(node, "matches");
                    requestCount = Integer.parseInt(matches);
                    break;
                case "request":
                    request = new OBSRequest();
                    requests.add(request);
                    break;
            }
            
            parseRequest(node, request);
            
        }
        return requests;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public List<String> parseList(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {        
        List<String> list = null;
        NodeList nodeList = getNodeList(is);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            switch (node.getNodeName()) {
                case "directory":
                    list = new ArrayList<>();
                    break;
                case "entry":
                    list.add(getAttributeValue(node, "name"));
                    break;
            }
        }
        return list;
    }
    
    public List<String> parseProjectList(String userHome, InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        List<String> list = new ArrayList<>();
        NodeList nodeList = getNodeList(is);
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
                    
            if (node.getNodeName().equals("entry")) {
                String entry = getAttributeValue(node, "name");
                if (!userHome.isEmpty()) {
                    if (entry.startsWith(userHome)) {
                        list.add(entry);
                    } else if (!entry.startsWith("home")) {
                        list.add(entry);
                    }
                } else {
                    list.add(entry);
                }                
            }
            
        }
        return list;
    }

    public OBSPrjMetaConfig parsePrjMetaConfig(InputStream is) throws
            ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSPrjMetaConfig prjMetaConfig = new OBSPrjMetaConfig();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            switch(node.getNodeName()) {
                case "project":
                    if (node.hasAttributes()) {
                        String name = getAttributeValue(node, "name");
                        prjMetaConfig.setName(name);
                    }
                    break;
                case "repository":
                    OBSRepository repository = parseRepository(node);
                    prjMetaConfig.addRepository(repository);
                    break;
            }

            parseMetaConfig(node, prjMetaConfig);

        }
        return prjMetaConfig;
    }
    
    private void parsePackage(Node node, OBSPackage pkg) {        
        if (node.getNodeName().equals("package")) {
            if (node.hasAttributes()) {
                String name = getAttributeValue(node, "name");
                pkg.setName(name);
                String project = getAttributeValue(node, "project");
                pkg.setProject(project);
            }
        }
        
        switch (node.getNodeName()) {
            case "title":
                pkg.setTitle(node.getTextContent());
                break;
            case "description":
                pkg.setDescription(node.getTextContent());
                break;
        }
    }

    public OBSPkgMetaConfig parsePkgMetaConfig(InputStream is) throws
            ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSPkgMetaConfig pkgMetaConfig = new OBSPkgMetaConfig();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            
            if (node.getNodeName().equals("package")) {
                if (node.hasAttributes()) {
                    String name = getAttributeValue(node, "name");
                    pkgMetaConfig.setName(name);
                    String project = getAttributeValue(node, "project");
                    pkgMetaConfig.setProject(project);
                }
            }
            
            parseMetaConfig(node, pkgMetaConfig);
            
            if (node.getNodeName().equals("url")) {
                pkgMetaConfig.setUrl(new URL(node.getTextContent()));
            }
        }
        return pkgMetaConfig;
    }
    
    public List<OBSPackage> parsePackageSearch(InputStream is) throws 
            ParserConfigurationException, SAXException, IOException {
                NodeList nodeList = getNodeList(is);
        OBSPackage pkg = null;
        List<OBSPackage> results = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("package")) {
                pkg = new OBSPackage();
                results.add(pkg);
            }
            parsePackage(node, pkg);
        }
        return results;        
    }
    
    public List<OBSDistribution> parseDistributions(InputStream is) throws 
            ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        List<OBSDistribution> distributions = new ArrayList<>();
        OBSDistribution distribution = null;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            switch (node.getNodeName()) {
                case "distribution":
                    distribution = new OBSDistribution();
                    distributions.add(distribution);
                    if (node.hasAttributes()) {
                        distribution.setVendor(getAttributeValue(node, "vendor"));
                        distribution.setVersion(getAttributeValue(node, "version"));
                        distribution.setId(getAttributeValue(node, "id"));
                    }
                    break;
                case "name":
                    distribution.setName(node.getTextContent());
                    break;
                case "project":
                    distribution.setProject(node.getTextContent());
                    break;
                case "reponame":
                    distribution.setRepoName(node.getTextContent());
                    break;
                case "repository":
                    distribution.setRepository(node.getTextContent());
                    break;
                case "link":
                    distribution.setLink(new URL(node.getTextContent()));
                    break;
                case "icon":
                    if (node.hasAttributes()) {
                        distribution.addIcon(new URL(getAttributeValue(node, "url")));
                    }
                    break;
                case "architecture":
                    distribution.addArch(node.getTextContent());
                    break;
            }
        }

        return distributions;        
    }

    public List<OBSResult> parseResultList(InputStream is) throws SAXException, IOException,
            ParserConfigurationException {
        List<OBSResult> list = null;
        OBSResult result = null;
        OBSStatus status = null;
        NodeList nodeList = getNodeList(is);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            switch (node.getNodeName()) {
                case "resultlist":
                    list = new ArrayList<>();
                    break;
                case "result":
                    result = new OBSResult();
                    parseResult(node, result);
                    list.add(result);
                    break;
                case "status":
                    status = new OBSStatus();
                    parseStatus(node, status);
                    result.addStatus(status);
                    break;
            }
        }
        return list;
    }

    private void parseResult(Node node, OBSResult result) {
        if (node.hasAttributes()) {
            NamedNodeMap attributes = node.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attribute = (Attr) (attributes.item(j));
                switch (attribute.getName()) {
                    case "project":
                        result.setProject(attribute.getValue());
                        break;
                    case "repository":
                        result.setRepository(attribute.getValue());
                        break;
                    case "arch":
                        result.setArch(attribute.getValue());
                        break;
                    case "code":
                        result.setCode(attribute.getValue());
                        break;
                    case "state":
                        result.setState(attribute.getValue());
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    public List<OBSRevision> parseRevisionList(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        List<OBSRevision> list = null;
        NodeList nodeList = getNodeList(is);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            switch (node.getNodeName()) {
                case "revisionlist":
                    list = new ArrayList<>();
                    break;
                case "revision":
                    OBSRevision revision = new OBSRevision();
                    parseRevision(node, revision);
                    list.add(revision);
                    break;
            }
        }

        return list;
    }
    
    private void parseRevision(Node node, OBSRevision revision) {        
        if (node.hasAttributes()) {
            NamedNodeMap attributes = node.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attribute = (Attr) (attributes.item(j));
                if (attribute.getName().equals("rev")) {
                    revision.setRev(Integer.parseInt(attribute.getValue()));
                }
            }
        }
        
        NodeList nodeList = node.getChildNodes();
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            switch (childNode.getNodeName()) {
                case "srcmd5":
                    revision.setSrcmd5(childNode.getTextContent());
                    break;
                case "version":
                    revision.setVersion(childNode.getTextContent());
                    break;
                case "time":
                    revision.setTime(Utils.unixDateToDate(childNode.getTextContent()));
                    break;
                case "user":
                    revision.setUser(childNode.getTextContent());
                    break;
                case "comment":
                    revision.setComment(childNode.getTextContent());
                    break;
            }
        }
    }

    public List<OBSFile> parseFileList(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        List<OBSFile> list = null;
        NodeList nodeList = getNodeList(is);
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            switch (node.getNodeName()) {
                case "directory":
                    list = new ArrayList<>();
                    break;
                case "entry":
                    if (node.hasAttributes()) {
                        OBSFile file = new OBSFile();
                        NamedNodeMap attributes = node.getAttributes();
                        for (int j = 0; j < attributes.getLength(); j++) {
                            Attr attribute = (Attr) (attributes.item(j));
                            switch (attribute.getName()) {
                                case "name":
                                    file.setName(attribute.getValue());
                                    break;
                                case "size":
                                    file.setSize(attribute.getValue());
                                    break;
                                case "mtime":
                                    file.setLastModified(Utils.unixDateToDate(
                                            attribute.getValue()));
                                    break;
                            }
                        }
                        list.add(file);
                    }
                    break;
            }
        }
        return list;
    }
    
    public OBSLink parseLink(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSLink link = null;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("link")) {
                link = new OBSLink();
                if (node.hasAttributes()) {
                    String prj = getAttributeValue(node, "project");
                    link.setProject(prj);
                    String pkg = getAttributeValue(node, "package");
                    link.setPkg(pkg);
                }
            }
        }
        return link;
    }
    
    public OBSRevision parseRevision(InputStream is) throws
            ParserConfigurationException, SAXException, IOException {

        NodeList nodeList = getNodeList(is);
        OBSRevision revision = null;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            switch (node.getNodeName()) {
                case "revision":
                    revision = new OBSRevision();
                    if (node.hasAttributes()) {
                        String rev = getAttributeValue(node, "rev");
                        revision.setRev(Integer.parseInt(rev));
                    }
                    break;
                case "version":
                    revision.setVersion(node.getTextContent());
                    break;
                case "time":
                    revision.setTime(Utils.unixDateToDate(node.getTextContent()));
                    break;
                case "user":
                    revision.setUser(node.getTextContent());
                    break;
                case "comment":
                    revision.setComment(node.getTextContent());
                    break;
            }
        }

        return revision;
    }
    
    public OBSPerson parsePerson(InputStream is) throws
            ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSPerson person = null;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            switch (node.getNodeName()) {
                case "person":
                    person = new OBSPerson();
                    break;
                case "login":
                    person.setLogin(node.getTextContent());
                    break;
                case "email":
                    person.setEmail(node.getTextContent());
                    break;
                case "realname":
                    person.setRealName(node.getTextContent());
                    break;
                case "state":
                    person.setState(node.getTextContent());
                    break;
                case "project":
                    if (node.hasAttributes()) {
                        person.addWatchItem(getAttributeValue(node, "name"));
                    }
                    break;
                case "package":
                    if (node.hasAttributes()) {
                        String location = getAttributeValue(node, "project") 
                                + "/" + getAttributeValue(node, "name");
                        person.addWatchItem(location);
                    }
                    break;
            }
        }

        return person;
    }
    
    public OBSAbout parseAbout(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSAbout about = null;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            switch (node.getNodeName()) {
                case "about":
                    about = new OBSAbout();
                    break;
                case "title":
                    about.setTitle(node.getTextContent());
                    break;
                case "description":
                    about.setDescription(node.getTextContent());
                    break;
                case "revision":
                    about.setRevision(node.getTextContent());
                    break;
                case "last_deployment":
                    about.setLastDeployment(node.getTextContent());
                    break;
            }
        }

        return about;
    }

    private NodeList getNodeList(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(is);
        return document.getElementsByTagName("*");
    }

    private void parseStatus(Node node, OBSStatus status) {
        switch (node.getNodeName()) {
            case "status":
                if (node.hasAttributes()) {
                    NamedNodeMap attributes = node.getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        Attr attribute = (Attr) (attributes.item(j));
                        switch (attribute.getName()) {
                            case "package":
                                status.setPkg(attribute.getValue());
                                break;
                            case "code":
                                status.setCode(attribute.getValue());
                                break;
                        }
                    }
                }
                break;
            case "summary":
                status.setSummary(node.getTextContent());
                break;
            case "details":
                status.setDetails(node.getTextContent());
                break;
        }
    }

    private void parseRequest(Node node, OBSRequest request) {
        if (node.hasAttributes()) {

            switch (node.getNodeName()) {
                case "request":
                    String id = getAttributeValue(node, "id");
                    String creator = getAttributeValue(node, "creator");
                    request.setId(id);
                    request.setCreator(creator);
                    break;
                case "action":
                    String actionType = getAttributeValue(node, "type");
                    request.setActionType(actionType);
                    break;
                case "source":
                    String sourceProject = getAttributeValue(node, "project");
                    String sourcePackage = getAttributeValue(node, "package");
                    request.setSourceProject(sourceProject);
                    request.setSourcePackage(sourcePackage);
                    break;
                case "target":
                    String targetProject = getAttributeValue(node, "project");
                    request.setTargetProject(targetProject);
                    // Target may not have package attribute (project openSUSE:Maintenance)
                    if (node.getAttributes().getNamedItem("package") != null) {
                        String targetPackage = getAttributeValue(node, "package");
                        request.setTargetPackage(targetPackage);
                    }
                    break;
                case "state":
                    String state = getAttributeValue(node, "name");
                    String requester = getAttributeValue(node, "who");
                    request.setState(state);
                    request.setRequester(requester);
                    request.setDate(Utils.iso8601DateToDate(getAttributeValue(node, "when")));
                    request.setCreated(Utils.iso8601DateToDate(getAttributeValue(node, "created")));
                    break;
            }

        } else {

            switch (node.getNodeName()) {
                case "sourceupdate":
                    String sourceUpdate = node.getTextContent();
                    request.setSourceUpdate(sourceUpdate);
                    break;
                case "description":
                    if (node.getParentNode().getNodeName().equals("request")) {
                        String description = node.getTextContent();
                        request.setDescription(description);
                    }
                    break;
            }

        }

    }

    private String getAttributeValue(Node node, String item) {
        Node childNode = node.getAttributes().getNamedItem(item);
        if (childNode == null) {
            throw new IllegalArgumentException("Attribute " + item + " not found!");
        }
        return childNode.getNodeValue();
    }

    private Map<String, Boolean> parseRepositoryFlags(Node node) {
        Map<String, Boolean> flagHash = new HashMap<>();

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.hasAttributes()) {
                NamedNodeMap attributes = childNode.getAttributes();
                for (int j = 0; j < attributes.getLength(); j++) {
                    Attr attribute = (Attr) (attributes.item(j));

                    switch (childNode.getNodeName()) {
                        case "enable":
                            if (attributes.getNamedItem("repository") == null) {
                                flagHash.put("all", true);
                            } else {
                                flagHash.put(attribute.getValue(), true);
                            }
                            break;
                        case "disable":
                            if (attributes.getNamedItem("repository") == null) {
                                flagHash.put("all", false);
                            } else {
                                flagHash.put(attribute.getValue(), false);
                            }
                            break;
                    }
                }
            }
        }
        return flagHash;
    }

    private void parseMetaConfig(Node node, OBSMetaConfig metaConfig) throws
            ParserConfigurationException, SAXException, IOException {

        switch (node.getNodeName()) {
            case "title":
                metaConfig.setTitle(node.getTextContent());
                break;
            case "description":
                metaConfig.setDescription(node.getTextContent());
                break;
            case "person":
                if (node.hasAttributes()) {
                    String userId = getAttributeValue(node, "userid");
                    String role = getAttributeValue(node, "role");
                    metaConfig.putPerson(userId, role);
                }
                break;
            case "group":
                if (node.hasAttributes()) {
                    String groupId = getAttributeValue(node, "groupid");
                    String role = getAttributeValue(node, "role");
                    metaConfig.putGroup(groupId, role);
                }
                break;
            case "build":
                metaConfig.setBuildFlag(parseRepositoryFlags(node));
                break;
            case "publish":
                metaConfig.setPublishFlag(parseRepositoryFlags(node));
                break;
            case "useforbuild":
                metaConfig.setUseForBuildFlag(parseRepositoryFlags(node));
                break;
            case "debuginfo":
                metaConfig.setDebugInfoFlag(parseRepositoryFlags(node));
                break;
        }

    }

    private OBSRepository parseRepository(Node node) {
        OBSRepository repository = new OBSRepository();
        if (node.hasAttributes()) {
            String name = getAttributeValue(node, "name");
            repository.setName(name);
        }

        NodeList childNodeList = node.getChildNodes();
        for (int k = 0; k < childNodeList.getLength(); k++) {
            Node childNode = childNodeList.item(k);
              
            switch (childNode.getNodeName()) {
                case "path":
                    if (childNode.hasAttributes()) {
                        String project = getAttributeValue(childNode, "project");
                        repository.setProject(project);
                        String repo = getAttributeValue(childNode, "repository");
                        repository.setRepository(repo);
                    }
                    break;
                case "arch":
                    repository.addArch(childNode.getTextContent());
                    break;
            }
        }

        return repository;
    }
    
}
