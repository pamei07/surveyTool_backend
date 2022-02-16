package iks.surveytool.integration;

import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.AnswerRepository;
import iks.surveytool.repositories.SurveyRepository;
import iks.surveytool.repositories.UserRepository;
import iks.surveytool.utils.builder.SurveyBuilder;
import iks.surveytool.utils.builder.UserBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.util.List;

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

    @LocalServerPort
    private int serverPort;

    /**
     * Fill database with complete survey to create answers to:
     * Question #1: id = 1L, hasCheckbox = false
     * Question #2: id = 2L, hasCheckbox = true
     * => Checkbox #1-#4: id = 1L-4L, #2 & #3: hasTextField = true
     */
    @BeforeAll
    static void fillDatabase(@Autowired UserRepository userRepository,
                             @Autowired SurveyRepository surveyRepository) {
        User user1 = new UserBuilder().createUser(1L, "Test Person", "user1@default.de");
        User user2 = new UserBuilder().createUser(2L, "Test Person #2", "user2@default.de");
        userRepository.saveAll(List.of(user1, user2));

        surveyRepository.save(new SurveyBuilder().createCompleteAndValidSurvey(user1));
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
    void findAnswersByQuestionId() {
    }

    @Test
    void findAnswersBySurveyId() {
    }
}