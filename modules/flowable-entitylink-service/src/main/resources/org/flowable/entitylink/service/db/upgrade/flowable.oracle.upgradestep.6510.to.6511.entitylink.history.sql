alter table ACT_HI_ENTITYLINK add ROOT_SCOPE_ID_ NVARCHAR2(255);
alter table ACT_HI_ENTITYLINK add ROOT_SCOPE_TYPE_ NVARCHAR2(255);
create index ACT_IDX_HI_ENT_LNK_ROOT_SCOPE on ACT_HI_ENTITYLINK(ROOT_SCOPE_ID_, ROOT_SCOPE_TYPE_, LINK_TYPE_);
