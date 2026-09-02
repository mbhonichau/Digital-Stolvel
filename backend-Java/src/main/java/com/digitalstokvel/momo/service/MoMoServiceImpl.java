package com.digitalstokvel.momo.service;

import com.digitalstokvel.momo.MomoCollectionsClient;
import com.digitalstokvel.momo.MomoDisbursementClient;
import com.digitalstokvel.momo.dto.MoMoDisbursementRequest;
import com.digitalstokvel.momo.dto.MoMoDisbursementResponse;
import com.digitalstokvel.momo.dto.MoMoRequest;
import com.digitalstokvel.momo.dto.MoMoResponse;
import com.digitalstokvel.momo.entity.MoMoTransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MoMoServiceImpl implements MoMoService {

    private static final Logger log = LoggerFactory.getLogger(MoMoServiceImpl.class);

    private final MomoCollectionsClient momoCollectionsClient;
    private final MomoDisbursementClient momoDisbursementClient;

    public MoMoServiceImpl(MomoCollectionsClient momoCollectionsClient,
                           MomoDisbursementClient momoDisbursementClient) {
        this.momoCollectionsClient = momoCollectionsClient;
        this.momoDisbursementClient = momoDisbursementClient;
    }

    @Override
    public MoMoResponse requestToPay(MoMoRequest request) {
        log.info("MoMoServiceImpl delegating requestToPay for payer: {}", request.getPayerPhoneNumber());
        return momoCollectionsClient.requestToPay(request);
    }

    @Override
    public MoMoDisbursementResponse disburse(MoMoDisbursementRequest request) {
        log.info("MoMoServiceImpl delegating disburse for payee: {}", request.getPayeePhoneNumber());
        return momoDisbursementClient.transfer(request);
    }

    @Override
    public MoMoTransactionStatus getTransactionStatus(String referenceId) {
        log.info("MoMoServiceImpl delegating getTransactionStatus for referenceId: {}", referenceId);
        return momoCollectionsClient.getCollectionStatus(referenceId);
    }
}
