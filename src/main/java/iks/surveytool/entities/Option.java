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
@Table(name = "surveyOption")
public class Option extends AbstractEntity {
    @NotNull
    private String text;

    @ManyToOne
    @JoinColumn(name = "rankingGroupId", nullable = false)
    private RankingGroup rankingGroup;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "optionId")
    private List<Answer> answers;

    public Option(String text) {
        this.text = text;
    }

    public boolean validate() {
        return this.text != null && this.text.length() <= 255;
    }
}
