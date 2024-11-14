package com.streaming.adjustmentservice.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static com.streaming.common.constant.DatasourceConstant.METADATA_DATASOURCE;
import static com.streaming.common.constant.DatasourceConstant.META_TRANSACTION_MANAGER;

@Configuration
public class MetaDataSourceConfig {

    /**
     * Meta DB 설정
     * Port: 5438
     * 배치 메타데이터는 @Primary에 생성됨
     */
    @Primary
    @BatchDataSource
    @Bean(METADATA_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.metadata.hikari")
    public DataSource metadataDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * 배치 메타 데이터용 TransactionManager
     */
    @Primary
    @Bean(META_TRANSACTION_MANAGER)
    public PlatformTransactionManager metaTransactionManager(
            @Qualifier(METADATA_DATASOURCE) DataSource metaDataSource
    ) {
        return new DataSourceTransactionManager(metaDataSource);
    }
}
