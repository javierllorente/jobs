/*
 * Copyright (C) 2015-2020 Javier Llorente <javier@opensuse.org>
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
package com.javierllorente.jobs.xml;

import com.javierllorente.jobs.OBSFile;
import com.javierllorente.jobs.OBSMetaConfig;
import com.javierllorente.jobs.OBSPkgMetaConfig;
import com.javierllorente.jobs.OBSPrjMetaConfig;
import com.javierllorente.jobs.OBSRepository;
import com.javierllorente.jobs.OBSRequest;
import com.javierllorente.jobs.OBSResult;
import com.javierllorente.jobs.OBSStatus;
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

    private OBSXmlReader() {
    }

    private static class OBSXmlReaderHolder {

        private static final OBSXmlReader INSTANCE = new OBSXmlReader();
    }

    public static OBSXmlReader getInstance() {
        return OBSXmlReaderHolder.INSTANCE;
    }

    public OBSRequest parseCreateRequest(InputStream is) throws ParserConfigurationException,
            IOException, SAXException {
        NodeList nodeList = getNodeList(is);
        OBSRequest request = new OBSRequest();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                node.getChildNodes();
            }
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                node.getChildNodes();
            }
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                node.getChildNodes();
            }
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                node.getChildNodes();
            }
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                node.getChildNodes();
            }
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                node.getChildNodes();
            }
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                node.getChildNodes();
            }
            parseStatus(node, status);
        }

        return status;
    }

    public List<OBSRequest> parseRequests(InputStream is) throws SAXException,
            IOException, ParserConfigurationException {

        OBSRequest request = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(is);

        NodeList requestNodes = document.getElementsByTagName("request");
        int requestsNum = requestNodes.getLength();
        System.out.println("Requests: " + requestsNum);
        List<OBSRequest> requests = new ArrayList<>();

        NodeList nodeList = document.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {

            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
//                System.out.println(node.getNodeName());
//		Get child nodes of collection
                node.getChildNodes();
            }
            if ("collection".equals(node.getNodeName())) {
                System.out.println("Collection found!");
                NamedNodeMap attributes = node.getAttributes();
                for (int j = 0; j < attributes.getLength(); j++) {
                    Attr attribute = (Attr) (attributes.item(j));
                    if ("matches".equals(attribute.getName())) {
                        requestCount = Integer.parseInt(attribute.getValue());
                        System.out.println("matches: " + attribute.getValue());
                    }
                }
            } else if ("request".equals(node.getNodeName())) {
                System.out.println("Request");
                request = new OBSRequest();
            }
            if (node.hasAttributes()) {
                NamedNodeMap attributes = node.getAttributes();
                for (int j = 0; j < attributes.getLength(); j++) {
                    Attr attribute = (Attr) (attributes.item(j));
//                    Get the attribute's name and value
//                  System.out.println(attribute.getName() + ": " + attribute.getValue());

                    if ("id".equals(attribute.getName())) {
                        request.setId(attribute.getValue());
                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                    }
                    if ("type".equals(attribute.getName())) {
                        request.setActionType(attribute.getValue());
                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                    }
                    if ("source".equals(node.getNodeName()) && "project".equals(attribute.getName())) {
                        request.setSourceProject(attribute.getValue());
                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                    }
                    if ("source".equals(node.getNodeName()) && "package".equals(attribute.getName())) {
                        request.setSourcePackage(attribute.getValue());
                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                    }
                    if ("target".equals(node.getNodeName()) && "project".equals(attribute.getName())) {
                        request.setTargetProject(attribute.getValue());
                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                    }
                    if ("target".equals(node.getNodeName()) && "package".equals(attribute.getName())) {
                        request.setTargetPackage(attribute.getValue());
                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                    }
                    if ("state".equals(node.getNodeName()) && "name".equals(attribute.getName())) {
                        request.setState(attribute.getValue());
                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                    }
                    if ("state".equals(node.getNodeName()) && "who".equals(attribute.getName())) {
                        request.setRequester(attribute.getValue());
                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                    }
                    if ("state".equals(node.getNodeName()) && "when".equals(attribute.getName())) {
                        request.setDate(attribute.getValue());
                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                    }
                }
            }
            if ("description".equals(node.getNodeName())) {
                String str = node.getTextContent().trim();
                if (str.length() > 0) {
                    // Text Element
                    System.out.println("Description: " + str);
                }
                requests.add(request);
            }
        }
        return requests;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public List<String> parseList(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(is);
        List<String> list = new ArrayList<>();

        NodeList nodeList = document.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
//		System.out.println(node.getNodeName());
//		Get child nodes of collection
                node.getChildNodes();
            }
            if (node.hasAttributes()) {
                NamedNodeMap attributes = node.getAttributes();
                for (int j = 0; j < attributes.getLength(); j++) {
                    Attr attribute = (Attr) (attributes.item(j));
//                    Get the attribute's name and value
//                    System.out.println(attribute.getName() + ": " + attribute.getValue());
                    if ("entry".equals(node.getNodeName()) && "name".equals(attribute.getName())) {
                        list.add(attribute.getValue());
                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                    } else if ("repository".equals(node.getNodeName()) && "name".equals(attribute.getName())) {
                        list.add(attribute.getValue());
                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                    }
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                node.getChildNodes();
            }

            if (node.getNodeName().equals("project")) {
                if (node.hasAttributes()) {
                    String name = getAttributeValue(node, "name");
                    prjMetaConfig.setName(name);
                }
            } else if (node.getNodeName().equals("repository")) {
                OBSRepository repository = parseRepository(node);
                prjMetaConfig.addRepository(repository);
            }

            parseMetaConfig(node, prjMetaConfig);

        }
        return prjMetaConfig;
    }

    public OBSPkgMetaConfig parsePkgMetaConfig(InputStream is) throws
            ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSPkgMetaConfig pkgMetaConfig = new OBSPkgMetaConfig();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                node.getChildNodes();
            }

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

    public List<OBSResult> parseResultList(InputStream is) throws SAXException, IOException,
            ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(is);
        List<OBSResult> list = new ArrayList<>();
        OBSResult result = null;

        NodeList nodeList = document.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                node.getChildNodes();
            }

            if ("result".equals(node.getNodeName())) {
                result = new OBSResult();

                if (node.hasAttributes()) {
                    NamedNodeMap attributes = node.getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        Attr attribute = (Attr) (attributes.item(j));

                        if ("result".equals(node.getNodeName())) {
                            if (null != attribute.getName()) {
                                switch (attribute.getName()) {
                                    case "project":
                                        result.setProject(attribute.getValue());
                                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                                        break;
                                    case "repository":
                                        result.setRepository(attribute.getValue());
                                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                                        break;
                                    case "arch":
                                        result.setArch(attribute.getValue());
                                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                                        break;
                                    case "code":
                                        result.setCode(attribute.getValue());
                                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                                        break;
                                    case "state":
                                        result.setState(attribute.getValue());
                                        System.out.println(attribute.getName() + ": " + attribute.getValue());
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                    }
                    list.add(result);
                }
            }

            if (result != null) {
                parseStatus(node, result.getStatus());
            }

        }
        return list;
    }

    public List<OBSFile> parseFileList(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(is);
        List<OBSFile> list = new ArrayList<>();

        NodeList nodeList = document.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                node.getChildNodes();
            }
            if (node.getNodeName().equals("entry") && node.hasAttributes()) {
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
                            file.setLastModified(attribute.getValue());
                            break;
                    }
                }
                list.add(file);
            }
        }
        return list;
    }

    private NodeList getNodeList(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(is);
        return document.getElementsByTagName("*");
    }

    private void parseStatus(Node node, OBSStatus status) {
        if ("status".equals(node.getNodeName())) {
            if (node.hasAttributes()) {
                NamedNodeMap attributes = node.getAttributes();
                for (int j = 0; j < attributes.getLength(); j++) {
                    Attr attribute = (Attr) (attributes.item(j));

                    switch (attribute.getName()) {
                        case "package":
                            status.setPkg(attribute.getValue());
                            System.out.println("package: " + attribute.getValue());
                            break;
                        case "code":
                            status.setCode(attribute.getValue());
                            System.out.println("code: " + attribute.getValue());
                            break;
                    }
                }
            }

        } else if ("summary".equals(node.getNodeName())) {
            status.setSummary(node.getTextContent());
            System.out.println("summary: " + node.getTextContent());
        } else if ("details".equals(node.getNodeName())) {
            status.setDetails(node.getTextContent());
        }
    }

    private void parseRequest(Node node, OBSRequest request) {
        if (node.hasAttributes()) {
            NamedNodeMap attributes = node.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attribute = (Attr) (attributes.item(j));

                switch (node.getNodeName()) {
                    case "request":
                        if (attribute.getValue().equals("id")) {
                            request.setId(attribute.getValue());
                            System.out.println(attribute.getName() + ": " + attribute.getValue());
                        }
                        break;

                    case "action":
                        if (attribute.getValue().equals("type")) {
                            request.setActionType(attribute.getValue());
                            System.out.println(attribute.getName() + ": " + attribute.getValue());
                        }
                        break;

                    case "source":
                        if (attribute.getValue().equals("project")) {
                            request.setSourceProject(attribute.getValue());
                            System.out.println(attribute.getName() + ": " + attribute.getValue());
                        } else if (attribute.getValue().equals("package")) {
                            request.setSourceProject(attribute.getValue());
                            System.out.println(attribute.getName() + ": " + attribute.getValue());
                        }
                        break;

                    case "target":
                        if (attribute.getValue().equals("project")) {
                            request.setTargetProject(attribute.getValue());
                            System.out.println(attribute.getName() + ": " + attribute.getValue());
                        } else if (attribute.getValue().equals("package")) {
                            request.setTargetProject(attribute.getValue());
                            System.out.println(attribute.getName() + ": " + attribute.getValue());
                        }
                        break;

                    case "state":
                        switch (attribute.getValue()) {
                            case "name":
                                request.setState(attribute.getValue());
                                System.out.println(attribute.getName() + ": " + attribute.getValue());
                                break;
                            case "who":
                                request.setRequester(attribute.getValue());
                                System.out.println(attribute.getName() + ": " + attribute.getValue());
                                break;
                            case "when":
                                request.setDate(attribute.getValue());
                                System.out.println(attribute.getName() + ": " + attribute.getValue());
                                break;
                            default:
                                break;
                        }
                        break;

                    case "description":
                        request.setDescription(node.getTextContent());
                        System.out.println(node.getNodeName() + ": " + node.getTextContent());
                        break;
                }
            }
        }
    }

    private String getAttributeValue(Node node, String item) {
        Node childNode = node.getAttributes().getNamedItem(item);
        if (childNode == null) {
            throw new NullPointerException("Attribute " + item + " not found!");
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
                metaConfig.setBuildFlag(parseRepositoryFlags(node));
                break;
            case "useforbuild":
                metaConfig.setBuildFlag(parseRepositoryFlags(node));
                break;
            case "debuginfo":
                metaConfig.setBuildFlag(parseRepositoryFlags(node));
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
            if (childNode.getNodeName().equals("path")) {
                if (childNode.hasAttributes()) {
                    String project = getAttributeValue(childNode, "project");
                    repository.setProject(project);
                    String repo = getAttributeValue(childNode, "repository");
                    repository.setRepository(repo);
                }
            } else if (childNode.getNodeName().equals("arch")) {
                repository.addArch(childNode.getTextContent());
            }
        }

        return repository;
    }
}
