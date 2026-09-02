package com.digitalstokvel.momo.service;

import com.digitalstokvel.momo.dto.MoMoDisbursementRequest;
import com.digitalstokvel.momo.dto.MoMoDisbursementResponse;
import com.digitalstokvel.momo.dto.MoMoRequest;
import com.digitalstokvel.momo.dto.MoMoResponse;
import com.digitalstokvel.momo.entity.MoMoTransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MoMoServiceImpl implements MoMoService {

    private static final Logger log = LoggerFactory.getLogger(MoMoServiceImpl.class);

    @Override
    public MoMoResponse requestToPay(MoMoRequest request) {
        String referenceId = UUID.randomUUID().toString();
        log.info("Initiating MoMo RequestToPay: referenceId={}, payer={}, amount={} {}",
                referenceId, request.getPayerPhoneNumber(), request.getAmount(), request.getCurrency());

        // In Phase 1 foundation, return a valid pending transaction acknowledgment
        return MoMoResponse.pending(referenceId, "Payment request accepted for processing");
    }

    @Override
    public MoMoDisbursementResponse disburse(MoMoDisbursementRequest request) {
        String referenceId = UUID.randomUUID().toString();
        log.info("Initiating MoMo Disbursement: referenceId={}, payee={}, amount={} {}",
                referenceId, request.getPayeePhoneNumber(), request.getAmount(), request.getCurrency());

        // In Phase 1 foundation, return a valid pending disbursement acknowledgment
        return MoMoDisbursementResponse.pending(referenceId, "Disbursement request accepted for processing");
    }

    @Override
    public MoMoTransactionStatus getTransactionStatus(String referenceId) {
        log.info("Querying MoMo transaction status: referenceId={}", referenceId);
        return MoMoTransactionStatus.SUCCESSFUL;
    }
}
