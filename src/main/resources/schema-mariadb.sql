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
