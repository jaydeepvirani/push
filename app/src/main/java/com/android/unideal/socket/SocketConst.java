package com.android.unideal.socket;

/** Created by bhavdip on 6/12/16. */
public class SocketConst {

  public static final String EVENT_AUTH_REQ = "requestAuthentication";
  public static final String EVENT_ON_AUTH_SUCCESS = "onAuthSuccess";

  public static final String EVENT_ASK_MESSAGES_LIST = "requestMessages";
  public static final String EVENT_ON_MESSAGES_LIST_RECEIVED = "onMessagesReceived";

  public static final String EVENT_MSG_SEND = "messageSend";
  public static final String EVENT_ON_MSG_RECEIVED = "onMessageReceived";

  public static final String EVENT_ASK_HISTORY = "requestHistory";
  public static final String EVENT_ON_RECEIVE_HISTORY = "onHistoryReceived";

  public static final String EVENT_ON_ERROR = "onError";

  public static final String EVENT_ASK_UPDATE_MESSAGES = "requestUpdatedMessages";
  public static final String EVENT_ON_UPDATE_MESSAGES = "onUpdatedMessages";

  public static final String EVENT_SEND_STATUS = "statusSend";
  public static final String EVENT_RECEIVED_STATUS = "onStatusReceived";

  public static final String EVENT_REQ_SEND_PROPOSAL = "requestSendProposal";
  public static final String EVENT_ON_NEW_AGENT_MSG = "onNewAgentMessages";

  public static final String EVENT_ON_RECEIVED_MESSAGE = "onReceivedMessages";
  public static final String EVENT_ON_READ_MESSAGE = "onReadMessages";

  public static final String EVENT_ON_UNREAD_COUNT = "onUnreadCount";

  public static final String EVENT_GET_UNREAD_COUNT = "getUnreadCount";
}
