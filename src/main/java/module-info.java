module com.javierllorente.jobs {
    requires java.base;
    requires java.naming;
    requires java.xml;
    requires java.logging;
    requires jakarta.ws.rs;
    requires jakarta.xml.bind;
    requires jersey.common;
    requires jersey.client;
    requires jersey.apache5.connector;
    opens com.javierllorente.jobs.entity to jakarta.xml.bind;
    exports com.javierllorente.jobs;
    exports com.javierllorente.jobs.entity;
    exports com.javierllorente.jobs.adapters;
}
