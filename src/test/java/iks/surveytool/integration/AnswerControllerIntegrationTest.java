package iks.surveytool.integration;

import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.*;
import iks.surveytool.utils.builder.AnswerBuilder;
import iks.surveytool.utils.builder.SurveyBuilder;
import iks.surveytool.utils.builder.UserBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Testing Answer-Integration")
class AnswerControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CheckboxRepository checkboxRepository;

    @LocalServerPort
    private int serverPort;

    /**
     * Fill database with complete survey (id: 1) to create answers to:
     * Question #1: id = 1L, hasCheckbox = false
     * Question #2: id = 2L, hasCheckbox = true
     * => Checkbox #1-#4: id = 1L-4L, #2 & #3: hasTextField = true
     * ...and Answers to fetch by questionId/surveyId
     */
    @BeforeAll
    static void fillDatabase(@Autowired UserRepository userRepository,
                             @Autowired SurveyRepository surveyRepository,
                             @Autowired QuestionRepository questionRepository,
                             @Autowired CheckboxRepository checkboxRepository,
                             @Autowired AnswerRepository answerRepository) {
        User user1 = new UserBuilder().createUser(1L, "Test Person #1", "user1@default.de");
        User user2 = new UserBuilder().createUser(2L, "Test Person #2", "user2@default.de");
        User user3 = new UserBuilder().createUser(3L, "Test Person #3", "user3@default.de");
        userRepository.saveAll(List.of(user1, user2, user3));

        Survey survey = new SurveyBuilder().createCompleteAndValidSurvey(user1);
        surveyRepository.save(survey);
    }

    @BeforeEach
    void addAnswers() {
        Optional<Question> firstQuestionOptional = questionRepository.findById(1L);
        Question firstQuestion = null;
        if (firstQuestionOptional.isPresent()) {
            firstQuestion = firstQuestionOptional.get();
        }
        Optional<Question> secondQuestionOptional = questionRepository.findById(2L);
        Question secondQuestion = null;
        if (secondQuestionOptional.isPresent()) {
            secondQuestion = secondQuestionOptional.get();
        }
        Optional<Checkbox> firstCheckboxOptional = checkboxRepository.findById(1L);
        Checkbox firstCheckbox = null;
        if (firstCheckboxOptional.isPresent()) {
            firstCheckbox = firstCheckboxOptional.get();
        }
        Optional<Checkbox> secondCheckboxOptional = checkboxRepository.findById(2L);
        Checkbox secondCheckbox = null;
        if (secondCheckboxOptional.isPresent()) {
            secondCheckbox = secondCheckboxOptional.get();
        }
        Optional<User> thirdUserOptional = userRepository.findById(3L);
        User thirdUserFromDb = null;
        if (thirdUserOptional.isPresent()) {
            thirdUserFromDb = thirdUserOptional.get();
        }

        Answer firstAnswer = new AnswerBuilder()
                .createAnswer(1L, "First Answer", thirdUserFromDb, firstQuestion, null);
        Answer secondAnswer = new AnswerBuilder()
                .createAnswer(2L, null, thirdUserFromDb, secondQuestion, firstCheckbox);
        Answer thirdAnswer = new AnswerBuilder()
                .createAnswer(3L, "Third Answer", thirdUserFromDb, secondQuestion, secondCheckbox);
        answerRepository.saveAll(List.of(firstAnswer, secondAnswer, thirdAnswer));
    }

    @AfterEach
    void removeAnswers() {
        answerRepository.deleteAll();
    }

    @Test
    @DisplayName("Successful POST-Mapping - One Answer added")
    void addOneAnswer() {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setText("Test answer");
        answerDTO.setParticipantName("Anonym");
        answerDTO.setUserId(2L);
        answerDTO.setQuestionId(1L);
        AnswerDTO[] answerDTOs = {answerDTO};

        ResponseEntity<AnswerDTO[]> answerResponse = restTemplate.exchange(
                getUriAddAnswers(),
                HttpMethod.POST,
                getHttpEntityWithJsonContentTypeWithBody(answerDTOs),
                AnswerDTO[].class);

        assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AnswerDTO[] answerResponseBody = answerResponse.getBody();
        if (answerResponseBody != null) {
            assertThat(answerResponseBody.getClass()).isEqualTo(AnswerDTO[].class);
            assertThat(answerResponseBody).isNotEmpty();
        } else {
            fail("ResponseBody is null!");
        }
    }

    @Test
    @DisplayName("Successful POST-Mapping - Two Answers added")
    void addTwoAnswer() {
        AnswerDTO firstAnswerDTO = new AnswerDTO();
        firstAnswerDTO.setText("Test answer");
        firstAnswerDTO.setParticipantName("Anonym");
        firstAnswerDTO.setUserId(2L);
        firstAnswerDTO.setQuestionId(1L);
        AnswerDTO secondAnswerDTo = new AnswerDTO();
        secondAnswerDTo.setParticipantName("Anonym");
        secondAnswerDTo.setUserId(2L);
        secondAnswerDTo.setQuestionId(2L);
        secondAnswerDTo.setCheckboxId(1L);
        AnswerDTO[] answerDTOs = {firstAnswerDTO, secondAnswerDTo};

        ResponseEntity<AnswerDTO[]> answerResponse = restTemplate.exchange(
                getUriAddAnswers(),
                HttpMethod.POST,
                getHttpEntityWithJsonContentTypeWithBody(answerDTOs),
                AnswerDTO[].class);

        assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AnswerDTO[] answerResponseBody = answerResponse.getBody();
        if (answerResponseBody != null) {
            assertThat(answerResponseBody.getClass()).isEqualTo(AnswerDTO[].class);
            assertThat(answerResponseBody).isNotEmpty();
        } else {
            fail("ResponseBody is null!");
        }
    }

    @Test
    @DisplayName("Failed POST-Mapping - Invalid Answer")
    void failWithInvalidAnswer() {
        AnswerDTO firstAnswerDTO = new AnswerDTO();
        firstAnswerDTO.setText("Test answer");
        firstAnswerDTO.setParticipantName("Anonym");
        firstAnswerDTO.setUserId(2L);
        firstAnswerDTO.setQuestionId(1L);
        AnswerDTO secondAnswerDTo = new AnswerDTO();
        secondAnswerDTo.setParticipantName("Anonym");
        secondAnswerDTo.setUserId(2L);
        secondAnswerDTo.setQuestionId(2L);
        // Checkbox with id: 2L has a text field, but not text is set => invalid
        secondAnswerDTo.setCheckboxId(2L);
        AnswerDTO[] answerDTOs = {firstAnswerDTO, secondAnswerDTo};

        ResponseEntity<AnswerDTO[]> answerResponse = restTemplate.exchange(
                getUriAddAnswers(),
                HttpMethod.POST,
                getHttpEntityWithJsonContentTypeWithBody(answerDTOs),
                AnswerDTO[].class);

        assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(answerResponse.getBody()).isNull();
    }

    private URI getUriAddAnswers() {
        String url = "http://localhost:" + serverPort + "/answers";
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(url);
        return builder.build().encode().toUri();
    }

    private HttpEntity<?> getHttpEntityWithJsonContentTypeWithBody(AnswerDTO[] answerDTOs) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(answerDTOs, headers);
    }

    @Test
    @DisplayName("Successful GET-Mapping - 0 Answers found by questionId")
    void findNoAnswersByQuestionId() {
        ResponseEntity<AnswerDTO[]> answerResponse = restTemplate.exchange(
                getUriFindAnswersByQuestionId(9999L),
                HttpMethod.GET,
                getHttpEntityWithJsonContentTypeNoBody(),
                AnswerDTO[].class);

        assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AnswerDTO[] answerResponseBody = answerResponse.getBody();
        if (answerResponseBody != null) {
            assertThat(answerResponseBody).isEmpty();
        } else {
            fail("ResponseBody is null!");
        }
    }

    @Test
    @DisplayName("Successful GET-Mapping - 2 Answers found by questionId")
    void findTwoAnswersByQuestionId() {
        ResponseEntity<AnswerDTO[]> answerResponse = restTemplate.exchange(
                getUriFindAnswersByQuestionId(2L),
                HttpMethod.GET,
                getHttpEntityWithJsonContentTypeNoBody(),
                AnswerDTO[].class);

        assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AnswerDTO[] answerResponseBody = answerResponse.getBody();
        if (answerResponseBody != null) {
            assertThat(answerResponseBody).hasSize(2);
        } else {
            fail("ResponseBody is null!");
        }
    }

    private URI getUriFindAnswersByQuestionId(Long questionId) {
        Map<String, Long> pathVariables = new HashMap<>();
        pathVariables.put("questionId", questionId);

        String url = "http://localhost:" + serverPort + "/answers/questions/{questionId}";
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(url);
        return builder.buildAndExpand(pathVariables).encode().toUri();
    }

    private HttpEntity<?> getHttpEntityWithJsonContentTypeNoBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(headers);
    }

    @Test
    @DisplayName("Successful GET-Mapping - 0 Answers found by surveyId")
    void findNoAnswersBySurveyId() {
        ResponseEntity<AnswerDTO[]> answerResponse = restTemplate.exchange(
                getUriFindAnswersBySurveyId(9999L),
                HttpMethod.GET,
                getHttpEntityWithJsonContentTypeNoBody(),
                AnswerDTO[].class);

        assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AnswerDTO[] answerResponseBody = answerResponse.getBody();
        if (answerResponseBody != null) {
            assertThat(answerResponseBody).isEmpty();
        } else {
            fail("ResponseBody is null!");
        }
    }

    @Test
    @DisplayName("Successful GET-Mapping - 3 Answers found by surveyId")
    void findThreeAnswersBySurveyId() {
        ResponseEntity<AnswerDTO[]> answerResponse = restTemplate.exchange(
                getUriFindAnswersBySurveyId(1L),
                HttpMethod.GET,
                getHttpEntityWithJsonContentTypeNoBody(),
                AnswerDTO[].class);

        assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AnswerDTO[] answerResponseBody = answerResponse.getBody();
        if (answerResponseBody != null) {
            for (AnswerDTO answerDTO : answerResponseBody) {
                System.out.println(answerDTO.getId());
                System.out.println(answerDTO.getText());
                System.out.println(answerDTO.getParticipantName());
            }
            assertThat(answerResponseBody).hasSize(3);
        } else {
            fail("ResponseBody is null!");
        }
    }

    private URI getUriFindAnswersBySurveyId(Long surveyId) {
        Map<String, Long> pathVariables = new HashMap<>();
        pathVariables.put("surveyId", surveyId);

        String url = "http://localhost:" + serverPort + "/answers/surveys/{surveyId}";
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(url);
        return builder.buildAndExpand(pathVariables).encode().toUri();
    }
}