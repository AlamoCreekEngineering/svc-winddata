# Basic Turbine Table
 
# --- !Ups
CREATE TABLE TURBINE (
	id SERIAL PRIMARY KEY,
	watts VARCHAR(32) NOT NULL
);
 
# --- !Downs
DROP TABLE TURBINE;