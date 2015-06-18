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
package com.aimluck.eip.modules.screens;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.jetspeed.om.security.UserIdPrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.util.ALCellularUtils;

/**
 * 顔写真を画像データとして出力するクラスです。 <br />
 *
 */
public class FileuploadFacePhotoScreen extends FileuploadThumbnailScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileuploadFacePhotoScreen.class.getName());

  /** 取得したい顔画像のユーザーID */
  private static final String KEY_FACE_PHOTO_ID = "uid";

  /**
   *
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    try {
      String uid = rundata.getParameters().getString(KEY_FACE_PHOTO_ID);
      if (uid == null || uid.length() <= 0) {
        return;
      }

      ALBaseUser user =
        (ALBaseUser) JetspeedSecurity.getUser(new UserIdPrincipal(uid));

      byte[] photo;
      if (!ALCellularUtils.isSmartPhone(rundata)) {
        photo = user.getPhoto();

        if (photo == null) {
          return;
        }
        Date date = user.getPhotoModified();
        if (date != null) {
          super.setLastModified(date);
        }
      } else {
        photo = user.getPhotoSmartphone();

        if (photo == null && user.getPhoto() == null) {
          return;
        } else if (photo == null && user.getPhoto() != null) {
          // スマホ用サムネがなければ作ってしまう。
          InputStream tmpStream = new ByteArrayInputStream(user.getPhoto());
          byte[] shrinkPhoto =
            FileuploadUtils.getBytesShrink(
              tmpStream,
              FileuploadUtils.DEF_THUMBNAIL_WIDTH_SMARTPHONE,
              FileuploadUtils.DEF_THUMBNAIL_HEIGHT_SMARTPHONE,
              new ArrayList<String>());
          user.setPhotoSmartphone(shrinkPhoto);
          user.setHasPhotoSmartphone(true);
          user.setPhotoModifiedSmartphone(new Date());
          JetspeedSecurity.saveUser(user);
          photo = shrinkPhoto;
        }

        Date date = user.getPhotoModifiedSmartphone();
        if (date != null) {
          super.setLastModified(date);
        }
      }

      super.setFile(photo);
      super.setFileName("photo.jpg");
      super.doOutput(rundata);
    } catch (Exception e) {
      logger.error("FileuploadFacePhotoScreen.doOutput", e);
    }
  }
}
