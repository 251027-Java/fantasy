package dev.revature.fantasy.ResponseModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Score {
    private double totalLuck;
    private double medLuck;
    private double apLuck;
    private double apWins;
    private double apLoses;
    private double apTies;
    private double wins;
    private double loses;
    private double ties;
}
