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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.cayenne.om.account.EipTAclPortletFeature;
import com.aimluck.eip.cayenne.om.account.EipTAclRole;
import com.aimluck.eip.cayenne.om.account.EipTAclUserRoleMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.VTurbineUserLite;
import com.aimluck.eip.http.HttpServletRequestLocator;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SQLTemplate;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.customlocalization.ALLocalizationTool;

/**
 * ユーザー情報、会社情報、部署情報、役職情報をメモリ上に保持するクラスです。 <br />
 *
 */
public class ALEipManager {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALEipManager.class.getName());

  /** Singleton */
  private static ALEipManager manager = new ALEipManager();

  /** ユーザーキー */
  private static String USERS_KEY = "com.aimluck.eip.common.ALEipManager.users";

  /** 会社キー */
  private static String COMPANIES_KEY =
    "com.aimluck.eip.common.ALEipManager.companies";

  /** 部署キー */
  private static String POSTS_KEY = "com.aimluck.eip.common.ALEipManager.posts";

  /** 役職キー */
  private static String POSITIONS_KEY =
    "com.aimluck.eip.common.ALEipManager.positions";

  /** ACLキー */
  private static String ACL_KEY = "com.aimluck.eip.common.ALEipManager.acls";

  /** eip_m_config prefix key */
  private static String CONFIG_PREFIX = "config_";

  /** container_config prefix key */
  private static String CONTAINER_PREFIX = "container_";

  /** turbineUser prefix key */
  private static String USER_PREFIX = "turbineUser_";

  /** psml prefix key */
  private static String PSML_PREFIX = "psml";

  /** LocalizationTool */
  private static String LOCALIZATION_PREFIX = "Localization";

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

  public ALEipUser getUser(Integer userId) {
    Map<Integer, ALEipUser> users = getUsers(Arrays.asList(userId));
    return users.get(userId);
  }

  @SuppressWarnings("unchecked")
  public Map<Integer, ALEipUser> getUsers(List<Integer> users) {
    Map<Integer, ALEipUser> results =
      new HashMap<Integer, ALEipUser>(users.size());
    Map<Integer, ALEipUser> map = new HashMap<Integer, ALEipUser>(users.size());

    List<Integer> fetchUsers = new ArrayList<Integer>(users.size());

    HttpServletRequest request = HttpServletRequestLocator.get();

    if (request != null) {
      map = (Map<Integer, ALEipUser>) request.getAttribute(USERS_KEY);
      if (map != null) {
        for (Integer userId : users) {
          ALEipUser user = map.get(userId);
          if (user != null) {
            results.put(userId, user);
          } else {
            fetchUsers.add(userId);
          }
        }
      } else {
        map = new HashMap<Integer, ALEipUser>(users.size());
        fetchUsers.addAll(users);
      }
    } else {
      fetchUsers.addAll(users);
    }

    if (fetchUsers.size() > 0) {

      StringBuilder select = new StringBuilder();

      select.append("SELECT");
      select.append(" turbine_user.user_id,");
      select.append(" turbine_user.last_name,");
      select.append(" turbine_user.first_name,");
      select.append(" turbine_user.login_name,");
      select.append(" turbine_user.has_photo,");
      select.append(" turbine_user.photo_modified");

      StringBuilder body = new StringBuilder();
      body.append(" FROM turbine_user WHERE ");
      body.append(" turbine_user.user_id IN (");
      boolean isFirst = true;
      for (Integer num : fetchUsers) {
        if (!isFirst) {
          body.append(",");

        }
        body.append(num.intValue());
        isFirst = false;
      }
      body.append(")");

      SQLTemplate<VTurbineUserLite> query =
        Database.sql(VTurbineUserLite.class, select.toString()
          + body.toString());
      List<VTurbineUserLite> list = query.fetchList();

      for (VTurbineUserLite model : list) {
        ALEipUser eipUser = new ALEipUser();
        eipUser.initField();
        eipUser.setAliasName(model.getFirstName(), model.getLastName());
        eipUser.setName(model.getLoginName());
        eipUser.setUserId(model.getUserId());
        eipUser.setHasPhoto("T".equals(model.getHasPhoto())
          || "N".equals(model.getHasPhoto()));
        eipUser.setPhotoModified(model.getPhotoModified() != null ? model
          .getPhotoModified()
          .getTime() : 0);

        results.put(model.getUserId(), eipUser);
        map.put(model.getUserId(), eipUser);
      }
    }

    // requestに登録
    if (request != null) {
      request.setAttribute(USERS_KEY, map);
    }

    return results;
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
      SelectQuery<EipMPost> query = Database.query(EipMPost.class);
      query.orderAscending(EipMPost.SORT_PROPERTY);
      List<EipMPost> list = query.fetchList();
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
      List<EipMPosition> list =
        Database.query(EipMPosition.class).orderAscending(
          EipMPosition.SORT_PROPERTY).fetchList();
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

  public Map<String, EipTAclRole> getAclRoleMap(int userId) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      // requestから取得
      @SuppressWarnings("unchecked")
      Map<String, EipTAclRole> map =
        (Map<String, EipTAclRole>) request.getAttribute(ACL_KEY);
      if (map != null) {
        return map;
      }
    }
    // データベースから新規取得
    Map<String, EipTAclRole> roleMap = new HashMap<String, EipTAclRole>();

    Expression exp =
      ExpressionFactory.matchDbExp(EipTAclRole.EIP_TACL_USER_ROLE_MAPS_PROPERTY
        + "."
        + EipTAclUserRoleMap.TURBINE_USER_PROPERTY
        + "."
        + TurbineUser.USER_ID_PK_COLUMN, userId);

    List<EipTAclRole> roleList =
      Database.query(EipTAclRole.class, exp).fetchList();

    List<EipTAclPortletFeature> featureList =
      Database.query(EipTAclPortletFeature.class).fetchList();

    Map<Integer, String> _map = new HashMap<Integer, String>();

    for (EipTAclPortletFeature feature : featureList) {
      _map.put(feature.getFeatureId(), feature.getFeatureName());
    }

    String _featureName;
    for (EipTAclRole _role : roleList) {
      _featureName = _map.get(_role.getFeatureId());
      roleMap.put(_featureName, _role);
    }

    // requestに登録
    if (request != null) {
      request.setAttribute(ACL_KEY, roleMap);
    }
    return roleMap;
  }

  public Object getConfig(String name) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      return request.getAttribute(CONFIG_PREFIX + name);
    } else {
      return null;
    }
  }

  public void setConfig(String name, Object obj) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute(CONFIG_PREFIX + name, obj);
    }
  }

  public Object getContainerConfig(String name) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      return request.getAttribute(CONTAINER_PREFIX + name);
    } else {
      return null;
    }
  }

  public void setContainerConfig(String name, Object obj) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute(CONTAINER_PREFIX + name, obj);
    }
  }

  public Object getTurbineUser(int userId) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      return request.getAttribute(USER_PREFIX + String.valueOf(userId));
    } else {
      return null;
    }
  }

  public void setTurbineUser(int userId, Object obj) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute(USER_PREFIX + String.valueOf(userId), obj);
    }
  }

  public Object getTurbineUser(String loginName) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      return request.getAttribute(USER_PREFIX + loginName);
    } else {
      return null;
    }
  }

  public void setTurbineUser(String loginName, Object obj) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute(USER_PREFIX + loginName, obj);
    }
  }

  public Object getUserProfile(ProfileLocator locator) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    String key = getUserProfileKey(locator);
    if (request != null && key != null) {
      return request.getAttribute(key);
    } else {
      return null;
    }
  }

  public void setUserProfile(ProfileLocator locator, Object obj) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    String key = getUserProfileKey(locator);
    if (request != null && key != null) {
      request.setAttribute(key, obj);
    }
  }

  public void removeProfile(ProfileLocator locator) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    String key = getUserProfileKey(locator);
    if (request != null && key != null) {
      request.removeAttribute(key);
    }
  }

  private String getUserProfileKey(ProfileLocator locator) {

    StringBuffer buffer = new StringBuffer(PSML_PREFIX);
    String userName = null;
    JetspeedUser user = locator.getUser();

    if (user != null) {
      userName = user.getUserName();
    } else {
      return null;
    }

    addKey(userName, buffer);
    addKey(locator.getMediaType(), buffer);
    addKey(locator.getLanguage(), buffer);
    addKey(locator.getCountry(), buffer);
    addKey(locator.getName(), buffer);

    return buffer.toString();
  }

  private void addKey(String key, StringBuffer buffer) {
    if (key != null && key.length() > 0) {
      buffer.append("_").append(key);
    }
  }

  public ALLocalizationTool getLocalizationTool() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      Object obj = request.getAttribute(LOCALIZATION_PREFIX);
      if (obj != null) {
        return (ALLocalizationTool) obj;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  public void setLocalizationTool(ALLocalizationTool tool) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute(LOCALIZATION_PREFIX, tool);
    }
  }

}
