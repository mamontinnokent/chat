create table massage
(
    id            bigint not null auto_increment,
    content       varchar(255),
    creation_date datetime(6),
    username      varchar(255),
    chat_id       bigint,
    owner_id      bigint,
    primary key (id)
) engine=InnoDB

create table user
(
    id       bigint not null auto_increment,
    blocked  bit    not null,
    email    varchar(255),
    password varchar(255),
    role     integer,
    username varchar(255),
    primary key (id)
) engine=InnoDB

create table user_in_chat
(
    id           bigint not null auto_increment,
    blocked_time datetime(6),
    in_chat      bit    not null,
    kicked_time  datetime(6),
    role         integer,
    chat_id      bigint,
    user_id      bigint,
    primary key (id)
) engine=InnoDB

alter table chat
    add constraint UK_4dk05bhn8f0jpuiqlnkdegbvs unique (name_chat)

alter table user
    add constraint UK_ob8kqyqqgmefl0aco34akdtpe unique (email)

alter table user
    add constraint UK_sb8bbouer5wak8vyiiy4pf2bx unique (username)

alter table massage
    add constraint FKl3be5ndc3265blrk9rkue3d39 foreign key (chat_id) references chat (id)

alter table massage
    add constraint FKicgqs8bw6m1yr9biep0yrlnks foreign key (owner_id) references user_in_chat (id)

alter table user_in_chat
    add constraint FKhgg75a2jd04r9si9p2o5dqwi foreign key (chat_id) references chat (id)

alter table user_in_chat
    add constraint FKik4awr50nn85xnjwjcs6g5urq foreign key (user_id) references user (id)