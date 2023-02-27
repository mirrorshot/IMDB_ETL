drop schema if exists imdb;
create schema if not exists imdb;
use imdb;

create
    or replace table title
(
    title           varchar(20)          not null primary key,
    type            varchar(100)         not null,
    primary_t       varchar(2000)        not null,
    original_t      varchar(2000)        not null,
    is_adult        tinyint(1) default 0 null,
    start_year      integer              null,
    end_year        integer              null,
    runtime_minutes integer              null,
    genres          text                 null
);

create
    or replace index title_original_t_index
    on title (original_t);

create
    or replace index title_primary_t_index
    on title (primary_t);

create
    or replace index title_startYear_index
    on title (start_year);

create
    or replace index title_endYear_index
    on title (end_year);

create or replace table episode
(
    episode        varchar(20) not null,
    title          varchar(20) not null,
    season_number  integer     null,
    episode_number integer     null
);

create or replace index episode_episode_index
    on episode (title, episode);

create or replace table aka
(
    title             varchar(20)          not null,
    ordering          integer              not null,
    localized         varchar(2000)        not null,
    region            varchar(50)          null,
    language          varchar(200)         null,
    types             text                 null,
    attributes        text                 null,
    is_original_title tinyint(1) default 0 null
);

create or replace index aka_title_index
    on aka (title);
create or replace index aka_localized_index
    on aka (localized);
create or replace index aka_region_index
    on aka (region);
create or replace index aka_language_index
    on aka (language);
create or replace unique index aka_ordering_index
    on aka (title, ordering);

create or replace table rating
(
    title          varchar(20) not null,
    average_rating double      null,
    votes          int         null
);

create or replace index rating_title_index
    on rating (title);

create or replace table person
(
    person     varchar(20)   not null primary key,
    name       varchar(1000) not null,
    birth_year integer       null,
    death_year integer       null,
    profession text          null,
    known_for  text          null
);

create or replace index person_name_index
    on person (name);

create or replace index person_birth_index
    on person (birth_year);

create or replace index person_death_index
    on person (death_year);

create or replace table principal
(
    title      varchar(20) not null,
    ordering   integer     null,
    person     varchar(20) null,
    category   text        null,
    job        text        null,
    characters text        null
);

create or replace index principal_title_index
    on principal (title);

create or replace index principal_person_index
    on principal (person);

create or replace table crew
(
    title     varchar(20) not null,
    directors text        null,
    writers   text        null
);

create or replace index crew_title_index
    on crew (title);
