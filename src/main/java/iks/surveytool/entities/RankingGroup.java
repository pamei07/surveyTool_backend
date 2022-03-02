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

    public boolean checkIfComplete() {
        return options.size() >= 2;
    }

    public boolean validate() {
        return this.validateData() && this.validateOptions();
    }

    private boolean validateData() {
        return checkLabelForLowestRated() && checkLabelForHighestRated();
    }

    private boolean checkLabelForHighestRated() {
        return this.highestRated != null && this.highestRated.length() <= 255;
    }

    private boolean checkLabelForLowestRated() {
        return this.lowestRated != null && this.lowestRated.length() <= 255;
    }

    private boolean validateOptions() {
        return this.options.stream().allMatch(Option::validate);
    }
}
