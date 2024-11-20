package com.streaming.adjustmentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class RevenueCalculatorService {

    /**
     * 영상 조회수 수익 정산
     *
     * @param totalVideoViews   (long)
     * @param currentVideoViews (long)
     * @return BigDecimal
     */
    public BigDecimal calculateVideoRevenue(long totalVideoViews, long currentVideoViews) {
        BigDecimal revenue = BigDecimal.ZERO;
        long previousVideoViews = totalVideoViews - currentVideoViews;
//        log.info("이전 영상 누적 조회수 : {}", previousVideoViews);
//        log.info("현재 영상 누적 조회수 : {}", totalVideoViews);

        // 100만 이상 구간 (1.5원)
        if (totalVideoViews >= 1_000_000) {
            long viewsOver1M = totalVideoViews - Math.max(previousVideoViews, 999_999);
            revenue = revenue.add(BigDecimal.valueOf(viewsOver1M).multiply(BigDecimal.valueOf(1.5)));
//            log.info("100만뷰 이상 영상 조회수 : {}", viewsOver1M);
        }

        // 50만 이상 100만 미만 구간 (1.3원)
        if (totalVideoViews >= 500_000 && previousVideoViews < 999_999) {
            long rangeStart = Math.max(previousVideoViews, 500_000);
            long rangeEnd = Math.min(totalVideoViews, 999_999);
            long viewsOver500K = rangeEnd - rangeStart;
            if (previousVideoViews < 500_000) viewsOver500K++;
            revenue = revenue.add(BigDecimal.valueOf(viewsOver500K).multiply(BigDecimal.valueOf(1.3)));
//            log.info("50만뷰 이상 영상 조회수 : {}", viewsOver500K);
        }

        // 10만 이상 50만 미만 구간 (1.1원)
        if (totalVideoViews >= 100_000 && previousVideoViews < 499_999) {
            long rangeStart = Math.max(previousVideoViews, 100_000);
            long rangeEnd = Math.min(totalVideoViews, 499_999);
            long viewsOver100K = rangeEnd - rangeStart;
            if (previousVideoViews < 100_000) viewsOver100K++;
            revenue = revenue.add(BigDecimal.valueOf(viewsOver100K).multiply(BigDecimal.valueOf(1.1)));
//            log.info("10만뷰 이상 영상 조회수 : {}", viewsOver100K);
        }

        // 10만 미만 구간 (1원)
        if (previousVideoViews < 99_999) {
            long viewsUnder100K = Math.min(totalVideoViews, 99_999) - previousVideoViews;
            revenue = revenue.add(BigDecimal.valueOf(viewsUnder100K));
//            log.info("10만뷰 미만 영상 조회수: {}", viewsUnder100K);
        }

//        log.info("총 영상 수익 : {}", revenue);
        return revenue.setScale(0, RoundingMode.DOWN);
    }

    /**
     * 광고 조회수 수익 정산
     *
     * @param totalAdvertisementViews   (long)
     * @param currentAdvertisementViews (long)
     * @return BigDecimal
     */
    public BigDecimal calculateAdvertisementRevenue(long totalAdvertisementViews, long currentAdvertisementViews) {
        BigDecimal revenue = BigDecimal.ZERO;
        long previousAdvertisementViews = totalAdvertisementViews - currentAdvertisementViews;
//        log.info("이전 누적 광고 조회수 : {}", previousAdvertisementViews);
//        log.info("현재 누적 광고 조회수 : {}", totalAdvertisementViews);

        // 100만 이상 구간 (20원)
        if (totalAdvertisementViews >= 1_000_000) {
            long viewsOver1M = totalAdvertisementViews - Math.max(previousAdvertisementViews, 999_999);
            revenue = revenue.add(BigDecimal.valueOf(viewsOver1M).multiply(BigDecimal.valueOf(20)));
//            log.info("100만뷰 이상 광고 조회수 : {}", viewsOver1M);
        }

        // 50만 이상 100만 미만 구간 (15원)
        if (totalAdvertisementViews >= 500_000 && previousAdvertisementViews < 999_999) {
            long rangeStart = Math.max(previousAdvertisementViews, 500_000);
            long rangeEnd = Math.min(totalAdvertisementViews, 999_999);
            long viewsOver500K = rangeEnd - rangeStart;
            if (previousAdvertisementViews < 500_000) viewsOver500K++;
            revenue = revenue.add(BigDecimal.valueOf(viewsOver500K).multiply(BigDecimal.valueOf(15)));
//            log.info("50만뷰 이상 광고 조회수 : {}", viewsOver500K);
        }

        // 10만 이상 50만 미만 구간 (12원)
        if (totalAdvertisementViews >= 100_000 && previousAdvertisementViews < 499_999) {
            long rangeStart = Math.max(previousAdvertisementViews, 100_000);
            long rangeEnd = Math.min(totalAdvertisementViews, 499_999);
            long viewsOver100K = rangeEnd - rangeStart;
            if (previousAdvertisementViews < 100_000) viewsOver100K++;
            revenue = revenue.add(BigDecimal.valueOf(viewsOver100K).multiply(BigDecimal.valueOf(12)));
//            log.info("10만뷰 이상 광고 조회수 : {}", viewsOver100K);
        }

        // 10만 미만 구간 (10원)
        if (previousAdvertisementViews < 99_999) {
            long viewsUnder100K = Math.min(totalAdvertisementViews, 99_999) - previousAdvertisementViews;
            revenue = revenue.add(BigDecimal.valueOf(viewsUnder100K).multiply(BigDecimal.valueOf(10)));
//            log.info("10만뷰 미만 광고 조회수 : {}", viewsUnder100K);
        }

//        log.info("총 광고 수익 : {}", revenue);
        return revenue.setScale(0, RoundingMode.DOWN);
    }
}
