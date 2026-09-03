package com.digitalstokvel.momo;

import com.digitalstokvel.api.StokvelService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MomoStatusPoller {
  private final StokvelService service;
  public MomoStatusPoller(StokvelService service) { this.service = service; }
  @Scheduled(fixedDelayString = "${momo.poll-interval-ms:5000}")
  public void refreshPendingTransactions() { service.poll(); }
}
