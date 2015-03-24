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
package com.aimluck.eip.workflow;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequest;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.workflow.util.WorkflowUtils;

/**
 * ワークフローカテゴリの複数削除を行うためのクラスです。 <BR>
 * 
 */
public class WorkflowCategoryMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger =
    JetspeedLogFactoryService.getLogger(WorkflowCategoryMultiDelete.class
      .getName());

  /**
   * 
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {
    try {

      List<Integer> intValues = new ArrayList<Integer>();
      int valuesize = values.size();
      for (int i = 0; i < valuesize; i++) {
        String value = values.get(i);
        if (!"1".equals(value)) {
          intValues.add(Integer.valueOf(value));
        }
      }

      SelectQuery<EipTWorkflowCategory> query =
        Database.query(EipTWorkflowCategory.class);
      Expression exp1 =
        ExpressionFactory.inDbExp(
          EipTWorkflowCategory.CATEGORY_ID_PK_COLUMN,
          intValues);
      query.setQualifier(exp1);
      List<EipTWorkflowCategory> categorylist = query.fetchList();
      if (categorylist == null || categorylist.size() == 0) {
        return false;
      }

      // カテゴリを削除
      Database.deleteAll(categorylist);

      // これらカテゴリに含まれる依頼をカテゴリ「その他」に移す。
      List<Integer> categoryIds = new ArrayList<Integer>();
      for (EipTWorkflowCategory category : categorylist) {
        categoryIds.add(category.getCategoryId());
      }
      SelectQuery<EipTWorkflowRequest> reqquery =
        Database.query(EipTWorkflowRequest.class);
      Expression reqexp1 =
        ExpressionFactory.inDbExp(
          EipTWorkflowRequest.EIP_TWORKFLOW_CATEGORY_PROPERTY
            + "."
            + EipTWorkflowCategory.CATEGORY_ID_PK_COLUMN,
          categoryIds);
      reqquery.setQualifier(reqexp1);
      List<EipTWorkflowRequest> requests = reqquery.fetchList();
      if (requests != null && requests.size() > 0) {
        EipTWorkflowCategory defaultCategory =
          WorkflowUtils.getEipTWorkflowCategory(Long.valueOf(1));
        for (EipTWorkflowRequest request : requests) {
          request.setEipTWorkflowCategory(defaultCategory);
        }
      }

      Database.commit();

      for (EipTWorkflowCategory category : categorylist) {
        // イベントログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          category.getCategoryId(),
          ALEventlogConstants.PORTLET_TYPE_WORKFLOW_CATEGORY,
          category.getCategoryName());
      }

    } catch (Exception ex) {
      Database.rollback();
      logger.error("workflow", ex);
      return false;
    }
    return true;
  }

}
