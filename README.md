# jobs
A Java-based OBS Library

Copyright (C) 2015 Javier Llorente <javier@opensuse.org>

Introduction
---------------
jOBS is a Java library for consuming the [Open Build Service](http://openbuildservice.org/) REST API.
It's written in pure Java.
WIP.

**Features**
- Getting build status
- Getting submit requests
- Getting submit request diff
- Accepting/declining submit requests
- Getting project list
- Getting package list for a project
- Getting project metadata


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
} catch (MalformedURLException ex) {
    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
} catch (IOException ex) {
    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
}

if (obs.isAuthenticated()) {
//  Get Build Status
    try {
        OBSBuild build = obs.getBuildStatus("KDE:Extra", "openSUSE_13.2", 
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

Other interesting methods are acceptRequest(), declineRequest() and getRequestDiff().


License
---------------
jOBS is under the GPL v3. See gpl-3.0.txt for more information.