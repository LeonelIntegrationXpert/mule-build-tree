/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleContext
 *  org.mule.api.MuleEvent
 *  org.mule.api.debug.DebugInfoProvider
 *  org.mule.api.debug.FieldDebugInfo
 *  org.mule.api.debug.FieldDebugInfoFactory
 *  org.mule.api.transformer.TransformerException
 *  org.mule.api.transformer.TransformerMessagingException
 *  org.mule.util.ClassUtils
 *  org.mule.util.TemplateParser
 */
package org.mule.modules.openair.generated.processors;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.debug.DebugInfoProvider;
import org.mule.api.debug.FieldDebugInfo;
import org.mule.api.debug.FieldDebugInfoFactory;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transformer.TransformerMessagingException;
import org.mule.modules.openair.generated.processors.GetCrystalInfoMessageProcessor;
import org.mule.util.ClassUtils;
import org.mule.util.TemplateParser;

public class GetCrystalInfoMessageProcessorDebuggable
extends GetCrystalInfoMessageProcessor
implements DebugInfoProvider {
    public GetCrystalInfoMessageProcessorDebuggable(String operationName) {
        super(operationName);
    }

    private boolean isConsumable(Object evaluate) {
        return ClassUtils.isConsumable(evaluate.getClass()) || Iterator.class.isAssignableFrom(evaluate.getClass());
    }

    private Object getEvaluatedValue(MuleContext muleContext, MuleEvent muleEvent, String fieldName, Object field) throws NoSuchFieldException, TransformerException, TransformerMessagingException {
        Object evaluate = null;
        if (field != null) {
            evaluate = this.evaluate(TemplateParser.createMuleStyleParser().getStyle(), muleContext.getExpressionManager(), muleEvent.getMessage(), field);
            Type genericType = ((Object)((Object)this)).getClass().getSuperclass().getDeclaredField(fieldName).getGenericType();
            if (!this.isConsumable(evaluate)) {
                evaluate = this.evaluateAndTransform(muleContext, muleEvent, genericType, null, field);
            }
        }
        return evaluate;
    }

    private FieldDebugInfo createDevKitFieldDebugInfo(String name, String friendlyName, Class type, Object value, MuleEvent muleEvent) {
        try {
            return FieldDebugInfoFactory.createFieldDebugInfo((String)friendlyName, (Class)type, (Object)this.getEvaluatedValue(this.muleContext, muleEvent, "_" + name + "Type", value));
        }
        catch (NoSuchFieldException e) {
            return FieldDebugInfoFactory.createFieldDebugInfo((String)friendlyName, (Class)type, (Exception)e);
        }
        catch (TransformerMessagingException e) {
            return FieldDebugInfoFactory.createFieldDebugInfo((String)friendlyName, (Class)type, (Exception)((Object)e));
        }
        catch (TransformerException e) {
            return FieldDebugInfoFactory.createFieldDebugInfo((String)friendlyName, (Class)type, (Exception)((Object)e));
        }
    }

    public List<FieldDebugInfo<?>> getDebugInfo(MuleEvent muleEvent) {
        ArrayList fieldDebugInfoList = new ArrayList();
        return fieldDebugInfoList;
    }
}
