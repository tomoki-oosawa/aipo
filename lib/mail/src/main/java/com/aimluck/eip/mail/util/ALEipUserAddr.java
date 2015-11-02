/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.mail.util;

import java.util.ArrayList;
import java.util.List;

/**
 * ユーザーの情報（メールアドレス）を保持するクラスです。 <br />
 * 
 */
public class ALEipUserAddr {

  private Integer userid = null;

  private String pc_mailaddr = null;

  private String cell_mailaddr = null;

  public ALEipUserAddr() {

  }

  public void setUserId(Integer userid) {
    this.userid = Integer.valueOf(userid.intValue());
  }

  public Integer getUserId() {
    return userid;
  }

  public void setPcMailAddr(String pc_mailaddr) {
    if (pc_mailaddr == null) {
      return;
    }
    this.pc_mailaddr = pc_mailaddr;
  }

  public String getPcMailAddr() {
    return pc_mailaddr;
  }

  public void setCellMailAddr(String cell_mailaddr) {
    if (cell_mailaddr == null) {
      return;
    }
    this.cell_mailaddr = cell_mailaddr;
  }

  public String getCellMailAddr() {
    return cell_mailaddr;
  }

  public String[] getAddrs() {
    List<String> list = new ArrayList<String>();

    if (pc_mailaddr != null && !"".equals(pc_mailaddr)) {
      list.add(pc_mailaddr);
    }

    if (cell_mailaddr != null && !"".equals(cell_mailaddr)) {
      list.add(cell_mailaddr);
    }

    int size = list.size();
    if (size == 0) {
      return null;
    }

    String[] addrs = new String[size];
    for (int i = 0; i < size; i++) {
      addrs[i] = list.get(i);
    }
    return addrs;
  }
}
