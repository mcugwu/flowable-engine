update ACT_ID_PROPERTY set VALUE_ = '6.2.1.0' where NAME_ = 'schema.version';
update ACT_GE_PROPERTY set VALUE_ = '6.2.1.0' where NAME_ = 'common.schema.version';

update ACT_GE_PROPERTY set VALUE_ = '6.2.1.0' where NAME_ = 'identitylink.schema.version';

update ACT_GE_PROPERTY set VALUE_ = '6.2.1.0' where NAME_ = 'task.schema.version';

update ACT_GE_PROPERTY set VALUE_ = '6.2.1.0' where NAME_ = 'variable.schema.version';

alter table ACT_RU_JOB add SCOPE_ID_ nvarchar(191);
alter table ACT_RU_JOB add SUB_SCOPE_ID_ nvarchar(191);
alter table ACT_RU_JOB add SCOPE_TYPE_ nvarchar(191);
alter table ACT_RU_JOB add SCOPE_DEFINITION_ID_ nvarchar(191);

alter table ACT_RU_TIMER_JOB add SCOPE_ID_ nvarchar(191);
alter table ACT_RU_TIMER_JOB add SUB_SCOPE_ID_ nvarchar(191);
alter table ACT_RU_TIMER_JOB add SCOPE_TYPE_ nvarchar(191);
alter table ACT_RU_TIMER_JOB add SCOPE_DEFINITION_ID_ nvarchar(191);

alter table ACT_RU_SUSPENDED_JOB add SCOPE_ID_ nvarchar(191);
alter table ACT_RU_SUSPENDED_JOB add SUB_SCOPE_ID_ nvarchar(191);
alter table ACT_RU_SUSPENDED_JOB add SCOPE_TYPE_ nvarchar(191);
alter table ACT_RU_SUSPENDED_JOB add SCOPE_DEFINITION_ID_ nvarchar(191);

alter table ACT_RU_DEADLETTER_JOB add SCOPE_ID_ nvarchar(191);
alter table ACT_RU_DEADLETTER_JOB add SUB_SCOPE_ID_ nvarchar(191);
alter table ACT_RU_DEADLETTER_JOB add SCOPE_TYPE_ nvarchar(191);
alter table ACT_RU_DEADLETTER_JOB add SCOPE_DEFINITION_ID_ nvarchar(191);

create index ACT_IDX_JOB_SCOPE on ACT_RU_JOB(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_JOB_SUB_SCOPE on ACT_RU_JOB(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_JOB_SCOPE_DEF on ACT_RU_JOB(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_TJOB_SCOPE on ACT_RU_TIMER_JOB(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_TJOB_SUB_SCOPE on ACT_RU_TIMER_JOB(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_TJOB_SCOPE_DEF on ACT_RU_TIMER_JOB(SCOPE_DEFINITION_ID_, SCOPE_TYPE_); 

create index ACT_IDX_SJOB_SCOPE on ACT_RU_SUSPENDED_JOB(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_SJOB_SUB_SCOPE on ACT_RU_SUSPENDED_JOB(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_SJOB_SCOPE_DEF on ACT_RU_SUSPENDED_JOB(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);   

create index ACT_IDX_DJOB_SCOPE on ACT_RU_DEADLETTER_JOB(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_DJOB_SUB_SCOPE on ACT_RU_DEADLETTER_JOB(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_DJOB_SCOPE_DEF on ACT_RU_DEADLETTER_JOB(SCOPE_DEFINITION_ID_, SCOPE_TYPE_); 

alter table ACT_RU_JOB add CUSTOM_VALUES_ID_ nvarchar(64);
alter table ACT_RU_TIMER_JOB add CUSTOM_VALUES_ID_ nvarchar(64);
alter table ACT_RU_SUSPENDED_JOB add CUSTOM_VALUES_ID_ nvarchar(64);
alter table ACT_RU_DEADLETTER_JOB add CUSTOM_VALUES_ID_ nvarchar(64);
alter table ACT_RU_HISTORY_JOB add CUSTOM_VALUES_ID_ nvarchar(64);

create index ACT_IDX_JOB_CUSTOM_VALUES_ID on ACT_RU_JOB(CUSTOM_VALUES_ID_);
create index ACT_IDX_TIMER_JOB_CUSTOM_VALUES_ID on ACT_RU_TIMER_JOB(CUSTOM_VALUES_ID_);
create index ACT_IDX_SUSPENDED_JOB_CUSTOM_VALUES_ID on ACT_RU_SUSPENDED_JOB(CUSTOM_VALUES_ID_);
create index ACT_IDX_DEADLETTER_JOB_CUSTOM_VALUES_ID on ACT_RU_DEADLETTER_JOB(CUSTOM_VALUES_ID_);

alter table ACT_RU_JOB
    add constraint ACT_FK_JOB_CUSTOM_VALUES
    foreign key (CUSTOM_VALUES_ID_)
    references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_TIMER_JOB
    add constraint ACT_FK_TIMER_JOB_CUSTOM_VALUES
    foreign key (CUSTOM_VALUES_ID_)
    references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_SUSPENDED_JOB
    add constraint ACT_FK_SUSPENDED_JOB_CUSTOM_VALUES
    foreign key (CUSTOM_VALUES_ID_)
    references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_DEADLETTER_JOB
    add constraint ACT_FK_DEADLETTER_JOB_CUSTOM_VALUES
    foreign key (CUSTOM_VALUES_ID_)
    references ACT_GE_BYTEARRAY (ID_);

update ACT_GE_PROPERTY set VALUE_ = '6.2.1.0' where NAME_ = 'job.schema.version';
update ACT_GE_PROPERTY set VALUE_ = '6.2.1.0' where NAME_ = 'schema.version';


ALTER TABLE [ACT_CMMN_CASEDEF] ADD [DGRM_RESOURCE_NAME_] [varchar](4000)
GO

ALTER TABLE [ACT_CMMN_CASEDEF] ADD [HAS_START_FORM_KEY_] [bit]
GO

ALTER TABLE [ACT_CMMN_DEPLOYMENT_RESOURCE] ADD [GENERATED_] [bit]
GO

ALTER TABLE [ACT_CMMN_RU_CASE_INST] ADD [LOCK_TIME_] [datetime]
GO

ALTER TABLE [ACT_CMMN_RU_PLAN_ITEM_INST] ADD [ITEM_DEFINITION_ID_] [varchar](255)
GO

ALTER TABLE [ACT_CMMN_RU_PLAN_ITEM_INST] ADD [ITEM_DEFINITION_TYPE_] [varchar](255)
GO

INSERT INTO [ACT_CMMN_DATABASECHANGELOG] ([ID], [AUTHOR], [FILENAME], [DATEEXECUTED], [ORDEREXECUTED], [MD5SUM], [DESCRIPTION], [COMMENTS], [EXECTYPE], [CONTEXTS], [LABELS], [LIQUIBASE], [DEPLOYMENT_ID]) VALUES ('2', 'flowable', 'changelog-xml/cmmn/6210.xml', GETDATE(), 3, '7:72a1f3f4767524ec0e22288a1621ebb9', 'addColumn tableName=ACT_CMMN_CASEDEF; addColumn tableName=ACT_CMMN_DEPLOYMENT_RESOURCE; addColumn tableName=ACT_CMMN_RU_CASE_INST; addColumn tableName=ACT_CMMN_RU_PLAN_ITEM_INST', '', 'EXECUTED', NULL, NULL, '3.5.3', '2985404726')
GO

