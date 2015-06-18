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
package com.aimluck.eip.note;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTNote;
import com.aimluck.eip.cayenne.om.portlet.EipTNoteMap;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.note.util.NoteUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 複数の伝言メモを既読にするクラスです
 */
public class NoteMultiStateUpdate extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(NoteMultiStateUpdate.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {
    try {

      Expression exp1 =
        ExpressionFactory.matchExp(EipTNoteMap.USER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      Expression exp2 =
        ExpressionFactory.inDbExp(EipTNote.NOTE_ID_PK_COLUMN, values);

      List<EipTNoteMap> list =
        Database.query(EipTNoteMap.class, exp1).andQualifier(exp2).fetchList();

      if (list == null || list.size() <= 0) {
        return false;
      }

      for (EipTNoteMap notemap : list) {
        notemap.setNoteStat(NoteUtils.NOTE_STAT_READ);
      }

      Database.commit();
    } catch (Exception ex) {
      Database.rollback();
      logger.error("note", ex);
      return false;
    }
    return true;
  }
}
