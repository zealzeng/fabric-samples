package com.github.zealzeng.disruptor;

import java.util.Map;

/**
 * 构造一个通用消息对象
 */
public class MessageEvent {

    private long messageId = 0;

    private int messageType = 0;

    private Map<String,String> messageParams = null;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public Map<String, String> getMessageParams() {
        return messageParams;
    }

    public void setMessageParams(Map<String, String> messageParams) {
        this.messageParams = messageParams;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[messageId=").append(this.messageId).append(",messageType=").
           append(this.messageType).append(",params=").append(this.messageParams).append("]");
        return sb.toString();
    }
}
