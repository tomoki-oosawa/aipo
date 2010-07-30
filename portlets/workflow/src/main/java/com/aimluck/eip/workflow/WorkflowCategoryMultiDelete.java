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
package com.aimluck.eip.workflow;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequest;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.workflow.util.WorkflowUtils;

/**
 * ワークフローカテゴリの複数削除を行うためのクラスです。 <BR>
 *
 */
public class WorkflowCategoryMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(WorkflowCategoryMultiDelete.class.getName());

  /**
   *
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractCheckList#action(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList,
   *      java.util.ArrayList)
   */
  protected boolean action(RunData rundata, Context context, List<String> values,
      List<String> msgList) {
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();

      List<Integer> intValues = new ArrayList<Integer>();
      int valuesize = values.size();
      for (int i = 0; i < valuesize; i++) {
        String value = values.get(i);
        if (!"1".equals(value)) {
          intValues.add(Integer.valueOf(value));
        }
      }

      SelectQuery query = new SelectQuery(EipTWorkflowCategory.class);
      Expression exp1 = ExpressionFactory.inDbExp(
          EipTWorkflowCategory.CATEGORY_ID_PK_COLUMN, intValues);
      query.setQualifier(exp1);
      List<?> categorylist = dataContext.performQuery(query);
      if (categorylist == null || categorylist.size() == 0)
        return false;

      // カテゴリを削除
      dataContext.deleteObjects(categorylist);

      // これらカテゴリに含まれる依頼をカテゴリ「未分類」に移す。
      List<Integer> categoryIds = new ArrayList<Integer>();
      EipTWorkflowCategory category = null;
      int catesize = categorylist.size();
      for (int i = 0; i < catesize; i++) {
        category = (EipTWorkflowCategory) categorylist.get(i);
        categoryIds.add(category.getCategoryId());
      }

      SelectQuery reqquery = new SelectQuery(EipTWorkflowRequest.class);
      Expression reqexp1 = ExpressionFactory.inDbExp(
          EipTWorkflowRequest.EIP_TWORKFLOW_CATEGORY_PROPERTY + "."
              + EipTWorkflowCategory.CATEGORY_ID_PK_COLUMN, categoryIds);
      reqquery.setQualifier(reqexp1);
      List<?> requests = dataContext.performQuery(reqquery);
      if (requests != null && requests.size() > 0) {
        EipTWorkflowRequest request = null;
        EipTWorkflowCategory defaultCategory = WorkflowUtils
            .getEipTWorkflowCategory(dataContext, Long.valueOf(1));
        int size = requests.size();
        for (int i = 0; i < size; i++) {
          request = (EipTWorkflowRequest) requests.get(i);
          request.setEipTWorkflowCategory(defaultCategory);

          // イベントログに保存
          ALEventlogFactoryService.getInstance().getEventlogHandler().log(
              category.getCategoryId(),
              ALEventlogConstants.PORTLET_TYPE_WORKFLOW_CATEGORY,
              category.getCategoryName());

          // ワークフローカテゴリを削除
          dataContext.deleteObject(category);
        }
      }

      dataContext.commitChanges();
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

}
