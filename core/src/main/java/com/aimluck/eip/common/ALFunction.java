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
package com.aimluck.eip.common;

/**
 * ポートレットの情報を表すクラスです。 <br />
 * 
 */
public class ALFunction {

  private String mode;

  private String image;

  private String caption;

  private String before_function;

  private String after_function;

  private boolean isScreen;

  /**
   * @return
   */
  public String getCaption() {
    return caption;
  }

  /**
   * @return
   */
  public String getImage() {
    return image;
  }

  /**
   * @return
   */
  public String getMode() {
    return mode;
  }

  /**
   * @return
   */
  public String getBeforeFunction() {
    return before_function;
  }

  /**
   * @return
   */
  public String getAfterFunction() {
    return after_function;
  }

  /**
   * @param string
   */
  public void setBeforeFunction(String string) {
    before_function = string;
  }

  /**
   * @param string
   */
  public void setAfterFunction(String string) {
    after_function = string;
  }

  /**
   * @param string
   */
  public void setCaption(String string) {
    caption = string;
  }

  /**
   * @param string
   */
  public void setImage(String string) {
    image = string;
  }

  /**
   * @param string
   */
  public void setMode(String string) {
    mode = string;
  }

  public boolean isScreen() {
    return isScreen;
  }

  public void setScreen(boolean isScreen) {
    this.isScreen = isScreen;
  }

}
