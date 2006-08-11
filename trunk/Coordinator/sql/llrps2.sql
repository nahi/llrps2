drop table Agent;
drop sequence hibernate_sequence;
create table Agent (
    id int8 not null,
    ipAddress varchar(255),
    name varchar(255),
    primary key (id)
);
create sequence hibernate_sequence;
