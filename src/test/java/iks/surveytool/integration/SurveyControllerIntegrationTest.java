package iks.surveytool.integration;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.Survey;
import iks.surveytool.entities.User;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Testing Survey-Integration")
class SurveyControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int serverPort;

    /**
     * Fill database with complete surveys (id: 1, not within TimeFrame) (id: 2, within TimeFrame) to search for
     */
    @BeforeAll
    static void fillDatabase(@Autowired UserRepository userRepository,
                             @Autowired SurveyRepository surveyRepository) {
        User user1 = new UserBuilder().createUser(1L, "Test Person #1", "user1@default.de");
        User user2 = new UserBuilder().createUser(2L, "Test Person #2", "user2@default.de");
        User user3 = new UserBuilder().createUser(3L, "Test Person #3", "user3@default.de");
        userRepository.saveAll(List.of(user1, user2, user3));

        Survey surveyNotWithinTimeFrame = new SurveyBuilder().createCompleteAndValidSurvey(user1);
        Survey surveyWithinTimeFrame = new SurveyBuilder().createCompleteAndValidSurvey(user1);
        surveyWithinTimeFrame.setId(2L);
        surveyWithinTimeFrame.setParticipationId("Survey within TimeFrame ParticipantId");
        surveyWithinTimeFrame.setAccessId("Survey within TimeFrame AccessId");
        surveyWithinTimeFrame.setOpenAccess(true);
        surveyWithinTimeFrame.setStartDate(LocalDateTime.of(2000, 1, 1, 12, 0));
        surveyWithinTimeFrame.setEndDate(LocalDateTime.of(2050, 1, 1, 12, 0));
        surveyRepository.saveAll(List.of(surveyNotWithinTimeFrame, surveyWithinTimeFrame));
    }

    @Test
    @DisplayName("Successful POST-Mapping - Survey added")
    void addValidSurvey() {
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
        firstQuestionDTO.setHasCheckbox(true);
        firstQuestionDTO.setCheckboxGroup(checkboxGroupDTO);

        QuestionDTO secondQuestionDTO = new QuestionDTO();
        secondQuestionDTO.setText("Test Text Question");
        secondQuestionDTO.setRequired(true);
        secondQuestionDTO.setHasCheckbox(false);

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

        ResponseEntity<SurveyOverviewDTO> surveyResponse = restTemplate.exchange(
                getUriCreateSurvey(),
                HttpMethod.POST,
                getHttpEntityWithJsonContentTypeWithBody(surveyDTO),
                SurveyOverviewDTO.class);

        assertThat(surveyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        SurveyOverviewDTO surveyResponseBody = surveyResponse.getBody();
        if (surveyResponseBody != null) {
            assertThat(surveyResponseBody.getClass()).isEqualTo(SurveyOverviewDTO.class);
        } else {
            fail("ResponseBody is null!");
        }
    }

    @Test
    @DisplayName("Failed POST-Mapping - invalid survey")
    void addInvalidSurvey() {
        CompleteSurveyDTO surveyDTO = new CompleteSurveyDTO();
        surveyDTO.setName("Test Survey");
        surveyDTO.setDescription("yo");
        surveyDTO.setStartDate(LocalDateTime.of(2050, 1, 1, 12, 0));
        surveyDTO.setEndDate(surveyDTO.getStartDate().plusWeeks(1L));
        surveyDTO.setOpenAccess(false);
        surveyDTO.setAnonymousParticipation(true);
        surveyDTO.setUserId(1L);
        surveyDTO.setCreatorName("Max Mustermann");
        surveyDTO.setQuestionGroups(new ArrayList<>());

        ResponseEntity<SurveyOverviewDTO> surveyResponse = restTemplate.exchange(
                getUriCreateSurvey(),
                HttpMethod.POST,
                getHttpEntityWithJsonContentTypeWithBody(surveyDTO),
                SurveyOverviewDTO.class);

        assertThat(surveyResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(surveyResponse.getBody()).isNull();
    }

    private URI getUriCreateSurvey() {
        String url = "http://localhost:" + serverPort + "/surveys";
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(url);
        return builder.build().encode().toUri();
    }

    private HttpEntity<?> getHttpEntityWithJsonContentTypeWithBody(CompleteSurveyDTO surveyDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(surveyDTO, headers);
    }

    @Test
    @DisplayName("Failed GET-Mapping - Survey not found by accessId")
    void cannotFindSurveyByAccessId() {
        String accessId = "Random String that doesn't match any survey";

        ResponseEntity<SurveyOverviewDTO> surveyResponse = restTemplate.exchange(
                getUriFindSurveyByAccessId(accessId),
                HttpMethod.GET,
                getHttpEntityWithJsonContentTypeNoBody(),
                SurveyOverviewDTO.class);

        assertThat(surveyResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Successful GET-Mapping - Survey found by accessId")
    void findSurveyByAccessId() {
        String accessId = "Test AccessId";

        ResponseEntity<SurveyOverviewDTO> surveyResponse = restTemplate.exchange(
                getUriFindSurveyByAccessId(accessId),
                HttpMethod.GET,
                getHttpEntityWithJsonContentTypeNoBody(),
                SurveyOverviewDTO.class);

        assertThat(surveyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        SurveyOverviewDTO surveyResponseBody = surveyResponse.getBody();
        if (surveyResponseBody != null) {
            assertThat(surveyResponseBody.getClass()).isEqualTo(SurveyOverviewDTO.class);
        } else {
            fail("ResponseBody is null!");
        }
    }

    private URI getUriFindSurveyByAccessId(String accessId) {
        String url = "http://localhost:" + serverPort + "/surveys";
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("accessId", accessId);
        return builder.build().encode().toUri();
    }

    @Test
    @DisplayName("Failed GET-Mapping - Survey not found by participationId")
    void cannotFindSurveyByParticipationId() {
        String participationId = "Random String that doesn't match any survey";

        ResponseEntity<SurveyOverviewDTO> surveyResponse = restTemplate.exchange(
                getUriFindSurveyByParticipationId(participationId),
                HttpMethod.GET,
                getHttpEntityWithJsonContentTypeNoBody(),
                SurveyOverviewDTO.class);

        assertThat(surveyResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Successful GET-Mapping - Survey found by participationId + within TimeFrame")
    void findSurveyByParticipationIdWithinTimeFrame() {
        String participationId = "Survey within TimeFrame ParticipantId";

        ResponseEntity<CompleteSurveyDTO> surveyResponse = restTemplate.exchange(
                getUriFindSurveyByParticipationId(participationId),
                HttpMethod.GET,
                getHttpEntityWithJsonContentTypeNoBody(),
                CompleteSurveyDTO.class);

        assertThat(surveyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        CompleteSurveyDTO surveyResponseBody = surveyResponse.getBody();
        if (surveyResponseBody != null) {
            assertThat(surveyResponseBody.getClass()).isEqualTo(CompleteSurveyDTO.class);
            assertThat(surveyResponseBody.getQuestionGroups()).isNotEmpty();
        } else {
            fail("ResponseBody is null!");
        }
    }

    @Test
    @DisplayName("Successful GET-Mapping - Survey found by participationId, but not within TimeFrame")
    void findSurveyByParticipationIdNotWithinTimeFrame() {
        String participationId = "Test ParticipationId";

        ResponseEntity<SurveyOverviewDTO> surveyResponse = restTemplate.exchange(
                getUriFindSurveyByParticipationId(participationId),
                HttpMethod.GET,
                getHttpEntityWithJsonContentTypeNoBody(),
                SurveyOverviewDTO.class);

        assertThat(surveyResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        SurveyOverviewDTO surveyResponseBody = surveyResponse.getBody();
        if (surveyResponseBody != null) {
            assertThat(surveyResponseBody.getClass()).isEqualTo(SurveyOverviewDTO.class);
        } else {
            fail("ResponseBody is null!");
        }
    }

    private URI getUriFindSurveyByParticipationId(String participationId) {
        String url = "http://localhost:" + serverPort + "/surveys";
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("participationId", participationId);
        return builder.build().encode().toUri();
    }

    private HttpEntity<?> getHttpEntityWithJsonContentTypeNoBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(headers);
    }

    @Test
    @DisplayName("Successful GET-Mapping - 2 OpenAccess Survey found")
    void findOpenAccessSurveys() {
        ResponseEntity<SurveyOverviewDTO[]> surveyResponse = restTemplate.exchange(
                getUriFindOpenAccessSurveys(),
                HttpMethod.GET,
                getHttpEntityWithJsonContentTypeNoBody(),
                SurveyOverviewDTO[].class);

        assertThat(surveyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        SurveyOverviewDTO[] surveyResponseBody = surveyResponse.getBody();
        if (surveyResponseBody != null) {
            assertThat(surveyResponseBody).hasSize(1);
        } else {
            fail("ResponseBody is null!");
        }
    }

    private URI getUriFindOpenAccessSurveys() {
        String url = "http://localhost:" + serverPort + "/surveys";
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(url);
        return builder.build().encode().toUri();
    }

    // TODO: Test @RolesAllowed-annotated mappings
}