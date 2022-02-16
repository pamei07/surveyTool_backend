package iks.surveytool.integration;

import iks.surveytool.dtos.UserDTO;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.UserRepository;
import iks.surveytool.utils.builder.UserBuilder;
import org.junit.jupiter.api.AfterEach;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Testing User-Integration")
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int serverPort;

    @AfterEach
    void emptyDatabase() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Successful POST-Mapping - User created")
    void createUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Test Username");
        userDTO.setFirstName("Vorname");
        userDTO.setLastName("Nachname");
        userDTO.setEmail("mail@mail.com");

        ResponseEntity<UserDTO> userResponse = restTemplate.exchange(
                getUriCreateUser(),
                HttpMethod.POST,
                getHttpEntityWithJsonContentTypeWithBody(userDTO),
                UserDTO.class);

        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserDTO userResponseBody = userResponse.getBody();
        if (userResponseBody != null) {
            assertThat(userResponseBody.getClass()).isEqualTo(UserDTO.class);
        } else {
            fail("ResponseBody is null!");
        }
    }

    @Test
    @DisplayName("Failed POST-Mapping - User invalid")
    void invalidUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(null);
        userDTO.setFirstName("Vorname");
        userDTO.setLastName("Nachname");
        userDTO.setEmail("mail@mail.com");

        ResponseEntity<UserDTO> userResponse = restTemplate.exchange(
                getUriCreateUser(),
                HttpMethod.POST,
                getHttpEntityWithJsonContentTypeWithBody(userDTO),
                UserDTO.class);

        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(userResponse.getBody()).isNull();
    }

    private URI getUriCreateUser() {
        String url = "http://localhost:" + serverPort + "/users";
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(url);
        return builder.build().encode().toUri();
    }

    private HttpEntity<?> getHttpEntityWithJsonContentTypeWithBody(UserDTO userDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(userDTO, headers);
    }

    @Test
    @DisplayName("Failed GET-Mapping - User not found by E-Mail")
    void userCannotBeFoundByEmail() {
        String mail = "nichtExistierendeEmail@email.com";

        ResponseEntity<UserDTO> userResponse = restTemplate.exchange(
                getUriFindUserByEmail(mail),
                HttpMethod.GET,
                getHttpEntityWithJsonContentTypeNoBody(),
                UserDTO.class);

        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Successful GET-Mapping - User found by E-Mail")
    void userFoundByEmail() {
        String email = "user@email.com";
        User user = new UserBuilder().createUser(null, "Test user", email);
        userRepository.save(user);

        ResponseEntity<UserDTO> userResponse = restTemplate.exchange(
                getUriFindUserByEmail(email),
                HttpMethod.GET,
                getHttpEntityWithJsonContentTypeNoBody(),
                UserDTO.class);

        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserDTO userResponseBody = userResponse.getBody();
        if (userResponseBody != null) {
            assertThat(userResponseBody.getClass()).isEqualTo(UserDTO.class);
        } else {
            fail("ResponseBody is null!");
        }
    }

    private URI getUriFindUserByEmail(String mail) {
        String url = "http://localhost:" + serverPort + "/users";
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("email", mail);
        return builder.build().encode().toUri();
    }

    private HttpEntity<?> getHttpEntityWithJsonContentTypeNoBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(headers);
    }
}