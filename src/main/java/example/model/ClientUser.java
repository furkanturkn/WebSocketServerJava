package example.model;

import com.google.gson.annotations.SerializedName;
import org.java_websocket.WebSocket;

public class ClientUser {

    @SerializedName("androidId")
    String androidId;
    @SerializedName("phoneModel")
    String phoneModel;
    @SerializedName("webSocket")
    WebSocket webSocket = null;

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }
}
