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
package com.aimluck.eip.psml.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.access.DataContext;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.account.JetspeedUserProfile;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

public class PsmlDBUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(PsmlDBUtils.class.getName());

  public static String getMyHtmlPsml(RunData runData) {

    Map<String, String> map = new HashMap<String, String>();
    map.put(JetspeedUserProfile.USER_NAME_PROPERTY, ALEipUtils
      .getLoginName(runData));
    map.put(JetspeedUserProfile.MEDIA_TYPE_PROPERTY, "html");

    return getPsmlOne(map);
  }

  public static String getTemplateHtmlPsml(RunData runData) {
    return getPsmlOne(getTemplateMap());
  }

  public static String getPsmlOne(Map<String, String> map) {

    SelectQuery<JetspeedUserProfile> query =
      Database.query(JetspeedUserProfile.class);

    setProfileOneCriteria(query, map);

    JetspeedUserProfile psml = query.fetchSingle();
    return new String(psml.getProfile());
  }

  public static JetspeedUserProfile getTemplateHtmlProfile() {
    DataContext dataContext = DataContext.getThreadDataContext();
    return getTemplateHtmlProfile(dataContext);
  }

  public static JetspeedUserProfile getTemplateHtmlProfile(
      DataContext dataContext) {
    return getProfileOne(getTemplateMap(), dataContext);
  }

  public static List<JetspeedUserProfile> getAllUserHtmlProfile() {
    DataContext dataContext = DataContext.getThreadDataContext();
    return getAllUserHtmlProfile(dataContext);
  }

  public static List<JetspeedUserProfile> getAllUserHtmlProfile(
      DataContext dataContext) {
    return getProfile(getHtmlMap(), dataContext);
  }

  public static JetspeedUserProfile getProfileOne(Map<String, String> map,
      DataContext dataContext) {
    SelectQuery<JetspeedUserProfile> query =
      Database.query(dataContext, JetspeedUserProfile.class);

    setProfileOneCriteria(query, map);

    return query.fetchSingle();
  }

  public static List<JetspeedUserProfile> getProfile(Map<String, String> map,
      DataContext dataContext) {

    SelectQuery<JetspeedUserProfile> query =
      Database.query(dataContext, JetspeedUserProfile.class);

    if (map.containsKey(JetspeedUserProfile.USER_NAME_PROPERTY)) {
      setProfileOneCriteria(query, map);
    } else {
      setAllUserProfileCriteria(query, map);
    }

    return query.fetchList();
  }

  public static void setProfileOneCriteria(
      SelectQuery<JetspeedUserProfile> query, Map<String, String> map) {
    query.where(Operations.eq(JetspeedUserProfile.MEDIA_TYPE_PROPERTY, map
      .get(JetspeedUserProfile.MEDIA_TYPE_PROPERTY)), Operations.and(Operations
      .eq(JetspeedUserProfile.USER_NAME_PROPERTY, map
        .get(JetspeedUserProfile.USER_NAME_PROPERTY))));
  }

  public static void setAllUserProfileCriteria(
      SelectQuery<JetspeedUserProfile> query, Map<String, String> map) {
    query.where(Operations.eq(JetspeedUserProfile.MEDIA_TYPE_PROPERTY, map
      .get(JetspeedUserProfile.MEDIA_TYPE_PROPERTY)), Operations.and(Operations
      .notIn(
        JetspeedUserProfile.USER_NAME_PROPERTY,
        PsmlUtils.ADMIN_NAME,
        PsmlUtils.ANON_NAME,
        PsmlUtils.TEMPLATE_NAME)));
  }

  public static Map<String, String> getTemplateMap() {
    Map<String, String> map = new HashMap<String, String>();
    map.put(JetspeedUserProfile.USER_NAME_PROPERTY, PsmlUtils.TEMPLATE_NAME);
    map.put(JetspeedUserProfile.MEDIA_TYPE_PROPERTY, "html");
    return map;
  }

  public static Map<String, String> getHtmlMap() {
    Map<String, String> map = new HashMap<String, String>();
    map.put(JetspeedUserProfile.MEDIA_TYPE_PROPERTY, "html");
    return map;
  }

  /**
   * check and delete duplication or inconsistency
   * 
   * @param userName
   */
  public static void checkAndFixInconsistency(String userName) {
    DataContext dataContext = DataContext.getThreadDataContext();

    Map<String, String> map = new HashMap<String, String>();
    map.put(JetspeedUserProfile.USER_NAME_PROPERTY, userName);
    map.put(JetspeedUserProfile.MEDIA_TYPE_PROPERTY, "html");

    List<JetspeedUserProfile> list = getProfile(map, dataContext);

    if (list.size() > 1) {
      try {
        list.remove(0);
        Database.deleteAll(list);
        Database.commit();
      } catch (Exception e) {
        Database.rollback();
        logger.error("[PsmlDBUtils]", e);
      }
    }

  }
}
