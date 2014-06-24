# --- !Ups

create table issue (
  id                        varchar(255) not null,
  project_name              varchar(255),
  priority                  varchar(255),
  issue_type                varchar(255),
  summary                   varchar(255),
  exception_stack_trace     clob,
  description               varchar(1000),
  reporter                  varchar(255),
  component_name            varchar(255),
  component_version         varchar(255),
  processing_state          varchar(255),
  open_date                 bigint,
  close_date                bigint,
  close_action              varchar(255),
  assignee                  varchar(255),
  comment                   clob,
  constraint pk_issue primary key (id))
;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists issue;

SET REFERENTIAL_INTEGRITY TRUE;
