package com.streaming.adjustmentservice.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static com.streaming.common.constant.DatasourceConstant.METADATA_DATASOURCE;

@Configuration
public class TransactionManagerConfig {

    /**
     * 배치 메타 데이터용 TransactionManager
     */
    @Primary
    @Bean("metaTransactionManager")
    public PlatformTransactionManager metaTransactionManager(
            @Qualifier(METADATA_DATASOURCE) DataSource metaDataSource
    ) {
        return new DataSourceTransactionManager(metaDataSource);
    }
}