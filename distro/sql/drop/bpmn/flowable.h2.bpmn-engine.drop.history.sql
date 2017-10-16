drop table if exists ACT_HI_PROCINST cascade constraints;
drop table if exists ACT_HI_ACTINST cascade constraints;
drop table if exists ACT_HI_DETAIL cascade constraints;
drop table if exists ACT_HI_COMMENT cascade constraints;
drop table if exists ACT_HI_ATTACHMENT cascade constraints;

drop index if exists ACT_IDX_HI_PRO_INST_END;
drop index if exists ACT_IDX_HI_PRO_I_BUSKEY;
drop index if exists ACT_IDX_HI_ACT_INST_START;
drop index if exists ACT_IDX_HI_ACT_INST_END;
drop index if exists ACT_IDX_HI_DETAIL_PROC_INST;
drop index if exists ACT_IDX_HI_DETAIL_ACT_INST;
drop index if exists ACT_IDX_HI_DETAIL_TIME;
drop index if exists ACT_IDX_HI_DETAIL_NAME;
drop index if exists ACT_IDX_HI_DETAIL_TASK_ID;
drop index if exists ACT_IDX_HI_PROCVAR_PROC_INST;
drop index if exists ACT_IDX_HI_PROCVAR_TASK_ID;
drop index if exists ACT_IDX_HI_PROCVAR_EXE;
drop index if exists ACT_IDX_HI_ACT_INST_PROCINST;
drop index if exists ACT_IDX_HI_IDENT_LNK_TASK;
drop index if exists ACT_IDX_HI_IDENT_LNK_PROCINST;
drop index if exists ACT_IDX_HI_TASK_INST_PROCINST;

drop table if exists ACT_HI_VARINST cascade constraints;

drop index if exists ACT_IDX_HI_PROCVAR_NAME_TYPE;
drop index if exists ACT_IDX_HI_VAR_SCOPE_ID_TYPE;
drop index if exists ACT_IDX_HI_VAR_SUB_ID_TYPE;

drop table if exists ACT_RU_TASK cascade constraints;

drop index if exists ACT_IDX_TASK_CREATE;
drop index if exists ACT_IDX_HI_TASK_SCOPE;
drop index if exists ACT_IDX_HI_TASK_SUB_SCOPE;
drop index if exists ACT_IDX_HI_TASK_SCOPE_DEF;
drop table if exists ACT_HI_IDENTITYLINK cascade constraints;

drop index if exists ACT_IDX_HI_IDENT_LNK_USER;
