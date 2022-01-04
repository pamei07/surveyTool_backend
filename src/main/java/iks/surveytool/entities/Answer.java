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

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "checkboxId")
    private Checkbox checkbox;

    public Answer(String text) {
        this.text = text;
    }

    public boolean validate() {
        if (this.user == null || this.question == null || (this.question.isHasCheckbox() && this.checkbox == null)) {
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
