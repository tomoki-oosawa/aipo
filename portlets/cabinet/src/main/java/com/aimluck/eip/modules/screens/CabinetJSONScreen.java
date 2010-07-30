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
package com.aimluck.eip.modules.screens;

import java.util.ArrayList;
import java.util.Hashtable;

import net.sf.json.JSONObject;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cabinet.FolderInfo;
import com.aimluck.eip.cabinet.beans.CabinetBean;
import com.aimluck.eip.cabinet.util.CabinetUtils;

/**
 * 共有フォルダのフォルダをJSONデータとして出力するクラスです。 <br />
 * 
 */
public class CabinetJSONScreen extends ALJSONScreen {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(CabinetJSONScreen.class.getName());

  private ArrayList<FolderInfo> folder_hierarchy_list;

  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = "";

    CabinetBean bean = null;
    ArrayList beanitems = new ArrayList();
    try {
      ArrayList folders = CabinetUtils.getFolderList();
      if (folders != null && folders.size() > 0) {
        int folderssize = folders.size();
        for (int i = 0; i < folderssize; i++) {
          FolderInfo folder = (FolderInfo) folders.get(i);
          if (folder.getFolderId() == 1) {
            // ルートフォルダのBeanは作らない
            continue;
          }
          bean = new CabinetBean();
          bean.initField();
          bean.setCabinetId(folder.getFolderId());
          bean.setCabinetName(folder.getFolderName());
          bean.settype(folder.getHierarchyIndex());
          // child要素を検索
          for (int j = 0; j < folderssize; j++) {
            FolderInfo folder_child = (FolderInfo) folders.get(j);
            if (folder_child.getParentFolderId() == folder.getFolderId()) {
              Hashtable hash = new Hashtable();
              hash.put("_reference", folder_child.getFolderName());
              bean.getchildren().add(hash);
            }
          }
          beanitems.add(bean);
        }
      }

      // データベースからの検索用
      // DataContext dataContext = DatabaseOrmService.getInstance()
      // .getDataContext();
      // SelectQuery query = new SelectQuery(EipTCabinetFolder.class);
      // List list = dataContext.performQuery(query);
      // if (list != null && list.size() > 0) {
      // int listsize = list.size();
      // for (int i = 0; i < listsize; i++) {
      // EipTCabinetFolder folder = (EipTCabinetFolder) list.get(i);
      // if(folder.getParentId() == 0){
      // //ルートフォルダのBeanは作らない
      // continue;
      // }
      // bean = new CabinetBean();
      // bean.initField();
      // bean.setCabinetId(folder.getFolderId().intValue());
      // bean.setCabinetName(folder.getFolderName());
      // bean.settype("depth1");
      // // child要素を検索
      // for (int j = 0; j < listsize; j++) {
      // EipTCabinetFolder folder_child = (EipTCabinetFolder) list.get(j);
      // if (folder_child.getFolderId() == folder.getFolderId().intValue()) {
      // Hashtable hash = new Hashtable();
      // hash.put("_reference", folder_child.getFolderName());
      // bean.getCabinetChild().add(hash);
      // }
      // }
      // beanitems.add(bean);
      // }
      // }

      JSONObject json = new JSONObject();
      ArrayList jsonlist = new ArrayList();
      int beansize = beanitems.size();
      for (int i = 0; i < beansize; i++) {
        jsonlist.add(beanitems.get(i));
      }
      json.put("items", jsonlist);
      json.put("identifier", "cabinetName");
      json.put("label", "cabinetName");

      result = json.toString();

    } catch (Exception e) {
      logger.error("[ERROR]", e);
    }

    return result;
  }

}
