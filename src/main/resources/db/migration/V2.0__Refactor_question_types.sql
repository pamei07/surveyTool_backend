ALTER TABLE question
    ADD COLUMN questionType VARCHAR(100) NULL;

UPDATE question
SET questionType = 'TEXT'
WHERE question.hascheckbox = FALSE;

UPDATE question
SET questionType = 'MULTIPLE_CHOICE'
WHERE question.hascheckbox = TRUE;

ALTER TABLE question
    ALTER COLUMN questionType SET NOT NULL;