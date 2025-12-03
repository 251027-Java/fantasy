package dev.revature.fantasy.dto;

import lombok.Data;

@Data
public class ScoreDto {
    private double totalLuck;
    private double medLuck;
    private double apLuck;
    private int apWins;
    private int apLoses;
    private int apTies;
    private int wins;
    private int loses;
    private int ties;
}
