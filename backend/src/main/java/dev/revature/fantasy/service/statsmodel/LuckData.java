package dev.revature.fantasy.service.statsmodel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class LuckData {
    private MedianLuck medianLuck;
    /**
     * Map of rosterUserId → all-play data
     */
    private Map<Long, AllPlayData> allPlayLuck;
}
