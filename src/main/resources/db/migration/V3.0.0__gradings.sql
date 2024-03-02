ALTER TABLE contest
	ADD COLUMN publicGrading BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE submission
	ADD COLUMN fileName VARCHAR(255);
UPDATE submission
SET fileName = (SELECT SPLIT_PART(REVERSE(SPLIT_PART(REVERSE(url), '/', 1)), '_', 2));
ALTER TABLE submission
	ALTER COLUMN fileName SET NOT NULL;

ALTER TABLE submission
ADD PRIMARY KEY (id);

CREATE TABLE grade (
	submission_id BIGINT  NOT NULL REFERENCES submission (id),
	person_id     UUID    NOT NULL REFERENCES person (id),
	score         INTEGER NOT NULL,
	comment       TEXT,
	PRIMARY KEY (submission_id, person_id)
);
