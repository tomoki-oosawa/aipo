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
package com.aimluck.eip.whatsnew;

import java.util.List;

import com.aimluck.eip.cayenne.om.portlet.EipTWhatsNew;

/**
 * 新着情報のアイテムを格納します。 <BR>
 * 
 */
public class WhatsNewContainer {

  /** List */
  private List<EipTWhatsNew> whatsnewList;

  private int type;

  /**
   * 
   * @param List
   */
  public void setList(List<EipTWhatsNew> list) {
    whatsnewList = list;
  }

  /**
   * 
   * @return
   */
  public List<EipTWhatsNew> getList() {
    return whatsnewList;
  }

  /**
   * 
   * @param int
   */
  public void setType(int i) {
    type = i;
  }

  /**
   * 
   * @return
   */
  public int getType() {
    return type;
  }

}
