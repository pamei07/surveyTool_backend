package iks.surveytool.services;

import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyEndDateDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.Answer;
import iks.surveytool.entities.Survey;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.AnswerRepository;
import iks.surveytool.repositories.QuestionGroupRepository;
import iks.surveytool.repositories.SurveyRepository;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Log4j2
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final QuestionGroupRepository questionGroupRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;
    private final Random random = new Random();

    public ResponseEntity<SurveyOverviewDTO> processSurveyDTO(CompleteSurveyDTO surveyDTO) {
        log.info("Processing new survey...");

        Survey newSurvey;
        try {
            newSurvey = mapSurveyToEntity(surveyDTO);
        } catch (Exception e) {
            log.error("Error while mapping SurveyDTO to Survey", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        if (newSurvey.validate()) {
            log.info("New survey is valid...");

            try {
                generateIds(newSurvey);
                Survey savedSurvey = saveSurvey(newSurvey);
                SurveyOverviewDTO completeSurveyDTO = mapSurveyToDTO(savedSurvey);

                log.info("Successfully saved new survey => id: {}, title: {}",
                        completeSurveyDTO.getId(), completeSurveyDTO.getName());
                return ResponseEntity.ok(completeSurveyDTO);
            } catch (Exception e) {
                log.error("Error while saving new survey/mapping Survey to SurveyDTO", e);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            }
        } else {
            log.error("New survey is not valid: 422 - Unprocessable Entity");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    private Survey mapSurveyToEntity(CompleteSurveyDTO surveyDTO) {
        return modelMapper.map(surveyDTO, Survey.class);
    }

    private void generateIds(Survey survey) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String accessId = generateAccessId(currentDateTime);
        survey.setAccessId(accessId);

        LocalDateTime startDateTime = survey.getStartDate();
        String participateId = generateParticipationId(startDateTime);
        survey.setParticipationId(participateId);
    }

    private String generateAccessId(LocalDateTime currentDateTime) {
        String currentDateTimeHex = convertDateTimeToHex(currentDateTime);
        String hexSuffix = generateHexSuffix();
        String accessId = currentDateTimeHex + "-" + hexSuffix;

        while (surveyRepository.findSurveyByAccessId(accessId).isPresent()) {
            hexSuffix = generateHexSuffix();
            accessId = currentDateTimeHex + "-" + hexSuffix;
        }

        return accessId.toUpperCase();
    }

    private String generateParticipationId(LocalDateTime startDate) {
        String currentDateTimeHex = convertDateTimeToHex(startDate);
        String hexSuffix = generateHexSuffix();
        String participationId = currentDateTimeHex + "-" + hexSuffix;

        while (surveyRepository.findSurveyByParticipationId(participationId).isPresent()) {
            hexSuffix = generateHexSuffix();
            participationId = currentDateTimeHex + "-" + hexSuffix;
        }

        return participationId.toUpperCase();
    }

    private String convertDateTimeToHex(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mmkddMMyy");
        String formattedDate = dateTime.format(formatter);
        long formattedDateLong = Long.parseLong(formattedDate);

        return Long.toHexString(formattedDateLong);
    }

    private String generateHexSuffix() {
        int randomNumber = this.random.nextInt(256);
        return Integer.toHexString(randomNumber);
    }

    private Survey saveSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    private SurveyOverviewDTO mapSurveyToDTO(Survey savedSurvey) {
        return modelMapper.map(savedSurvey, CompleteSurveyDTO.class);
    }

    public ResponseEntity<SurveyOverviewDTO> processSurveyByAccessId(String accessId) {
        log.trace("Looking for survey by accessId: {}", accessId);

        SurveyOverviewDTO surveyOverviewDTO;
        try {
            surveyOverviewDTO = mapSurveyToDTOByAccessId(accessId);
        } catch (Exception e) {
            log.error("Error while mapping Survey to SurveyDTO", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        if (surveyOverviewDTO != null) {
            log.trace("Successfully fetched survey by accessId ({}) => id: {}, title: {}",
                    surveyOverviewDTO.getAccessId(), surveyOverviewDTO.getId(), surveyOverviewDTO.getName());
            return ResponseEntity.ok(surveyOverviewDTO);
        } else {
            log.trace("Cannot find survey with accessId: {}", accessId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private SurveyOverviewDTO mapSurveyToDTOByAccessId(String accessId) {
        Optional<Survey> surveyOptional = findSurveyByAccessId(accessId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            return modelMapper.map(survey, CompleteSurveyDTO.class);
        }
        return null;
    }

    private Optional<Survey> findSurveyByAccessId(String accessId) {
        return surveyRepository.findSurveyByAccessId(accessId);
    }

    public ResponseEntity<SurveyOverviewDTO> processSurveyByParticipationId(String participationId) {
        log.trace("Looking for survey by participationId: {}", participationId);

        SurveyOverviewDTO surveyDTO;
        try {
            surveyDTO = mapSurveyToDTOByParticipationId(participationId);
        } catch (Exception e) {
            log.error("Error while mapping Survey to SurveyDTO", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        if (surveyDTO != null) {
            if (surveyDTO instanceof CompleteSurveyDTO) {
                log.trace("Successfully fetched survey by participationId ({}) => id: {}, title: {}",
                        surveyDTO.getAccessId(), surveyDTO.getId(), surveyDTO.getName());
                return ResponseEntity.ok(surveyDTO);
            } else {
                // If current time is not within start- and endDate: return survey without questions to fill information
                // in front-end
                log.trace("Successfully fetched survey by participationId ({}) (not within timeframe) => id: {}, title: {}",
                        surveyDTO.getAccessId(), surveyDTO.getId(), surveyDTO.getName());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(surveyDTO);
            }
        } else {
            log.trace("Cannot find survey with participationId: {}", participationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private SurveyOverviewDTO mapSurveyToDTOByParticipationId(String participationId) {
        Optional<Survey> surveyOptional = findSurveyByParticipationId(participationId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            ZoneId berlinTime = ZoneId.of("Europe/Berlin");
            LocalDateTime surveyStartDate = survey.getStartDate();
            ZonedDateTime zonedStartDate = ZonedDateTime.of(surveyStartDate, berlinTime);
            LocalDateTime surveyEndDate = survey.getEndDate();
            ZonedDateTime zonedEndDate = ZonedDateTime.of(surveyEndDate, berlinTime);
            ZonedDateTime currentDateTime = ZonedDateTime.now(berlinTime);
            if (currentDateTime.isAfter(zonedStartDate) && currentDateTime.isBefore(zonedEndDate)) {
                return modelMapper.map(survey, CompleteSurveyDTO.class);
            } else {
                // If current time is not within start- and endDate: return survey without questions to fill information
                // in front-end
                return modelMapper.map(survey, SurveyOverviewDTO.class);
            }
        } else {
            return null;
        }
    }

    private Optional<Survey> findSurveyByParticipationId(String participationId) {
        return surveyRepository.findSurveyByParticipationId(participationId);
    }

    public ResponseEntity<List<SurveyOverviewDTO>> processOpenAccessSurveys() {
        log.trace("Looking for open access surveys...");

        try {
            List<SurveyOverviewDTO> openAccessSurveys = mapSurveysToDTOByOpenIsTrue();
            log.trace("Successfully fetched open access surveys. {} available.", openAccessSurveys.size());
            return ResponseEntity.ok(openAccessSurveys);
        } catch (Exception e) {
            log.error("Error while mapping open access surveys to surveyDTOs", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    private List<SurveyOverviewDTO> mapSurveysToDTOByOpenIsTrue() {
        List<Survey> openAccessSurveys = findSurveysByOpenAccessIsTrue();
        Type surveyOverviewList = new TypeToken<List<SurveyOverviewDTO>>() {
        }.getType();
        return modelMapper.map(openAccessSurveys, surveyOverviewList);
    }

    private List<Survey> findSurveysByOpenAccessIsTrue() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return surveyRepository.findSurveysByOpenAccessIsTrueAndEndDateIsAfterOrderByStartDate(currentDateTime);
    }

    public ResponseEntity<List<SurveyOverviewDTO>> processSurveysByUserId(Long id) {
        log.trace("Looking for surveys by userId (id: {})...", id);

        try {
            List<SurveyOverviewDTO> surveys = mapSurveysToDTOByUserId(id);
            log.trace("Successfully fetched surveys by userId ({}). {} available.", id, surveys.size());
            return ResponseEntity.ok(surveys);
        } catch (Exception e) {
            log.error("Error while mapping open access surveys to surveyDTOs", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    private List<SurveyOverviewDTO> mapSurveysToDTOByUserId(Long id) {
        List<Survey> surveys = findSurveysByUserId(id);
        Type surveyOverviewList = new TypeToken<List<SurveyOverviewDTO>>() {
        }.getType();
        return modelMapper.map(surveys, surveyOverviewList);
    }

    private List<Survey> findSurveysByUserId(Long id) {
        return surveyRepository.findSurveysByUserIdOrderByCreationTimeDesc(id);
    }

    public ResponseEntity<Long> processDeletionOfSurveyById(Long id, KeycloakAuthenticationToken token) {
        log.info("Process deletion of survey (id: {})...", id);

        Optional<Survey> surveyOptional = surveyRepository.findById(id);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();

            if (!checkIfUserAuthorizedForSurvey(token, survey)) {
                log.error("Current user not authorized to delete survey (id: {})", id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            return deleteSurveyById(id);
        } else {
            log.error("Cannot find survey (id: {})", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private ResponseEntity<Long> deleteSurveyById(Long id) {
        try {
            surveyRepository.deleteById(id);
            log.info("Successfully deleted survey (id: {}).", id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            log.error("Error while deleting survey (id: {})", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private boolean checkIfUserAuthorizedForSurvey(KeycloakAuthenticationToken token, Survey survey) {
        KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) token.getPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
        Optional<User> currentUser = userRepository.findUserByEmail(accessToken.getEmail());
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            boolean authorized;
            try {
                authorized = Objects.equals(user.getId(), survey.getUser().getId());
            } catch (NullPointerException exception) {
                return false;
            }
            return authorized;
        } else {
            return false;
        }
    }

    @Transactional
    public ResponseEntity<SurveyOverviewDTO> processUpdateOfSurvey(CompleteSurveyDTO surveyDTO,
                                                                   KeycloakAuthenticationToken token) {
        log.info("Process update of survey (id: {}, title: {})...", surveyDTO.getId(), surveyDTO.getName());

        List<Answer> answers = answerRepository.findAnswersBySurvey_Id(surveyDTO.getId());
        if (!answers.isEmpty()) {
            log.error("Cannot update survey. This survey has already been answered. (id: {}, title: {})",
                    surveyDTO.getId(), surveyDTO.getName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Survey updatedSurvey;
        try {
            updatedSurvey = mapSurveyToEntity(surveyDTO);
        } catch (Exception e) {
            log.error("Error while mapping SurveyDTO to Survey", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        if (!updatedSurvey.validate()) {
            log.error("Updated survey is not valid - 422: Unprocessable Entity");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        Long id = updatedSurvey.getId();
        Optional<Survey> surveyOptional = surveyRepository.findById(id);
        if (surveyOptional.isPresent()) {
            Survey surveyToUpdate = surveyOptional.get();

            if (!checkIfUserAuthorizedForSurvey(token, surveyToUpdate)) {
                log.error("Current user not authorized to update survey (id: {})", id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            try {
                updateSurvey(surveyToUpdate, updatedSurvey);
                Survey savedSurvey = saveSurvey(surveyToUpdate);
                SurveyOverviewDTO completeSurveyDTO = mapSurveyToDTO(savedSurvey);
                log.info("Successfully updated survey (id: {}, (new) title: {})",
                        completeSurveyDTO.getId(), completeSurveyDTO.getName());
                return ResponseEntity.ok(completeSurveyDTO);
            } catch (Exception e) {
                log.error("Error while saving updated Survey/mapping Survey to SurveyDTO", e);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            }
        } else {
            log.error("Cannot find survey (id: {})", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private void updateSurvey(Survey surveyToUpdate, Survey updatedSurvey) {
        surveyToUpdate.setName(updatedSurvey.getName());
        surveyToUpdate.setDescription(updatedSurvey.getDescription());
        surveyToUpdate.setCreatorName(updatedSurvey.getCreatorName());
        surveyToUpdate.setStartDate(updatedSurvey.getStartDate());
        surveyToUpdate.setEndDate(updatedSurvey.getEndDate());
        surveyToUpdate.setOpenAccess(updatedSurvey.isOpenAccess());
        surveyToUpdate.setAnonymousParticipation(updatedSurvey.isAnonymousParticipation());
        questionGroupRepository.deleteQuestionGroupsBySurvey_Id(surveyToUpdate.getId());
        surveyToUpdate.setQuestionGroups(updatedSurvey.getQuestionGroups());
    }

    public ResponseEntity<SurveyOverviewDTO> processPatchingSurveyEndDate(SurveyEndDateDTO surveyEndDateDTO,
                                                                          KeycloakAuthenticationToken token) {
        log.info("Process patch of endDate of survey (id: {})...", surveyEndDateDTO.getId());

        if (!surveyEndDateDTO.checkIfEndDateValid()) {
            log.error("New endDate not valid - 422: Unprocessable Entity");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        Long id = surveyEndDateDTO.getId();
        Optional<Survey> surveyOptional = surveyRepository.findById(id);
        if (surveyOptional.isPresent()) {
            Survey surveyToUpdate = surveyOptional.get();

            if (!checkIfUserAuthorizedForSurvey(token, surveyToUpdate)) {
                log.error("Current user not authorized to update endDate of survey (id: {})", id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            try {
                surveyToUpdate.setEndDate(surveyEndDateDTO.getEndDate());
                Survey savedSurvey = saveSurvey(surveyToUpdate);
                SurveyOverviewDTO completeSurveyDTO = mapSurveyToDTO(savedSurvey);
                log.info("Successfully updated endDate of survey (id: {})", id);
                return ResponseEntity.ok(completeSurveyDTO);
            } catch (Exception e) {
                log.error("Error while saving updated Survey/mapping Survey to SurveyDTO", e);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            }
        } else {
            log.error("Cannot find survey (id: {})", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
