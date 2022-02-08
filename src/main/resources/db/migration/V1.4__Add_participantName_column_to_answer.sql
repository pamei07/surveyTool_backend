ALTER TABLE answer
    ADD COLUMN participantName VARCHAR(255) NULL,
    ADD COLUMN participantId   VARCHAR(10)  NULL;

UPDATE answer
SET participantName = surveytooluser.name
FROM surveytooluser
WHERE userid = surveytooluser.id;

UPDATE answer
SET participantId = answer.userid::VARCHAR(10);

ALTER TABLE answer
    ALTER COLUMN participantName SET NOT NULL,
    ALTER COLUMN participantId SET NOT NULL;
