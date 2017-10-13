create table ACT_GE_PROPERTY (
    NAME_ varchar(64),
    VALUE_ varchar(300),
    REV_ integer,
    primary key (NAME_)
);

create table ACT_GE_BYTEARRAY (
    ID_ varchar(64),
    REV_ integer,
    NAME_ varchar(255),
    DEPLOYMENT_ID_ varchar(64),
    BYTES_ longvarbinary,
    GENERATED_ bit,
    primary key (ID_)
);

insert into ACT_GE_PROPERTY
values ('common.schema.version', '6.2.0.0', 1);

insert into ACT_GE_PROPERTY
values ('next.dbid', '1', 1);

create table ACT_RU_IDENTITYLINK (
    ID_ varchar(64),
    REV_ integer,
    GROUP_ID_ varchar(255),
    TYPE_ varchar(255),
    USER_ID_ varchar(255),
    TASK_ID_ varchar(64),
    PROC_INST_ID_ varchar(64) null,
    PROC_DEF_ID_ varchar(64),
    primary key (ID_)
);

create index ACT_IDX_IDENT_LNK_USER on ACT_RU_IDENTITYLINK(USER_ID_);
create index ACT_IDX_IDENT_LNK_GROUP on ACT_RU_IDENTITYLINK(GROUP_ID_);

insert into ACT_GE_PROPERTY values ('identitylink.schema.version', '6.2.0.0', 1);
create table ACT_RU_TASK (
    ID_ varchar(64),
    REV_ integer,
    EXECUTION_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    SCOPE_ID_ varchar(255),
    SUB_SCOPE_ID_ varchar(255),
    SCOPE_TYPE_ varchar(255),
    SCOPE_DEFINITION_ID_ varchar(255),
    NAME_ varchar(255),
    PARENT_TASK_ID_ varchar(64),
    DESCRIPTION_ varchar(4000),
    TASK_DEF_KEY_ varchar(255),
    OWNER_ varchar(255),
    ASSIGNEE_ varchar(255),
    DELEGATION_ varchar(64),
    PRIORITY_ integer,
    CREATE_TIME_ timestamp,
    DUE_DATE_ timestamp,
    CATEGORY_ varchar(255),
    SUSPENSION_STATE_ integer,
    TENANT_ID_ varchar(255) default '',
    FORM_KEY_ varchar(255),
    CLAIM_TIME_ timestamp,
    IS_COUNT_ENABLED_ bit,
    VAR_COUNT_ integer, 
    ID_LINK_COUNT_ integer,
    primary key (ID_)
);

create index ACT_IDX_TASK_CREATE on ACT_RU_TASK(CREATE_TIME_);
create index ACT_IDX_TASK_SCOPE on ACT_RU_TASK(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_TASK_SUB_SCOPE on ACT_RU_TASK(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_TASK_SCOPE_DEF on ACT_RU_TASK(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

insert into ACT_GE_PROPERTY values ('task.schema.version', '6.2.0.0', 1);
create table ACT_RU_VARIABLE (
    ID_ varchar(64) not null,
    REV_ integer,
    TYPE_ varchar(255) not null,
    NAME_ varchar(255) not null,
    EXECUTION_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    TASK_ID_ varchar(64),
    SCOPE_ID_ varchar(255),
    SUB_SCOPE_ID_ varchar(255),
    SCOPE_TYPE_ varchar(255),
    BYTEARRAY_ID_ varchar(64),
    DOUBLE_ double,
    LONG_ bigint,
    TEXT_ varchar(4000),
    TEXT2_ varchar(4000),
    primary key (ID_)
);

create index ACT_IDX_RU_VAR_SCOPE_ID_TYPE on ACT_RU_VARIABLE(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_RU_VAR_SUB_ID_TYPE on ACT_RU_VARIABLE(SUB_SCOPE_ID_, SCOPE_TYPE_);

alter table ACT_RU_VARIABLE
    add constraint ACT_FK_VAR_BYTEARRAY
    foreign key (BYTEARRAY_ID_)
    references ACT_GE_BYTEARRAY;

insert into ACT_GE_PROPERTY values ('variable.schema.version', '6.2.0.0', 1);

CREATE TABLE PUBLIC.ACT_CMMN_DATABASECHANGELOG (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED TIMESTAMP NOT NULL, ORDEREXECUTED INT NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20), CONTEXTS VARCHAR(255), LABELS VARCHAR(255), DEPLOYMENT_ID VARCHAR(10));

CREATE TABLE PUBLIC.ACT_CMMN_DEPLOYMENT (ID_ VARCHAR(255) NOT NULL, NAME_ VARCHAR(255), CATEGORY_ VARCHAR(255), KEY_ VARCHAR(255), DEPLOY_TIME_ TIMESTAMP, PARENT_DEPLOYMENT_ID_ VARCHAR(255), TENANT_ID_ VARCHAR(255) DEFAULT '', CONSTRAINT PK_ACT_CMMN_DEPLOYMENT PRIMARY KEY (ID_));

CREATE TABLE PUBLIC.ACT_CMMN_DEPLOYMENT_RESOURCE (ID_ VARCHAR(255) NOT NULL, NAME_ VARCHAR(255), DEPLOYMENT_ID_ VARCHAR(255), RESOURCE_BYTES_ BLOB, CONSTRAINT PK_CMMN_DEPLOYMENT_RESOURCE PRIMARY KEY (ID_));

ALTER TABLE PUBLIC.ACT_CMMN_DEPLOYMENT_RESOURCE ADD CONSTRAINT ACT_FK_CMMN_RSRC_DPL FOREIGN KEY (DEPLOYMENT_ID_) REFERENCES PUBLIC.ACT_CMMN_DEPLOYMENT (ID_);

CREATE INDEX PUBLIC.ACT_IDX_CMMN_RSRC_DPL ON PUBLIC.ACT_CMMN_DEPLOYMENT_RESOURCE(DEPLOYMENT_ID_);

CREATE TABLE PUBLIC.ACT_CMMN_CASEDEF (ID_ VARCHAR(255) NOT NULL, REV_ INT NOT NULL, NAME_ VARCHAR(255), KEY_ VARCHAR(255) NOT NULL, VERSION_ INT NOT NULL, CATEGORY_ VARCHAR(255), DEPLOYMENT_ID_ VARCHAR(255), RESOURCE_NAME_ VARCHAR(4000), DESCRIPTION_ VARCHAR(4000), HAS_GRAPHICAL_NOTATION_ BOOLEAN, TENANT_ID_ VARCHAR(255) DEFAULT '', CONSTRAINT PK_ACT_CMMN_CASEDEF PRIMARY KEY (ID_));

ALTER TABLE PUBLIC.ACT_CMMN_CASEDEF ADD CONSTRAINT ACT_FK_CASE_DEF_DPLY FOREIGN KEY (DEPLOYMENT_ID_) REFERENCES PUBLIC.ACT_CMMN_DEPLOYMENT (ID_);

CREATE INDEX PUBLIC.ACT_IDX_CASE_DEF_DPLY ON PUBLIC.ACT_CMMN_CASEDEF(DEPLOYMENT_ID_);

CREATE TABLE PUBLIC.ACT_CMMN_RU_CASE_INST (ID_ VARCHAR(255) NOT NULL, REV_ INT NOT NULL, BUSINESS_KEY_ VARCHAR(255), NAME_ VARCHAR(255), PARENT_ID_ VARCHAR(255), CASE_DEF_ID_ VARCHAR(255), STATE_ VARCHAR(255), START_TIME_ TIMESTAMP, START_USER_ID_ VARCHAR(255), CALLBACK_ID_ VARCHAR(255), CALLBACK_TYPE_ VARCHAR(255), TENANT_ID_ VARCHAR(255) DEFAULT '', CONSTRAINT PK_ACT_CMMN_RU_CASE_INST PRIMARY KEY (ID_));

ALTER TABLE PUBLIC.ACT_CMMN_RU_CASE_INST ADD CONSTRAINT ACT_FK_CASE_INST_CASE_DEF FOREIGN KEY (CASE_DEF_ID_) REFERENCES PUBLIC.ACT_CMMN_CASEDEF (ID_);

CREATE INDEX PUBLIC.ACT_IDX_CASE_INST_CASE_DEF ON PUBLIC.ACT_CMMN_RU_CASE_INST(CASE_DEF_ID_);

CREATE INDEX PUBLIC.ACT_IDX_CASE_INST_PARENT ON PUBLIC.ACT_CMMN_RU_CASE_INST(PARENT_ID_);

CREATE TABLE PUBLIC.ACT_CMMN_RU_PLAN_ITEM_INST (ID_ VARCHAR(255) NOT NULL, REV_ INT NOT NULL, CASE_DEF_ID_ VARCHAR(255), CASE_INST_ID_ VARCHAR(255), STAGE_INST_ID_ VARCHAR(255), IS_STAGE_ BOOLEAN, ELEMENT_ID_ VARCHAR(255), NAME_ VARCHAR(255), STATE_ VARCHAR(255), START_TIME_ TIMESTAMP, START_USER_ID_ VARCHAR(255), REFERENCE_ID_ VARCHAR(255), REFERENCE_TYPE_ VARCHAR(255), TENANT_ID_ VARCHAR(255) DEFAULT '', CONSTRAINT PK_CMMN_PLAN_ITEM_INST PRIMARY KEY (ID_));

ALTER TABLE PUBLIC.ACT_CMMN_RU_PLAN_ITEM_INST ADD CONSTRAINT ACT_FK_PLAN_ITEM_CASE_DEF FOREIGN KEY (CASE_DEF_ID_) REFERENCES PUBLIC.ACT_CMMN_CASEDEF (ID_);

CREATE INDEX PUBLIC.ACT_IDX_PLAN_ITEM_CASE_DEF ON PUBLIC.ACT_CMMN_RU_PLAN_ITEM_INST(CASE_DEF_ID_);

ALTER TABLE PUBLIC.ACT_CMMN_RU_PLAN_ITEM_INST ADD CONSTRAINT ACT_FK_PLAN_ITEM_CASE_INST FOREIGN KEY (CASE_INST_ID_) REFERENCES PUBLIC.ACT_CMMN_RU_CASE_INST (ID_);

CREATE INDEX PUBLIC.ACT_IDX_PLAN_ITEM_CASE_INST ON PUBLIC.ACT_CMMN_RU_PLAN_ITEM_INST(CASE_INST_ID_);

CREATE TABLE PUBLIC.ACT_CMMN_RU_SENTRY_PART_INST (ID_ VARCHAR(255) NOT NULL, REV_ INT NOT NULL, CASE_DEF_ID_ VARCHAR(255), CASE_INST_ID_ VARCHAR(255), PLAN_ITEM_INST_ID_ VARCHAR(255), ON_PART_ID_ VARCHAR(255), IF_PART_ID_ VARCHAR(255), TIME_STAMP_ TIMESTAMP, CONSTRAINT PK_CMMN_SENTRY_PART_INST PRIMARY KEY (ID_));

ALTER TABLE PUBLIC.ACT_CMMN_RU_SENTRY_PART_INST ADD CONSTRAINT ACT_FK_SENTRY_CASE_DEF FOREIGN KEY (CASE_DEF_ID_) REFERENCES PUBLIC.ACT_CMMN_CASEDEF (ID_);

CREATE INDEX PUBLIC.ACT_IDX_SENTRY_CASE_DEF ON PUBLIC.ACT_CMMN_RU_SENTRY_PART_INST(CASE_DEF_ID_);

ALTER TABLE PUBLIC.ACT_CMMN_RU_SENTRY_PART_INST ADD CONSTRAINT ACT_FK_SENTRY_CASE_INST FOREIGN KEY (CASE_INST_ID_) REFERENCES PUBLIC.ACT_CMMN_RU_CASE_INST (ID_);

CREATE INDEX PUBLIC.ACT_IDX_SENTRY_CASE_INST ON PUBLIC.ACT_CMMN_RU_SENTRY_PART_INST(CASE_INST_ID_);

ALTER TABLE PUBLIC.ACT_CMMN_RU_SENTRY_PART_INST ADD CONSTRAINT ACT_FK_SENTRY_PLAN_ITEM FOREIGN KEY (PLAN_ITEM_INST_ID_) REFERENCES PUBLIC.ACT_CMMN_RU_PLAN_ITEM_INST (ID_);

CREATE INDEX PUBLIC.ACT_IDX_SENTRY_PLAN_ITEM ON PUBLIC.ACT_CMMN_RU_SENTRY_PART_INST(PLAN_ITEM_INST_ID_);

CREATE TABLE PUBLIC.ACT_CMMN_RU_MIL_INST (ID_ VARCHAR(255) NOT NULL, NAME_ VARCHAR(255) NOT NULL, TIME_STAMP_ TIMESTAMP NOT NULL, CASE_INST_ID_ VARCHAR(255) NOT NULL, CASE_DEF_ID_ VARCHAR(255) NOT NULL, ELEMENT_ID_ VARCHAR(255) NOT NULL, CONSTRAINT PK_ACT_CMMN_RU_MIL_INST PRIMARY KEY (ID_));

ALTER TABLE PUBLIC.ACT_CMMN_RU_MIL_INST ADD CONSTRAINT ACT_FK_MIL_CASE_DEF FOREIGN KEY (CASE_DEF_ID_) REFERENCES PUBLIC.ACT_CMMN_CASEDEF (ID_);

CREATE INDEX PUBLIC.ACT_IDX_MIL_CASE_DEF ON PUBLIC.ACT_CMMN_RU_MIL_INST(CASE_DEF_ID_);

ALTER TABLE PUBLIC.ACT_CMMN_RU_MIL_INST ADD CONSTRAINT ACT_FK_MIL_CASE_INST FOREIGN KEY (CASE_INST_ID_) REFERENCES PUBLIC.ACT_CMMN_RU_CASE_INST (ID_);

CREATE INDEX PUBLIC.ACT_IDX_MIL_CASE_INST ON PUBLIC.ACT_CMMN_RU_MIL_INST(CASE_INST_ID_);

CREATE TABLE PUBLIC.ACT_CMMN_HI_CASE_INST (ID_ VARCHAR(255) NOT NULL, REV_ INT NOT NULL, BUSINESS_KEY_ VARCHAR(255), NAME_ VARCHAR(255), PARENT_ID_ VARCHAR(255), CASE_DEF_ID_ VARCHAR(255), STATE_ VARCHAR(255), START_TIME_ TIMESTAMP, END_TIME_ TIMESTAMP, START_USER_ID_ VARCHAR(255), CALLBACK_ID_ VARCHAR(255), CALLBACK_TYPE_ VARCHAR(255), TENANT_ID_ VARCHAR(255) DEFAULT '', CONSTRAINT PK_ACT_CMMN_HI_CASE_INST PRIMARY KEY (ID_));

CREATE TABLE PUBLIC.ACT_CMMN_HI_MIL_INST (ID_ VARCHAR(255) NOT NULL, REV_ INT NOT NULL, NAME_ VARCHAR(255) NOT NULL, TIME_STAMP_ TIMESTAMP NOT NULL, CASE_INST_ID_ VARCHAR(255) NOT NULL, CASE_DEF_ID_ VARCHAR(255) NOT NULL, ELEMENT_ID_ VARCHAR(255) NOT NULL, CONSTRAINT PK_ACT_CMMN_HI_MIL_INST PRIMARY KEY (ID_));

INSERT INTO PUBLIC.ACT_CMMN_DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('1', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', NOW(), 1, '7:1ed01100eeb9bb6054c28320b6c5fb22', 'createTable tableName=ACT_CMMN_DEPLOYMENT; createTable tableName=ACT_CMMN_DEPLOYMENT_RESOURCE; addForeignKeyConstraint baseTableName=ACT_CMMN_DEPLOYMENT_RESOURCE, constraintName=ACT_FK_CMMN_RSRC_DPL, referencedTableName=ACT_CMMN_DEPLOYMENT; create...', '', 'EXECUTED', NULL, NULL, '3.5.3', '7878883761');


