DELIMITER $$

CREATE PROCEDURE test(IN m_title VARCHAR(100), IN m_year INT, IN m_dir VARCHAR(100), IN s_name VARCHAR(100), IN genre VARCHAR(32))
BEGIN
DECLARE message VARCHAR(100);
DECLARE check_rate VARCHAR(100);
DECLARE check_price VARCHAR(100);
DECLARE check_exists VARCHAR(100);

DECLARE new_m_id VARCHAR(10);
DECLARE new_m_num INT;

DECLARE g_id INT;

DECLARE s_id VARCHAR(10);
DECLARE new_star_id VARCHAR(10);
DECLARE max_star_id INT;

SELECT m.id into check_exists FROM movies m WHERE m.title=m_title and m.year=m_year and m.director=m_dir;

-- Check if movie exists
IF (check_exists IS NOT NULL) THEN
SET message=CONCAT("old ",check_exists);
SELECT message as msg;

-- Create movie if:
ELSE
-- create new movie id

SELECT MAX(CAST(SUBSTRING(id,3) AS UNSIGNED INT)) INTO new_m_num FROM movies;
SET new_m_id = CONCAT('tt0', new_m_num+1);

SET message=CONCAT("new ", new_m_id);

INSERT INTO movies VALUES (new_m_id, m_title, m_year, m_dir);

-- genres
SELECT id INTO g_id from genres WHERE name=genre;
-- Actual NULL value
IF (genre!='' AND g_id IS NULL) THEN
-- make new genre -- Empty string
ALTER TABLE genres AUTO_INCREMENT=1;
INSERT INTO genres (name) values (genre);
-- get new genre id
SELECT id INTO g_id from genres WHERE name=genre;
INSERT into genres_in_movies VALUES (g_id, new_m_id);

ELSEIF (g_id IS NOT NULL) THEN
INSERT into genres_in_movies VALUES (g_id, new_m_id);
-- insert into genres in movies
END IF;

SET message=CONCAT(message, " ", g_id);


-- stars
SELECT id INTO s_id from stars WHERE name=s_name;
IF (s_id IS NULL) THEN
-- make new star id
SELECT MAX(CAST(SUBSTRING(id,3) AS UNSIGNED INT)) INTO max_star_id FROM stars;
SET new_star_id = CONCAT('nm', max_star_id+1);
-- make new star
INSERT INTO stars VALUES (new_star_id, s_name, NULL);
INSERT INTO stars_in_movies VALUES (new_star_id, new_m_id);
SET message=CONCAT(message, " ", new_star_id);
ELSE
-- insert into stars in movies
INSERT INTO stars_in_movies VALUES (s_id, new_m_id);
SET message=CONCAT(message, " ", s_id);
END IF;

SELECT movieId INTO check_rate FROM ratings WHERE movieId=new_m_id;
SELECT movieId INTO check_price FROM prices WHERE movieId=new_m_id;

IF (check_rate IS NULL) THEN
INSERT INTO ratings VALUES (new_m_id, 0, 0);
END IF;
IF (check_price IS NULL) THEN
INSERT INTO prices VALUES (new_m_id, 10);
END IF;


SELECT message as msg;

END IF ;

END
$$

DELIMITER ;
