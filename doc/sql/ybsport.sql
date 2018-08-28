create table if not exists ybsport_type
(
	id serial not null
		constraint ybsport_type_pk
			primary key,
	need_steps integer not null,
	get_money integer not null,
	is_enable boolean default true not null
)
;

alter table ybsport_type owner to yiban
;

create table if not exists ybsport_buy
(
	id serial not null
		constraint ybsport_buy_pk
			primary key,
	yb_user json,
	sport_steps integer,
	type integer
		constraint ybsport_buy_ybsport_type_id_fk
			references ybsport_type,
	date date default now(),
	is_enable boolean default false
)
;

comment on table ybsport_buy is '易运动兑换记录'
;

alter table ybsport_buy owner to yiban
;

create unique index if not exists ybsport_buy_id_uindex
	on ybsport_buy (id)
;

create unique index if not exists ybsport_type_id_uindex
	on ybsport_type (id)
;

create table if not exists ybsport_time
(
	id serial not null
		constraint ybsport_time_pkey
			primary key,
	start_time timestamp default now(),
	end_time timestamp default now(),
	remark varchar(255),
	is_enable boolean default false
)
;

comment on table ybsport_time is '易运动开启时间'
;

alter table ybsport_time owner to yiban
;

create unique index if not exists ybsport_time_id_uindex
	on ybsport_time (id)
;

