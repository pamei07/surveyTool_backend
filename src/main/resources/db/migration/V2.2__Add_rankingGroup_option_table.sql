CREATE TABLE rankingGroup
(
    id           SERIAL PRIMARY KEY,
    creationTime TIMESTAMP    NOT NULL,
    lastUpdated  TIMESTAMP    NOT NULL,
    version      INT          NOT NULL,
    lowestRated  VARCHAR(255) NOT NULL,
    highestRated VARCHAR(255) NOT NULL,
    questionId   BIGINT,
    FOREIGN KEY (questionId) REFERENCES question (id)
);

CREATE TABLE surveyOption
(
    id             SERIAL PRIMARY KEY,
    creationTime   TIMESTAMP    NOT NULL,
    lastUpdated    TIMESTAMP    NOT NULL,
    version        INT          NOT NULL,
    text           VARCHAR(255) NOT NULL,
    rankingGroupId BIGINT,
    FOREIGN KEY (rankingGroupId) REFERENCES rankingGroup (id)
);

ALTER TABLE answer
    ADD COLUMN rankNumber BIGINT;

ALTER TABLE answer
    ADD COLUMN optionId BIGINT;

ALTER TABLE answer
    ADD CONSTRAINT answer_optionid_fkey FOREIGN KEY (optionId) REFERENCES surveyOption (id)