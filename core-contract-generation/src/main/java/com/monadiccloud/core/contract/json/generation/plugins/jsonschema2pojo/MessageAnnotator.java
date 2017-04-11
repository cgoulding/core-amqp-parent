package com.monadiccloud.core.contract.json.generation.plugins.jsonschema2pojo;

import com.fasterxml.jackson.databind.JsonNode;
import com.monadiccloud.core.amqp.message.annotation.Message;
import com.monadiccloud.core.amqp.message.annotation.MessageContentType;
import com.monadiccloud.core.amqp.message.annotation.stereotypes.MessageError;
import com.monadiccloud.core.amqp.message.annotation.stereotypes.MessageEvent;
import com.monadiccloud.core.amqp.message.annotation.stereotypes.MessageReply;
import com.monadiccloud.core.amqp.message.annotation.stereotypes.MessageRequest;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import org.jsonschema2pojo.AbstractAnnotator;

public class MessageAnnotator extends AbstractAnnotator {
    public MessageAnnotator() {
        super();
    }

    @Override
    public void propertyInclusion(JDefinedClass clazz, JsonNode schema) {
        JsonNode meta = schema.get("_meta");
        if (meta != null) {
            annotateMessage(clazz, meta);
            annotateStereotype(clazz, meta);
        }
    }

    private void annotateMessage(JDefinedClass clazz, JsonNode meta) {
        JsonNode message = meta.get("message");
        if (message != null) {
            JAnnotationUse messageAnnotation = null;
            messageAnnotation = clazz.annotate(Message.class);
            messageAnnotation.param("value", message.asText());

            JsonNode version = meta.get("version");
            if (version != null) {
                messageAnnotation.param("version", version.asText());
            }

            JsonNode content = meta.get("content");
            if (content != null) {
                messageAnnotation.param("content", MessageContentType.valueOf(content.asText().toUpperCase()));
            }
        }
    }

    private void annotateStereotype(JDefinedClass clazz, JsonNode meta) {
        JsonNode stereotype = meta.get("stereotype");
        if (stereotype != null) {
            String stereoTypeValue = stereotype.asText();
            if ("REQUEST".equalsIgnoreCase(stereoTypeValue)) {
                clazz.annotate(MessageRequest.class);
            } else if ("REPLY".equalsIgnoreCase(stereoTypeValue)) {
                clazz.annotate(MessageReply.class);
            } else if ("EVENT".equalsIgnoreCase(stereoTypeValue)) {
                clazz.annotate(MessageEvent.class);
            } else if ("ERROR".equalsIgnoreCase(stereoTypeValue)) {
                clazz.annotate(MessageError.class);
            }
        }
    }
}
