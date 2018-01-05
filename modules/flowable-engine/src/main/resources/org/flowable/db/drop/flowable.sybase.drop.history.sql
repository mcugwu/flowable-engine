IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_PRO_INST_END') drop index ACT_HI_PROCINST.ACT_IDX_HI_PRO_INST_END;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_PRO_I_BUSKEY') drop index ACT_HI_PROCINST.ACT_IDX_HI_PRO_I_BUSKEY;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_ACT_INST_START') drop index ACT_HI_ACTINST.ACT_IDX_HI_ACT_INST_START;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_ACT_INST_END') drop index ACT_HI_ACTINST.ACT_IDX_HI_ACT_INST_END;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_DETAIL_PROC_INST') drop index ACT_HI_DETAIL.ACT_IDX_HI_DETAIL_PROC_INST;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_DETAIL_ACT_INST') drop index ACT_HI_DETAIL.ACT_IDX_HI_DETAIL_ACT_INST;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_DETAIL_TIME') drop index ACT_HI_DETAIL.ACT_IDX_HI_DETAIL_TIME;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_DETAIL_NAME') drop index ACT_HI_DETAIL.ACT_IDX_HI_DETAIL_NAME;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_DETAIL_TASK_ID') drop index ACT_HI_DETAIL.ACT_IDX_HI_DETAIL_TASK_ID;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_PROCVAR_PROC_INST') drop index ACT_HI_VARINST.ACT_IDX_HI_PROCVAR_PROC_INST;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_PROCVAR_TASK_ID') drop index ACT_HI_VARINST.ACT_IDX_HI_PROCVAR_TASK_ID;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_PROCVAR_EXE') drop index ACT_HI_VARINST.ACT_IDX_HI_PROCVAR_EXE;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_ACT_INST_PROCINST') drop index ACT_HI_ACTINST.ACT_IDX_HI_ACT_INST_PROCINST;

IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_IDENT_LNK_TASK') drop index ACT_HI_IDENTITYLINK.ACT_IDX_HI_IDENT_LNK_TASK;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_IDENT_LNK_PROCINST') drop index ACT_HI_IDENTITYLINK.ACT_IDX_HI_IDENT_LNK_PROCINST;
IF EXISTS (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_TASK_INST_PROCINST') drop index ACT_HI_TASKINST.ACT_IDX_HI_TASK_INST_PROCINST;

if exists (select convert(varchar(30),o.name) AS table_name from sysobjects o where type = 'U' and o.name = 'ACT_HI_PROCINST') drop table ACT_HI_PROCINST;
if exists (select convert(varchar(30),o.name) AS table_name from sysobjects o where type = 'U' and o.name = 'ACT_HI_ACTINST') drop table ACT_HI_ACTINST;
if exists (select convert(varchar(30),o.name) AS table_name from sysobjects o where type = 'U' and o.name = 'ACT_HI_DETAIL') drop table ACT_HI_DETAIL;
if exists (select convert(varchar(30),o.name) AS table_name from sysobjects o where type = 'U' and o.name = 'ACT_HI_COMMENT') drop table ACT_HI_COMMENT;
if exists (select convert(varchar(30),o.name) AS table_name from sysobjects o where type = 'U' and o.name = 'ACT_HI_ATTACHMENT') drop table ACT_HI_ATTACHMENT;

