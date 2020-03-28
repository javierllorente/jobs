/*
 * Copyright (C) 2018-2019 Javier Llorente <javier@opensuse.org>
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

import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author javier
 */
public class OBSXmlWriter {

    DocumentBuilderFactory factory;
    DocumentBuilder documentBuilder;

    public OBSXmlWriter() throws ParserConfigurationException {
        factory = DocumentBuilderFactory.newInstance();
        documentBuilder = factory.newDocumentBuilder();
    }

    private Element createProjectElement(Document document, String project) {
        Element rootElement = document.createElement("project");
        document.appendChild(rootElement);
        rootElement.setAttribute("name", project);
        return rootElement;
    }
    
    private Element createPackageElement(Document document,
            String pkg, String project) {
        Element rootElement = document.createElement("package");
        document.appendChild(rootElement);
        rootElement.setAttribute("name", pkg);
        rootElement.setAttribute("project", project);
        return rootElement;
    }
    
    private void createTextNode(Document document, Element rootElement, String tag, 
            String text) {
        Element element = document.createElement(tag);
        element.appendChild(document.createTextNode(text));
        rootElement.appendChild(element);
    }
    
    private void createPersonElement(Document document, Element rootElement, String userid, 
            String role) {
        Element personElement = document.createElement("person");
        rootElement.appendChild(personElement);
        personElement.setAttribute("userid", userid);
        personElement.setAttribute("role", role);
    }
    
    private void addRepository(Document document, Element rootElement, String name,
            String project, String repository, String... archs) {
        Element repositoryElement = document.createElement("repository");
        rootElement.appendChild(repositoryElement);
        repositoryElement.setAttribute("name", name);

        Element pathElement = document.createElement("path");
        repositoryElement.appendChild(pathElement);
        pathElement.setAttribute("project", project);
        pathElement.setAttribute("repository", repository);
        
        for (String arch : archs) {
            Element archElement = document.createElement("arch");
            archElement.appendChild(document.createTextNode(arch));
            repositoryElement.appendChild(archElement);
        }
    }
    
    public String createProjectMeta(String project, String title, String description,
            String userid) throws TransformerException {
        Document document = documentBuilder.newDocument();

        Element projectElement = createProjectElement(document, project);
        createTextNode(document, projectElement, "title", title);
        createTextNode(document, projectElement, "description", description);
        createPersonElement(document, projectElement, userid, "maintainer");

        addRepository(document, projectElement, "openSUSE_Current", "openSUSE_Current",
                "standard", "x86_64");
        addRepository(document, projectElement, "openSUSE_Tumbleweed", "openSUSE:Factory",
                "snapshot", "x86_64");

        return documentToString(document);
    }
    
    public String createPackageMeta(String project, String pkg, String title, 
            String description, String userid) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element packageElement = createPackageElement(document, pkg, project);
        createTextNode(document, packageElement, "title", title);
        createTextNode(document, packageElement, "description", description);
        createPersonElement(document, packageElement, userid, "maintainer"); 
        
        return documentToString(document);
    }  

    private String documentToString(Document document) throws TransformerException {
            StringWriter stringWriter = new StringWriter();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
    }
    
}
