/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.http.HttpServletRequestLocator;
import com.aimluck.eip.orm.Database;

/**
 * 会社情報、部署情報、役職情報をメモリ上に保持するクラスです。 <br />
 * 
 */
public class ALEipManager {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALEipManager.class.getName());

  /** Singleton */
  private static ALEipManager manager = new ALEipManager();

  /** 会社キー */
  private static String COMPANIES_KEY =
    "com.aimluck.eip.common.ALEipManager.companies";

  /** 部署キー */
  private static String POSTS_KEY = "com.aimluck.eip.common.ALEipManager.posts";

  /** 役職キー */
  private static String POSITIONS_KEY =
    "com.aimluck.eip.common.ALEipManager.positions";

  /**
   * 
   * @return
   */
  public static ALEipManager getInstance() {
    return manager;
  }

  /**
   * 会社情報を更新します。
   */
  public void reloadCompany() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute(COMPANIES_KEY, null);
    }
  }

  /**
   * 部署情報を更新します。
   */
  public void reloadPost() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute(POSTS_KEY, null);
    }
  }

  /**
   * 役職情報を更新します。
   */
  public void reloadPosition() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute(POSITIONS_KEY, null);
    }
  }

  /**
   * 会社情報を返します。
   * 
   * @return
   */
  public Map<Integer, ALEipCompany> getCompanyMap() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      // requestから取得
      @SuppressWarnings("unchecked")
      Map<Integer, ALEipCompany> map =
        (Map<Integer, ALEipCompany>) request.getAttribute(COMPANIES_KEY);
      if (map != null) {
        return map;
      }
    }
    // データベースから新規取得
    Map<Integer, ALEipCompany> companyMap =
      new LinkedHashMap<Integer, ALEipCompany>();
    try {
      List<EipMCompany> list = Database.query(EipMCompany.class).fetchList();
      for (EipMCompany record : list) {
        ALEipCompany company = new ALEipCompany();
        company.initField();
        company.setCompanyId(record.getCompanyId().intValue());
        company.setCompanyName(record.getCompanyName());
        companyMap.put(record.getCompanyId(), company);
      }
    } catch (Exception e) {
      logger.error("[" + Database.getDomainName() + ":ALEipManager]", e);
    }
    // requestに登録
    if (request != null) {
      request.setAttribute(COMPANIES_KEY, companyMap);
    }
    return companyMap;
  }

  /**
   * 部署情報を返します。
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      // requestから取得
      @SuppressWarnings("unchecked")
      Map<Integer, ALEipPost> map =
        (Map<Integer, ALEipPost>) request.getAttribute(POSTS_KEY);
      if (map != null) {
        return map;
      }
    }
    // データベースから新規取得
    Map<Integer, ALEipPost> postMap = new LinkedHashMap<Integer, ALEipPost>();
    try {
      List<EipMPost> list = Database.query(EipMPost.class).fetchList();
      for (EipMPost record : list) {
        ALEipPost post = new ALEipPost();
        post.initField();
        post.setPostId(record.getPostId().intValue());
        post.setPostName(record.getPostName());
        post.setGroupName(record.getGroupName());
        postMap.put(record.getPostId(), post);
      }
    } catch (Exception e) {
      logger.error("[" + Database.getDomainName() + ":ALEipManager]", e);
    }
    // requestに登録
    if (request != null) {
      request.setAttribute(POSTS_KEY, postMap);
    }
    return postMap;
  }

  /**
   * 役職情報を返します。
   * 
   * @return
   */
  public Map<Integer, ALEipPosition> getPositionMap() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      // requestから取得
      @SuppressWarnings("unchecked")
      Map<Integer, ALEipPosition> map =
        (Map<Integer, ALEipPosition>) request.getAttribute(POSITIONS_KEY);
      if (map != null) {
        return map;
      }
    }
    // データベースから新規取得
    Map<Integer, ALEipPosition> positionMap =
      new LinkedHashMap<Integer, ALEipPosition>();
    try {
      List<EipMPosition> list = Database.query(EipMPosition.class).fetchList();
      for (EipMPosition record : list) {
        ALEipPosition position = new ALEipPosition();
        position.initField();
        position.setPositionId(record.getPositionId().intValue());
        position.setPositionName(record.getPositionName());
        positionMap.put(record.getPositionId(), position);
      }
    } catch (Exception e) {
      logger.error("[" + Database.getDomainName() + ":ALEipManager]", e);
    }
    // requestに登録
    if (request != null) {
      request.setAttribute(POSITIONS_KEY, positionMap);
    }
    return positionMap;
  }

}
