package com.streaming.adjustmentservice.entity.statistic;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public enum PeriodType {

    DAILY("PERIOD_DAILY") {
        @Override
        public PeriodTypeRange calculateRange(LocalDate date) {
            return new PeriodTypeRange(date, date);
        }
    },
    WEEKLY("PERIOD_WEEKLY") {
        @Override
        public PeriodTypeRange calculateRange(LocalDate date) {
            LocalDate start = date.with(java.time.DayOfWeek.MONDAY);
            LocalDate end = date.with(java.time.DayOfWeek.SUNDAY);
            return new PeriodTypeRange(start, end);
        }
    },
    MONTHLY("PERIOD_MONTHLY") {
        @Override
        public PeriodTypeRange calculateRange(LocalDate date) {
            LocalDate start = date.withDayOfMonth(1);
            LocalDate end = date.withDayOfMonth(date.lengthOfMonth());
            return new PeriodTypeRange(start, end);
        }
    };

    private final String period;

    PeriodType(String period) {
        this.period = period;
    }

    public abstract PeriodTypeRange calculateRange(LocalDate date);

    public record PeriodTypeRange(LocalDate start, LocalDate end) {
    }
}
