package com.ftn.sbnz.model.utils;

import com.ftn.sbnz.model.events.AirQualityEvent;
import java.util.List;

public class AirQualityStats {

    // CEP-2: Count episodes where VOC/PM spike above threshold in 24h
    public static boolean hasMultipleEpisodes(List<AirQualityEvent> events,
            double vocThreshold,
            double pmThreshold,
            int minEpisodes) {
        if (events == null || events.size() < minEpisodes)
            return false;

        int episodeCount = 0;
        for (AirQualityEvent event : events) {
            if (event.getVocLevel() >= vocThreshold || event.getPmLevel() >= pmThreshold) {
                episodeCount++;
            }
        }

        return episodeCount >= minEpisodes;
    }

    // Helper to get total episode count
    public static int getEpisodeCount(List<AirQualityEvent> events,
            double vocThreshold,
            double pmThreshold) {
        if (events == null || events.isEmpty())
            return 0;

        int episodeCount = 0;
        for (AirQualityEvent event : events) {
            if (event.getVocLevel() >= vocThreshold || event.getPmLevel() >= pmThreshold) {
                episodeCount++;
            }
        }

        return episodeCount;
    }

    // Helper to get latest event
    public static AirQualityEvent getLatestEvent(List<AirQualityEvent> events) {
        if (events == null || events.isEmpty())
            return null;

        AirQualityEvent latest = events.get(0);
        for (AirQualityEvent event : events) {
            if (event.getTimestamp() > latest.getTimestamp()) {
                latest = event;
            }
        }
        return latest;
    }
}
