ALTER TABLE survey
    ADD COLUMN creatorName VARCHAR(255) NULL;

UPDATE survey
SET creatorName = surveytooluser.name
FROM surveytooluser
WHERE userid = surveytooluser.id;

ALTER TABLE survey
    ALTER COLUMN creatorName SET NOT NULL;
