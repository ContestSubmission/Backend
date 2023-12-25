CREATE TABLE contest (
	id           UUID         NOT NULL,
	deadline     TIMESTAMP(6),
	description  VARCHAR(255),
	maxteamsize  INTEGER      NOT NULL,
	name         VARCHAR(255) NOT NULL,
	public       BOOLEAN      NOT NULL,
	organizer_id UUID         NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE person (
	id UUID NOT NULL,
	PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS contest
	ADD CONSTRAINT fk_contest_organizer_id
		FOREIGN KEY (organizer_id)
			REFERENCES person;
