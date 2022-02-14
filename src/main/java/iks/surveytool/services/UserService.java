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
        User newUser = modelMapper.map(userDTO, User.class);
        if (newUser.validate()) {
            User savedUser = saveUser(newUser);
            UserDTO savedUserDTO = modelMapper.map(savedUser, UserDTO.class);
            return ResponseEntity.ok(savedUserDTO);
        } else {
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

    public ResponseEntity<UserDTO> processUserByEMail(String eMail) {
        UserDTO userDTO = mapUserToDTOByEMail(eMail);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        } else {
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
