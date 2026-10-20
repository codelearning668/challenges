drop table if exists participant_aud;
drop table if exists revinfo;
drop sequence if exists revinfo_seq;

create sequence revinfo_seq start with 1 increment by 50;
create table revinfo (rev integer not null, revtstmp bigint, primary key (rev));

create table participant_aud (
	rev integer not null,
	revtype smallint,
	id integer not null,
	best_lap_time interval,
	created_at timestamp(6),
	modified_at timestamp(6),
	primary key (rev, id)
);

alter table participant_aud add constraint FKc6vyjg23bk5dup9rhqn10p678 foreign key (rev) references revinfo;

