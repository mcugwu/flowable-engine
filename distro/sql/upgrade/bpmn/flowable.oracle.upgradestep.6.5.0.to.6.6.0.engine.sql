alter table ACT_RU_EXECUTION add LOCK_OWNER_ NVARCHAR2(255);
alter table ACT_RU_EXECUTION add EXTERNAL_WORKER_JOB_COUNT_ INTEGER;

alter table ACT_RU_ACTINST add TRANSACTION_ORDER_ INTEGER;

alter table ACT_HI_ACTINST add TRANSACTION_ORDER_ INTEGER;

update ACT_GE_PROPERTY set VALUE_ = '6.6.0.0' where NAME_ = 'schema.version';
