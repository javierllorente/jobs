/*
 * Copyright (C) 2015-2018 Javier Llorente <javier@opensuse.org>
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
import org.w3c.dom.Element;
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

    private static class OBSXmlReaderHolder {

        private static final OBSXmlReader INSTANCE = new OBSXmlReader();
    }

    private int requestNumber;

    private OBSStatus parseStatus(InputStream is) throws SAXException, IOException,
            ParserConfigurationException {
        OBSStatus obsStatus = new OBSStatus();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(is);
        Element rootElement = document.getDocumentElement();
        NodeList nl = document.getElementsByTagName("*");
        
        for (int i = 0; i < nl.getLength(); i++) {
            if ("status".equals(nl.item(i).getNodeName())) {
                if (rootElement.hasAttributes()) {
                    NamedNodeMap attributes = rootElement.getAttributes();

                    for (int j = 0; j < attributes.getLength(); j++) {
                        Attr attribute = (Attr) attributes.item(j);
                        if ("package".equals(attribute.getName())) {
                            obsStatus.setPkg(attribute.getValue());
                        } else if ("code".equals(attribute.getName())) {
                            obsStatus.setCode(attribute.getValue());
                        }
                    }
                }
            } else if ("summary".equals(nl.item(i).getNodeName())) {
                obsStatus.setSummary(nl.item(i).getNodeValue());
            } else if ("details".equals(nl.item(i).getNodeName())) {
                obsStatus.setDetails(nl.item(i).getNodeValue());
            }
        }

        return obsStatus;
    }

    OBSStatus parseDeleteProject(String project, InputStream is) throws
            SAXException, IOException, ParserConfigurationException {
        OBSStatus status = parseStatus(is);
        status.setProject(project);
        return status;
    }

    OBSStatus parseDeletePackage(String project, String pkg, InputStream is)
            throws SAXException, IOException, ParserConfigurationException {
        OBSStatus status = parseStatus(is);
        status.setProject(project);
        status.setPkg(pkg);
        return status;
    }

    OBSStatus parseDeleteFile(String project, String pkg, String file,
            InputStream is) throws SAXException, IOException,
            ParserConfigurationException {
        OBSStatus status = parseStatus(is);
        status.setProject(project);
        status.setPkg(pkg);
        status.setDetails(file);
        return status;
    }

    OBSStatus parseBuildStatus(InputStream is) throws SAXException,
            IOException, ParserConfigurationException {
        OBSStatus status = parseStatus(is);
        return status;
    }
    
    OBSStatus parseChangeRequestState(InputStream is) throws SAXException,
            IOException, ParserConfigurationException {
        OBSStatus status = parseStatus(is);
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
                        requestNumber = Integer.parseInt(attribute.getValue());
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

    int getRequestNumber() {
        return requestNumber;
    }
}
