# jOBS
A Java-based OBS Library

Copyright (C) 2015-2025 Javier Llorente <javier@opensuse.org>

Introduction
---------------
jOBS is a Java library for consuming the [Open Build Service](http://openbuildservice.org/) REST API.
It's written in pure Java.

**Features**
- Getting build status
- Getting incoming/outgoing/declined submit requests
- Getting project/package submit requests
- Getting submit request diff
- Accepting/declining/creating submit requests
- Getting project list
- Gettings project results
- Getting revisions
- Getting package list for a project
- Seaching for packages
- Getting file list
- Getting project/package metadata
- Branching/linking/copying/creating/deleting packages
- Creating/deleting projects
- Uploading/downloading/deleting files
- Getting build log
- Getting distributions
- Getting links
- Getting/update persons
- Getting about


Basic usage
---------------

**Classes**
- The main class is OBS - it's the façade
- OBSBuild holds the build data such as the build status
- OBSRequest holds the submit request data

```java
OBS obs = new OBS();
// Set API URL and authenticate
try {
    obs.setApiUrl(new URL("https://api.opensuse.org"));
    obs.setUsername("yourusername");
    obs.setPassword("yourpassword");
    obs.authenticate();
} catch (MalformedURLException | IOException ex) {
    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
}

if (obs.isAuthenticated()) {
//  Get Build Status
    try {
        OBSBuild build = obs.getBuild("KDE:Extra", "openSUSE_Leap_15.6", 
                "x86_64", "amarok");
        System.out.println("Status: " + build.getStatus());
    } catch (SAXException | IOException | ParserConfigurationException ex) {
        Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
    }

//  Get Requests
    try {
        ArrayList<OBSRequest> requests = obs.getRequests();
    } catch (IOException | SAXException | ParserConfigurationException ex) {
        Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
    }
    for (OBSRequest request : requests) {
/*      Do something with each request
 *   
        request.getId();
        request.getState();
        request.getActionType();
        request.getSourceProject();
        request.getSourcePackage();
        request.getTargetProject();
        request.getTargetPackage();
        request.getRequester();
        request.getDate(); */
    }
}


```


License
---------------
jOBS is under the Apache License 2.0. See LICENSE for more information.