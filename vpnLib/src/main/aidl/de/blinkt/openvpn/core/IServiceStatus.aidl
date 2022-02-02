
package de.blinkt.openvpn.core;


import de.blinkt.openvpn.core.IStatusCallbacks;
import android.os.ParcelFileDescriptor;
import de.blinkt.openvpn.core.TrafficHistory;


interface IServiceStatus {

         ParcelFileDescriptor registerStatusCallback(in IStatusCallbacks cb);


        void unregisterStatusCallback(in IStatusCallbacks cb);


        String getLastConnectedVPN();


       void setCachedPassword(in String uuid, int type, String password);


       TrafficHistory getTrafficHistory();
}
