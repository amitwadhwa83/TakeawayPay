-- #1=customer,0=restaurant
--CREATE TABLE account (
--id integer NOT NULL AUTO_INCREMENT,
--balance decimal(5,2) NOT NULL,
--last_update timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
--is_customer BIT NOT NULL DEFAULT 1,
--PRIMARY KEY (id)
--);
--INSERT INTO account(balance,last_update,is_customer) VALUES (40,NOW(),1);
--INSERT INTO account(balance,last_update,is_customer) VALUES (10,NOW(),0);

CREATE TABLE transfer (
id integer NOT NULL AUTO_INCREMENT,
source_account integer NOT NULL,
dest_account integer NOT NULL,
amount decimal(5,2) NOT NULL,
last_update timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (id)--,
--FOREIGN KEY (source_account) REFERENCES account(id),
--FOREIGN KEY (dest_account) REFERENCES account(id)
);