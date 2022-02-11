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
public class Checkbox extends AbstractEntity {

    @NotNull
    private String text;
    @NotNull
    private boolean hasTextField;
    private String placeholder;

    @ManyToOne
    @JoinColumn(name = "checkboxGroupId", nullable = false)
    private CheckboxGroup checkboxGroup;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "checkboxId")
    private List<Answer> answers;

    public Checkbox(String text, boolean hasTextField, String placeholder) {
        this.text = text;
        this.hasTextField = hasTextField;
        this.placeholder = placeholder;
    }

    boolean validate() {
        return this.text != null && this.text.length() <= 255;
    }
}
