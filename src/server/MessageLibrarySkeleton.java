package server;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONArray;

public class MessageLibrarySkeleton extends Object {
    private static final boolean debugOn = false;
    MessageLibrary msgLib;
    public MessageLibrarySkeleton(MessageLibrary msgLib) {
        this.msgLib = msgLib;
    }

    private void debug(String message) {
        if (debugOn) {
            System.out.println("debug: " + message);
        }
    }

    public String callMethod(String request) {
        JSONObject result = new JSONObject();
        try {
            JSONObject theCall = new JSONObject(request);
            debug("Request is: " + theCall.toString());
            String method = theCall.getString("method");
            int id = theCall.getInt("id");
            JSONArray params = null;
            if(!theCall.isNull("params")) {
                params = theCall.getJSONArray("params");
            }
            result.put("id", id);
            result.put("jsonrpc","2.0");
            if (method.equals("deleteMessage")) {
                String header = params.getString(0);
                String toAUserName = params.getString(1);
                debug("Removing msg: " + header);
                msgLib.deleteMessage(header, toAUserName);
                result.put("result", true);
            } else if (method.equals("addMessage")) {
                JSONObject msgJson = params.getJSONObject(0);
                Message msgToAdd = new Message(msgJson);
                debug("Adding message: " + msgToAdd.getSubject());
                msgLib.addMessage(msgToAdd);
                result.put("result", true);
            } else if (method.equals("getMessage")) {
                String header = params.getString(0);
                Message msg = msgLib.getMessage(header);
                JSONObject msgJson = msg.getJSON();
                debug("Get request found: " + msgJson.toString());
                result.put("result", msgJson);
            } else if (method.equals("getMessageFromHeaders")) {
                String toAUserName = params.getString(0);
                String[] headers = msgLib.getMessageFromHeaders(toAUserName);
                JSONArray resArr = new JSONArray();
                for (int i = 0; i < headers.length; i++) {
                    resArr.put(headers[i]);
                }
                debug("getMessageFromHeaders request found: " +resArr.toString());
                result.put("result",resArr);
            } else if (method.equals("getMessageFromIndex")) {
                int msgIndex = params.getInt(0);
                debug("getMessageFromIndex for index " + msgIndex);
                Message m = msgLib.messages.get(msgIndex);
                JSONObject msgJson = m.getJSON();
                result.put("result", msgJson);
            } else if (method.equals("getHeader")) {
                JSONObject msgJson = params.getJSONObject(0);
                Message msg = new Message(msgJson);
                String header = msgLib.getHeader(msg);
                debug("getHeader found: " + header);
                result.put("result", header);
            } else {
                debug("Unable to match method: " + method + ". Returning 0.");
                result.put("result", 0);
            }


        } catch(Exception ex) {
            System.out.println("Exception in callMethod: " + ex.getMessage());
        }
        return result.toString();
    }
}
