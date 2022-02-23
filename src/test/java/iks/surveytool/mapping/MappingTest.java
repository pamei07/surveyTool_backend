package iks.surveytool.mapping;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.SurveyRepository;
import iks.surveytool.repositories.UserRepository;
import iks.surveytool.utils.assertions.MappingAssertions;
import iks.surveytool.utils.builder.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Testcontainers
@DisplayName("Testing Mapper")
class MappingTest {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Fill database as SurveyConverter and AnswerConverter retrieve objects from db to convert DTOs into DAOs:
     * Question #1: id = 1L, QuestionType = TEXT
     * Question #2: id = 2L, QuestionType = MULTIPLE_CHOICE
     * => Checkbox #1-#4: id = 1L-4L, #2 & #3: hasTextField = true
     */
    @BeforeAll
    static void fillDatabase(@Autowired UserRepository userRepository,
                             @Autowired SurveyRepository surveyRepository) {
        userRepository.deleteAll();
        surveyRepository.deleteAll();

        User user1 = new UserBuilder().createUser(1L, "Test Person", "user1@default.de");
        User user2 = new UserBuilder().createUser(2L, "Test Person #2", "user2@default.de");
        userRepository.saveAll(List.of(user1, user2));

        surveyRepository.save(new SurveyBuilder().createCompleteAndValidSurvey(user1));
    }

    @Test
    @DisplayName("Survey to CompleteSurveyDTO")
    void mapSurveyToDTO() {
        User user = new UserBuilder().createUser(1L, "Test Person");
        Survey survey = new SurveyBuilder().createCompleteAndValidSurvey(user);

        CompleteSurveyDTO surveyDTO = modelMapper.map(survey, CompleteSurveyDTO.class);

        MappingAssertions.assertSurveyMapping(survey, surveyDTO);
    }

    @Test
    @DisplayName("CompleteSurveyDTO to Survey")
    void mapSurveyDTOtoEntity() {
        CheckboxDTO firstCheckboxDTO = new CheckboxDTO();
        firstCheckboxDTO.setText("First Checkbox DTO");
        firstCheckboxDTO.setHasTextField(false);
        CheckboxDTO secondCheckboxDTO = new CheckboxDTO();
        secondCheckboxDTO.setText("Second Checkbox DTO");
        secondCheckboxDTO.setHasTextField(true);

        CheckboxGroupDTO checkboxGroupDTO = new CheckboxGroupDTO();
        checkboxGroupDTO.setMultipleSelect(false);
        checkboxGroupDTO.setMinSelect(0);
        checkboxGroupDTO.setMaxSelect(2);
        checkboxGroupDTO.setCheckboxes(List.of(firstCheckboxDTO, secondCheckboxDTO));

        QuestionDTO firstQuestionDTO = new QuestionDTO();
        firstQuestionDTO.setText("Test Checkbox Question");
        firstQuestionDTO.setRequired(false);
        firstQuestionDTO.setCheckboxGroup(checkboxGroupDTO);

        QuestionDTO secondQuestionDTO = new QuestionDTO();
        secondQuestionDTO.setText("Test Text Question");
        secondQuestionDTO.setRequired(true);

        QuestionGroupDTO questionGroupDTO = new QuestionGroupDTO();
        questionGroupDTO.setTitle("Test QuestionGroup");
        questionGroupDTO.setQuestions(List.of(firstQuestionDTO, secondQuestionDTO));

        CompleteSurveyDTO surveyDTO = new CompleteSurveyDTO();
        surveyDTO.setName("Test Survey");
        surveyDTO.setDescription("yo");
        surveyDTO.setStartDate(LocalDateTime.of(2050, 1, 1, 12, 0));
        surveyDTO.setEndDate(surveyDTO.getStartDate().plusWeeks(1L));
        surveyDTO.setOpenAccess(false);
        surveyDTO.setAnonymousParticipation(true);
        surveyDTO.setUserId(1L);
        surveyDTO.setCreatorName("Max Mustermann");
        surveyDTO.setQuestionGroups(List.of(questionGroupDTO));

        Survey surveyConverted = modelMapper.map(surveyDTO, Survey.class);

        MappingAssertions.assertSurveyMapping(surveyConverted, surveyDTO);
    }

    @Test
    @DisplayName("AnswerList to AnswerDTOList")
    void mapAnswerListToAnswerDTOs() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, false, 0, 2);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox));

        Question firstQuestion = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, QuestionType.TEXT);
        Question secondQuestion = new QuestionBuilder()
                .createQuestion(2L, "Test Question", false, QuestionType.MULTIPLE_CHOICE);
        secondQuestion.setCheckboxGroup(checkboxGroup);

        User user = new UserBuilder().createUser(2L, "Test Person #2");

        Answer firstAnswer = new AnswerBuilder()
                .createAnswer(1L, "Test Answer", user, firstQuestion, null);
        Answer secondAnswer = new AnswerBuilder()
                .createAnswer(2L, null, user, secondQuestion, firstCheckbox);

        List<Answer> answers = List.of(firstAnswer, secondAnswer);

        Type answerDTOListType = new TypeToken<List<AnswerDTO>>() {
        }.getType();
        List<AnswerDTO> answerDTOList = modelMapper.map(answers, answerDTOListType);

        MappingAssertions.assertAnswerMapping(answers, answerDTOList);
    }

    @Test
    @DisplayName("AnswerDTOList to AnswerList")
    void mapAnswerDTOsToAnswers() {
        AnswerDTO firstAnswerDTO = new AnswerDTO();
        firstAnswerDTO.setText("Text");
        firstAnswerDTO.setUserId(2L);
        firstAnswerDTO.setParticipantName("Test User");
        firstAnswerDTO.setQuestionId(1L);
        AnswerDTO secondAnswerDTO = new AnswerDTO();
        secondAnswerDTO.setUserId(2L);
        secondAnswerDTO.setParticipantName("Test User");
        secondAnswerDTO.setQuestionId(2L);
        secondAnswerDTO.setCheckboxId(1L);

        List<AnswerDTO> answerDTOs = List.of(secondAnswerDTO, secondAnswerDTO);

        Type answerListType = new TypeToken<List<Answer>>() {
        }.getType();
        List<Answer> answers = modelMapper.map(answerDTOs, answerListType);

        MappingAssertions.assertAnswerMapping(answers, answerDTOs);
    }

    @Test
    @DisplayName("User to UserDTO")
    void mapUserToUserDTO() {
        User user = new UserBuilder()
                .createUser(1L, "Test User");

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        MappingAssertions.assertUserMapping(user, userDTO);
    }

    @Test
    @DisplayName("UserDTO to User")
    void mapUserDTOToUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test User");

        User user = modelMapper.map(userDTO, User.class);

        MappingAssertions.assertUserMapping(user, userDTO);
    }

}
