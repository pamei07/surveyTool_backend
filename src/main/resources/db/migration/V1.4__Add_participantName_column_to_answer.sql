ALTER TABLE answer
    ADD COLUMN participantName VARCHAR(255) NULL;

UPDATE answer
SET participantName = surveytooluser.name
FROM surveytooluser
WHERE userid = surveytooluser.id;

ALTER TABLE answer
    ALTER COLUMN participantName SET NOT NULL;
