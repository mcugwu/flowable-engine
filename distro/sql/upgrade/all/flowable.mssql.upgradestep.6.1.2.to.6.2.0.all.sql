insert into ACT_GE_PROPERTY values ('common.schema.version', '6.2.0.0', 1);
insert into ACT_GE_PROPERTY values ('identitylink.schema.version', '6.2.0.0', 1);
alter table ACT_RU_TASK add SCOPE_ID_ nvarchar(255);
alter table ACT_RU_TASK add SUB_SCOPE_ID_ nvarchar(255);
alter table ACT_RU_TASK add SCOPE_TYPE_ nvarchar(255);
alter table ACT_RU_TASK add SCOPE_DEFINITION_ID_ nvarchar(255);

create index ACT_IDX_TASK_SCOPE on ACT_RU_TASK(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_TASK_SUB_SCOPE on ACT_RU_TASK(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_TASK_SCOPE_DEF on ACT_RU_TASK(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

insert into ACT_GE_PROPERTY values ('task.schema.version', '6.2.0.0', 1);
alter table ACT_RU_VARIABLE add SCOPE_ID_ nvarchar(255);
alter table ACT_RU_VARIABLE add SUB_SCOPE_ID_ nvarchar(255);
alter table ACT_RU_VARIABLE add SCOPE_TYPE_ nvarchar(255);

create index ACT_IDX_RU_VAR_SCOPE_ID_TYPE on ACT_RU_VARIABLE(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_RU_VAR_SUB_ID_TYPE on ACT_RU_VARIABLE(SUB_SCOPE_ID_, SCOPE_TYPE_);

insert into ACT_GE_PROPERTY values ('variable.schema.version', '6.2.0.0', 1);
insert into ACT_GE_PROPERTY values ('job.schema.version', '6.2.0.0', 1);

alter table ACT_HI_TASKINST add SCOPE_ID_ nvarchar(255);
alter table ACT_HI_TASKINST add SUB_SCOPE_ID_ nvarchar(255);
alter table ACT_HI_TASKINST add SCOPE_TYPE_ nvarchar(255);
alter table ACT_HI_TASKINST add SCOPE_DEFINITION_ID_ nvarchar(255);

create index ACT_IDX_HI_TASK_SCOPE on ACT_HI_TASKINST(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_HI_TASK_SUB_SCOPE on ACT_HI_TASKINST(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_HI_TASK_SCOPE_DEF on ACT_HI_TASKINST(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);
alter table ACT_HI_VARINST add SCOPE_ID_ nvarchar(255);
alter table ACT_HI_VARINST add SUB_SCOPE_ID_ nvarchar(255);
alter table ACT_HI_VARINST add SCOPE_TYPE_ nvarchar(255);

create index ACT_IDX_HI_VAR_SCOPE_ID_TYPE on ACT_HI_VARINST(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_HI_VAR_SUB_ID_TYPE on ACT_HI_VARINST(SUB_SCOPE_ID_, SCOPE_TYPE_);
alter table ACT_RU_EXECUTION add CALLBACK_ID_ nvarchar(255);
alter table ACT_RU_EXECUTION add CALLBACK_TYPE_ nvarchar(255);

update ACT_GE_PROPERTY set VALUE_ = '6.2.0.0' where NAME_ = 'schema.version';

update ACT_ID_PROPERTY set VALUE_ = '6.2.0.0' where NAME_ = 'schema.version';

CREATE TABLE [ACT_CMMN_DATABASECHANGELOGLOCK] ([ID] [int] NOT NULL, [LOCKED] [bit] NOT NULL, [LOCKGRANTED] [datetime2](3), [LOCKEDBY] [nvarchar](255), CONSTRAINT [PK_ACT_CMMN_DATABASECHANGELOGLOCK] PRIMARY KEY ([ID]))

DELETE FROM [ACT_CMMN_DATABASECHANGELOGLOCK]

INSERT INTO [ACT_CMMN_DATABASECHANGELOGLOCK] ([ID], [LOCKED]) VALUES (1, 0)

UPDATE [ACT_CMMN_DATABASECHANGELOGLOCK] SET [LOCKED] = 1, [LOCKEDBY] = '192.168.1.5 (192.168.1.5)', [LOCKGRANTED] = '2019-03-13T21:40:36.922' WHERE [ID] = 1 AND [LOCKED] = 0

CREATE TABLE [ACT_CMMN_DATABASECHANGELOG] ([ID] [nvarchar](255) NOT NULL, [AUTHOR] [nvarchar](255) NOT NULL, [FILENAME] [nvarchar](255) NOT NULL, [DATEEXECUTED] [datetime2](3) NOT NULL, [ORDEREXECUTED] [int] NOT NULL, [EXECTYPE] [nvarchar](10) NOT NULL, [MD5SUM] [nvarchar](35), [DESCRIPTION] [nvarchar](255), [COMMENTS] [nvarchar](255), [TAG] [nvarchar](255), [LIQUIBASE] [nvarchar](20), [CONTEXTS] [nvarchar](255), [LABELS] [nvarchar](255), [DEPLOYMENT_ID] [nvarchar](10))

CREATE TABLE [ACT_CMMN_DEPLOYMENT] ([ID_] [varchar](255) NOT NULL, [NAME_] [varchar](255), [CATEGORY_] [varchar](255), [KEY_] [varchar](255), [DEPLOY_TIME_] [datetime], [PARENT_DEPLOYMENT_ID_] [varchar](255), [TENANT_ID_] [varchar](255) CONSTRAINT [DF_ACT_CMMN_DEPLOYMENT_TENANT_ID_] DEFAULT '', CONSTRAINT [PK_ACT_CMMN_DEPLOYMENT] PRIMARY KEY ([ID_]))

CREATE TABLE [ACT_CMMN_DEPLOYMENT_RESOURCE] ([ID_] [varchar](255) NOT NULL, [NAME_] [varchar](255), [DEPLOYMENT_ID_] [varchar](255), [RESOURCE_BYTES_] [varbinary](MAX), CONSTRAINT [PK_CMMN_DEPLOYMENT_RESOURCE] PRIMARY KEY ([ID_]))

ALTER TABLE [ACT_CMMN_DEPLOYMENT_RESOURCE] ADD CONSTRAINT [ACT_FK_CMMN_RSRC_DPL] FOREIGN KEY ([DEPLOYMENT_ID_]) REFERENCES [ACT_CMMN_DEPLOYMENT] ([ID_])

CREATE NONCLUSTERED INDEX ACT_IDX_CMMN_RSRC_DPL ON [ACT_CMMN_DEPLOYMENT_RESOURCE]([DEPLOYMENT_ID_])

CREATE TABLE [ACT_CMMN_CASEDEF] ([ID_] [varchar](255) NOT NULL, [REV_] [int] NOT NULL, [NAME_] [varchar](255), [KEY_] [varchar](255) NOT NULL, [VERSION_] [int] NOT NULL, [CATEGORY_] [varchar](255), [DEPLOYMENT_ID_] [varchar](255), [RESOURCE_NAME_] [varchar](4000), [DESCRIPTION_] [varchar](4000), [HAS_GRAPHICAL_NOTATION_] [bit], [TENANT_ID_] [varchar](255) CONSTRAINT [DF_ACT_CMMN_CASEDEF_TENANT_ID_] DEFAULT '', CONSTRAINT [PK_ACT_CMMN_CASEDEF] PRIMARY KEY ([ID_]))

ALTER TABLE [ACT_CMMN_CASEDEF] ADD CONSTRAINT [ACT_FK_CASE_DEF_DPLY] FOREIGN KEY ([DEPLOYMENT_ID_]) REFERENCES [ACT_CMMN_DEPLOYMENT] ([ID_])

CREATE NONCLUSTERED INDEX ACT_IDX_CASE_DEF_DPLY ON [ACT_CMMN_CASEDEF]([DEPLOYMENT_ID_])

CREATE TABLE [ACT_CMMN_RU_CASE_INST] ([ID_] [varchar](255) NOT NULL, [REV_] [int] NOT NULL, [BUSINESS_KEY_] [varchar](255), [NAME_] [varchar](255), [PARENT_ID_] [varchar](255), [CASE_DEF_ID_] [varchar](255), [STATE_] [varchar](255), [START_TIME_] [datetime], [START_USER_ID_] [varchar](255), [CALLBACK_ID_] [varchar](255), [CALLBACK_TYPE_] [varchar](255), [TENANT_ID_] [varchar](255) CONSTRAINT [DF_ACT_CMMN_RU_CASE_INST_TENANT_ID_] DEFAULT '', CONSTRAINT [PK_ACT_CMMN_RU_CASE_INST] PRIMARY KEY ([ID_]))

ALTER TABLE [ACT_CMMN_RU_CASE_INST] ADD CONSTRAINT [ACT_FK_CASE_INST_CASE_DEF] FOREIGN KEY ([CASE_DEF_ID_]) REFERENCES [ACT_CMMN_CASEDEF] ([ID_])

CREATE NONCLUSTERED INDEX ACT_IDX_CASE_INST_CASE_DEF ON [ACT_CMMN_RU_CASE_INST]([CASE_DEF_ID_])

CREATE NONCLUSTERED INDEX ACT_IDX_CASE_INST_PARENT ON [ACT_CMMN_RU_CASE_INST]([PARENT_ID_])

CREATE TABLE [ACT_CMMN_RU_PLAN_ITEM_INST] ([ID_] [varchar](255) NOT NULL, [REV_] [int] NOT NULL, [CASE_DEF_ID_] [varchar](255), [CASE_INST_ID_] [varchar](255), [STAGE_INST_ID_] [varchar](255), [IS_STAGE_] [bit], [ELEMENT_ID_] [varchar](255), [NAME_] [varchar](255), [STATE_] [varchar](255), [START_TIME_] [datetime], [START_USER_ID_] [varchar](255), [REFERENCE_ID_] [varchar](255), [REFERENCE_TYPE_] [varchar](255), [TENANT_ID_] [varchar](255) CONSTRAINT [DF_ACT_CMMN_RU_PLAN_ITEM_INST_TENANT_ID_] DEFAULT '', CONSTRAINT [PK_CMMN_PLAN_ITEM_INST] PRIMARY KEY ([ID_]))

ALTER TABLE [ACT_CMMN_RU_PLAN_ITEM_INST] ADD CONSTRAINT [ACT_FK_PLAN_ITEM_CASE_DEF] FOREIGN KEY ([CASE_DEF_ID_]) REFERENCES [ACT_CMMN_CASEDEF] ([ID_])

CREATE NONCLUSTERED INDEX ACT_IDX_PLAN_ITEM_CASE_DEF ON [ACT_CMMN_RU_PLAN_ITEM_INST]([CASE_DEF_ID_])

ALTER TABLE [ACT_CMMN_RU_PLAN_ITEM_INST] ADD CONSTRAINT [ACT_FK_PLAN_ITEM_CASE_INST] FOREIGN KEY ([CASE_INST_ID_]) REFERENCES [ACT_CMMN_RU_CASE_INST] ([ID_])

CREATE NONCLUSTERED INDEX ACT_IDX_PLAN_ITEM_CASE_INST ON [ACT_CMMN_RU_PLAN_ITEM_INST]([CASE_INST_ID_])

CREATE TABLE [ACT_CMMN_RU_SENTRY_PART_INST] ([ID_] [varchar](255) NOT NULL, [REV_] [int] NOT NULL, [CASE_DEF_ID_] [varchar](255), [CASE_INST_ID_] [varchar](255), [PLAN_ITEM_INST_ID_] [varchar](255), [ON_PART_ID_] [varchar](255), [IF_PART_ID_] [varchar](255), [TIME_STAMP_] [datetime], CONSTRAINT [PK_CMMN_SENTRY_PART_INST] PRIMARY KEY ([ID_]))

ALTER TABLE [ACT_CMMN_RU_SENTRY_PART_INST] ADD CONSTRAINT [ACT_FK_SENTRY_CASE_DEF] FOREIGN KEY ([CASE_DEF_ID_]) REFERENCES [ACT_CMMN_CASEDEF] ([ID_])

CREATE NONCLUSTERED INDEX ACT_IDX_SENTRY_CASE_DEF ON [ACT_CMMN_RU_SENTRY_PART_INST]([CASE_DEF_ID_])

ALTER TABLE [ACT_CMMN_RU_SENTRY_PART_INST] ADD CONSTRAINT [ACT_FK_SENTRY_CASE_INST] FOREIGN KEY ([CASE_INST_ID_]) REFERENCES [ACT_CMMN_RU_CASE_INST] ([ID_])

CREATE NONCLUSTERED INDEX ACT_IDX_SENTRY_CASE_INST ON [ACT_CMMN_RU_SENTRY_PART_INST]([CASE_INST_ID_])

ALTER TABLE [ACT_CMMN_RU_SENTRY_PART_INST] ADD CONSTRAINT [ACT_FK_SENTRY_PLAN_ITEM] FOREIGN KEY ([PLAN_ITEM_INST_ID_]) REFERENCES [ACT_CMMN_RU_PLAN_ITEM_INST] ([ID_])

CREATE NONCLUSTERED INDEX ACT_IDX_SENTRY_PLAN_ITEM ON [ACT_CMMN_RU_SENTRY_PART_INST]([PLAN_ITEM_INST_ID_])

CREATE TABLE [ACT_CMMN_RU_MIL_INST] ([ID_] [varchar](255) NOT NULL, [NAME_] [varchar](255) NOT NULL, [TIME_STAMP_] [datetime] NOT NULL, [CASE_INST_ID_] [varchar](255) NOT NULL, [CASE_DEF_ID_] [varchar](255) NOT NULL, [ELEMENT_ID_] [varchar](255) NOT NULL, CONSTRAINT [PK_ACT_CMMN_RU_MIL_INST] PRIMARY KEY ([ID_]))

ALTER TABLE [ACT_CMMN_RU_MIL_INST] ADD CONSTRAINT [ACT_FK_MIL_CASE_DEF] FOREIGN KEY ([CASE_DEF_ID_]) REFERENCES [ACT_CMMN_CASEDEF] ([ID_])

CREATE NONCLUSTERED INDEX ACT_IDX_MIL_CASE_DEF ON [ACT_CMMN_RU_MIL_INST]([CASE_DEF_ID_])

ALTER TABLE [ACT_CMMN_RU_MIL_INST] ADD CONSTRAINT [ACT_FK_MIL_CASE_INST] FOREIGN KEY ([CASE_INST_ID_]) REFERENCES [ACT_CMMN_RU_CASE_INST] ([ID_])

CREATE NONCLUSTERED INDEX ACT_IDX_MIL_CASE_INST ON [ACT_CMMN_RU_MIL_INST]([CASE_INST_ID_])

CREATE TABLE [ACT_CMMN_HI_CASE_INST] ([ID_] [varchar](255) NOT NULL, [REV_] [int] NOT NULL, [BUSINESS_KEY_] [varchar](255), [NAME_] [varchar](255), [PARENT_ID_] [varchar](255), [CASE_DEF_ID_] [varchar](255), [STATE_] [varchar](255), [START_TIME_] [datetime], [END_TIME_] [datetime], [START_USER_ID_] [varchar](255), [CALLBACK_ID_] [varchar](255), [CALLBACK_TYPE_] [varchar](255), [TENANT_ID_] [varchar](255) CONSTRAINT [DF_ACT_CMMN_HI_CASE_INST_TENANT_ID_] DEFAULT '', CONSTRAINT [PK_ACT_CMMN_HI_CASE_INST] PRIMARY KEY ([ID_]))

CREATE TABLE [ACT_CMMN_HI_MIL_INST] ([ID_] [varchar](255) NOT NULL, [REV_] [int] NOT NULL, [NAME_] [varchar](255) NOT NULL, [TIME_STAMP_] [datetime] NOT NULL, [CASE_INST_ID_] [varchar](255) NOT NULL, [CASE_DEF_ID_] [varchar](255) NOT NULL, [ELEMENT_ID_] [varchar](255) NOT NULL, CONSTRAINT [PK_ACT_CMMN_HI_MIL_INST] PRIMARY KEY ([ID_]))

INSERT INTO [ACT_CMMN_DATABASECHANGELOG] ([ID], [AUTHOR], [FILENAME], [DATEEXECUTED], [ORDEREXECUTED], [MD5SUM], [DESCRIPTION], [COMMENTS], [EXECTYPE], [CONTEXTS], [LABELS], [LIQUIBASE], [DEPLOYMENT_ID]) VALUES ('1', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', GETDATE(), 1, '7:1ed01100eeb9bb6054c28320b6c5fb22', 'createTable tableName=ACT_CMMN_DEPLOYMENT; createTable tableName=ACT_CMMN_DEPLOYMENT_RESOURCE; addForeignKeyConstraint baseTableName=ACT_CMMN_DEPLOYMENT_RESOURCE, constraintName=ACT_FK_CMMN_RSRC_DPL, referencedTableName=ACT_CMMN_DEPLOYMENT; create...', '', 'EXECUTED', NULL, NULL, '3.5.3', '2509637441')

UPDATE [ACT_CMMN_DATABASECHANGELOGLOCK] SET [LOCKED] = 0, [LOCKEDBY] = NULL, [LOCKGRANTED] = NULL WHERE [ID] = 1



UPDATE [ACT_DMN_DATABASECHANGELOGLOCK] SET [LOCKED] = 1, [LOCKEDBY] = '192.168.1.5 (192.168.1.5)', [LOCKGRANTED] = '2019-03-13T21:40:44.227' WHERE [ID] = 1 AND [LOCKED] = 0

UPDATE [ACT_DMN_DATABASECHANGELOGLOCK] SET [LOCKED] = 0, [LOCKEDBY] = NULL, [LOCKGRANTED] = NULL WHERE [ID] = 1



UPDATE [ACT_FO_DATABASECHANGELOGLOCK] SET [LOCKED] = 1, [LOCKEDBY] = '192.168.1.5 (192.168.1.5)', [LOCKGRANTED] = '2019-03-13T21:40:50.989' WHERE [ID] = 1 AND [LOCKED] = 0

ALTER TABLE [ACT_FO_FORM_INSTANCE] ADD [SCOPE_ID_] [varchar](255)

ALTER TABLE [ACT_FO_FORM_INSTANCE] ADD [SCOPE_TYPE_] [varchar](255)

ALTER TABLE [ACT_FO_FORM_INSTANCE] ADD [SCOPE_DEFINITION_ID_] [varchar](255)

INSERT INTO [ACT_FO_DATABASECHANGELOG] ([ID], [AUTHOR], [FILENAME], [DATEEXECUTED], [ORDEREXECUTED], [MD5SUM], [DESCRIPTION], [COMMENTS], [EXECTYPE], [CONTEXTS], [LABELS], [LIQUIBASE], [DEPLOYMENT_ID]) VALUES ('2', 'flowable', 'org/flowable/form/db/liquibase/flowable-form-db-changelog.xml', GETDATE(), 2, '7:4850f9311e7503d7ea30a372e79b4ea2', 'addColumn tableName=ACT_FO_FORM_INSTANCE', '', 'EXECUTED', NULL, NULL, '3.5.3', '2509651315')

UPDATE [ACT_FO_DATABASECHANGELOGLOCK] SET [LOCKED] = 0, [LOCKEDBY] = NULL, [LOCKGRANTED] = NULL WHERE [ID] = 1



UPDATE [ACT_CO_DATABASECHANGELOGLOCK] SET [LOCKED] = 1, [LOCKEDBY] = '192.168.1.5 (192.168.1.5)', [LOCKGRANTED] = '2019-03-13T21:40:57.891' WHERE [ID] = 1 AND [LOCKED] = 0

UPDATE [ACT_CO_DATABASECHANGELOGLOCK] SET [LOCKED] = 0, [LOCKEDBY] = NULL, [LOCKGRANTED] = NULL WHERE [ID] = 1

