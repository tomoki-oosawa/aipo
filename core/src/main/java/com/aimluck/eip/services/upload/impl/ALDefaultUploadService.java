package com.aimluck.eip.services.upload.impl;

import org.apache.jetspeed.services.upload.JetspeedUploadService;

public class ALDefaultUploadService extends JetspeedUploadService {

  @Override
  public int getSizeMax() {
    // byte -> MB を基準に変更
    String sizeMax = getProperties().getProperty("size.max", "1");
    try {
      return Integer.parseInt(sizeMax) * 1024 * 1024;
    } catch (NumberFormatException e) {
      return 1 * 1024 * 1024;
    }
  }
}
