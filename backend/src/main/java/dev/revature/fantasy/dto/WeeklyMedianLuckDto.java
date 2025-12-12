package dev.revature.fantasy.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class WeeklyMedianLuckDto {
    @NonNull
    private String userName;

    @NonNull
    private Double[] stats;
}
