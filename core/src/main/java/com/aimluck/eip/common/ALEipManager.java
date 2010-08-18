/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.access.DataContext;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;

import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.DatabaseOrmService;

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

  /** The JetspeedRunData Service. */
  private JetspeedRunDataService runDataService = null;

  /** 会社リスト */
  private final Map<String, Map<Integer, ALEipCompany>> companysMap = new LinkedHashMap<String, Map<Integer, ALEipCompany>>();

  /** 部署リスト */
  private final Map<String, Map<Integer, ALEipPost>> postsMap = new LinkedHashMap<String, Map<Integer, ALEipPost>>();

  /** 役職リスト */
  private final Map<String, Map<Integer, ALEipPosition>> positionsMap = new LinkedHashMap<String, Map<Integer, ALEipPosition>>();

  /**
   *
   *
   */
  private ALEipManager() {
    this.runDataService = (JetspeedRunDataService) TurbineServices
      .getInstance().getService(RunDataService.SERVICE_NAME);
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

    String org_id = null;
    List<String> org_list = DatabaseOrmService.getInstance().getOrgKeys();
    Iterator<String> iter = org_list.iterator();
    while (iter.hasNext()) {
      try {
        /** 会社リスト */
        Map<Integer, ALEipCompany> companyMap = new LinkedHashMap<Integer, ALEipCompany>();

        org_id = iter.next();
        DataContext dataContext = DataContext.createDataContext(org_id);
        List<EipMCompany> list = Database.query(dataContext, EipMCompany.class)
          .perform();
        for (EipMCompany record : list) {
          ALEipCompany company = new ALEipCompany();
          company.initField();
          company.setCompanyId(record.getCompanyId().intValue());
          company.setCompanyName(record.getCompanyName());
          companyMap.put(record.getCompanyId(), company);
        }
        companysMap.put(org_id, companyMap);
      } catch (Exception e) {
        logger.error("[" + org_id + ":ALEipManager]", e);
      }
    }
  }

  /**
   *
   *
   */
  private void initPost() {
    postsMap.clear();

    String org_id = null;
    List<String> org_list = DatabaseOrmService.getInstance().getOrgKeys();
    Iterator<String> iter = org_list.iterator();
    while (iter.hasNext()) {
      try {
        /** 部署リスト */
        Map<Integer, ALEipPost> postMap = new LinkedHashMap<Integer, ALEipPost>();

        org_id = iter.next();
        DataContext dataContext = DataContext.createDataContext(org_id);
        List<EipMPost> list = Database.query(dataContext, EipMPost.class)
          .perform();
        Collections.sort(list, new Comparator<EipMPost>() {
          public int compare(EipMPost l1, EipMPost l2) {
            return (l1).getPostName().compareTo((l2).getPostName());
          }
        });
        for (EipMPost record : list) {
          ALEipPost post = new ALEipPost();
          post.initField();
          post.setPostId(record.getPostId().intValue());
          post.setPostName(record.getPostName());
          post.setGroupName(record.getGroupName());
          postMap.put(record.getPostId(), post);
        }

        postsMap.put(org_id, postMap);
      } catch (Exception e) {
        logger.error("[" + org_id + ":ALEipManager]", e);
      }
    }
  }

  /**
   *
   *
   */
  private void initPosition() {
    positionsMap.clear();

    String org_id = null;
    List<String> org_list = DatabaseOrmService.getInstance().getOrgKeys();
    Iterator<String> iter = org_list.iterator();
    while (iter.hasNext()) {
      try {
        /** 役職リスト */
        Map<Integer, ALEipPosition> positionMap = new LinkedHashMap<Integer, ALEipPosition>();

        org_id = iter.next();
        DataContext dataContext = DataContext.createDataContext(org_id);
        List<EipMPosition> list = Database.query(dataContext,
          EipMPosition.class).perform();
        for (EipMPosition record : list) {
          ALEipPosition position = new ALEipPosition();
          position.initField();
          position.setPositionId(record.getPositionId().intValue());
          position.setPositionName(record.getPositionName());
          positionMap.put(record.getPositionId(), position);
        }
        positionsMap.put(org_id, positionMap);
      } catch (Exception e) {
        logger.error("[" + org_id + ":ALEipManager]", e);
      }
    }
  }

  /**
   *
   */
  public void reloadCompany() {
    String org_id = "";
    synchronized (companysMap) {
      try {
        org_id = DatabaseOrmService.getInstance().getOrgId(getRunData());
        List<EipMCompany> list = Database.query(EipMCompany.class).perform();
        Map<Integer, ALEipCompany> companyMap = companysMap.remove(org_id);
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

        companysMap.put(org_id, companyMap);
      } catch (Exception e) {
        logger.error("[" + org_id + ":ALEipManager]", e);
      }
    }
  }

  /**
   *
   *
   */
  public void reloadPost() {
    String org_id = "";
    synchronized (postsMap) {
      try {
        org_id = DatabaseOrmService.getInstance().getOrgId(getRunData());
        List<EipMPost> list = Database.query(EipMPost.class).perform();
        Collections.sort(list, new Comparator<EipMPost>() {
          public int compare(EipMPost l1, EipMPost l2) {
            return (l1).getPostName().compareTo((l2).getPostName());
          }
        });
        Map<Integer, ALEipPost> postMap = postsMap.remove(org_id);
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

        postsMap.put(org_id, postMap);
      } catch (Exception e) {
        logger.error("[" + org_id + ":ALEipManager]", e);
      }
    }
  }

  /**
   *
   *
   */
  public void reloadPosition() {
    String org_id = "";
    synchronized (positionsMap) {
      try {
        org_id = DatabaseOrmService.getInstance().getOrgId(getRunData());
        List<EipMPosition> list = Database.query(EipMPosition.class).perform();

        Map<Integer, ALEipPosition> positionMap = positionsMap.remove(org_id);
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

        positionsMap.put(org_id, positionMap);
      } catch (Exception e) {
        logger.error("[" + org_id + ":ALEipManager]", e);
      }
    }
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipCompany> getCompanyMap() {
    synchronized (companysMap) {
      String org_id = DatabaseOrmService.getInstance().getOrgId(getRunData());
      if (!companysMap.containsKey(org_id)) {
        reloadCompany();
        if (!companysMap.containsKey(org_id)) {
          return null;
        } else {
          return companysMap.get(org_id);
        }
      }
      return companysMap.get(org_id);
    }
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    synchronized (postsMap) {
      String org_id = DatabaseOrmService.getInstance().getOrgId(getRunData());
      if (!postsMap.containsKey(org_id)) {
        reloadPost();
        if (!postsMap.containsKey(org_id)) {
          return null;
        } else {
          return postsMap.get(org_id);
        }
      }
      return postsMap.get(org_id);
    }
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPosition> getPositionMap() {
    synchronized (positionsMap) {
      String org_id = DatabaseOrmService.getInstance().getOrgId(getRunData());
      if (!positionsMap.containsKey(org_id)) {
        reloadPosition();
        if (!positionsMap.containsKey(org_id)) {
          return null;
        } else {
          return positionsMap.get(org_id);
        }
      }
      return positionsMap.get(org_id);
    }
  }

  protected JetspeedRunData getRunData() {
    JetspeedRunData rundata = null;
    if (this.runDataService != null) {
      rundata = this.runDataService.getCurrentRunData();
    }
    return rundata;
  }
}
