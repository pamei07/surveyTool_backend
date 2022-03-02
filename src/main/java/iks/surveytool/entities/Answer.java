package iks.surveytool.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Answer extends AbstractEntity {

    private String text;
    private String participantName;
    private String participantId;
    @Column(name = "ranknumber")
    private Integer rank;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "checkboxId")
    private Checkbox checkbox;

    @ManyToOne
    @JoinColumn(name = "optionId")
    private Option option;

    public Answer(String text, String participantName) {
        this.text = text;
        this.participantName = participantName;
    }

    public boolean validate() {
        if (this.participantName == null || this.question == null ||
                (this.question.getQuestionType() == QuestionType.MULTIPLE_CHOICE && this.checkbox == null) ||
                (this.question.getQuestionType() == QuestionType.RANKING &&
                        (this.option == null || rank == null))) {
            return false;
        }
        if (this.question.getQuestionType() == QuestionType.TEXT ||
                (this.question.getQuestionType() == QuestionType.MULTIPLE_CHOICE && this.checkbox.isHasTextField())) {
            return this.checkIfAnswerTextValid();
        }
        return true;
    }

    private boolean checkIfAnswerTextValid() {
        return this.text != null && this.text.length() <= 1500;
    }
}
