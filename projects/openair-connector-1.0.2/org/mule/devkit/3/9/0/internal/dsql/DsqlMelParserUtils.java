/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleContext
 *  org.mule.api.MuleEvent
 *  org.mule.api.expression.ExpressionManager
 *  org.mule.util.TemplateParser
 *  org.mule.util.TemplateParser$PatternInfo
 *  org.mule.util.TemplateParser$TemplateCallback
 */
package org.mule.devkit.3.9.0.internal.dsql;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.expression.ExpressionManager;
import org.mule.util.TemplateParser;

public class DsqlMelParserUtils {
    private TemplateParser parser = TemplateParser.createMuleStyleParser();

    public Object parseDsql(MuleContext muleContext, MuleEvent event, Object query) {
        if (!(query instanceof String)) {
            return query;
        }
        String stringQuery = (String)query;
        TemplateParser.PatternInfo style = TemplateParser.createMuleStyleParser().getStyle();
        ExpressionManager expressionManager = muleContext.getExpressionManager();
        if (stringQuery.startsWith(style.getPrefix()) && stringQuery.endsWith(style.getSuffix())) {
            return expressionManager.evaluate(stringQuery, event);
        }
        return this.parse(expressionManager, event, stringQuery);
    }

    private Object parse(ExpressionManager expressionManager, MuleEvent event, String source) {
        return this.parser.parse((TemplateParser.TemplateCallback)new /* Unavailable Anonymous Inner Class!! */, source);
    }
}
