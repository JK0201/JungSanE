package com.streaming.adjustmentservice.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static com.streaming.common.constant.DatasourceConstant.*;

@Configuration
public class DataSourceConfig {

    /**
     * Write DB 설정
     * Primary DB - CUD 작업 수행
     * Port: 5436
     */
    @Bean(WRITE_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.write.hikari")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * Read DB 설정
     * Replica DB - Read 작업 수행
     * Port: 5437
     */
    @Bean(READ_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.read.hikari")
    public DataSource readDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * Meta DB 설정
     * Port: 5438
     * 배치 메타데이터는 @Primary에 생성됨
     */
    @Primary
    @Bean(METADATA_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.metadata.hikari")
    public DataSource metadataDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * 트랜젝션의 ReadOnly 여부에 따라 DB를 분기하는 라우팅 데이터소스 설정
     * Write DB [@Transactional]
     * Read DB [@Transactional(readOnly = true)]
     */
    @Bean
    public DataSource routingDataSource(
            @Qualifier(WRITE_DATASOURCE) DataSource writeDataSource,
            @Qualifier(READ_DATASOURCE) DataSource readDataSource
    ) {
        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put("write", writeDataSource);
        dataSources.put("read", readDataSource);

        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);

        return routingDataSource;
    }

    /**
     * 실제 DB 연결이 필요한 시점까지 연결 지연 설정
     * "@Primary"는 배치 메타데이터가 들어가야 하므로 어노테이션 제거
     */
    @Bean("dataSource")
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}
