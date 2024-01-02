CREATE TABLE team (
	id         UUID         NOT NULL,
	name       VARCHAR(255) NOT NULL,
	owner_id   UUID         NOT NULL,
	contest_id UUID         NOT NULL,
	PRIMARY KEY (id)
);

ALTER TABLE team
	ADD CONSTRAINT fk_team_contest_id
		FOREIGN KEY (contest_id)
			REFERENCES contest;

ALTER TABLE team
	ADD CONSTRAINT fk_team_owner_id
		FOREIGN KEY (owner_id)
			REFERENCES person;

CREATE TABLE team_member (
	team_id   UUID NOT NULL REFERENCES team (id),
	person_id UUID NOT NULL REFERENCES person (id),
	PRIMARY KEY (team_id, person_id)
);
