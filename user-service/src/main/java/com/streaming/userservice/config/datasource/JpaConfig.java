package com.streaming.userservice.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

    /**
     * JPA EntityManager 설정
     *
     * @param dataSource    (Primary로 설정된 라우팅 데이터소스)
     * @param entityPackage (Entity 스캔 패키지 경로 - application.yml 설정)
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dataSource") DataSource dataSource,
            @Value("${spring.jpa.entity-package}") String entityPackage
    ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(entityPackage);
        em.setJpaVendorAdapter(jpaVendorAdapter());
        em.setJpaPropertyMap(jpaProperties());

        return em;
    }

    /**
     * JPA 트랜젝션 메니저 설정
     */
    @Bean
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());

        return transactionManager;
    }

    /**
     * Hibernate DB 설정
     */
    private HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");

        return vendorAdapter;
    }

    /**
     * JPA 속성 설정
     */
    private Map<String, Object> jpaProperties() {
        Map<String, Object> properties = new HashMap<>();
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        if (isReadOnly) {
            properties.put("hibernate.hbm2ddl.auto", "none");
            properties.put("hibernate.connection.readOnly", "true");
            properties.put("hibernate.flush.mode", "manual");
        } else {
            properties.put("hibernate.hbm2ddl.auto", "update");
        }

        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.jpa.open-in-view", "false");

        return properties;
    }
}
