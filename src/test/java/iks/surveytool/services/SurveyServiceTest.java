package iks.surveytool.services;

import iks.surveytool.builder.*;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.SurveyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testing SurveyService")
class SurveyServiceTest {

    @Mock
    private SurveyRepository surveyRepository;

    @InjectMocks
    private SurveyService surveyService;

    @Test
    @DisplayName("Adding Survey")
    void addSurvey() throws MalformedURLException {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Umfrage");
        when(surveyRepository.save(survey)).thenReturn(survey);

        surveyService.addSurvey(survey);

        verify(surveyRepository, times(2)).save(survey);
    }

    @Test
    @DisplayName("Adding QuestionGroup to Survey")
    void addQuestionGroupToSurvey() {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Umfrage");

        QuestionGroup questionGroup = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "Add test group");

        surveyService.addQuestionGroupToSurvey(survey, questionGroup);

        assertEquals(questionGroup, survey.getQuestionGroups().get(0));
    }

    @Test
    @DisplayName("Adding multiple QuestionGroups to Survey")
    void addMultipleQuestionGroupsToSurvey() {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Umfrage");

        QuestionGroup questionGroup1 = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "Add first test group");
        QuestionGroup questionGroup2 = new QuestionGroupBuilder()
                .createQuestionGroup(2L, "Add second test group");

        surveyService.addQuestionGroupToSurvey(survey, questionGroup1);
        surveyService.addQuestionGroupToSurvey(survey, questionGroup2);

        assertEquals(questionGroup1, survey.getQuestionGroups().get(0));
        assertEquals(questionGroup2, survey.getQuestionGroups().get(1));
    }

    @Test
    @DisplayName("Adding Question to QuestionGroup - No Checkbox")
    void addQuestionToQuestionGroup_NoCheckbox() {
        QuestionGroup questionGroup = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "Test group for adding questions");

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Umfrage");
        survey.setQuestionGroups(List.of(questionGroup));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Add Question", false, false);
        CheckboxGroup checkboxGroup = new CheckboxGroup();

        surveyService.addQuestionToQuestionGroup(survey, 0, question, checkboxGroup);

        assertEquals(question, survey.getQuestionGroups().get(0).getQuestions().get(0));
        assertNull(survey.getQuestionGroups().get(0).getQuestions().get(0).getCheckboxGroup());
    }

    @Test
    @DisplayName("Adding multiple Questions to QuestionGroup")
    void addMultipleQuestionsToQuestionGroup() {
        QuestionGroup questionGroup = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "Test group for adding questions");

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Umfrage");
        survey.setQuestionGroups(List.of(questionGroup));

        Question firstQuestion = new QuestionBuilder()
                .createQuestion(1L, "Add First Question", false, false);
        Question secondQuestion = new QuestionBuilder()
                .createQuestion(2L, "Add Second Question", true, true);
        CheckboxGroup firstCheckboxGroup = new CheckboxGroup();
        CheckboxGroup secondCheckboxGroup = new CheckboxGroup();

        surveyService.addQuestionToQuestionGroup(survey, 0, firstQuestion, firstCheckboxGroup);
        surveyService.addQuestionToQuestionGroup(survey, 0, secondQuestion, secondCheckboxGroup);

        assertEquals(firstQuestion, survey.getQuestionGroups().get(0).getQuestions().get(0));
        assertNull(survey.getQuestionGroups().get(0).getQuestions().get(0).getCheckboxGroup());
        assertEquals(secondQuestion, survey.getQuestionGroups().get(0).getQuestions().get(1));
        assertNotNull(survey.getQuestionGroups().get(0).getQuestions().get(1).getCheckboxGroup());
    }

    @Test
    @DisplayName("Adding Question to second QuestionGroup - No Checkbox")
    void addQuestionToSecondQuestionGroup_NoCheckbox() {
        QuestionGroup questionGroup1 = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "First test group for adding questions");
        QuestionGroup questionGroup2 = new QuestionGroupBuilder()
                .createQuestionGroup(2L, "Second test group for adding questions");

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Umfrage");
        survey.setQuestionGroups(List.of(questionGroup1, questionGroup2));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Add Question", false, false);
        CheckboxGroup checkboxGroup = new CheckboxGroup();

        surveyService.addQuestionToQuestionGroup(survey, 1, question, checkboxGroup);

        assertEquals(question, survey.getQuestionGroups().get(1).getQuestions().get(0));
        assertNull(survey.getQuestionGroups().get(1).getQuestions().get(0).getCheckboxGroup());
    }

    @Test
    @DisplayName("Adding Checkbox to Question")
    void addCheckboxToQuestion() {
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, false, 0, 0);

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Add Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroup = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "Test group for adding checkboxes to question");
        questionGroup.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Umfrage");
        survey.setQuestionGroups(List.of(questionGroup));

        Checkbox checkbox = new CheckboxBuilder()
                .createCheckbox(1L, "Test Checkbox", false);

        surveyService.addCheckboxToQuestion(survey, 0, 0, checkbox);

        assertEquals(checkbox,
                survey.getQuestionGroups().get(0).getQuestions().get(0).getCheckboxGroup().getCheckboxes().get(0));
    }

    @Test
    @DisplayName("Adding multiple Checkboxes to Question")
    void addMultipleCheckboxesToQuestion() {
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, false, 0, 0);

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Add Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroup = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "Test group for adding checkboxes to question");
        questionGroup.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Umfrage");
        survey.setQuestionGroups(List.of(questionGroup));

        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);

        surveyService.addCheckboxToQuestion(survey, 0, 0, firstCheckbox);
        surveyService.addCheckboxToQuestion(survey, 0, 0, secondCheckbox);

        assertEquals(firstCheckbox,
                survey.getQuestionGroups().get(0).getQuestions().get(0).getCheckboxGroup().getCheckboxes().get(0));
        assertEquals(secondCheckbox,
                survey.getQuestionGroups().get(0).getQuestions().get(0).getCheckboxGroup().getCheckboxes().get(1));
    }

    @Test
    @DisplayName("Successful validation - startDate before endDate")
    void startDateBeforeEndDate_Successful() {
        LocalDateTime startDate = LocalDateTime.of(3000, 9, 1, 13, 0);
        LocalDateTime endDate = LocalDateTime.of(3000, 10, 1, 13, 0);

        boolean result = surveyService.startDateBeforeEndDate(startDate, endDate);

        assertTrue(result);
    }

    @Test
    @DisplayName("Failed validation - startDate before endDate")
    void startDateBeforeEndDate_Failed() {
        LocalDateTime startDate = LocalDateTime.of(2021, 10, 31, 13, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 10, 1, 13, 0);

        boolean result = surveyService.startDateBeforeEndDate(startDate, endDate);

        assertFalse(result);
    }

    @Test
    @DisplayName("Failed validation - Survey missing QuestionGroups")
    void surveyIsMissingQuestionGroups_Failed() {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Survey without QuestionGroup");

        List<String> errorMessages = surveyService.checkIfAnythingEmpty(survey);

        assertFalse(errorMessages.isEmpty());
    }

    @Test
    @DisplayName("Failed validation - QuestionGroup missing Questions")
    void questionGroupIsMissingQuestion_failed() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));
        QuestionGroup questionGroupWithoutQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(2L, "QuestionGroup without Question");

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Survey with empty QuestionGroup");
        survey.setQuestionGroups(List.of(questionGroupWithQuestion, questionGroupWithoutQuestion));

        List<String> errorMessages = surveyService.checkIfAnythingEmpty(survey);

        assertFalse(errorMessages.isEmpty());
    }

    @Test
    @DisplayName("Failed validation - No Checkboxes - No multipleSelect")
    void questionNoCheckboxes_failed() {
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, false, 0, 0);

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Survey with no checkboxes for question");
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        List<String> errorMessages = surveyService.checkIfAnythingEmpty(survey);

        assertFalse(errorMessages.isEmpty());
    }

    @Test
    @DisplayName("Failed validation - Not enough Checkboxes - No multipleSelect")
    void questionNotEnoughCheckboxes_failed() {
        Checkbox onlyCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, false, 0, 0);
        checkboxGroup.setCheckboxes(List.of(onlyCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Survey with not enough checkboxes for question");
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        List<String> errorMessages = surveyService.checkIfAnythingEmpty(survey);

        assertFalse(errorMessages.isEmpty());
    }

    @Test
    @DisplayName("Failed validation - Not enough Checkboxes - With multipleSelect (max: 4)")
    void questionNotEnoughCheckboxes_multipleSelect_failed() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);
        Checkbox thirdCheckbox = new CheckboxBuilder()
                .createCheckbox(3L, "Third Test Checkbox", true);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, true, 2, 4);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Survey with not enough checkboxes for question");
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        List<String> errorMessages = surveyService.checkIfAnythingEmpty(survey);

        assertFalse(errorMessages.isEmpty());
    }

    @Test
    @DisplayName("Successful validation - saving complete survey")
    void surveyIsComplete_Successful() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);
        Checkbox thirdCheckbox = new CheckboxBuilder()
                .createCheckbox(3L, "Third Test Checkbox", true);
        Checkbox fourthCheckbox = new CheckboxBuilder()
                .createCheckbox(4L, "Fourth Test Checkbox", false);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, true, 1, 3);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox, fourthCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Complete Survey");
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        List<String> errorMessages = surveyService.checkIfAnythingEmpty(survey);

        assertTrue(errorMessages.isEmpty());
    }
}