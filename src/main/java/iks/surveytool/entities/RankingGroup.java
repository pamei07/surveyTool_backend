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
public class RankingGroup extends AbstractEntity {
    @NotNull
    private String lowestRated;
    @NotNull
    private String highestRated;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "rankingGroupId")
    private List<Option> options;

    public RankingGroup(String lowestRated, String highestRated, List<Option> options) {
        this.lowestRated = lowestRated;
        this.highestRated = highestRated;
        this.options = options;
    }
}
