package com.streaming.adjustmentservice.controller.port;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface SettlementQueryService {
}
