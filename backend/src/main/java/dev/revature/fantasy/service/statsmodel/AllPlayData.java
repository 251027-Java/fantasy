package dev.revature.fantasy.service.statsmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllPlayData {

    private double allPlayPercentage;
    private double allPlayLuckScore;
    private int allPlayWins;
    private int allPlayLosses;
    private int allPlayTies;
    private int actualWins;
    private int actualLosses;
    private int actualTies;

    public String toString() {
        return "AllPlayData{" + "allPlayPercentage="
                + allPlayPercentage + ", allPlayLuckScore="
                + allPlayLuckScore + ", allPlayWins="
                + allPlayWins + ", allPlayLosses="
                + allPlayLosses + ", allPlayTies="
                + allPlayTies + ", actualWins="
                + actualWins + ", actualLosses="
                + actualLosses + ", actualTies="
                + actualTies + '}';
    }
}
