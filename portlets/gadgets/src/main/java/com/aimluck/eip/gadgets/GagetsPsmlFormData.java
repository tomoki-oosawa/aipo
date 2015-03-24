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
package com.aimluck.eip.gadgets;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.psmlmanager.PsmlManagerService;
import org.apache.jetspeed.services.psmlmanager.db.DBUtils;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.exolab.castor.mapping.Mapping;
import org.xml.sax.InputSource;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.account.JetspeedUserProfile;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.gadgets.util.PsmlDBUtils;
import com.aimluck.eip.gadgets.util.PsmlUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 『テンプレート更新』のフォームデータを管理するクラス．
 * 
 */
public class GagetsPsmlFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GagetsPsmlFormData.class.getName());

  private ALStringField note;

  private boolean adminUser;

  private String attach_status;

  private String default_status;

  private String all_user_status;

  private static final String UPDATE_SUCCESS = "success";

  private static final String UPDATE_ERROR = "error";

  private static final String ATTACH_VIEW_STATUS = "attach_psml_status";

  private static final String DEFAULT_VIEW_STATUS = "default_psml_status";

  private static final String ALL_USER_VIEW_STATUS = "all_user_psml_status";

  // castor mapping
  public static final String DEFAULT_MAPPING =
    "${webappRoot}/WEB-INF/conf/psml-mapping.xml";

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, "profile");
    adminUser = ALEipUtils.isAdminUser(rundata);
    attach_status = ALEipUtils.getTemp(rundata, context, ATTACH_VIEW_STATUS);
    default_status = ALEipUtils.getTemp(rundata, context, DEFAULT_VIEW_STATUS);
    all_user_status =
      ALEipUtils.getTemp(rundata, context, ALL_USER_VIEW_STATUS);
    ALEipUtils.removeTemp(rundata, context, ATTACH_VIEW_STATUS);
    ALEipUtils.removeTemp(rundata, context, DEFAULT_VIEW_STATUS);
    ALEipUtils.removeTemp(rundata, context, ALL_USER_VIEW_STATUS);
    super.init(action, rundata, context);
  }

  /**
   * 各フィールドを初期化する
   * 
   */
  @Override
  public void initField() {
    note = new ALStringField();
    note.setFieldName(ALLocalizationUtils.getl10n("GADGETS_SETFIELDNAME_NOTE"));
    note.setTrim(false);
    adminUser = false;
  }

  /**
   * 各フィールドに対する制約条件を設定する
   */
  @Override
  protected void setValidator() {

  }

  /**
   * フォームに入力されたデータの妥当性を検証します
   * 
   * @param msgList
   * @return
   */
  @Override
  protected boolean validate(List<String> msgList) {

    return (msgList.size() == 0);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
    } catch (Exception ex) {

      return false;
    }
    return true;
  }

  protected void setNote(String psml) {
    note.setValue(psml);
  }

  public ALStringField getNote() {
    return note;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    boolean success;
    if ("default".equals(rundata.getParameters().getString("mode"))) {
      success = updateDefaultFormData(rundata, context, msgList);
    } else if ("all_user".equals(rundata.getParameters().getString("mode"))) {
      success = updateAllUserFormData(rundata, context, msgList);
    } else {
      success = updateAttachFormData(rundata, context, msgList);
    }
    return success;
  }

  private boolean updateAttachFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      String psml;
      psml = PsmlDBUtils.getMyHtmlPsml(rundata);
      psml = PsmlUtils.parsePsml(psml);
      if (psml == null) {
        return false;
      }
      psml = PsmlUtils.PSMLEncode(psml);
      PsmlDBUtils.checkAndFixInconsistency(PsmlUtils.TEMPLATE_NAME);

      JetspeedUserProfile profile = PsmlDBUtils.getTemplateHtmlProfile();
      profile.setProfile(psml.getBytes());
      Database.commit();
      ALEipUtils.setTemp(rundata, context, ATTACH_VIEW_STATUS, UPDATE_SUCCESS);
    } catch (Exception ex) {
      Database.rollback();
      logger.error("gadgets", ex);
      ALEipUtils.setTemp(rundata, context, ATTACH_VIEW_STATUS, UPDATE_ERROR);
      return false;
    }
    return true;
  }

  private boolean updateDefaultFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      String psml;
      Mapping mapping = PsmlUtils.getMapping(rundata);
      File psmlFile = PsmlUtils.getTemplateHtmlDefaultPsmlFile(rundata);
      PSMLDocument doc = PsmlUtils.loadDocument(psmlFile, mapping);
      byte[] psmlByte =
        DBUtils.portletsToBytes(doc.getPortlets(), PsmlUtils
          .getMapping(rundata));
      psml = new String(psmlByte);
      psml = PsmlUtils.parsePsml(psml);
      if (psml == null) {
        return false;
      }
      psml = PsmlUtils.PSMLEncode(psml);

      PsmlDBUtils.checkAndFixInconsistency(PsmlUtils.TEMPLATE_NAME);
      JetspeedUserProfile profile = PsmlDBUtils.getTemplateHtmlProfile();
      profile.setProfile(psml.getBytes());
      Database.commit();
      ALEipUtils.setTemp(rundata, context, DEFAULT_VIEW_STATUS, UPDATE_SUCCESS);

    } catch (Exception ex) {
      Database.rollback();
      logger.error("gadgets", ex);
      ALEipUtils.setTemp(rundata, context, DEFAULT_VIEW_STATUS, UPDATE_ERROR);
      return false;
    }
    return true;
  }

  private boolean updateAllUserFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      if (isAdminUser()) {
        return false;
      }

      String psml;
      psml = PsmlDBUtils.getMyHtmlPsml(rundata);
      psml = PsmlUtils.parsePsmlForAllUser(psml);
      psml = PsmlUtils.PSMLEncode(psml);

      PsmlDBUtils.checkAndFixInconsistency(PsmlUtils.TEMPLATE_NAME);
      JetspeedUserProfile profileTemplate =
        PsmlDBUtils.getTemplateHtmlProfile();
      profileTemplate.setProfile(psml.getBytes());

      Mapping mapping = loadMapping();
      Portlets portlets = DBUtils.bytesToPortlets(psml.getBytes(), mapping);

      List<JetspeedUserProfile> profiles = PsmlDBUtils.getAllUserHtmlProfile();
      for (JetspeedUserProfile profile : profiles) {
        String userName = profile.getUserName();
        PsmlDBUtils.checkAndFixInconsistency(profile.getUserName());
        if (!profile.getUserName().equals(ALEipUtils.getLoginName(rundata))) {
          ALEipUser alEipUser = ALEipUtils.getALEipUser(userName);
          if (alEipUser == null) {
            continue;
          }
          long userId = alEipUser.getUserId().getValue();
          boolean isAdmin = ALEipUtils.isAdmin((int) userId);
          org.apache.jetspeed.util.PortletUtils.regenerateIds(portlets);
          Portlets myportlets = (Portlets) portlets.clone();

          Portlets[] portletList = myportlets.getPortletsArray();

          int length = portletList.length;

          for (int i = 0; i < length; i++) {
            Entry[] entries = portletList[i].getEntriesArray();
            if (entries == null || entries.length <= 0) {
              continue;
            }

            int ent_length = entries.length;
            for (int j = 0; j < ent_length; j++) {
              if (entries[j].getParent().equals("Schedule")
                || entries[j].getParent().equals("AjaxScheduleWeekly")) {
                Parameter scheduleParameter =
                  entries[j].getParameter("p6a-uids");
                if (scheduleParameter != null) {
                  scheduleParameter.setValue("");
                  entries[j].setParameter(0, scheduleParameter);
                }
              }

              if (entries[j].getParent().equals("WebMail")) {
                Parameter webmailParameter =
                  entries[j].getParameter("p3a-accounts");
                if (webmailParameter != null) {
                  webmailParameter.setValue("");
                  entries[j].setParameter(0, webmailParameter);
                }
              }

            }
          }
          profile.setProfile(DBUtils.portletsToBytes(myportlets, mapping));
          if (isAdmin == true) {
            ALEipUtils.addAdminPage(userName);
          }
        }
      }

      Database.commit();
      ALEipUtils
        .setTemp(rundata, context, ALL_USER_VIEW_STATUS, UPDATE_SUCCESS);
    } catch (Exception ex) {
      Database.rollback();
      logger.error("gadgets", ex);
      ALEipUtils.setTemp(rundata, context, ALL_USER_VIEW_STATUS, UPDATE_ERROR);
      return false;
    }
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * CayenneDatabasePsmlManagerService.loadMapping()を踏襲
   * 
   * @return
   * @throws InitializationException
   */
  private Mapping loadMapping() throws InitializationException {
    // psml castor mapping file
    ResourceService serviceConf =
      ((TurbineServices) TurbineServices.getInstance())
        .getResources(PsmlManagerService.SERVICE_NAME);
    String mapFile = serviceConf.getString("mapping", DEFAULT_MAPPING);
    mapFile = TurbineServlet.getRealPath(mapFile);

    if (mapFile != null) {
      File map = new File(mapFile);
      if (logger.isDebugEnabled()) {
        logger.debug("Loading psml mapping file " + mapFile);
      }
      if (map.exists() && map.isFile() && map.canRead()) {
        try {
          Mapping mapping = new Mapping();
          InputSource is = new InputSource(new FileReader(map));
          is.setSystemId(mapFile);
          mapping.loadMapping(is);
          return mapping;
        } catch (Exception e) {
          logger.error("Error in psml mapping creation", e);
          throw new InitializationException("Error in mapping", e);
        }
      } else {
        throw new InitializationException(
          "PSML Mapping not found or not a file or unreadable: " + mapFile);
      }
    }
    return null;
  }

  public boolean isAdminUser() {
    return adminUser;
  }

  public String getAttachStatus() {
    return attach_status;
  }

  public String getDefaultStatus() {
    return default_status;
  }

  public String getAllUserStatus() {
    return all_user_status;
  }
}
