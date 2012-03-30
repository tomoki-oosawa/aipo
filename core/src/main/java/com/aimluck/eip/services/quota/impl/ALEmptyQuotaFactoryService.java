package com.aimluck.eip.services.quota.impl;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.services.quota.ALQuotaFactoryService;
import com.aimluck.eip.services.quota.ALQuotaHandler;

public class ALEmptyQuotaFactoryService extends ALQuotaFactoryService {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALEmptyQuotaHandler.class.getName());

  /**
   * @return
   */
  @Override
  public ALQuotaHandler getQuotaHandler() {
    return ALEmptyQuotaHandler.getInstance();
  }

}
