package org.dbflute.erflute.db;

public abstract class EclipseDBManagerBase implements EclipseDBManager {

    public EclipseDBManagerBase() {
        EclipseDBManagerFactory.addDB(this);
    }

}
