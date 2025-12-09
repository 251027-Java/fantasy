package dev.revature.fantasy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Optional;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RosterIdObj {

    @ManyToOne
    @JoinColumn(name = "roster_user_id", foreignKey = @ForeignKey(name = "fk_roster_user_id"))
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RosterUser rosterUser;

    @Column(name = "player_id")
    private String playerId;

    @Column(name = "week_num")
    private int weekNum;

    @ToString.Include
    private Long rosterUserId() {
        return Optional.ofNullable(rosterUser).map(RosterUser::getId).orElse(null);
    }
}
