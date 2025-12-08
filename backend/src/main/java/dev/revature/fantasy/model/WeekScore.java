package dev.revature.fantasy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Entity
@Table(name = "week_score")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeekScore {

    @EmbeddedId
    private WeekScoreId id;

    private Double score;

    @ManyToOne
    @JoinColumn(name = "league_id", foreignKey = @ForeignKey(name = "fk_league_id"))
    @ToString.Exclude
    private League league;

    @ToString.Include
    private String leagueId() {
        return Optional.ofNullable(league).map(League::getId).orElse(null);
    }
}
