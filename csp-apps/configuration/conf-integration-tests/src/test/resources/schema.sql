;
CREATE USER IF NOT EXISTS SA SALT '633340d183bd810b' HASH '41acd80f6ba5d2d22694a98582fcc3c5969af6e65d2e84a109b9b5feab387d4c' ADMIN;
CREATE SEQUENCE PUBLIC.CSP_IP_SEQ START WITH 853 BELONGS_TO_TABLE;
CREATE SEQUENCE PUBLIC.CSP_MANAGEMENT_SEQ START WITH 98 BELONGS_TO_TABLE;
CREATE SEQUENCE PUBLIC.CSP_INFO_SEQ START WITH 160 BELONGS_TO_TABLE;
CREATE SEQUENCE PUBLIC.MODULE_SEQ START WITH 34 BELONGS_TO_TABLE;
CREATE SEQUENCE PUBLIC.CSP_CONTACT_SEQ START WITH 541 BELONGS_TO_TABLE;
CREATE SEQUENCE PUBLIC.CSP_MODULE_INFO_SEQ START WITH 121 BELONGS_TO_TABLE;
CREATE SEQUENCE PUBLIC.MODULE_VERSION_SEQ START WITH 66 BELONGS_TO_TABLE;
CREATE CACHED TABLE PUBLIC.CSP(
    ID VARCHAR(255) NOT NULL,
    DOMAIN_NAME VARCHAR(255) NOT NULL,
    NAME VARCHAR(255) NOT NULL,
    REGISTRATION_DATE VARCHAR(255)
);
ALTER TABLE PUBLIC.CSP ADD CONSTRAINT PUBLIC.CONSTRAINT_1 PRIMARY KEY(ID);


CREATE CACHED TABLE PUBLIC.CSP_CONTACT(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.CSP_CONTACT_SEQ) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.CSP_CONTACT_SEQ,
    CONTACT_TYPE INTEGER NOT NULL,
    CSP_ID VARCHAR(255) NOT NULL,
    PERSON_EMAIL VARCHAR(255) NOT NULL,
    PERSON_NAME VARCHAR(255) NOT NULL
);
ALTER TABLE PUBLIC.CSP_CONTACT ADD CONSTRAINT PUBLIC.CONSTRAINT_7 PRIMARY KEY(ID);


CREATE CACHED TABLE PUBLIC.CSP_INFO(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.CSP_INFO_SEQ) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.CSP_INFO_SEQ,
    CSP_ID VARCHAR(255) NOT NULL,
    RECORD_DATE_TIME VARCHAR(255) NOT NULL
);
ALTER TABLE PUBLIC.CSP_INFO ADD CONSTRAINT PUBLIC.CONSTRAINT_E PRIMARY KEY(ID);


CREATE CACHED TABLE PUBLIC.CSP_IP(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.CSP_IP_SEQ) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.CSP_IP_SEQ,
    CSP_ID VARCHAR(255) NOT NULL,
    EXTERNAL INTEGER NOT NULL,
    IP VARCHAR(255) NOT NULL
);
ALTER TABLE PUBLIC.CSP_IP ADD CONSTRAINT PUBLIC.CONSTRAINT_77 PRIMARY KEY(ID);


CREATE CACHED TABLE PUBLIC.CSP_MANAGEMENT(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.CSP_MANAGEMENT_SEQ) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.CSP_MANAGEMENT_SEQ,
    CSP_ID VARCHAR(255) NOT NULL,
    DATE_CHANGED VARCHAR(255),
    MODULE_ID BIGINT NOT NULL,
    MODULE_VERSION_ID BIGINT NOT NULL
);
ALTER TABLE PUBLIC.CSP_MANAGEMENT ADD CONSTRAINT PUBLIC.CONSTRAINT_C PRIMARY KEY(ID);


CREATE CACHED TABLE PUBLIC.CSP_MODULE_INFO(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.CSP_MODULE_INFO_SEQ) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.CSP_MODULE_INFO_SEQ,
    CSP_INFO_ID BIGINT NOT NULL,
    MODULE_INSTALLED_ON VARCHAR(255) NOT NULL,
    MODULE_IS_ACTIVE INTEGER NOT NULL,
    MODULE_VERSION_ID BIGINT NOT NULL
);
ALTER TABLE PUBLIC.CSP_MODULE_INFO ADD CONSTRAINT PUBLIC.CONSTRAINT_5 PRIMARY KEY(ID);


CREATE CACHED TABLE PUBLIC.MODULE(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.MODULE_SEQ) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.MODULE_SEQ,
    IS_DEFAULT INTEGER NOT NULL,
    NAME VARCHAR(255) NOT NULL,
    START_PRIORITY INTEGER NOT NULL
);
ALTER TABLE PUBLIC.MODULE ADD CONSTRAINT PUBLIC.CONSTRAINT_8 PRIMARY KEY(ID);


CREATE CACHED TABLE PUBLIC.MODULE_VERSION(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.MODULE_VERSION_SEQ) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.MODULE_VERSION_SEQ,
    DESCRIPTION VARCHAR(255),
    FULL_NAME VARCHAR(255) NOT NULL,
    HASH VARCHAR(255) NOT NULL,
    MODULE_ID BIGINT NOT NULL,
    RELEASED_ON VARCHAR(255) NOT NULL,
    VERSION INTEGER NOT NULL
);
ALTER TABLE PUBLIC.MODULE_VERSION ADD CONSTRAINT PUBLIC.CONSTRAINT_4 PRIMARY KEY(ID);


ALTER TABLE PUBLIC.MODULE ADD CONSTRAINT PUBLIC.UK_F73DSVAOR0F4CYCVLDYT2IDF1 UNIQUE(NAME);
ALTER TABLE PUBLIC.MODULE_VERSION ADD CONSTRAINT PUBLIC.UK_EPUC9SKUK4MCRJ90T64Q6N25C UNIQUE(HASH);