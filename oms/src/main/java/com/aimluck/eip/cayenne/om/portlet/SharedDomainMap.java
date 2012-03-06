package com.aimluck.eip.cayenne.om.portlet;

import com.aimluck.eip.cayenne.om.portlet.auto._SharedDomainMap;

public class SharedDomainMap extends _SharedDomainMap {

    private static SharedDomainMap instance;

    private SharedDomainMap() {}

    public static SharedDomainMap getInstance() {
        if(instance == null) {
            instance = new SharedDomainMap();
        }

        return instance;
    }
}
