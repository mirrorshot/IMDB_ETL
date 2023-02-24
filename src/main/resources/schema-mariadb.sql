-- drop schema if exists imdb;
-- create schema if not exists imdb;

create
    or replace table imdb.title
(
    title           varchar(20)          not null primary key,
    type            varchar(100)         not null,
    primary_t       varchar(250)         not null,
    original_t      varchar(250)         not null,
    is_adult        tinyint(1) default 0 null,
    start_year      integer              null,
    end_year        integer              null,
    runtime_minutes integer              null,
    genres          text                 null
);

create
    or replace index title_original_t_index
    on imdb.title (original_t);

create
    or replace index title_primary_t_index
    on imdb.title (primary_t);

create
    or replace index title_startYear_index
    on imdb.title (start_year);

create
    or replace index title_endYear_index
    on imdb.title (end_year);

create or replace table imdb.episode
(
    episode        varchar(20) not null,
    title          varchar(20) not null,
    season_number  integer     null,
    episode_number integer     null
);

create or replace index episode_episode_index
    on imdb.episode (title, episode);

create or replace table imdb.aka
(
    title             varchar(20)          not null,
    ordering          integer              not null,
    localized         varchar(250)         not null,
    region            varchar(5)           null,
    language          varchar(20)          null,
    types             text                 null,
    attributes        text                 null,
    is_original_title tinyint(1) default 0 null
);

create or replace index aka_title_index
    on imdb.aka (title);
create or replace index aka_localized_index
    on imdb.aka (localized);
create or replace index aka_region_index
    on imdb.aka (region);
create or replace index aka_language_index
    on imdb.aka (language);
create or replace unique index aka_ordering_index
    on imdb.aka (title, ordering);
