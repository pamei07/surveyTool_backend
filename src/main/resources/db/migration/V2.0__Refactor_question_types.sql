CREATE TYPE questionTypes AS ENUM (
    'TEXT',
    'MULTIPLE_CHOICE',
    'RATING_MATRIX',
    'DROPDOWN',
    'RANKING',
    'IMAGE_CHOICE',
    'FILE_UPLOAD');

ALTER TABLE question
    ADD COLUMN questionType questionTypes NULL;

UPDATE question
SET questionType = 'TEXT'
WHERE question.hascheckbox = FALSE;

UPDATE question
SET questionType = 'MULTIPLE_CHOICE'
WHERE question.hascheckbox = TRUE;