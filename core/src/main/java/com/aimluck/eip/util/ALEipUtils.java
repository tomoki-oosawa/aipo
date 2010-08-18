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
package com.aimluck.eip.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.CapabilityMapFactory;
import org.apache.jetspeed.modules.actions.controls.Restore;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.registry.MediaTypeEntry;
import org.apache.jetspeed.om.security.UserIdPrincipal;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.portal.security.portlets.PortletWrapper;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.util.MimeType;
import org.apache.jetspeed.util.template.BaseJetspeedLink;
import org.apache.jetspeed.util.template.ContentTemplateLink;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.account.AipoLicense;
import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALMyGroups;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;

/**
 * Aimluck EIP のユーティリティクラスです。 <br />
 * 
 */
public class ALEipUtils {

  public static final String dummy_user_head = "dummy_";

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALEipUtils.class.getName());

  /**
   * セッション変数に値を格納します。 <br />
   * セッション変数は各ポートレット毎に管理されます。
   * 
   * @param rundata
   * @param context
   * @param key
   *          セッション変数名
   * @param value
   *          セッション変数の値
   */
  public static void setTemp(RunData rundata, Context context, String key,
      String value) {

    JetspeedRunData jdata = (JetspeedRunData) rundata;
    VelocityPortlet portlet = ((VelocityPortlet) context
      .get(JetspeedResources.PATH_PORTLET_KEY));

    if (portlet == null) {
      // Screen の場合
      String js_peid = rundata.getParameters().getString(
        JetspeedResources.PATH_PORTLETID_KEY);
      jdata.getUser().setTemp(
        new StringBuffer().append(js_peid).append(key).toString(), value);
    } else {
      // Action の場合
      jdata.getUser().setTemp(
        new StringBuffer().append(portlet.getID()).append(key).toString(),
        value);
    }
  }

  /**
   * セッション変数を削除します。 <br />
   * セッション変数は各ポートレット毎に管理されます。
   * 
   * @param rundata
   * @param context
   * @param key
   *          セッション変数名
   */
  public static void removeTemp(RunData rundata, Context context, String key) {
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    VelocityPortlet portlet = ((VelocityPortlet) context
      .get(JetspeedResources.PATH_PORTLET_KEY));
    if (portlet == null) {
      // Screen の場合
      String js_peid = rundata.getParameters().getString(
        JetspeedResources.PATH_PORTLETID_KEY);
      jdata.getUser().removeTemp(
        new StringBuffer().append(js_peid).append(key).toString());
    } else {
      // Action の場合
      jdata.getUser().removeTemp(
        new StringBuffer().append(portlet.getID()).append(key).toString());
    }
  }

  public static void removeTemp(RunData rundata, Context context,
      List<String> list) {
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    VelocityPortlet portlet = ((VelocityPortlet) context
      .get(JetspeedResources.PATH_PORTLET_KEY));
    int size = list.size();
    if (portlet == null) {
      // Screen の場合
      String js_peid = rundata.getParameters().getString(
        JetspeedResources.PATH_PORTLETID_KEY);
      for (int i = 0; i < size; i++) {
        jdata.getUser().removeTemp(
          new StringBuffer().append(js_peid).append(list.get(i).toString())
            .toString());
      }
    } else {
      // Action の場合
      String peid = portlet.getID();
      for (int i = 0; i < size; i++) {
        jdata.getUser().removeTemp(
          new StringBuffer().append(peid).append(list.get(i).toString())
            .toString());
      }
    }
  }

  /**
   * セッション変数の値を取得します。 <br />
   * セッション変数は各ポートレット毎に管理されます。
   * 
   * @param rundata
   * @param context
   * @param key
   *          セッション変数名
   * @return セッション変数の値
   */
  public static String getTemp(RunData rundata, Context context, String key) {
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    Object obj = null;
    VelocityPortlet portlet = ((VelocityPortlet) context
      .get(JetspeedResources.PATH_PORTLET_KEY));
    if (portlet == null) {
      // Screen の場合
      String js_peid = rundata.getParameters().getString(
        JetspeedResources.PATH_PORTLETID_KEY);
      obj = jdata.getUser().getTemp(
        new StringBuffer().append(js_peid).append(key).toString());
    } else {
      // Action の場合
      obj = jdata.getUser().getTemp(
        new StringBuffer().append(portlet.getID()).append(key).toString());
    }
    return (obj == null) ? null : obj.toString();
  }

  /**
   * セッションに保存されているエンティティIDを整数値として返します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static int getEntityId(RunData rundata, Context context) {
    int entity_id = 0;
    String entity_id_str = ALEipUtils.getTemp(rundata, context,
      ALEipConstants.ENTITY_ID);
    try {
      entity_id = Integer.parseInt(entity_id_str);
    } catch (Exception e) {
      entity_id = 0;
    }
    return entity_id;
  }

  /**
   * ユーザーIDを返します。
   * 
   * @param rundata
   * @return ユーザーID
   */
  public static int getUserId(RunData rundata) {
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    String id = jdata.getJetspeedUser().getUserId();
    return Integer.parseInt(id);
  }

  /**
   * ポートレットを返します。
   * 
   * @param rundata
   * @param context
   * @return 自ポートレット
   */
  public static VelocityPortlet getPortlet(RunData rundata, Context context) {
    return ((VelocityPortlet) context.get(JetspeedResources.PATH_PORTLET_KEY));
  }

  /**
   * 指定したポートレット ID を持つポートレットのオブジェクトを取得します。
   * 
   * @param rundata
   * @param portletId
   * @return 自ポートレット
   */
  public static Portlet getPortlet(RunData rundata, String portletId) {
    try {
      Portlets portlets = ((JetspeedRunData) rundata).getProfile()
        .getDocument().getPortlets();
      if (portlets == null) {
        return null;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return null;
      }

      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getId().equals(portletId)) {
            PortletWrapper wrapper = (PortletWrapper) PortletFactory
              .getPortlet(entries[j]);
            if (wrapper != null) {
              return wrapper.getPortlet();
            } else {
              return null;
            }
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
    return null;
  }

  /**
   * リクエストが自ポートレットに対するものであるかを返します。 <br />
   * true となる場合、そのポートレットに対するフォーム送信となります。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static boolean isMatch(RunData rundata, Context context) {
    VelocityPortlet portlet = getPortlet(rundata, context);

    if (portlet == null) {
      // Screen の場合
      return true;
    }
    String peid1 = portlet.getID();
    String peid2 = rundata.getParameters().getString(
      JetspeedResources.PATH_PORTLETID_KEY);
    if (peid1 == null || peid2 == null) {
      return false;
    }
    return peid1.equals(peid2);
  }

  /**
   * 指定されたグループに所属するユーザーを取得します。
   * 
   * @param groupname
   *          グループ名
   * @return ALEipUser の List
   */
  public static List<ALEipUser> getUsers(String groupname) {
    List<ALEipUser> list = new ArrayList<ALEipUser>();

    // SQLの作成
    StringBuffer statement = new StringBuffer();
    statement.append("SELECT DISTINCT ");
    statement
      .append("  B.USER_ID, B.LOGIN_NAME, B.FIRST_NAME, B.LAST_NAME, D.POSITION ");
    statement.append("FROM TURBINE_USER_GROUP_ROLE as A ");
    statement.append("LEFT JOIN TURBINE_USER as B ");
    statement.append("  on A.USER_ID = B.USER_ID ");
    statement.append("LEFT JOIN TURBINE_GROUP as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("LEFT JOIN EIP_M_USER_POSITION as D ");
    statement.append("  on A.USER_ID = D.USER_ID ");
    statement.append("WHERE B.USER_ID > 3 AND B.DISABLED = 'F'");
    statement.append(" AND C.GROUP_NAME = #bind($groupName) ");
    statement.append("ORDER BY D.POSITION");
    String query = statement.toString();

    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    @SuppressWarnings("deprecation")
    SQLTemplate rawSelect = new SQLTemplate(TurbineUser.class, query, true);
    rawSelect.setParameters(Collections.singletonMap("groupName", groupname));
    rawSelect.setFetchingDataRows(true);
    List<?> ulist = dataContext.performQuery(rawSelect);

    int recNum = ulist.size();

    ALEipUser user;
    DataRow dataRow;
    // ユーザデータを作成し、返却リストへ格納
    for (int j = 0; j < recNum; j++) {
      dataRow = (DataRow) ulist.get(j);
      user = new ALEipUser();
      user.initField();
      user.setUserId(((Integer) ALEipUtils.getObjFromDataRow(dataRow,
        TurbineUser.USER_ID_PK_COLUMN)).intValue());
      user.setName((String) ALEipUtils.getObjFromDataRow(dataRow,
        TurbineUser.LOGIN_NAME_COLUMN));
      user.setAliasName((String) ALEipUtils.getObjFromDataRow(dataRow,
        TurbineUser.FIRST_NAME_COLUMN), (String) ALEipUtils.getObjFromDataRow(
        dataRow, TurbineUser.LAST_NAME_COLUMN));
      list.add(user);
    }
    return list;
  }

  /**
   * 指定されたグループに所属するユーザーのIDを取得します。
   * 
   * @param groupname
   *          グループ名
   * @return Integer の List
   */
  public static List<Integer> getUserIds(String groupname) {
    List<Integer> list = new ArrayList<Integer>();

    // SQLの作成
    StringBuffer statement = new StringBuffer();
    statement.append("SELECT DISTINCT ");
    statement.append("  B.USER_ID, D.POSITION ");
    statement.append("FROM TURBINE_USER_GROUP_ROLE as A ");
    statement.append("LEFT JOIN TURBINE_USER as B ");
    statement.append("  on A.USER_ID = B.USER_ID ");
    statement.append("LEFT JOIN TURBINE_GROUP as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("LEFT JOIN EIP_M_USER_POSITION as D ");
    statement.append("  on A.USER_ID = D.USER_ID ");
    statement.append("WHERE B.USER_ID > 3 AND B.DISABLED = 'F'");
    statement.append(" AND C.GROUP_NAME = #bind($groupName) ");
    statement.append("ORDER BY D.POSITION");
    String query = statement.toString();

    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
        .getDataContext();
      @SuppressWarnings("deprecation")
      SQLTemplate rawSelect = new SQLTemplate(TurbineUser.class, query, true);
      rawSelect.setParameters(Collections.singletonMap("groupName", groupname));
      rawSelect.setFetchingDataRows(true);
      List<?> ulist = dataContext.performQuery(rawSelect);

      int recNum = ulist.size();

      DataRow dataRow;
      // ユーザデータを作成し、返却リストへ格納
      for (int j = 0; j < recNum; j++) {
        dataRow = (DataRow) ulist.get(j);
        list.add((Integer) ALEipUtils.getObjFromDataRow(dataRow,
          TurbineUser.USER_ID_PK_COLUMN));
      }
    } catch (Exception ex) {
      logger.error("[ALEipUtils]", ex);
    }
    return list;
  }

  /**
   * 指定された部署に所属するユーザーを取得します。
   * 
   * @param postid
   *          部署ID
   * @return ALEipUser の List
   */
  public static List<ALEipUser> getUsersFromPost(int postid) {
    List<ALEipUser> list = new ArrayList<ALEipUser>();

    // SQLの作成
    StringBuffer statement = new StringBuffer();
    statement.append("SELECT DISTINCT ");
    statement
      .append("  B.USER_ID, B.LOGIN_NAME, B.FIRST_NAME, B.LAST_NAME, D.POSITION ");
    statement.append("FROM TURBINE_USER_GROUP_ROLE as A ");
    statement.append("LEFT JOIN TURBINE_USER as B ");
    statement.append("  on A.USER_ID = B.USER_ID ");
    statement.append("LEFT JOIN TURBINE_GROUP as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("LEFT JOIN EIP_M_USER_POSITION as D ");
    statement.append("  on A.USER_ID = D.USER_ID ");
    statement.append("WHERE B.USER_ID > 3 AND B.DISABLED = 'F'");
    statement.append(" AND B.POST_ID = #bind($postId) ");
    statement.append("ORDER BY D.POSITION");
    String query = statement.toString();

    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    @SuppressWarnings("deprecation")
    SQLTemplate rawSelect = new SQLTemplate(TurbineUser.class, query, true);
    rawSelect.setParameters(Collections.singletonMap("postId", postid));
    rawSelect.setFetchingDataRows(true);
    List<?> ulist = dataContext.performQuery(rawSelect);

    int recNum = ulist.size();

    ALEipUser user;
    DataRow dataRow;
    // ユーザデータを作成し、返却リストへ格納
    for (int j = 0; j < recNum; j++) {
      dataRow = (DataRow) ulist.get(j);
      user = new ALEipUser();
      user.initField();
      user.setUserId(((Integer) ALEipUtils.getObjFromDataRow(dataRow,
        TurbineUser.USER_ID_PK_COLUMN)).intValue());
      user.setName((String) ALEipUtils.getObjFromDataRow(dataRow,
        TurbineUser.LOGIN_NAME_COLUMN));
      user.setAliasName((String) ALEipUtils.getObjFromDataRow(dataRow,
        TurbineUser.FIRST_NAME_COLUMN), (String) ALEipUtils.getObjFromDataRow(
        dataRow, TurbineUser.LAST_NAME_COLUMN));
      list.add(user);
    }
    return list;
  }

  /**
   * <code>SelectQuery</code> の条件に従ってユーザーを取得します。
   * 
   * @param crt
   * @return ALEipUser の List
   */
  public static List<ALEipUser> getUsersFromSelectQuery(SelectQuery query) {
    List<ALEipUser> list = new ArrayList<ALEipUser>();
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
        .getDataContext();
      query.addOrdering(TurbineUser.EIP_MUSER_POSITION_PROPERTY + "."
        + EipMUserPosition.POSITION_PROPERTY, true);
      List<?> ulist = dataContext.performQuery(query);
      int size = ulist.size();

      for (int i = 0; i < size; i++) {
        TurbineUser record = (TurbineUser) ulist.get(i);
        ALEipUser user = new ALEipUser();
        user.initField();
        user.setUserId(record.getUserId().intValue());
        user.setName(record.getLoginName());
        user.setAliasName(record.getFirstName(), record.getLastName());
        list.add(user);
      }
    } catch (Exception ex) {
      logger.error("[ALEipUtils]", ex);
    }
    return list;
  }

  /**
   * 自ユーザーの簡易オブジェクトを取得します。
   * 
   * @param crt
   * @return ALEipUser
   */
  public static ALEipUser getALEipUser(RunData rundata) {
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    ALEipUser user = new ALEipUser();
    user.initField();
    user.setUserId(Integer.parseInt(jdata.getJetspeedUser().getUserId()));
    user.setName(jdata.getJetspeedUser().getUserName());
    user.setAliasName(jdata.getJetspeedUser().getFirstName(), jdata
      .getJetspeedUser().getLastName());
    return user;
  }

  /**
   * 指定したユーザーIDの簡易オブジェクトを取得します。
   * 
   * @param id
   * @return
   */
  public static ALEipUser getALEipUser(int id) throws ALDBErrorException {
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    Expression exp = ExpressionFactory.matchDbExp(
      TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(id));
    SelectQuery query = new SelectQuery(TurbineUser.class, exp);
    List<?> users = dataContext.performQuery(query);
    if (users.size() == 0) {
      return null;
    }

    TurbineUser tuser = (TurbineUser) users.get(0);
    ALEipUser user = new ALEipUser();
    user.initField();
    user.setUserId(tuser.getUserId().intValue());
    user.setName(tuser.getLoginName());
    user.setAliasName(tuser.getFirstName(), tuser.getLastName());

    return user;
  }

  /**
   * 指定したユーザーIDの簡易オブジェクトを取得します。
   * 
   * @param id
   * @return
   */
  public static ALEipUser getALEipUser(String loginname)
      throws ALDBErrorException {
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    Expression exp = ExpressionFactory.matchExp(
      TurbineUser.LOGIN_NAME_PROPERTY, loginname);
    SelectQuery query = new SelectQuery(TurbineUser.class, exp);
    List<?> users = dataContext.performQuery(query);
    if (users.size() == 0) {
      return null;
    }

    TurbineUser tuser = (TurbineUser) users.get(0);
    ALEipUser user = new ALEipUser();
    user.initField();
    user.setUserId(tuser.getUserId().intValue());
    user.setName(tuser.getLoginName());
    user.setAliasName(tuser.getFirstName(), tuser.getLastName());

    return user;
  }

  /**
   * 指定したユーザーIDのオブジェクトを取得します。
   * 
   * @param userid
   *          ユーザID
   * @return
   */
  public static ALBaseUser getBaseUser(int userid) {
    String uid = String.valueOf(userid);
    try {
      if (uid == null) {
        logger.debug("Empty ID...");
        return null;
      }
      return (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(uid));
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * ユーザーのフルネームを取得します。
   * 
   * @param userid
   *          ユーザID
   * @return
   */
  public static String getUserFullName(int userid) {
    String userName = "";
    ALBaseUser user = getBaseUser(userid);
    if (user != null) {
      userName = new StringBuffer().append(user.getLastName()).append(" ")
        .append(user.getFirstName()).toString();
    }
    return userName;
  }

  /**
   * 部署の変更を行います。 <br>
   * 部署に関連付けされているグループの更新も同時に行います。
   * 
   * @param rundata
   * @param username
   *          ユーザー名
   * @param postid
   *          部署ID
   * @return true 部署変更成功 false 部署変更失敗
   */
  public static boolean changePost(RunData rundata, String username, int postid)
      throws ALDBErrorException {
    try {
      ALBaseUser user = (ALBaseUser) JetspeedSecurity.getUser(username);

      // グループへ追加
      JetspeedSecurity.joinGroup(username, (ALEipManager.getInstance()
        .getPostMap().get(Integer.valueOf(postid))).getGroupName().getValue());

      // 部署を変更
      user.setPostId(postid);

      // ユーザーを更新
      JetspeedSecurity.saveUser(user);

      ALBaseUser currentUser = (ALBaseUser) rundata.getUser();
      if (currentUser.getUserName().equals(user.getUserName())) {
        // 自ユーザーのセッション情報を更新する
        currentUser.setPostId(user.getPostId());
      }

    } catch (JetspeedSecurityException ex) {
      logger.error("[ALEipUtils]", ex);
      throw new ALDBErrorException();
    }
    return true;
  }

  /**
   * 自ユーザーのマイグループを再読み込みします。 <br>
   * 読み込まれたマイグループはセッションに保存されます。 <br>
   * マイグループの更新が行われた場合はこのメソッドを呼び出してください。
   * 
   * @param rundata
   */
  public static void reloadMygroup(RunData rundata) throws ALDBErrorException {
    List<ALEipGroup> ulist = new ArrayList<ALEipGroup>();
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
        .getDataContext();
      Expression exp = ExpressionFactory.matchExp(
        TurbineGroup.OWNER_ID_PROPERTY, Integer.valueOf(getUserId(rundata)));
      SelectQuery query = new SelectQuery(TurbineGroup.class, exp);
      List<?> list = dataContext.performQuery(query);

      int size = list.size();
      for (int i = 0; i < size; i++) {
        TurbineGroup record = (TurbineGroup) list.get(i);
        ALEipGroup group = new ALEipGroup();
        group.initField();
        group.setName(record.getGroupName());
        group.setAliasName(record.getGroupAliasName());
        ulist.add(group);
      }
    } catch (Exception ex) {
      logger.error("[ALEipUtils]", ex);
      throw new ALDBErrorException();
    }

    // セッションのマイグループに保存
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    ALMyGroups mygroups = new ALMyGroups();
    mygroups.addList(ulist);
    jdata.getUser().setTemp(ALEipConstants.MYGROUP, mygroups);

  }

  /**
   * 自ユーザーのマイグループを取得します。
   * 
   * @param rundata
   * @return ALEipGroup の List
   */
  public static List<ALEipGroup> getMyGroups(RunData rundata)
      throws ALDBErrorException {
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    // セッションからマイグループのリストを読み込む
    Object obj = jdata.getUser().getTemp(ALEipConstants.MYGROUP);
    if (obj == null || !(obj instanceof ALMyGroups)) {
      // まだMyGroupが読み込まれていない場合はセッションに読み込む
      reloadMygroup(rundata);
      obj = jdata.getUser().getTemp(ALEipConstants.MYGROUP);
    }
    ALMyGroups mygroups = (ALMyGroups) obj;
    return mygroups.getList();
  }

  /**
   * 会社名を取得します。
   * 
   * @param id
   * @return
   */
  public static String getCompanyName(int id) {
    String companyName = null;
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
        .getDataContext();
      SelectQuery query = new SelectQuery(EipMCompany.class);
      query.addCustomDbAttribute(EipMCompany.COMPANY_NAME_COLUMN);
      Expression exp = ExpressionFactory.matchDbExp(
        EipMCompany.COMPANY_ID_PK_COLUMN, Integer.valueOf(id));
      query.setQualifier(exp);
      List<?> list = dataContext.performQuery(query);

      if (list == null || list.size() == 0) {
        // 指定したCompany IDのレコードが見つからない場合
        logger.debug("[ALEipUtils] Not found ComapnyID...");
        return null;
      }

      DataRow dataRow = (DataRow) list.get(0);
      companyName = (String) ALEipUtils.getObjFromDataRow(dataRow,
        EipMCompany.COMPANY_NAME_COLUMN);

    } catch (Exception ex) {
      logger.error("[ALEipUtils]", ex);
      companyName = null;
    }

    return companyName;
  }

  /**
   * 部署名を取得します。
   * 
   * @param id
   * @return
   */
  public static String getPostName(int id) {
    if (ALEipManager.getInstance().getPostMap()
      .containsKey(Integer.valueOf(id))) {
      return (ALEipManager.getInstance().getPostMap().get(Integer.valueOf(id)))
        .getPostName().getValue();
    }
    return null;
  }

  /**
   * 役職名を取得します。
   * 
   * @param id
   * @return
   */
  public static String getPositionName(int id) {
    if (ALEipManager.getInstance().getPositionMap().containsKey(
      Integer.valueOf(id))) {
      return (ALEipManager.getInstance().getPositionMap().get(Integer
        .valueOf(id))).getPositionName().getValue();
    }
    return null;
  }

  /**
   * ページが見つからない場合に、リダイレクト処理します。
   * 
   * @return
   */
  public static boolean redirectPageNotFound(RunData rundata) {
    try {
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      DynamicURI duri = jsLink.getPage();
      duri.addPathInfo("template", "PageNotFound");
      rundata.setRedirectURI(duri.toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());

      JetspeedLinkFactory.putInstance(jsLink);
      jsLink = null;
      return true;
    } catch (TurbineException e) {

      logger.error("Exception", e);
      return false;
    } catch (IOException e) {

      logger.error("Exception", e);
      return false;
    }
  }

  /**
   * データベースエラーの場合に、リダイレクト処理します。
   * 
   * @return
   */
  public static boolean redirectDBError(RunData rundata) {
    try {
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      DynamicURI duri = jsLink.getPage();
      String template = rundata.getParameters().getString(
        JetspeedResources.PATH_TEMPLATE_KEY);
      if (template != null && !("".equals(template))) {
        if (template.endsWith("DetailScreen")) {
          VelocityContext context = new VelocityContext();
          setupContext(rundata, context);
          try {
            ServletOutputStream out = null;
            HttpServletResponse response = rundata.getResponse();
            out = response.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
              out, ALEipConstants.DEF_CONTENT_ENCODING));
            Template templete = Velocity
              .getTemplate("screens/html/AjaxDBError.vm");
            templete.merge(context, writer);
            writer.flush();
            writer.close();
          } catch (Exception e) {
            return false;
          }
          return true;
        }
      }
      duri.addPathInfo(JetspeedResources.PATH_TEMPLATE_KEY, "DBError");
      rundata.setRedirectURI(duri.toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());

      JetspeedLinkFactory.putInstance(jsLink);
      jsLink = null;
      return true;
    } catch (TurbineException e) {

      logger.error("Exception", e);
      return false;
    } catch (IOException e) {

      logger.error("Exception", e);
      return false;
    }
  }

  /**
   * パーミッションエラーの場合に、リダイレクト処理します。
   * 
   * @return
   */
  public static boolean redirectPermissionError(RunData rundata) {
    try {
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      DynamicURI duri = jsLink.getPage();
      String template = rundata.getParameters().getString(
        JetspeedResources.PATH_TEMPLATE_KEY);
      if (template != null && !("".equals(template))) {

        if (template.endsWith("JSONScreen")) {
          VelocityContext context = new VelocityContext();
          setupContext(rundata, context);
          try {
            ServletOutputStream out = null;
            HttpServletResponse response = rundata.getResponse();
            out = response.getOutputStream();

            List<String> list = new ArrayList<String>();
            list.add("PermissionError");
            list.add(ALAccessControlConstants.DEF_PERMISSION_ERROR_STR);
            JSONArray json = JSONArray.fromObject(list);

            StringBuffer result = new StringBuffer().append("/* ").append(
              json.toString()).append(" */");

            out.print(result.toString());
            out.flush();
            out.close();
          } catch (Exception e) {
            return false;
          }
          return true;
        } else if (template.endsWith("FormScreen")
          || template.endsWith("DetailScreen")) {
          VelocityContext context = new VelocityContext();
          setupContext(rundata, context);
          try {
            ServletOutputStream out = null;
            HttpServletResponse response = rundata.getResponse();
            out = response.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
              out, ALEipConstants.DEF_CONTENT_ENCODING));
            Template templete = Velocity
              .getTemplate("screens/html/AjaxPermissionError.vm");
            templete.merge(context, writer);
            writer.flush();
            writer.close();
          } catch (Exception e) {
            return false;
          }
          return true;
        } else if (template.endsWith("Screen")) {
          // 一覧表示の場合
          VelocityContext context = new VelocityContext();
          setupContext(rundata, context);
          try {
            ServletOutputStream out = null;
            HttpServletResponse response = rundata.getResponse();
            out = response.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
              out, ALEipConstants.DEF_CONTENT_ENCODING));
            Template templete = Velocity
              .getTemplate("portlets/html/PermissionError.vm");
            templete.merge(context, writer);
            writer.flush();
            writer.close();
          } catch (Exception e) {
            return false;
          }
          return true;

        } else if (template.equals("Customize") || template.equals("Home")) {
          // ポートレットカスタマイズ
          duri.addPathInfo(JetspeedResources.PATH_TEMPLATE_KEY,
            "PermissionError");
          rundata.setRedirectURI(duri.toString());
          rundata.getResponse().sendRedirect(rundata.getRedirectURI());
          // TODO add by Haruo Kaneko
          JetspeedLinkFactory.putInstance(jsLink);

          jsLink = null;
          return true;
        } else if (isCellularPhone(rundata)) {
          duri.addPathInfo(JetspeedResources.PATH_TEMPLATE_KEY,
            "CellPermissionError");
          rundata.setRedirectURI(duri.toString());
          rundata.getResponse().sendRedirect(rundata.getRedirectURI());
          JetspeedLinkFactory.putInstance(jsLink);
          jsLink = null;
          Restore restore = new Restore();
          try {
            restore.doPerform(rundata);
          } catch (Exception e) {
          }
          return true;
        } else if (isSmartPhone(rundata)) {
          duri.addPathInfo(JetspeedResources.PATH_TEMPLATE_KEY,
            "CellPermissionError");
          rundata.setRedirectURI(duri.toString());
          rundata.getResponse().sendRedirect(rundata.getRedirectURI());
          JetspeedLinkFactory.putInstance(jsLink);
          jsLink = null;
          Restore restore = new Restore();
          try {
            restore.doPerform(rundata);
          } catch (Exception e) {
          }
          return true;
        }

      }

      try {
        Restore restore = new Restore();
        restore.doPerform(rundata);
      } catch (Exception e) {
      }

      JetspeedRunData jdata = (JetspeedRunData) rundata;
      if (jdata.getMode() == JetspeedRunData.MAXIMIZE) {
        duri
          .addPathInfo(JetspeedResources.PATH_TEMPLATE_KEY, "PermissionError");
        rundata.setRedirectURI(duri.toString());
        rundata.getResponse().sendRedirect(rundata.getRedirectURI());
        // TODO add by Haruo Kaneko
        JetspeedLinkFactory.putInstance(jsLink);

        jsLink = null;
      } else {
        Context context = (Context) jdata.getTemplateInfo().getTemplateContext(
          "VelocityPortletContext");
        context.put(JetspeedResources.PATH_TEMPLATE_KEY, "PermissionError");
      }

      return true;
    } catch (TurbineException e) {

      logger.error("Exception", e);
      return false;
    } catch (IOException e) {

      logger.error("Exception", e);
      return false;
    }
  }

  /**
   * 改行コードを含む文字列を、複数行に分割します。
   * 
   * @return
   */
  public static String getMessageList(String msgline) {
    StringBuffer sb = new StringBuffer();
    ALStringField field = null;

    if (msgline == null || msgline.equals("")) {
      return "";
    }
    if (msgline.indexOf("\r") < 0 && msgline.indexOf("\n") < 0
      && msgline.indexOf("\r\n") < 0) {
      field = new ALStringField();
      field.setTrim(false);
      field.setValue(msgline);
      return ALCommonUtils.replaceToAutoCR(replaceStrToLink(field.toString()));
    }

    String token = null;
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new StringReader(msgline));
      while ((token = reader.readLine()) != null) {
        field = new ALStringField();
        field.setTrim(false);
        field.setValue(token);
        sb.append(
          ALCommonUtils.replaceToAutoCR(replaceStrToLink(replaseLeftSpace(field
            .toString())))).append("<br/>");
      }
      reader.close();
    } catch (IOException ioe) {
      try {
        reader.close();
      } catch (IOException e) {
      }
      return "";
    }

    int index = sb.lastIndexOf("<br/>");
    if (index == -1) {
      return sb.toString();
    }
    return sb.substring(0, index);
  }

  /**
   * 左端の半角空文字を「&nbsp;」に変換する。
   * 
   * @param str
   * @return
   */
  public static String replaseLeftSpace(String str) {
    if (str == null || str.length() <= 0) {
      return str;
    }

    int len = str.length();
    int st = 0;
    char[] val = str.toCharArray();

    StringBuffer sb = new StringBuffer();
    while ((st < len)) {
      if (val[st] == ' ') {
        sb.append("&nbsp;");
      } else {
        sb.append(val[st]);
      }
      st++;
    }
    return (sb.length() > 0) ? sb.toString() : str;
  }

  /**
   * アクセス元の端末が携帯電話であるかを判定します。
   * 
   * @param data
   * @return
   */
  public static boolean isCellularPhone(RunData data) {
    boolean isCellularPhone = false;
    CapabilityMap cm = CapabilityMapFactory.getCapabilityMap(data.getRequest()
      .getHeader("User-Agent"));
    MimeType mime = cm.getPreferredType();
    if (mime != null) {
      MediaTypeEntry media = (MediaTypeEntry) Registry.getEntry(
        Registry.MEDIA_TYPE, cm.getPreferredMediaType());
      String mediatype = media.getName();
      if ("docomo_imode".equals(mediatype) || "docomo_foma".equals(mediatype)
        || "au".equals(mediatype) || "vodafone".equals(mediatype)) {
        isCellularPhone = true;
      }
    }
    return isCellularPhone;
  }

  /**
   * アクセス元の端末がスマートフォンであるかを判定します。
   * 
   * @param data
   * @return
   */
  public static boolean isSmartPhone(RunData data) {
    boolean isSmartPhone = false;
    CapabilityMap cm = CapabilityMapFactory.getCapabilityMap(data.getRequest()
      .getHeader("User-Agent"));
    MimeType mime = cm.getPreferredType();
    if (mime != null) {
      MediaTypeEntry media = (MediaTypeEntry) Registry.getEntry(
        Registry.MEDIA_TYPE, cm.getPreferredMediaType());
      String mediatype = media.getName();
      if ("iphone".equals(mediatype) || "wm".equals(mediatype)) {
        isSmartPhone = true;
      }
    }
    return isSmartPhone;
  }

  /**
   * 指定した2つの日付を比較します。
   * 
   * @param date1
   * @param date2
   * @return 等しい場合、0。date1>date2の場合、1。date1 < date2の場合、2。
   */
  public static int compareToDate(Date date1, Date date2) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(date1);
    cal2.setTime(date2);

    int date1Year = cal1.get(Calendar.YEAR);
    int date1Month = cal1.get(Calendar.MONTH) + 1;
    int date1Day = cal1.get(Calendar.DATE);
    int date1Hour = cal1.get(Calendar.HOUR);
    int date1Minute = cal1.get(Calendar.MINUTE);
    int date1Second = cal1.get(Calendar.SECOND);
    int date2Year = cal2.get(Calendar.YEAR);
    int date2Month = cal2.get(Calendar.MONTH) + 1;
    int date2Day = cal2.get(Calendar.DATE);
    int date2Hour = cal2.get(Calendar.HOUR);
    int date2Minute = cal2.get(Calendar.MINUTE);
    int date2Second = cal2.get(Calendar.SECOND);

    if (date1Year == date2Year && date1Month == date2Month
      && date1Day == date2Day && date1Hour == date2Hour
      && date1Minute == date2Minute && date1Second == date2Second) {
      return 0;
    }
    if (cal1.after(cal2)) {
      return 2;
    } else {
      return 1;
    }
  }

  /**
   * データベースの検索結果から、指定したキーに対応する値を取得します。
   * 
   * @param dataRow
   * @param key
   * @return
   */
  public static Object getObjFromDataRow(DataRow dataRow, String key) {
    String lowerKey = key.toLowerCase();
    if (dataRow.containsKey(lowerKey)) {
      return dataRow.get(lowerKey);
    } else {
      return dataRow.get(key.toUpperCase());
    }
  }

  /**
   * 会社情報のオブジェクトを取得します。
   * 
   * @param id
   * @return
   */
  public static EipMCompany getEipMCompany(String id) {
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    SelectQuery query = new SelectQuery(EipMCompany.class);
    Expression exp = ExpressionFactory.matchDbExp(
      EipMCompany.COMPANY_ID_PK_COLUMN, Integer.valueOf(id));
    query.setQualifier(exp);
    List<?> list = dataContext.performQuery(query);
    if (list == null || list.size() == 0) {
      logger.debug("Not found ID...");
      return null;
    }
    return (EipMCompany) list.get(0);
  }

  /**
   * 
   * @param ip
   * @param port
   * @param servername
   *          Webアプリケーション名
   * @return
   */
  public static String getUrl(String ip, int port, String servername) {
    if (ip == null || ip.length() == 0 || port == -1) {
      return "";
    }

    String protocol = JetspeedResources
      .getString("access.url.protocol", "http");

    StringBuffer url = new StringBuffer();

    if (port == 80) {
      url.append(protocol).append("://").append(ip).append("/").append(
        servername).append("/");
    } else {
      url.append(protocol).append("://").append(ip).append(":").append(port)
        .append("/").append(servername).append("/");
    }

    return url.toString();
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public static void setupContext(RunData rundata, Context context) {
    String js_peid;
    if (!rundata.getParameters().containsKey(
      JetspeedResources.PATH_PORTLETID_KEY)) {
      return;
    }
    js_peid = rundata.getParameters().getString(
      JetspeedResources.PATH_PORTLETID_KEY);

    Portlet portlet = getPortlet(rundata, js_peid);
    context.put("portlet", portlet);
    context.put("jslink", new BaseJetspeedLink(rundata));
    context.put("clink", new ContentTemplateLink(rundata));
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param key
   * @return
   */
  public static String getParameter(RunData rundata, Context context, String key) {
    String name = null;
    String idParam = rundata.getParameters().getString(key);
    name = ALEipUtils.getTemp(rundata, context, key);
    if (idParam == null && name == null) {
      ALEipUtils.removeTemp(rundata, context, key);
      name = null;
    } else if (idParam != null) {
      ALEipUtils.setTemp(rundata, context, key, idParam);
      name = idParam;
    }
    return name;
  }

  /**
   * 
   * @param dataContext
   * @param classname
   * @param search_key
   * @param search_value
   * @return
   */
  public static List<?> getObjectModels(DataContext dataContext,
      Class<?> classname, String search_key, Integer search_value, boolean isDb) {
    SelectQuery query = new SelectQuery(classname);
    Expression exp = isDb ? ExpressionFactory.matchDbExp(search_key,
      search_value) : ExpressionFactory.matchExp(search_key, search_value);
    query.setQualifier(exp);
    List<?> list = dataContext.performQuery(query);
    if (list == null || list.size() <= 0) {
      return null;
    }
    return list;
  }

  /**
   * 指定したエントリー名のポートレットへの URI を取得します。
   * 
   * @param rundata
   * @param portletEntryName
   *          PSML ファイルに記述されているタグ entry の要素 parent
   * @return
   */
  public static String getPortletURI(RunData rundata, String portletEntryId) {
    try {
      Portlets portlets = ((JetspeedRunData) rundata).getProfile()
        .getDocument().getPortlets();
      if (portlets == null) {
        return null;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return null;
      }

      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getId().equals(portletEntryId)) {
            JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
            DynamicURI duri = jsLink.getLink(JetspeedLink.CURRENT, null, null,
              JetspeedLink.CURRENT, null);
            duri = duri.addPathInfo(JetspeedResources.PATH_PANEID_KEY,
              portletList[i].getId()).addPathInfo(
              JetspeedResources.PATH_PORTLETID_KEY, entries[j].getId())
              .addQueryData(JetspeedResources.PATH_ACTION_KEY,
                "controls.Maximize");
            return duri.toString();
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
    return null;
  }

  /**
   * 
   * @param rundata
   * @param portletEntryId
   * @return
   */
  public static String getPortletURItoTopPage(RunData rundata,
      String portletEntryId) {
    try {
      Portlets portlets = ((JetspeedRunData) rundata).getProfile()
        .getDocument().getPortlets();
      if (portlets == null) {
        return null;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return null;
      }

      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getId().equals(portletEntryId)) {
            JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
            DynamicURI duri = jsLink.getLink(JetspeedLink.CURRENT, null, null,
              JetspeedLink.CURRENT, null);
            duri = duri.addPathInfo(JetspeedResources.PATH_PANEID_KEY,
              portletList[i].getId()).addQueryData(
              JetspeedResources.PATH_ACTION_KEY, "controls.Restore");
            return duri.toString();
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
    return null;
  }

  /**
   * 文字列内のリンクにタグAを追加します。
   * 
   * @param msg
   * @return
   */
  public static String replaceStrToLink(String msg) {
    if (msg != null) {
      String newMsg = msg
        .replaceAll(
          "(https?|ftp|gopher|telnet|whois|news)\\:([\\w|\\:\\!\\#\\$\\%\\=\\&\\-\\^\\`\\\\|\\@\\~\\[\\{\\]\\}\\;\\+\\*\\,\\.\\?\\/]+)",
          "<a href=\"$1\\:$2\" target=\"_blank\">$1\\:$2</a>");
      return newMsg.replaceAll("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+",
        "<a href='mailto:$0'>$0</a>");
    } else {
      return "";
    }
  }

  /**
   * 古いファイル(parent_folder下のファイル)を消します。
   * 
   * @param parent_folder
   *          親フォルダ
   * @param cal
   *          カレンダークラスで設定された時間より前のものは消去
   * @return フォルダの中身が全て消去されたときのみtrueを返します
   */
  public static boolean deleteOldFolder(File parent_folder, Calendar cal) {
    Calendar mod = Calendar.getInstance();
    boolean flag = true;
    try {
      if (!parent_folder.exists()) {
        return false;
      }
      if (parent_folder.isFile()) {
        return false;
      }
      String folders_path[] = parent_folder.list();
      if (folders_path.length == 0) {
        return true;
      }
      int length = folders_path.length;
      for (int i = 0; i < length; i++) {
        File folder = new File(parent_folder.getAbsolutePath() + File.separator
          + folders_path[i]);
        mod.setTimeInMillis(folder.lastModified());// ファイルの最終更新日時を格納
        if (folder.isDirectory()) {
          if (!deleteOldFolder(folder, cal)) {// フォルダの中身が空もしくは全部削除された場合
            flag = false;
          } else if (mod.before(cal)) {// 空のフォルダが古い場合
            if (!folder.delete()) {
              flag = false;
            }
          }
        } else {
          if (mod.before(cal)) {
            // 一つでも消えないファイルがあればフラグを動かす
            if (!folder.delete()) {
              flag = false;
            }
          } else {
            flag = false;
          }
        }

      }
    } catch (Exception e) {
      logger.error(e);
      return false;
    }
    return flag;
  }

  /**
   * フォルダを再帰的に消します。
   * 
   * @param parent_folder
   *          親フォルダ
   * @param cal
   * 
   * @return フォルダの中身が全て消去されたときのみtrueを返します
   */
  public static boolean deleteFolder(File parent_folder) {
    boolean flag = true;
    try {
      if (!parent_folder.exists()) {
        return false;
      }
      if (parent_folder.isFile()) {
        if (!parent_folder.delete()) {
          flag = false;
        }
      }
      String folders_path[] = parent_folder.list();
      if (folders_path.length == 0) {
        return true;
      }
      int length = folders_path.length;
      for (int i = 0; i < length; i++) {
        File folder = new File(parent_folder.getAbsolutePath() + File.separator
          + folders_path[i]);
        if (folder.isDirectory()) {
          if (!deleteFolder(folder)) {// フォルダの中身が空もしくは全部削除された場合
            flag = false;
          } else if (!folder.delete()) {
            flag = false;
          }
        } else {
          // 一つでも消えないファイルがあればフラグを動かす
          if (!folder.delete()) {
            flag = false;
          }
        }
      }
    } catch (Exception e) {
      logger.error(e);
      return false;
    }
    return flag;
  }

  /**
   * ユーザーの所属する部署を取得します。
   * 
   * @param id
   *          ユーザーID
   * @return 所属する部署リスト
   */
  public static List<String> getPostNameList(int id) {
    SelectQuery query = new SelectQuery(TurbineUserGroupRole.class);
    Expression exp1 = ExpressionFactory.matchExp(
      TurbineUserGroupRole.TURBINE_USER_PROPERTY, Integer.valueOf(id));
    Expression exp2 = ExpressionFactory.greaterExp(
      TurbineUserGroupRole.TURBINE_GROUP_PROPERTY, Integer.valueOf(3));
    Expression exp3 = ExpressionFactory.matchExp(
      TurbineUserGroupRole.TURBINE_GROUP_PROPERTY + "."
        + TurbineGroup.OWNER_ID_PROPERTY, Integer.valueOf(1));
    query.setQualifier(exp1);
    query.andQualifier(exp2);
    query.andQualifier(exp3);
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    List<?> list = dataContext.performQuery(query);

    List<String> postNames = new ArrayList<String>();
    TurbineUserGroupRole ugr = null;
    for (int j = 0; j < list.size(); j++) {
      ugr = (TurbineUserGroupRole) list.get(j);
      postNames.add(ugr.getTurbineGroup().getGroupAliasName());
    }

    return postNames;
  }

  /**
   * アクセス権限をチェックします（ポートレットカスタマイズ）
   * 
   * @return
   */
  public static boolean CheckAclPermissionForCustomize(RunData rundata,
      Context context, int defineAclType) {
    try {
      if (defineAclType == 0) {
        return true;
      }

      boolean hasAuthority = getHasAuthority(rundata, context, defineAclType);
      if (!hasAuthority) {
        throw new ALPermissionException();
      }

      return true;
    } catch (ALPermissionException e) {
      ALEipUtils.redirectPermissionError(rundata);
      return false;
    }
  }

  public static boolean getHasAuthority(RunData rundata, Context context,
      int defineAclType) {
    String pfeature = ALAccessControlConstants.POERTLET_FEATURE_PORTLET_CUSTOMIZE;
    if (pfeature == null || "".equals(pfeature)) {
      return true;
    }

    ALAccessControlFactoryService aclservice = (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
      .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
    return aclhandler.hasAuthority(ALEipUtils.getUserId(rundata), pfeature,
      defineAclType);
  }

  public static int getLimitUsers() {
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    try {
      SelectQuery query = new SelectQuery(AipoLicense.class);

      query.addCustomDbAttribute(AipoLicense.LIMIT_USERS_COLUMN);

      List<?> list = dataContext.performQuery(query);

      if (list != null && list.size() > 0) {
        DataRow dataRow = (DataRow) list.get(0);
        Integer result = (Integer) ALEipUtils.getObjFromDataRow(dataRow,
          AipoLicense.LIMIT_USERS_COLUMN);
        return result.intValue();
      }
    } catch (Exception e) {
      logger.error("Exception", e);
    }
    return 0;
  }

  /**
   * 現在登録されている有効なユーザー数(システムユーザ、論理削除は除く)を取得します。
   * 
   * @return
   */
  public static int getCurrentUserNumEnabledOnly(RunData rundata) {
    int registeredUserNum = -1;
    try {
      // 論理削除ユーザーを除く
      // ユーザーテーブルDISABLEDがTのものが論理削除
      // システムユーザtemplateは論理削除されているため
      // RES_USER_NUMは3だが2として計算しないといけない。

      DataContext dataContext = DatabaseOrmService.getInstance()
        .getDataContext();
      Expression exp = ExpressionFactory.matchExp(
        TurbineUser.DISABLED_PROPERTY, "F");
      SelectQuery query = new SelectQuery(TurbineUser.class);
      query.setQualifier(exp);
      List<?> list = dataContext.performQuery(query);
      if (list == null || list.size() <= 0) {
        return -1;
      }
      int size = list.size();
      // admin,anonユーザが含まれるので2ユーザ分減算
      registeredUserNum = size - 2;
    } catch (Exception ex) {
      logger.error("ユーザー情報をDBから取得できませんでした。");
      logger.error(ex);
      return -1;
    }
    return registeredUserNum;
  }

  /**
   * 現在登録されている有効なユーザー数(システムユーザ、論理削除、無効化は除く)を取得します。
   * 
   * @return
   */
  public static int getCurrentUserNum(RunData rundata) {
    int registeredUserNum = -1;
    try {
      // 論理削除ユーザーを除く
      // ユーザーテーブルDISABLEDがTのものが論理削除
      // システムユーザtemplateは論理削除されているため
      // RES_USER_NUMは3だが2として計算しないといけない。

      DataContext dataContext = DatabaseOrmService.getInstance()
        .getDataContext();
      Expression exp = ExpressionFactory.noMatchExp(
        TurbineUser.DISABLED_PROPERTY, "T");
      SelectQuery query = new SelectQuery(TurbineUser.class);
      query.setQualifier(exp);
      List<?> list = dataContext.performQuery(query);
      if (list == null || list.size() <= 0) {
        return -1;
      }
      int size = list.size();
      // admin,anonユーザが含まれるので2ユーザ分減算
      registeredUserNum = size - 2;
    } catch (Exception ex) {
      logger.error("ユーザー情報をDBから取得できませんでした。");
      logger.error(ex);
      return -1;
    }
    return registeredUserNum;
  }

}
