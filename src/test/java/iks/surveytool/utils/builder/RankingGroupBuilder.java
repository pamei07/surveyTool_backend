package iks.surveytool.utils.builder;

import iks.surveytool.entities.RankingGroup;

import java.util.ArrayList;

public class RankingGroupBuilder {
    public RankingGroup createRankingGroup(Long id) {
        RankingGroup rankingGroup = new RankingGroup("low", "high", new ArrayList<>());
        rankingGroup.setId(id);
        return rankingGroup;
    }
}
