package iks.surveytool.entities;

import javax.persistence.Embeddable;

@Embeddable
public enum QuestionType {
    TEXT,
    MULTIPLE_CHOICE,
    RATING_MATRIX,
    DROPDOWN,
    RANKING,
    IMAGE_CHOICE,
    FILE_UPLOAD
}
