package client;

import server.Message;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;
import server.MsgServer;

public class ControllerTCPProxy extends Object implements MsgServer {

    private static final boolean debugOn = false;
    private static final int buffSize = 4096;
    private static int id = 0;
    private String host;
    private int port;

    public ControllerTCPProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void debug(String message) {
        if (debugOn) {
            System.out.println("debug: " + message);
        }
    }

    public String callMethod(String method, Object[] params) {
        JSONObject theCall = new JSONObject();
        String ret = "{}";
        try {
            debug("Request is: "+theCall.toString());
            theCall.put("method",method);
            theCall.put("id",id);
            theCall.put("jsonrpc","2.0");
            ArrayList<Object> al = new ArrayList<>();
            for (int i=0; i<params.length; i++) {
                al.add(params[i]);
            }

            JSONArray paramsJSON = new JSONArray(al);
            theCall.put("params",paramsJSON);
            Socket sock = new Socket(host,port);
            OutputStream os = sock.getOutputStream();
            InputStream is = sock.getInputStream();
            int numBytesReceived;
            int bufLen = 1024;
            String strToSend = theCall.toString();
            byte bytesReceived[] = new byte[buffSize];
            byte bytesToSend[] = strToSend.getBytes();
            os.write(bytesToSend, 0, bytesToSend.length);
            numBytesReceived = is.read(bytesReceived, 0, bufLen);
            ret = new String(bytesReceived, 0 , numBytesReceived);
            debug("callMethod received from server: "+ret);
            os.close();
            is.close();
            sock.close();
        } catch (Exception ex) {
            System.out.println("exception in callMethod: " + ex.getMessage());
        }
        return ret;
    }

    public boolean writeFile() {
        boolean ret = false;
        String result = callMethod("writeFile", new Object[]{});
        JSONObject res = new JSONObject(result);
        ret = res.optBoolean("result",false);
        return ret;
    }

    public boolean loadJSON() {
        boolean ret = false;
        String result = callMethod("loadJSON", new Object[]{});
        JSONObject res = new JSONObject(result);
        ret = res.optBoolean("result", false);
        return ret;
    }

    public boolean deleteMessage(String header, String toAUserName) {
        boolean ret = false;
        String result = callMethod("deleteMessage", new Object[]{header, toAUserName});
        JSONObject res = new JSONObject(result);
        ret = res.optBoolean("result", false);
        return ret;
    }

    public boolean addMessage(Message m) {
        boolean ret = false;
	System.out.println(m.getJSON());
        String result = callMethod("addMessage", new Object[]{m.getJSON()});
        JSONObject res = new JSONObject(result);
        ret = res.optBoolean("result", false);
        return ret;
    }

    public Message getMessage(String header) {
        Message ret = new Message("Unknown", "Unknown", "Unknown", "Unknown", "Unknown", "Unknown");
        String result = callMethod("getMessage", new Object[]{header});
        JSONObject res = new JSONObject(result);
        JSONObject msgJson = res.optJSONObject("result");
        ret = new Message(msgJson);
        return ret;
    }

    public String[] getMessageFromHeaders(String toAUserName) {
        String[] ret = new String[]{};
        String result = callMethod("getMessageFromHeaders", new Object[]{toAUserName});
        debug("result of getMessageFromHeaders is: " + result);
        JSONObject res = new JSONObject(result);
        JSONArray msgsJson = res.optJSONArray("result");
        ret = new String[msgsJson.length()];
        for (int i = 0; i < msgsJson.length(); i++) {
            ret[i] = msgsJson.optString(i, "unknown");
        }
        return ret;
    }

    public Message getMessageFromIndex(int index) {
        Message ret = new Message("Unknown", "Unknown", "Unknown", "Unknown", "Unknown", "Unknown");
        String result = callMethod("getMessageFromIndex", new Object[]{index});
        JSONObject res = new JSONObject(result);
        JSONObject msgJson = res.optJSONObject("result");
        ret = new Message(msgJson);
        return ret;
    }

    public String getHeader(Message msg) {
        String ret = "unknown";
        String result = callMethod("getHeader", new Object[]{msg.getJSON()});
        JSONObject res = new JSONObject(result);
        ret = res.optString("result", "unknown");
        return ret;
    }
}
