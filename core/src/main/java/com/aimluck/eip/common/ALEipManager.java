/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.cayenne.om.account.EipMPost;
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

  /** 会社リスト */
  private final Map<String, Map<Integer, ALEipCompany>> companysMap =
    new LinkedHashMap<String, Map<Integer, ALEipCompany>>();

  /** 部署リスト */
  private final Map<String, Map<Integer, ALEipPost>> postsMap =
    new LinkedHashMap<String, Map<Integer, ALEipPost>>();

  /** 役職リスト */
  private final Map<String, Map<Integer, ALEipPosition>> positionsMap =
    new LinkedHashMap<String, Map<Integer, ALEipPosition>>();

  /**
   *
   *
   */
  private ALEipManager() {
    initCompany();
    initPost();
    initPosition();
  }

  /**
   * 
   * @return
   */
  public static ALEipManager getInstance() {
    return manager;
  }

  /**
   *
   */
  private void initCompany() {
    companysMap.clear();
  }

  /**
   *
   *
   */
  private void initPost() {
    postsMap.clear();
  }

  /**
   *
   *
   */
  private void initPosition() {
    positionsMap.clear();
  }

  /**
   *
   */
  public void reloadCompany() {
    String orgId = Database.getDomainName();
    synchronized (companysMap) {
      try {
        List<EipMCompany> list = Database.query(EipMCompany.class).fetchList();
        Map<Integer, ALEipCompany> companyMap = companysMap.remove(orgId);
        if (companyMap == null) {
          companyMap = new LinkedHashMap<Integer, ALEipCompany>();
        } else {
          companyMap.clear();
        }
        for (EipMCompany record : list) {
          ALEipCompany company = new ALEipCompany();
          company.initField();
          company.setCompanyId(record.getCompanyId().intValue());
          company.setCompanyName(record.getCompanyName());
          companyMap.put(record.getCompanyId(), company);
        }

        companysMap.put(orgId, companyMap);
      } catch (Exception e) {
        logger.error("[" + orgId + ":ALEipManager]", e);
      }
    }
  }

  /**
   * @param orgId
   * 
   * 
   */
  public void reloadPost() {
    String orgId = Database.getDomainName();
    synchronized (postsMap) {
      try {
        List<EipMPost> list = Database.query(EipMPost.class).fetchList();
        Collections.sort(list, new Comparator<EipMPost>() {
          public int compare(EipMPost l1, EipMPost l2) {
            return (l1).getPostName().compareTo((l2).getPostName());
          }
        });
        Map<Integer, ALEipPost> postMap = postsMap.remove(orgId);
        if (postMap == null) {
          postMap = new LinkedHashMap<Integer, ALEipPost>();
        } else {
          postMap.clear();
        }
        for (EipMPost record : list) {
          ALEipPost post = new ALEipPost();
          post.initField();
          post.setPostId(record.getPostId().intValue());
          post.setPostName(record.getPostName());
          post.setGroupName(record.getGroupName());
          postMap.put(record.getPostId(), post);
        }

        postsMap.put(orgId, postMap);
      } catch (Exception e) {
        logger.error("[" + orgId + ":ALEipManager]", e);
      }
    }
  }

  /**
   * @param orgId
   * 
   * 
   */
  public void reloadPosition() {
    String orgId = Database.getDomainName();
    synchronized (positionsMap) {
      try {
        List<EipMPosition> list =
          Database.query(EipMPosition.class).fetchList();

        Map<Integer, ALEipPosition> positionMap = positionsMap.remove(orgId);
        if (positionMap == null) {
          positionMap = new LinkedHashMap<Integer, ALEipPosition>();
        } else {
          positionMap.clear();
        }
        for (EipMPosition record : list) {
          ALEipPosition position = new ALEipPosition();
          position.initField();
          position.setPositionId(record.getPositionId().intValue());
          position.setPositionName(record.getPositionName());
          positionMap.put(record.getPositionId(), position);
        }

        positionsMap.put(orgId, positionMap);
      } catch (Exception e) {
        logger.error("[" + orgId + ":ALEipManager]", e);
      }
    }
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipCompany> getCompanyMap() {
    synchronized (companysMap) {
      String orgId = Database.getDomainName();
      if (!companysMap.containsKey(orgId)) {
        reloadCompany();
        if (!companysMap.containsKey(orgId)) {
          return null;
        } else {
          return companysMap.get(orgId);
        }
      }
      return companysMap.get(orgId);
    }
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    synchronized (postsMap) {
      String orgId = Database.getDomainName();
      if (!postsMap.containsKey(orgId)) {
        reloadPost();
        if (!postsMap.containsKey(orgId)) {
          return null;
        } else {
          return postsMap.get(orgId);
        }
      }
      return postsMap.get(orgId);
    }
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPosition> getPositionMap() {
    synchronized (positionsMap) {
      String orgId = Database.getDomainName();
      if (!positionsMap.containsKey(orgId)) {
        reloadPosition();
        if (!positionsMap.containsKey(orgId)) {
          return null;
        } else {
          return positionsMap.get(orgId);
        }
      }
      return positionsMap.get(orgId);
    }
  }

}
