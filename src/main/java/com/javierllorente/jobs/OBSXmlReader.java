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
package com.javierllorente.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
class OBSXmlReader {

    private OBSXmlReader() {
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

    private static class OBSXmlReaderHolder {

        private static final OBSXmlReader INSTANCE = new OBSXmlReader();
    }

    private int requestCount;

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

    OBSStatus parseDeleteProject(String project, InputStream is) throws
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

    OBSStatus parseDeletePackage(String project, String pkg, InputStream is)
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

    OBSStatus parseDeleteFile(String project, String pkg, String file,
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

    OBSStatus parseBuildStatus(InputStream is) throws SAXException,
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

    OBSStatus parseChangeRequestState(InputStream is) throws SAXException,
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

    ArrayList<OBSRequest> parseRequests(InputStream is) throws SAXException,
            IOException, ParserConfigurationException {

        OBSRequest request = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(is);

        NodeList requestNodes = document.getElementsByTagName("request");
        int requestsNum = requestNodes.getLength();
        System.out.println("Requests: " + requestsNum);
        ArrayList<OBSRequest> requests = new ArrayList<>();

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

    ArrayList<String> parseList(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(is);
        ArrayList<String> list = new ArrayList<>();

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

    ArrayList<OBSResult> parseResultList(InputStream is) throws SAXException, IOException, 
            ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(is);
        ArrayList<OBSResult> list = new ArrayList<>();
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
                            if (null != attribute.getName()) switch (attribute.getName()) {
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
                    list.add(result);
                }
            }

            if (result != null) {
                parseStatus(node, result.getStatus());
            }

        }
        return list;
    }

    int getRequestCount() {
        return requestCount;
    }
}
