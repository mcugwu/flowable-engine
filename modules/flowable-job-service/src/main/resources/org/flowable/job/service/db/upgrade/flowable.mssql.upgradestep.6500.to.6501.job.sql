alter table ACT_RU_JOB add ELEMENT_ID_ nvarchar(255);
alter table ACT_RU_JOB add ELEMENT_NAME_ nvarchar(255);

alter table ACT_RU_TIMER_JOB add ELEMENT_ID_ nvarchar(255);
alter table ACT_RU_TIMER_JOB add ELEMENT_NAME_ nvarchar(255);

alter table ACT_RU_SUSPENDED_JOB add ELEMENT_ID_ nvarchar(255);
alter table ACT_RU_SUSPENDED_JOB add ELEMENT_NAME_ nvarchar(255);

alter table ACT_RU_DEADLETTER_JOB add ELEMENT_ID_ nvarchar(255);
alter table ACT_RU_DEADLETTER_JOB add ELEMENT_NAME_ nvarchar(255);

update ACT_GE_PROPERTY set VALUE_ = '6.5.0.1' where NAME_ = 'job.schema.version';