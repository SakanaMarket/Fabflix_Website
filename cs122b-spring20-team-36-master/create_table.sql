CREATE DATABASE IF NOT EXISTS moviedb;

USE moviedb;

CREATE TABLE IF NOT EXISTS movies(
	id varchar(10) PRIMARY KEY,
	title varchar(100) NOT NULL DEFAULT '',
	year int NOT NULL,
	director varchar(100) NOT NULL DEFAULT ''
);

CREATE TABLE IF NOT EXISTS stars(
	id varchar(10) PRIMARY KEY,
	name varchar(100) NOT NULL DEFAULT '',
	birthYear int
);

CREATE TABLE IF NOT EXISTS stars_in_movies(
	starId varchar(10),
	movieId varchar(10),
	PRIMARY KEY (starId, movieId),
	FOREIGN KEY (starId) REFERENCES stars(id) ON DELETE CASCADE,
	FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres(
	id int AUTO_INCREMENT PRIMARY KEY,
	name varchar(32) NOT NULL DEFAULT ''
);

CREATE TABLE IF NOT EXISTS genres_in_movies(
	genreId int,
	movieId varchar(10),
	PRIMARY KEY (genreId, movieId),
	FOREIGN KEY (genreId) REFERENCES genres(id) ON DELETE CASCADE,
	FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS creditcards(
	id varchar(20) PRIMARY KEY,
	firstName varchar(50) NOT NULL DEFAULT '',
	lastName varchar(50) NOT NULL DEFAULT '',
	expiration date NOT NULL
);

CREATE TABLE IF NOT EXISTS customers(
	id int AUTO_INCREMENT PRIMARY KEY,
	firstName varchar(50) NOT NULL DEFAULT '',
	lastName varchar(50) NOT NULL DEFAULT '',
	ccId varchar(20),
	address varchar(200) NOT NULL DEFAULT '',
	email varchar(50) NOT NULL DEFAULT '',
	password varchar(20) NOT NULL DEFAULT '',
	FOREIGN KEY (ccId) REFERENCES creditcards(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS sales(
	id int AUTO_INCREMENT PRIMARY KEY,
	customerId int,
	movieId varchar(10),
	saleDate date NOT NULL,
	FOREIGN KEY (customerId) REFERENCES customers(id) ON DELETE NO ACTION,
	FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS ratings(
	movieId varchar(10),
	rating float NOT NULL,
	numVotes int NOT NULL,
	PRIMARY KEY (movieId),
	FOREIGN KEY (movieId) references movies(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS employees(
	email varchar(50) PRIMARY KEY,
	password varchar(20) NOT NULL,
	fullname varchar(100)
);
