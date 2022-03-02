package iks.surveytool.utils.builder;

import iks.surveytool.entities.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SurveyBuilder {

    String description = "Default Beschreibung";
    String creatorName = "Test Creator";
    LocalDateTime startDate = LocalDateTime.of(2050, 1, 1, 12, 0);
    LocalDateTime endDate = startDate.plusWeeks(1L);
    boolean openAccess = false;
    boolean anonymousParticipation = false;
    String accessId = "Test AccessId";
    String participationId = "Test ParticipationId";

    public Survey createDefaultSurvey() {
        Survey newSurvey = new Survey();
        User user = new User("Test Person");
        newSurvey.setId(1L);
        newSurvey.setName("Test Survey");
        newSurvey.setUser(user);
        setDefaults(newSurvey);
        return newSurvey;
    }

    public Survey createSurveyWithDefaultDate(Long id, String name) {
        Survey newSurvey = new Survey();
        newSurvey.setId(id);
        newSurvey.setName(name);
        setDefaults(newSurvey);
        return newSurvey;
    }

    public Survey createSurveyWithUserAndDefaultDate(Long id, String name, User user) {
        Survey newSurvey = new Survey();
        newSurvey.setId(id);
        newSurvey.setName(name);
        newSurvey.setUser(user);
        setDefaults(newSurvey);
        return newSurvey;
    }

    private void setDefaults(Survey newSurvey) {
        newSurvey.setDescription(description);
        newSurvey.setCreatorName(creatorName);
        newSurvey.setStartDate(startDate);
        newSurvey.setEndDate(endDate);
        newSurvey.setOpenAccess(openAccess);
        newSurvey.setAccessId(accessId);
        newSurvey.setParticipationId(participationId);
        newSurvey.setAnonymousParticipation(anonymousParticipation);
        newSurvey.setQuestionGroups(new ArrayList<>());
    }

    public Survey createCompleteAndValidSurvey(User user) {
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, true, 1, 3);
        checkboxGroup.setCheckboxes(new CheckboxBuilder().createListOfFourValidCheckboxes());

        RankingGroup rankingGroup = new RankingGroupBuilder()
                .createRankingGroup(1L);
        rankingGroup.setOptions(new OptionBuilder().createListOfFourValidOptions());

        Question question1 = new QuestionBuilder().createQuestion(1L, "Frage 1", true, QuestionType.TEXT);
        Question question2 = new QuestionBuilder().createQuestion(2L, "Frage 2", false, QuestionType.MULTIPLE_CHOICE);
        question2.setCheckboxGroup(checkboxGroup);
        checkboxGroup.setQuestion(question2);
        Question question3 = new QuestionBuilder().createQuestion(3L, "Frage 3", false, QuestionType.RANKING);
        question3.setRankingGroup(rankingGroup);
        rankingGroup.setQuestion(question3);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Questions");
        questionGroupWithQuestion.setQuestions(List.of(question1, question2, question3));

        Survey survey = this.createSurveyWithUserAndDefaultDate(1L, "Complete Survey init", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        return survey;
    }
}
