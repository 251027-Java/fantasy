package dev.revature.fantasy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Optional;

@Entity
@Table(
        name = "roster_user",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"roster_id", "league_id"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RosterUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment in DB
    private Long id;

    @Column(name = "roster_id", nullable = false)
    private Integer rosterId;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_id"), nullable = false)
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "league_id", foreignKey = @ForeignKey(name = "fk_league_id"), nullable = false)
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private League league;

    private Integer wins;

    private Integer ties;

    private Integer losses;

    @Column(name = "fpts_decimal")
    private Integer fptsDecimal;

    @Column(name = "fpts_against_decimal")
    private Integer fptsAgainstDecimal;

    @Column(name = "fpts_against")
    private Integer fptsAgainst;

    private Integer fpts;

    // constructor where everything except rosterUserId is passed in
    public RosterUser(
            Integer rosterId,
            User user,
            League league,
            Integer wins,
            Integer ties,
            Integer losses,
            Integer fptsDecimal,
            Integer fptsAgainstDecimal,
            Integer fptsAgainst,
            Integer fpts) {
        this.rosterId = rosterId;
        this.user = user;
        this.league = league;
        this.wins = wins;
        this.ties = ties;
        this.losses = losses;
        this.fptsDecimal = fptsDecimal;
        this.fptsAgainstDecimal = fptsAgainstDecimal;
        this.fptsAgainst = fptsAgainst;
        this.fpts = fpts;
    }

    @ToString.Include
    private String userId() {
        return Optional.ofNullable(user).map(User::getId).orElse(null);
    }

    @ToString.Include
    private String leagueId() {
        return Optional.ofNullable(league).map(League::getId).orElse(null);
    }
}
