package com.digitalstokvel.momo.service;

import com.digitalstokvel.momo.dto.MoMoDisbursementRequest;
import com.digitalstokvel.momo.dto.MoMoDisbursementResponse;
import com.digitalstokvel.momo.dto.MoMoRequest;
import com.digitalstokvel.momo.dto.MoMoResponse;
import com.digitalstokvel.momo.entity.MoMoTransactionStatus;

public interface MoMoService {

    /**
     * Initiates a Request-To-Pay (collection) from a member's mobile money wallet.
     */
    MoMoResponse requestToPay(MoMoRequest request);

    /**
     * Initiates a disbursement / transfer to a member's mobile money wallet.
     */
    MoMoDisbursementResponse disburse(MoMoDisbursementRequest request);

    /**
     * Checks the current transaction status from the MoMo API gateway.
     */
    MoMoTransactionStatus getTransactionStatus(String referenceId);
}
