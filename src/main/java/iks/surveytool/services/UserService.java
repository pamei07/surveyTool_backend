package iks.surveytool.services;

import iks.surveytool.dtos.UserDTO;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ResponseEntity<UserDTO> processUserDTO(UserDTO userDTO) {
        log.info("Processing new user...");

        User newUser;
        try {
            newUser = modelMapper.map(userDTO, User.class);
        } catch (Exception e) {
            log.error("Error while mapping UserDTO to User", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        if (newUser.validate()) {
            log.info("New user is valid...");
            try {
                User savedUser = saveUser(newUser);
                UserDTO savedUserDTO = modelMapper.map(savedUser, UserDTO.class);

                log.info("Successfully created new user => id: {}, username: {}",
                        savedUserDTO.getId(), savedUserDTO.getName());
                return ResponseEntity.ok(savedUserDTO);
            } catch (Exception e) {
                log.error("Error while saving new user/mapping User to UserDTO.", e);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            }
        } else {
            log.error("New user is not valid: 422 - Unprocessable Entity");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    private User saveUser(User newUser) {
        return userRepository.save(newUser);
    }

    public ResponseEntity<List<UserDTO>> processParticipatingUsersBySurveyId(Long surveyId) {
        List<UserDTO> users = mapParticipatingUsersToDTOBySurveyId(surveyId);
        return ResponseEntity.ok(users);
    }

    private List<UserDTO> mapParticipatingUsersToDTOBySurveyId(Long surveyId) {
        List<User> users = findParticipatingUsersBySurveyId(surveyId);
        Type userDTOList = new TypeToken<List<UserDTO>>() {
        }.getType();
        return modelMapper.map(users, userDTOList);
    }

    private List<User> findParticipatingUsersBySurveyId(Long surveyId) {
        return userRepository.findParticipatingUsersBySurveyId(surveyId);
    }

    // TODO: process User by Username rather than Email
    public ResponseEntity<UserDTO> processUserByEMail(String eMail) {
        log.trace("Looking for user associated with: {} ...", eMail);

        UserDTO userDTO;
        try {
            userDTO = mapUserToDTOByEMail(eMail);
        } catch (Exception e) {
            log.error("Error while mapping User to UserDTO", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        if (userDTO != null) {
            log.trace("Successfully fetched user by e-mail ({}) => id: {}, username: {}",
                    userDTO.getEmail(), userDTO.getId(), userDTO.getName());
            return ResponseEntity.ok(userDTO);
        } else {
            log.trace("Cannot find user associated with: {}", eMail);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private UserDTO mapUserToDTOByEMail(String eMail) {
        Optional<User> userOptional = findUserByEmail(eMail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return modelMapper.map(user, UserDTO.class);
        }
        return null;
    }

    private Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
