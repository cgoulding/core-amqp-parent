package com.monadiccloud.core.amqp.context.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * @author Connor Goulding
 */
public class MessageMetaDataReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageMetaDataReader.class);

    private final ObjectMapper mapper = new ObjectMapper();

    public List<MessageMetaData> read(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessagingData messagingData = mapper.readValue(fis, MessagingData.class);
            return messagingData.getMessages();
        } catch (IOException e) {
            LOGGER.error("Unable to read meta data file: " + file, e);
        }
        return Collections.emptyList();
    }

    public List<MessageMetaData> read(InputStream is) throws IOException {
        MessagingData messagingData = mapper.readValue(is, MessagingData.class);
        return messagingData.getMessages();
    }
}
