package com.streaming.adjustmentservice.config.batch.settlement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SettlementPartitioner implements Partitioner {

    private final JdbcTemplate jdbcTemplate;
    private final LocalDate TARGET_DATE;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        IdRange idRange = jdbcTemplate.queryForObject("""
                        SELECT MIN(daily_statistic_id) AS min_statistic_id, MAX(daily_statistic_id) AS max_statistic_id
                        FROM daily_statistic\s
                        WHERE statistic_date = ?
                        """,
                (rs, rowNum) -> new IdRange(
                        rs.getLong("min_statistic_id"),
                        rs.getLong("max_statistic_id")
                ),
                TARGET_DATE
        );

        if (idRange == null || idRange.minId() == null || idRange.maxId() == null) {
            log.warn("No data found for date: {}", TARGET_DATE);
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

            context.putLong("minStatisticId", start);
            context.putLong("maxStatisticId", end);

            log.info("Partition {}: ID {} ~ {}", i, start, end);
            partitions.put("partition" + i, context);
        }

        return partitions;
    }

    private record IdRange(Long minId, Long maxId) {
    }
}
