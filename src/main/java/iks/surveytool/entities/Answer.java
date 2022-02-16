package iks.surveytool.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Answer extends AbstractEntity {

    private String text;
    private String participantName;
    private String participantId;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "checkboxId")
    private Checkbox checkbox;

    public Answer(String text, String participantName) {
        this.text = text;
        this.participantName = participantName;
    }

    public boolean validate() {
        if (this.participantName == null || this.question == null || (this.question.isHasCheckbox() && this.checkbox == null)) {
            return false;
        }
        if (!this.question.isHasCheckbox() || this.checkbox.isHasTextField()) {
            return this.checkIfAnswerTextValid();
        }
        return true;
    }

    private boolean checkIfAnswerTextValid() {
        return this.text != null && this.text.length() <= 1500;
    }
}
