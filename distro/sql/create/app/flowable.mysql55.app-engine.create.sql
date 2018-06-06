
CREATE TABLE ACT_APP_DATABASECHANGELOG (ID varchar(191) NOT NULL, AUTHOR varchar(191) NOT NULL, FILENAME varchar(191) NOT NULL, DATEEXECUTED datetime NOT NULL, ORDEREXECUTED INT NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35) NULL, DESCRIPTION varchar(191) NULL, COMMENTS varchar(191) NULL, TAG varchar(191) NULL, LIQUIBASE VARCHAR(20) NULL, CONTEXTS varchar(191) NULL, LABELS varchar(191) NULL, DEPLOYMENT_ID VARCHAR(10) NULL);

CREATE TABLE ACT_APP_DEPLOYMENT (ID_ varchar(191) NOT NULL, NAME_ varchar(191) NULL, CATEGORY_ varchar(191) NULL, KEY_ varchar(191) NULL, DEPLOY_TIME_ datetime NULL, TENANT_ID_ varchar(191) DEFAULT '' NULL, CONSTRAINT PK_ACT_APP_DEPLOYMENT PRIMARY KEY (ID_));

CREATE TABLE ACT_APP_DEPLOYMENT_RESOURCE (ID_ varchar(191) NOT NULL, NAME_ varchar(191) NULL, DEPLOYMENT_ID_ varchar(191) NULL, RESOURCE_BYTES_ LONGBLOB NULL, CONSTRAINT PK_APP_DEPLOYMENT_RESOURCE PRIMARY KEY (ID_));

ALTER TABLE ACT_APP_DEPLOYMENT_RESOURCE ADD CONSTRAINT ACT_FK_APP_RSRC_DPL FOREIGN KEY (DEPLOYMENT_ID_) REFERENCES ACT_APP_DEPLOYMENT (ID_);

CREATE INDEX ACT_IDX_APP_RSRC_DPL ON ACT_APP_DEPLOYMENT_RESOURCE(DEPLOYMENT_ID_);

CREATE TABLE ACT_APP_APPDEF (ID_ varchar(191) NOT NULL, REV_ INT NOT NULL, NAME_ varchar(191) NULL, KEY_ varchar(191) NOT NULL, VERSION_ INT NOT NULL, CATEGORY_ varchar(191) NULL, DEPLOYMENT_ID_ varchar(191) NULL, RESOURCE_NAME_ VARCHAR(4000) NULL, DESCRIPTION_ VARCHAR(4000) NULL, TENANT_ID_ varchar(191) DEFAULT '' NULL, CONSTRAINT PK_ACT_APP_APPDEF PRIMARY KEY (ID_));

ALTER TABLE ACT_APP_APPDEF ADD CONSTRAINT ACT_FK_APP_DEF_DPLY FOREIGN KEY (DEPLOYMENT_ID_) REFERENCES ACT_APP_DEPLOYMENT (ID_);

CREATE INDEX ACT_IDX_APP_DEF_DPLY ON ACT_APP_APPDEF(DEPLOYMENT_ID_);

INSERT INTO ACT_APP_DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('1', 'flowable', 'org/flowable/app/db/liquibase/flowable-app-db-changelog.xml', NOW(), 1, '8:496fc778bdf2ab13f2e1926d0e63e0a2', 'createTable tableName=ACT_APP_DEPLOYMENT; createTable tableName=ACT_APP_DEPLOYMENT_RESOURCE; addForeignKeyConstraint baseTableName=ACT_APP_DEPLOYMENT_RESOURCE, constraintName=ACT_FK_APP_RSRC_DPL, referencedTableName=ACT_APP_DEPLOYMENT; createIndex...', '', 'EXECUTED', NULL, NULL, '3.6.1', '6986085411');

