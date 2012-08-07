package com.aimluck.eip.cayenne.om.security;

import com.aimluck.eip.cayenne.om.security.auto._SecuritySharedDomainMap;

public class SecuritySharedDomainMap extends _SecuritySharedDomainMap {

    private static SecuritySharedDomainMap instance;

    private SecuritySharedDomainMap() {}

    public static SecuritySharedDomainMap getInstance() {
        if(instance == null) {
            instance = new SecuritySharedDomainMap();
        }

        return instance;
    }
}
