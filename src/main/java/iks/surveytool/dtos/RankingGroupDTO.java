package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RankingGroupDTO extends AbstractDTO {

    private String lowestRated;
    private String highestRated;

    private List<OptionDTO> options;
}
