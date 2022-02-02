

package de.blinkt.openvpn.core;



interface IOpenVPNServiceInternal {

    boolean protect(int fd);

    void userPause(boolean b);

    boolean stopVPN(boolean replaceConnection);

    void addAllowedExternalApp(String packagename);

    boolean isAllowedExternalApp(String packagename);

    void challengeResponse(String repsonse);
}
