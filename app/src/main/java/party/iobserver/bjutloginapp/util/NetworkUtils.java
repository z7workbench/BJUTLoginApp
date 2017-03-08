package party.iobserver.bjutloginapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * Created by ZeroGo on 2017.2.28.
 */

public class NetworkUtils {
    public final static int STATE_NO_NETWORK = 0;
    public final static int STATE_MOBILE = 1;
    public final static int STATE_BJUT_WIFI = 2;
    public final static int STATE_OTHER_WIFI = 3;

    public static int getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    return STATE_MOBILE;
                case ConnectivityManager.TYPE_WIFI: {
                    if (getWifiSSID(context).replace("\"", "").equals("bjut_wifi"))
                        return STATE_BJUT_WIFI;
                    else return STATE_OTHER_WIFI;
                }
                default:
                    return STATE_NO_NETWORK;
            }
        } else return STATE_NO_NETWORK;
    }

    public static String getWifiSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getSSID();
    }
}
