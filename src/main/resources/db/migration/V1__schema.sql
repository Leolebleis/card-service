DROP TABLE IF EXISTS card;

CREATE TABLE card
(
    id           SERIAL PRIMARY KEY NOT NULL,
    number       VARCHAR(19)        NOT NULL,
    expiry_month VARCHAR(2)         NOT NULL,
    expiry_year  VARCHAR(4)         NOT NULL,
    cvc          VARCHAR(3)         NOT NULL,
    token        VARCHAR(36)        NOT NULL
);

-- Use this to make each card unique in the DB
-- ALTER TABLE card
--     ADD CONSTRAINT unique_values UNIQUE (number, expiry_month, expiry_year, cvc);