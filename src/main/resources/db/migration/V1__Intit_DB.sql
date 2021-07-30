create table chat
(
    id            bigint not null auto_increment,
    caption       varchar(255),
    creation_date datetime(6),
    name_chat     varchar(255),
    privacy       bit    not null,
    primary key (id)
);

create table massage
(
    id            bigint not null auto_increment,
    content       varchar(255),
    creation_date datetime(6),
    username      varchar(255),
    chat_id       bigint,
    owner_id      bigint,
    primary key (id)
);

create table user
(
    id       bigint not null auto_increment,
    blocked  bit    not null,
    email    varchar(255),
    password varchar(255),
    role     integer,
    username varchar(255),
    primary key (id)
);

create table user_in_chat
(
    id      bigint not null auto_increment,
    blocked bit    not null,
    in_chat bit    not null,
    role    integer,
    chat_id bigint,
    user_id bigint,
    primary key (id)
);