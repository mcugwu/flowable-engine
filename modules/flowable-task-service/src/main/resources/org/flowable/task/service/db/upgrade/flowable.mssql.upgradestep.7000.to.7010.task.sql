alter table ACT_RU_TASK add STATE_ nvarchar(255);
alter table ACT_RU_TASK add IN_PROGRESS_TIME_ datetime;
alter table ACT_RU_TASK add IN_PROGRESS_STARTED_BY_ nvarchar(255);
alter table ACT_RU_TASK add CLAIMED_BY_ nvarchar(255);
alter table ACT_RU_TASK add SUSPENDED_TIME_ datetime;
alter table ACT_RU_TASK add SUSPENDED_BY_ nvarchar(255);
alter table ACT_RU_TASK add IN_PROGRESS_DUE_DATE_ datetime;

update ACT_GE_PROPERTY set VALUE_ = '7.0.1.0' where NAME_ = 'task.schema.version';
