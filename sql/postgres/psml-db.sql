-----------------------------------------------------------------------------
-- JETSPEED_USER_PROFILE
-----------------------------------------------------------------------------

CREATE TABLE JETSPEED_USER_PROFILE (
    PSML_ID serial,
    USER_NAME VARCHAR(32) NULL,
    COUNTRY VARCHAR(2) NULL,
    LANGUAGE VARCHAR(2) NULL,
    MEDIA_TYPE VARCHAR(99) NULL,
    PAGE VARCHAR(99) NULL,
    PROFILE bytea NULL,
    PRIMARY KEY (PSML_ID),
    UNIQUE (USER_NAME, MEDIA_TYPE, LANGUAGE, COUNTRY, PAGE)
);

-----------------------------------------------------------------------------
-- JETSPEED_GROUP_PROFILE
-----------------------------------------------------------------------------

CREATE TABLE JETSPEED_GROUP_PROFILE (
    PSML_ID serial,
    GROUP_NAME VARCHAR(99) NULL,
    COUNTRY VARCHAR(2) NULL,
    LANGUAGE VARCHAR(2) NULL,
    MEDIA_TYPE VARCHAR(99) NULL,
    PAGE VARCHAR(99) NULL,
    PROFILE bytea NULL,
    PRIMARY KEY (PSML_ID),
    UNIQUE (GROUP_NAME, MEDIA_TYPE, LANGUAGE, COUNTRY, PAGE)
);

-----------------------------------------------------------------------------
-- JETSPEED_ROLE_PROFILE
-----------------------------------------------------------------------------

CREATE TABLE JETSPEED_ROLE_PROFILE (
    PSML_ID serial,
    ROLE_NAME VARCHAR(99) NULL,
    COUNTRY VARCHAR(2) NULL,
    LANGUAGE VARCHAR(2) NULL,
    MEDIA_TYPE VARCHAR(99) NULL,
    PAGE VARCHAR(99) NULL,
    PROFILE bytea NULL,
    PRIMARY KEY (PSML_ID),
    UNIQUE (ROLE_NAME, MEDIA_TYPE, LANGUAGE, COUNTRY, PAGE)
);

