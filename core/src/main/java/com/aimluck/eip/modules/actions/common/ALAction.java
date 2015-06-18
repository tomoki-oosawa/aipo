/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
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
package com.aimluck.eip.modules.actions.common;

import java.util.List;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * Aimluck EIP でユーザーからのリクエストを処理するインターフェイスです。 <br />
 * 
 */
public interface ALAction {

  /**
   * 
   * @param obj
   */
  public void setResultData(Object obj);

  /**
   * 
   * @param obj
   */
  public void addResultData(Object obj);

  /**
   * 
   * @param objList
   */
  public void setResultDataList(List<Object> objList);

  /**
   * 
   * @param msg
   */
  public void addErrorMessage(String msg);

  /**
   * 
   * @param msg
   */
  public void addErrorMessages(List<String> msgs);

  /**
   * 
   * @param msgs
   */
  public void setErrorMessages(List<String> msgs);

  /**
   * 
   * @param mode
   */
  public void setMode(String mode);

  /**
   * 
   * @return
   */
  public String getMode();

  /**
   * 
   * @param context
   */
  public void putData(RunData rundata, Context context);

}
