package iks.surveytool.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Question extends AbstractEntity {

    @NotNull
    private String text;
    @NotNull
    private boolean required;

    private boolean hasCheckbox;

    @NotNull
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @ManyToOne
    @JoinColumn(name = "questionGroupId", nullable = false)
    private QuestionGroup questionGroup;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "questionId")
    private List<Answer> answers;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
    private CheckboxGroup checkboxGroup;

    public Question(String text, boolean required, boolean hasCheckbox, QuestionType questionType) {
        this.text = text;
        this.required = required;
        this.hasCheckbox = hasCheckbox;
        this.questionType = questionType;
    }

    public Question(String text, boolean required, QuestionType questionType) {
        this.text = text;
        this.required = required;
        this.questionType = questionType;
    }

    boolean checkIfComplete() {
        if (this.questionType == QuestionType.MULTIPLE_CHOICE) {
            return this.checkboxGroup != null && checkboxGroup.checkIfComplete();
        }
        return true;
    }

    boolean validate() {
        return validateData() &&
                (this.questionType != QuestionType.MULTIPLE_CHOICE || checkboxGroup.validate(this.required));
    }

    private boolean validateData() {
        return this.text != null && this.text.length() <= 500;
    }
}
