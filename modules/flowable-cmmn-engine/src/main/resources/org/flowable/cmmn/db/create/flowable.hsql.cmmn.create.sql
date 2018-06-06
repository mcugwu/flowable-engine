
CREATE TABLE PUBLIC.DATABASECHANGELOG (ID varchar(191) NOT NULL, AUTHOR varchar(191) NOT NULL, FILENAME varchar(191) NOT NULL, DATEEXECUTED TIMESTAMP NOT NULL, ORDEREXECUTED INT NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION varchar(191), COMMENTS varchar(191), TAG varchar(191), LIQUIBASE VARCHAR(20), CONTEXTS varchar(191), LABELS varchar(191), DEPLOYMENT_ID VARCHAR(10));

CREATE TABLE PUBLIC.ACT_CMMN_RE_DEPLOYMENT (ID_ varchar(191) NOT NULL, NAME_ varchar(191), CATEGORY_ varchar(191), DEPLOY_TIME_ TIMESTAMP, PARENT_DEPLOYMENT_ID_ varchar(191), TENANT_ID_ varchar(191), CONSTRAINT PK_ACT_CMMN_RE_DEPLOYMENT PRIMARY KEY (ID_));

CREATE TABLE PUBLIC.ACT_CMMN_RE_DEPLOYMENT_RESOURCE (ID_ varchar(191) NOT NULL, NAME_ varchar(191), DEPLOYMENT_ID_ varchar(191), RESOURCE_BYTES_ BLOB, CONSTRAINT PK_ACT_CMMN_RE_DEPLOYMENT_RESOURCE PRIMARY KEY (ID_));

ALTER TABLE PUBLIC.ACT_CMMN_RE_DEPLOYMENT_RESOURCE ADD CONSTRAINT ACT_FK_CMMN_RSRC_DPL FOREIGN KEY (DEPLOYMENT_ID_) REFERENCES PUBLIC.ACT_CMMN_RE_DEPLOYMENT (ID_);

CREATE INDEX PUBLIC.ACT_IDX_CMMN_RSRC_DPL ON PUBLIC.ACT_CMMN_RE_DEPLOYMENT_RESOURCE(DEPLOYMENT_ID_);

CREATE TABLE PUBLIC.ACT_CMMN_RE_CASEDEF (ID_ varchar(191) NOT NULL, REV_ INT NOT NULL, NAME_ varchar(191), KEY_ varchar(191) NOT NULL, VERSION_ varchar(191) NOT NULL, CATEGORY_ varchar(191), DEPLOYMENT_ID_ varchar(191), RESOURCE_NAME_ VARCHAR(4000), DESCRIPTION_ VARCHAR(4000), HAS_GRAPHICAL_NOTATION_ BOOLEAN, TENANT_ID_ varchar(191) DEFAULT '' NOT NULL, CONSTRAINT PK_ACT_CMMN_RE_CASEDEF PRIMARY KEY (ID_));

ALTER TABLE PUBLIC.ACT_CMMN_RE_CASEDEF ADD CONSTRAINT ACT_FK_CASE_DEF_DPLY FOREIGN KEY (DEPLOYMENT_ID_) REFERENCES PUBLIC.ACT_CMMN_RE_DEPLOYMENT (ID_);

CREATE INDEX PUBLIC.ACT_IDX_CASE_DEF_DPLY ON PUBLIC.ACT_CMMN_RE_CASEDEF(DEPLOYMENT_ID_);

CREATE TABLE PUBLIC.ACT_CMMN_RU_CASE_INST (ID_ varchar(191) NOT NULL, REV_ INT NOT NULL, BUSINESS_KEY_ varchar(191), NAME_ varchar(191), PARENT_ID_ varchar(191), CASE_DEF_ID_ varchar(191), STATE_ varchar(191), START_TIME_ TIMESTAMP, START_USER_ID_ varchar(191), CALLBACK_ID_ varchar(191), CALLBACK_TYPE_ varchar(191), TENANT_ID_ varchar(191) DEFAULT '', CONSTRAINT PK_ACT_CMMN_RU_CASE_INST PRIMARY KEY (ID_));

ALTER TABLE PUBLIC.ACT_CMMN_RU_CASE_INST ADD CONSTRAINT ACT_FK_CASE_INST_CASE_DEF FOREIGN KEY (CASE_DEF_ID_) REFERENCES PUBLIC.ACT_CMMN_RE_CASEDEF (ID_);

CREATE INDEX PUBLIC.ACT_IDX_CASE_INST_CASE_DEF ON PUBLIC.ACT_CMMN_RU_CASE_INST(CASE_DEF_ID_);

CREATE INDEX PUBLIC.ACT_IDX_CASE_INST_PARENT ON PUBLIC.ACT_CMMN_RU_CASE_INST(PARENT_ID_);

CREATE TABLE PUBLIC.ACT_CMMN_RU_PLAN_ITEM_INST (ID_ varchar(191) NOT NULL, REV_ INT NOT NULL, CASE_DEF_ID_ varchar(191), CASE_INST_ID_ varchar(191), STAGE_INST_ID_ varchar(191), IS_STAGE_ BOOLEAN, ELEMENT_ID_ varchar(191), NAME_ varchar(191), STATE_ varchar(191), START_TIME_ TIMESTAMP, START_USER_ID_ varchar(191), REFERENCE_ID_ varchar(191), REFERENCE_TYPE_ varchar(191), TENANT_ID_ varchar(191) DEFAULT '', CONSTRAINT PK_ACT_CMMN_RU_PLAN_ITEM_INST PRIMARY KEY (ID_));

ALTER TABLE PUBLIC.ACT_CMMN_RU_PLAN_ITEM_INST ADD CONSTRAINT ACT_FK_PLAN_ITEM_CASE_DEF FOREIGN KEY (CASE_DEF_ID_) REFERENCES PUBLIC.ACT_CMMN_RE_CASEDEF (ID_);

CREATE INDEX PUBLIC.ACT_IDX_PLAN_ITEM_CASE_DEF ON PUBLIC.ACT_CMMN_RU_PLAN_ITEM_INST(CASE_DEF_ID_);

ALTER TABLE PUBLIC.ACT_CMMN_RU_PLAN_ITEM_INST ADD CONSTRAINT ACT_FK_PLAN_ITEM_CASE_INST FOREIGN KEY (CASE_INST_ID_) REFERENCES PUBLIC.ACT_CMMN_RU_CASE_INST (ID_);

CREATE INDEX PUBLIC.ACT_IDX_PLAN_ITEM_CASE_INST ON PUBLIC.ACT_CMMN_RU_PLAN_ITEM_INST(CASE_INST_ID_);

CREATE TABLE PUBLIC.ACT_CMMN_RU_SENTRY_ON_PART_INST (ID_ varchar(191) NOT NULL, REV_ INT NOT NULL, CASE_DEF_ID_ varchar(191), CASE_INST_ID_ varchar(191), PLAN_ITEM_INST_ID_ varchar(191), ON_PART_ID_ varchar(191), TIME_STAMP_ TIMESTAMP, CONSTRAINT PK_ACT_CMMN_RU_SENTRY_ON_PART_INST PRIMARY KEY (ID_));

ALTER TABLE PUBLIC.ACT_CMMN_RU_SENTRY_ON_PART_INST ADD CONSTRAINT ACT_FK_SENTRY_CASE_DEF FOREIGN KEY (CASE_DEF_ID_) REFERENCES PUBLIC.ACT_CMMN_RE_CASEDEF (ID_);

CREATE INDEX PUBLIC.ACT_IDX_SENTRY_CASE_DEF ON PUBLIC.ACT_CMMN_RU_SENTRY_ON_PART_INST(CASE_DEF_ID_);

ALTER TABLE PUBLIC.ACT_CMMN_RU_SENTRY_ON_PART_INST ADD CONSTRAINT ACT_FK_SENTRY_CASE_INST FOREIGN KEY (CASE_INST_ID_) REFERENCES PUBLIC.ACT_CMMN_RU_CASE_INST (ID_);

CREATE INDEX PUBLIC.ACT_IDX_SENTRY_CASE_INST ON PUBLIC.ACT_CMMN_RU_SENTRY_ON_PART_INST(CASE_INST_ID_);

ALTER TABLE PUBLIC.ACT_CMMN_RU_SENTRY_ON_PART_INST ADD CONSTRAINT ACT_FK_SENTRY_PLAN_ITEM FOREIGN KEY (PLAN_ITEM_INST_ID_) REFERENCES PUBLIC.ACT_CMMN_RU_PLAN_ITEM_INST (ID_);

CREATE INDEX PUBLIC.ACT_IDX_SENTRY_PLAN_ITEM ON PUBLIC.ACT_CMMN_RU_SENTRY_ON_PART_INST(PLAN_ITEM_INST_ID_);

CREATE TABLE PUBLIC.ACT_CMMN_RU_MIL_INST (ID_ varchar(191) NOT NULL, NAME_ varchar(191) NOT NULL, TIME_STAMP_ TIMESTAMP NOT NULL, CASE_INST_ID_ varchar(191) NOT NULL, CASE_DEF_ID_ varchar(191) NOT NULL, ELEMENT_ID_ varchar(191) NOT NULL, CONSTRAINT PK_ACT_CMMN_RU_MIL_INST PRIMARY KEY (ID_));

ALTER TABLE PUBLIC.ACT_CMMN_RU_MIL_INST ADD CONSTRAINT ACT_FK_MIL_CASE_DEF FOREIGN KEY (CASE_DEF_ID_) REFERENCES PUBLIC.ACT_CMMN_RE_CASEDEF (ID_);

CREATE INDEX PUBLIC.ACT_IDX_MIL_CASE_DEF ON PUBLIC.ACT_CMMN_RU_MIL_INST(CASE_DEF_ID_);

ALTER TABLE PUBLIC.ACT_CMMN_RU_MIL_INST ADD CONSTRAINT ACT_FK_MIL_CASE_INST FOREIGN KEY (CASE_INST_ID_) REFERENCES PUBLIC.ACT_CMMN_RU_CASE_INST (ID_);

CREATE INDEX PUBLIC.ACT_IDX_MIL_CASE_INST ON PUBLIC.ACT_CMMN_RU_MIL_INST(CASE_INST_ID_);

CREATE TABLE PUBLIC.ACT_CMMN_HI_CASE_INST (ID_ varchar(191) NOT NULL, REV_ INT NOT NULL, BUSINESS_KEY_ varchar(191), NAME_ varchar(191), PARENT_ID_ varchar(191), CASE_DEF_ID_ varchar(191), STATE_ varchar(191), START_TIME_ TIMESTAMP, END_TIME_ TIMESTAMP, START_USER_ID_ varchar(191), CALLBACK_ID_ varchar(191), CALLBACK_TYPE_ varchar(191), TENANT_ID_ varchar(191) DEFAULT '', CONSTRAINT PK_ACT_CMMN_HI_CASE_INST PRIMARY KEY (ID_));

CREATE TABLE PUBLIC.ACT_CMMN_HI_MIL_INST (ID_ varchar(191) NOT NULL, REV_ INT NOT NULL, NAME_ varchar(191) NOT NULL, TIME_STAMP_ TIMESTAMP NOT NULL, CASE_INST_ID_ varchar(191) NOT NULL, CASE_DEF_ID_ varchar(191) NOT NULL, ELEMENT_ID_ varchar(191) NOT NULL, CONSTRAINT PK_ACT_CMMN_HI_MIL_INST PRIMARY KEY (ID_));

INSERT INTO PUBLIC.DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('1', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', NOW, 1, '7:28e5931d36abab0185c189c584a7c2d0', 'createTable tableName=ACT_CMMN_RE_DEPLOYMENT; createTable tableName=ACT_CMMN_RE_DEPLOYMENT_RESOURCE; addForeignKeyConstraint baseTableName=ACT_CMMN_RE_DEPLOYMENT_RESOURCE, constraintName=ACT_FK_CMMN_RSRC_DPL, referencedTableName=ACT_CMMN_RE_DEPLOY...', '', 'EXECUTED', NULL, NULL, '3.5.3', '4471205090');

