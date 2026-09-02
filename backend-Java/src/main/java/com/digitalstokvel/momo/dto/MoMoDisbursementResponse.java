package com.digitalstokvel.momo.dto;

import com.digitalstokvel.momo.entity.MoMoTransactionStatus;

public class MoMoDisbursementResponse {

    private String referenceId;
    private MoMoTransactionStatus status;
    private String message;

    public MoMoDisbursementResponse() {
    }

    public MoMoDisbursementResponse(String referenceId, MoMoTransactionStatus status, String message) {
        this.referenceId = referenceId;
        this.status = status;
        this.message = message;
    }

    public static MoMoDisbursementResponse pending(String referenceId, String message) {
        return new MoMoDisbursementResponse(referenceId, MoMoTransactionStatus.PENDING, message);
    }

    public static MoMoDisbursementResponse successful(String referenceId, String message) {
        return new MoMoDisbursementResponse(referenceId, MoMoTransactionStatus.SUCCESSFUL, message);
    }

    public static MoMoDisbursementResponse failed(String referenceId, String message) {
        return new MoMoDisbursementResponse(referenceId, MoMoTransactionStatus.FAILED, message);
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public MoMoTransactionStatus getStatus() {
        return status;
    }

    public void setStatus(MoMoTransactionStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
