#!/usr/bin/env bash

if [ -d "imdb" ]; then
  cd imdb
else
  mkdir "imdb"
  cd imdb
fi

if [ -d "data" ]; then
  cd data
else
  mkdir "data"
  cd data
fi

if [ -d "name.basics" ]; then
  cd name.basics
else
  mkdir "name.basics"
  cd name.basics
fi

wget https://datasets.imdbws.com/name.basics.tsv.gz && gunzip name.basics.tsv.gz && ln name.basics.tsv data.tsv
cd ..

if [ -d "title.akas" ]; then
  cd title.akas
else
  mkdir "title.akas"
  cd title.akas
fi

wget https://datasets.imdbws.com/title.akas.tsv.gz && gunzip title.akas.tsv.gz && ln title.akas.tsv data.tsv
cd ..

if [ -d "title.basics" ]; then
  cd title.basics
else
  mkdir "title.basics"
  cd title.basics
fi

wget https://datasets.imdbws.com/title.basics.tsv.gz && gunzip title.basics.tsv.gz && ln title.basics.tsv data.tsv
cd ..

if [ -d "title.crew" ]; then
  cd title.crew
else
  mkdir "title.crew"
  cd title.crew
fi

wget https://datasets.imdbws.com/title.crew.tsv.gz && gunzip title.crew.tsv.gz && ln title.crew.tsv data.tsv
cd ..

if [ -d "title.episode" ]; then
  cd title.episode
else
  mkdir "title.episode"
  cd title.episode
fi

wget https://datasets.imdbws.com/title.episode.tsv.gz && gunzip title.episode.tsv.gz && ln title.episode.tsv data.tsv
cd ..

if [ -d "title.principals" ]; then
  cd title.principals
else
  mkdir "title.principals"
  cd title.principals
fi

wget https://datasets.imdbws.com/title.principals.tsv.gz && gunzip title.principals.tsv.gz && ln title.principals.tsv data.tsv
cd ..

if [ -d "title.ratings" ]; then
  cd title.ratings
else
  mkdir "title.ratings"
  cd title.ratings
fi

wget https://datasets.imdbws.com/title.ratings.tsv.gz && gunzip title.ratings.tsv.gz && ln title.ratings.tsv data.tsv
