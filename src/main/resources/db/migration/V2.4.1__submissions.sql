CREATE TABLE submission (
	id            BIGINT       NOT NULL,
	url           VARCHAR(255) NOT NULL UNIQUE,
	uploadedby_id UUID         NOT NULL REFERENCES person (id),
	team_id       UUID         NOT NULL REFERENCES team (id),
	handedinat    TIMESTAMP    NOT NULL
);

CREATE SEQUENCE submission_seq
	START WITH 1
	INCREMENT BY 50;
