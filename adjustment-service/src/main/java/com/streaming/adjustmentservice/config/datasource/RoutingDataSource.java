package com.streaming.adjustmentservice.config.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Value("${spring.application.name}")
    private String serviceName;

    /**
     * 현재 트랜젝션의 속성을 확인하여 데이터소스 키를 반환
     * DataSourceConfig/routingDataSource 에서 키값 설정
     *
     * @return readOnly = true -> "read" / readOnly = false -> "write"
     */
    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        if (isReadOnly) {
            log.info("[{}] Using READ DB [Port : 5437]", serviceName);
        } else {
            log.info("[{}] Using WRITE DB [Port : 5436]", serviceName);
        }

        return isReadOnly ? "read" : "write";
    }
}
