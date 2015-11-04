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
package com.aimluck.eip.wiki;

import info.bliki.wiki.model.WikiModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 掲示板トピックのResultData <BR>
 *
 */
public class WikiResultData implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiResultData.class.getName());

  private ALNumberField id;

  private ALStringField name;

  /** 親 ID */
  private ALNumberField parent_id;

  /** 親wiki名 */
  private ALStringField parent_name;

  /** 親フラグ */
  private boolean is_parent;

  /** 公開/非公開フラグ */
  private boolean is_public;

  /** 新着フラグ */
  private boolean new_wiki;

  /** メモ */
  private ALStringField note;

  /** 所有者 ID */
  private ALNumberField owner_id;

  /** 登録者名 */
  private ALStringField create_user;

  /** 更新者名 */
  private ALStringField update_user;

  /** 更新者ID */
  private ALNumberField update_user_id;

  /** 作成日 */
  private ALDateTimeField create_date;

  /** 更新日 */
  private ALDateTimeField update_date;

  /** 添付ファイルリスト */
  private List<FileuploadBean> attachmentFileList =
    new ArrayList<FileuploadBean>();

  private WikiModel model;

  /** URL */
  private ALStringField baseImageLink;

  private ALStringField baseImageRawLink;

  /**
   *
   *
   */
  @Override
  public void initField() {
    id = new ALNumberField();
    name = new ALStringField();
    parent_id = new ALNumberField();
    parent_name = new ALStringField();
    owner_id = new ALNumberField();
    create_user = new ALStringField();
    update_user = new ALStringField();
    update_user_id = new ALNumberField();
    create_date = new ALDateTimeField();
    update_date = new ALDateTimeField();
    note = new ALStringField();
    baseImageLink = new ALStringField();
    baseImageRawLink = new ALStringField();

    is_public = true;
    new_wiki = false;
  }

  public void initalizeWikiModel(String imgUri, String internalLinkUri) {
    model = new WikiModel(imgUri, internalLinkUri);
  }

  /**
   * @return
   */
  public ALNumberField getId() {
    return id;
  }

  /**
   * @param i
   */
  public void setId(long i) {
    id.setValue(i);
  }

  /**
   * @return
   */
  public String getName() {
    return name.toString();
  }

  public String getNameHtml() {
    return ALCommonUtils.replaceToAutoCR(name.toString());
  }

  /**
   * @param string
   */
  public void setName(String string) {
    name.setValue(string);
  }

  /**
   * @return
   */
  public String getNote() {
    String htmlText;
    try {
      String noteText = note.toString();

      /* 太字 */
      noteText = noteText.replaceAll("&#39;&#39;&#39;", "'''");

      /* 斜体 */
      noteText = noteText.replaceAll("&#39;&#39;", "''");

      /* 下線 */
      noteText = noteText.replaceAll("&lt;u&gt;", "<u>");
      noteText = noteText.replaceAll("&lt;/u&gt;", "</u>");

      /* 取り消し線 */
      noteText = noteText.replaceAll("&lt;del&gt;", "<del>");
      noteText = noteText.replaceAll("&lt;/del&gt;", "</del>");

      if (null == model) {
        model = new WikiModel("", "");
      }
      htmlText = model.render(noteText);

      /* 添付ファイル */
      for (FileuploadBean attachmentfile : attachmentFileList) {
        htmlText =
          htmlText
            .replace(
              "!" + attachmentfile.getFileName() + "!",
              "<br>"
                + "<a href=\"javascript:void(0);\" onclick=\"aipo.fileupload.showImageDialog('"
                + baseImageRawLink
                + "/ownerid/"
                + owner_id.getValue()
                + "/entityid/"
                + id
                + "/attachmentindex/"
                + attachmentfile.getFileId()
                + "/f/"
                + attachmentfile.getFileName()
                + "');\" >"
                + "<img class='width_thumbs' border='0' alt='"
                + attachmentfile.getFileName()
                + "' title='"
                + attachmentfile.getFileName()
                + "' src='"
                + baseImageLink
                + "?entityid="
                + id
                + "&attachmentindex="
                + attachmentfile.getFileId()
                + "'></a>");
      }

    } catch (Exception e) {
      logger.error("WikiResultData.getNote", e);
      return ALLocalizationUtils.getl10n("WIKI_ERROR");
    }
    return htmlText;
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @return
   */
  public ALDateTimeField getCreateDate() {
    return ALEipUtils.getFormattedTime(create_date);
  }

  /**
   * @return
   */
  public ALDateTimeField getUpdateDate() {
    return ALEipUtils.getFormattedTime(update_date);
  }

  /**
   * @param string
   */
  public void setCreateDate(Date date) {
    create_date.setValue(date);
  }

  /**
   * @param string
   */
  public void setUpdateDate(Date date) {
    if (date == null) {
      return;
    }
    this.update_date.setValue(date);
  }

  public ALStringField getCreateUser() {
    return create_user;
  }

  public void setCreateUser(String str) {
    create_user.setValue(str);
  }

  public void setCreateUser(ALEipUser user) {
    create_user = user.getName();
  }

  public ALStringField getUpdateUser() {
    return update_user;
  }

  public void setUpdateUser(ALEipUser user) {
    update_user = user.getName();
  }

  public void setUpdateUser(String str) {
    update_user.setValue(str);
  }

  public void setBaseInternalLink(String str) {
    baseImageLink.setValue(str);
  }

  public void setBaseImageRawLink(String str) {
    baseImageRawLink.setValue(str);
  }

  /**
   * 公開/非公開フラグ．
   *
   * @return
   */
  public boolean isPublic() {
    return is_public;
  }

  /**
   * @return
   */
  public void setPublicFlag(boolean bool) {
    is_public = bool;
  }

  public boolean isNew() {
    return new_wiki;
  }

  public void setNewFlag(boolean bool) {
    new_wiki = bool;
  }

  public List<FileuploadBean> getAttachmentFileList() {
    return attachmentFileList;
  }

  public void setAttachmentFiles(List<FileuploadBean> list) {
    attachmentFileList = list;
  }

  public ALNumberField getOwnerId() {
    return owner_id;
  }

  public ALNumberField getParentId() {
    return parent_id;
  }

  public ALNumberField getUpdateUserId() {
    return update_user_id;
  }

  public void setOwnerId(long i) {
    this.owner_id.setValue(i);
  }

  public void setParentId(long i) {
    this.parent_id.setValue(i);
  }

  public void setUpdateUserId(long i) {
    this.update_user_id.setValue(i);
  }

  public ALStringField getParentName() {
    return parent_name;
  }

  public String getParentNameHtml() {
    return ALCommonUtils.replaceToAutoCR(parent_name.toString());
  }

  public void setParentName(String str) {
    this.parent_name.setValue(str);
  }

  public boolean isParent() {
    return is_parent;
  }

  public void setParentFlag(boolean bool) {
    this.is_parent = bool;
  }

  public boolean isChild() {
    return 0 != this.parent_id.getValue();
  }

}
