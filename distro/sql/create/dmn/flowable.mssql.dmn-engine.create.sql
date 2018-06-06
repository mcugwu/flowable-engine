
CREATE TABLE ACT_DMN_DATABASECHANGELOG (ID nvarchar(191) NOT NULL, AUTHOR nvarchar(191) NOT NULL, FILENAME nvarchar(191) NOT NULL, DATEEXECUTED datetime2(3) NOT NULL, ORDEREXECUTED int NOT NULL, EXECTYPE nvarchar(10) NOT NULL, MD5SUM nvarchar(35), DESCRIPTION nvarchar(191), COMMENTS nvarchar(191), TAG nvarchar(191), LIQUIBASE nvarchar(20), CONTEXTS nvarchar(191), LABELS nvarchar(191), DEPLOYMENT_ID nvarchar(10))
GO

CREATE TABLE ACT_DMN_DEPLOYMENT (ID_ varchar(191) NOT NULL, NAME_ varchar(191), CATEGORY_ varchar(191), DEPLOY_TIME_ datetime, TENANT_ID_ varchar(191), PARENT_DEPLOYMENT_ID_ varchar(191), CONSTRAINT PK_ACT_DMN_DEPLOYMENT PRIMARY KEY (ID_))
GO

CREATE TABLE ACT_DMN_DEPLOYMENT_RESOURCE (ID_ varchar(191) NOT NULL, NAME_ varchar(191), DEPLOYMENT_ID_ varchar(191), RESOURCE_BYTES_ varbinary(MAX), CONSTRAINT PK_ACT_DMN_DEPLOYMENT_RESOURCE PRIMARY KEY (ID_))
GO

CREATE TABLE ACT_DMN_DECISION_TABLE (ID_ varchar(191) NOT NULL, NAME_ varchar(191), VERSION_ int, KEY_ varchar(191), CATEGORY_ varchar(191), DEPLOYMENT_ID_ varchar(191), PARENT_DEPLOYMENT_ID_ varchar(191), TENANT_ID_ varchar(191), RESOURCE_NAME_ varchar(191), DESCRIPTION_ varchar(191), CONSTRAINT PK_ACT_DMN_DECISION_TABLE PRIMARY KEY (ID_))
GO

INSERT INTO ACT_DMN_DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('1', 'activiti', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', GETDATE(), 1, '8:c8701f1c71018b55029f450b2e9a10a1', 'createTable tableName=ACT_DMN_DEPLOYMENT; createTable tableName=ACT_DMN_DEPLOYMENT_RESOURCE; createTable tableName=ACT_DMN_DECISION_TABLE', '', 'EXECUTED', NULL, NULL, '3.6.1', '6986084221')
GO

CREATE TABLE ACT_DMN_HI_DECISION_EXECUTION (ID_ varchar(191) NOT NULL, DECISION_DEFINITION_ID_ varchar(191), DEPLOYMENT_ID_ varchar(191), START_TIME_ datetime, END_TIME_ datetime, INSTANCE_ID_ varchar(191), EXECUTION_ID_ varchar(191), ACTIVITY_ID_ varchar(191), FAILED_ bit CONSTRAINT DF_ACT_DMN_HI_DECISION_EXECUTION_FAILED_ DEFAULT 0, TENANT_ID_ varchar(191), EXECUTION_JSON_ varchar(MAX), CONSTRAINT PK_ACT_DMN_HI_DECISION_EXECUTION PRIMARY KEY (ID_))
GO

INSERT INTO ACT_DMN_DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('2', 'flowable', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', GETDATE(), 3, '8:47f94b27feb7df8a30d4e338c7bd5fb8', 'createTable tableName=ACT_DMN_HI_DECISION_EXECUTION', '', 'EXECUTED', NULL, NULL, '3.6.1', '6986084221')
GO

ALTER TABLE ACT_DMN_HI_DECISION_EXECUTION ADD SCOPE_TYPE_ varchar(191)
GO

INSERT INTO ACT_DMN_DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('3', 'flowable', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', GETDATE(), 5, '8:ac17eae89fbdccb6e08daf3c7797b579', 'addColumn tableName=ACT_DMN_HI_DECISION_EXECUTION', '', 'EXECUTED', NULL, NULL, '3.6.1', '6986084221')
GO

ALTER TABLE ACT_DMN_DECISION_TABLE DROP COLUMN PARENT_DEPLOYMENT_ID_
GO

INSERT INTO ACT_DMN_DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('4', 'flowable', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', GETDATE(), 7, '8:f73aabc4529e7292c2942073d1cff6f9', 'dropColumn columnName=PARENT_DEPLOYMENT_ID_, tableName=ACT_DMN_DECISION_TABLE', '', 'EXECUTED', NULL, NULL, '3.6.1', '6986084221')
GO

