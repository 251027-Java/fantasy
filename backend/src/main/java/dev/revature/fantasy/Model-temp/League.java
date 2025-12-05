package dev.revature.fantasy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "league")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class League {

    @Id
    @Column(name = "league_id")
    private String leagueId;
    @Column(name = "num_rosters")
    private int numRosters;
    @Column(name = "league_name", length = 50)
    private String leagueName;
    @Column(name = "season")
    private int seasonYear;


    @Override
    public String toString() {
        return "League{" +
                "leagueId=" + leagueId +
                ", numRosters=" + numRosters +
                ", name='" + leagueName + '\'' +
                ", season=" + seasonYear +
                '}';
    }
    
}
