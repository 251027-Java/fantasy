package dev.revature.fantasy.service.statsModels;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LuckData {
    private MedianLuck medianLuck;
    /**
     * Map of rosterUserId → all-play data
     */
    private Map<Long, AllPlayData> allPlayLuck;
}
