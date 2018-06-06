alter table ACT_ID_USER add TENANT_ID_ varchar(191) default '';

alter table ACT_ID_PRIV alter column NAME_ set not null;
Call Sysproc.admin_cmd ('REORG TABLE ACT_ID_PRIV');

alter table ACT_ID_PRIV add constraint ACT_UNIQ_PRIV_NAME unique (NAME_);
Call Sysproc.admin_cmd ('REORG TABLE ACT_ID_PRIV');

update ACT_ID_PROPERTY set VALUE_ = '6.3.0.0' where NAME_ = 'schema.version';
