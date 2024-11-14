package com.streaming.adjustmentservice.config.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class StatisticPartitioner implements Partitioner {

    private final JdbcTemplate jdbcTemplate;
    private final LocalDateTime START_TIME;
    private final LocalDateTime END_TIME;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        IdRange idRange = jdbcTemplate.queryForObject("""
                        SELECT min(video_id) as min_id, max(video_id) as max_id 
                        FROM playback_log 
                        WHERE created_at >= ?
                        AND created_at < ?
                        """,
                (rs, rowNum) -> new IdRange(
                        rs.getLong("min_id"),
                        rs.getLong("max_id")
                ),
                START_TIME,
                END_TIME
        );

        if (idRange == null || idRange.minId() == null || idRange.maxId() == null) {
            log.warn("No data found range: {} ~ {}", START_TIME, END_TIME);
            return Map.of();
        }

        log.info("Found ID range : {} ~ {}", idRange.minId(), idRange.maxId());
        return createPartitions(idRange.minId(), idRange.maxId(), gridSize);
    }


    private Map<String, ExecutionContext> createPartitions(Long minId, Long maxId, int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        long range = (maxId - minId) / gridSize + 1;

        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();
            long start = minId + (i * range);
            long end = i == gridSize - 1 ? maxId + 1 : minId + ((i + 1) * range);

            context.putLong("minVideoId", start);
            context.putLong("maxVideoId", end);

            log.info("Partition {}: Video ID {} ~ {}", i, start, end);
            partitions.put("partition" + i, context);
        }

        return partitions;
    }

    private record IdRange(Long minId, Long maxId) {
    }
}

