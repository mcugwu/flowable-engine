alter table ACT_ID_MEMBERSHIP 
    drop CONSTRAINT ACT_FK_MEMB_GROUP;
    
alter table ACT_ID_MEMBERSHIP 
    drop CONSTRAINT ACT_FK_MEMB_USER;

drop index ACT_IDX_MEMB_GROUP;
drop index ACT_IDX_MEMB_USER;
drop index ACT_IDX_CAP_NAME;

drop table ACT_ID_PROPERTY;
drop table ACT_ID_BYTEARRAY;
drop table ACT_ID_INFO;
drop table ACT_ID_MEMBERSHIP;
drop table ACT_ID_GROUP;
drop table ACT_ID_USER;
drop table ACT_ID_TOKEN;
drop table ACT_ID_CAPABILITY;
