package iks.surveytool.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "surveyToolUser")
public class User extends AbstractEntity {

    private String name;
    private String firstName;
    private String lastName;
    private String email;

    @OneToMany
    @JoinColumn(name = "userId")
    private List<Answer> answers;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private List<Survey> surveys;

    public User(String name) {
        this.name = name;
    }

    public boolean validate() {
        return this.name != null && this.name.length() <= 255;
    }
}
