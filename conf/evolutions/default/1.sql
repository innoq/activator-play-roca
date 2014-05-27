# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table issue (
  id                        bigint not null,
  project_name              varchar(255),
  priority                  varchar(255),
  issue_type                varchar(255),
  summary                   varchar(255),
  exception_stack_trace     clob,
  description               varchar(1000),
  reporter                  varchar(255),
  component_name            varchar(255),
  component_version         varchar(255),
  processing_state          varchar(7),
  open_date                 bigint,
  close_date                bigint,
  close_action              varchar(8),
  USER_NAME                 bigint,
  comment                   clob,
  constraint ck_issue_processing_state check (processing_state in ('OPEN','CLAIMED','CLOSED')),
  constraint ck_issue_close_action check (close_action in ('RETRY','ABORT','COMPLETE')),
  constraint pk_issue primary key (id))
;

create table service_arguments (
  id                        bigint not null,
  issue_id                  bigint not null,
  argument_id               varchar(255),
  arguments                 varchar(1000),
  constraint pk_service_arguments primary key (id))
;

create table user (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_user primary key (id))
;

create sequence issue_seq;

create sequence service_arguments_seq;

create sequence user_seq;

alter table issue add constraint fk_issue_assignedUser_1 foreign key (USER_NAME) references user (id) on delete restrict on update restrict;
create index ix_issue_assignedUser_1 on issue (USER_NAME);
alter table service_arguments add constraint fk_service_arguments_issue_2 foreign key (issue_id) references issue (id) on delete restrict on update restrict;
create index ix_service_arguments_issue_2 on service_arguments (issue_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists issue;

drop table if exists service_arguments;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists issue_seq;

drop sequence if exists service_arguments_seq;

drop sequence if exists user_seq;

