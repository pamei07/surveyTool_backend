package iks.surveytool.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Survey extends AbstractEntity {

    @NotNull
    private String name;
    private String description;
    private String creatorName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private boolean openAccess;
    private boolean anonymousParticipation;
    // id for creator of survey to view results
    private String accessId;
    // id for users to participate in survey
    private String participationId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = true)
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "surveyId")
    private List<QuestionGroup> questionGroups;

    public Survey(String name,
                  String description,
                  String creatorName,
                  LocalDateTime startDate,
                  LocalDateTime endDate,
                  boolean openAccess,
                  boolean anonymousParticipation,
                  String accessId,
                  String participationId,
                  List<QuestionGroup> questionGroups) {
        this.name = name;
        this.description = description;
        this.creatorName = creatorName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openAccess = openAccess;
        this.anonymousParticipation = anonymousParticipation;
        this.accessId = accessId;
        this.participationId = participationId;
        this.questionGroups = questionGroups;
    }

    public Survey(Long id,
                  String name,
                  String description,
                  String creatorName,
                  LocalDateTime startDate,
                  LocalDateTime endDate,
                  boolean openAccess,
                  boolean anonymousParticipation,
                  String accessId,
                  String participationId,
                  List<QuestionGroup> questionGroups) {
        super(id);
        this.name = name;
        this.description = description;
        this.creatorName = creatorName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openAccess = openAccess;
        this.anonymousParticipation = anonymousParticipation;
        this.accessId = accessId;
        this.participationId = participationId;
        this.questionGroups = questionGroups;
    }

    public boolean validate() {
        return this.checkIfComplete() && checkIfDataIsValid();
    }

    private boolean checkIfComplete() {
        return this.creatorName != null
                && !this.questionGroups.isEmpty()
                && this.checkIfQuestionGroupsComplete();
    }

    private boolean checkIfQuestionGroupsComplete() {
        return this.questionGroups.stream().allMatch(QuestionGroup::checkIfComplete);
    }

    private boolean checkIfDataIsValid() {
        return this.validateData() && this.validateQuestionGroups();
    }

    private boolean validateData() {
        return this.checkNameDescriptionAndCreatorName() && this.checkTimeframe();
    }

    private boolean checkNameDescriptionAndCreatorName() {
        return this.name != null
                && this.name.length() <= 255
                && this.creatorName.length() <= 255
                && this.description.length() <= 3000;
    }

    private boolean checkTimeframe() {
        return this.startDate != null
                && this.endDate != null
                && dateTimeInFuture(this.startDate)
                && dateTimeInFuture(this.endDate)
                && startDateBeforeEndDate();
    }

    private boolean startDateBeforeEndDate() {
        return this.startDate.isBefore(this.endDate);
    }

    private boolean dateTimeInFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }

    private boolean validateQuestionGroups() {
        return this.questionGroups.stream().allMatch(QuestionGroup::validate);
    }

}
