package dev.revature.fantasy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "system_metadata")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SystemMetadata {

    @Id
    private String key;

    private String value;
}
