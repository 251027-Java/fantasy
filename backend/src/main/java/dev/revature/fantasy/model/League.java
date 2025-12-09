package dev.revature.fantasy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "league")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class League {

    @Id
    private String id;

    @Column(name = "num_rosters")
    private int numRosters;

    @Column(length = 50)
    private String name;

    @Column(name = "season")
    private int seasonYear;
}
