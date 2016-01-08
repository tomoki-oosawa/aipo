package com.aipo.orm.model.account;

import com.aipo.orm.model.account.auto._AccountSharedDomainMap;

public class AccountSharedDomainMap extends _AccountSharedDomainMap {

    private static AccountSharedDomainMap instance;

    private AccountSharedDomainMap() {}

    public static AccountSharedDomainMap getInstance() {
        if(instance == null) {
            instance = new AccountSharedDomainMap();
        }

        return instance;
    }
}
