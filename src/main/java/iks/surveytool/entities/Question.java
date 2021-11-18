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
    private boolean hasCheckbox;

    @ManyToOne
    @JoinColumn(name = "question_group_id")
    private QuestionGroup questionGroup;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    private List<Answer> answers;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
    private CheckboxGroup checkboxGroup;

    public Question(String text, boolean required, boolean hasCheckbox) {
        this.text = text;
        this.required = required;
        this.hasCheckbox = hasCheckbox;
    }
}
