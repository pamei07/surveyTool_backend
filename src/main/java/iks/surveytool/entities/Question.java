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

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
    private RankingGroup rankingGroup;

    public Question(String text, boolean required, QuestionType questionType) {
        this.text = text;
        this.required = required;
        this.questionType = questionType;
    }

    boolean checkIfComplete() {
        if (this.questionType == QuestionType.MULTIPLE_CHOICE) {
            return this.checkboxGroup != null && checkboxGroup.checkIfComplete();
        } else if (this.questionType == QuestionType.RANKING) {
            return this.rankingGroup != null && rankingGroup.checkIfComplete();
        }
        return true;
    }

    boolean validate() {
        return validateData() &&
                (this.questionType != QuestionType.MULTIPLE_CHOICE || checkboxGroup.validate(this.required)) &&
                (this.questionType != QuestionType.RANKING || rankingGroup.validate());
    }

    private boolean validateData() {
        return this.text != null && this.text.length() <= 500;
    }
}
