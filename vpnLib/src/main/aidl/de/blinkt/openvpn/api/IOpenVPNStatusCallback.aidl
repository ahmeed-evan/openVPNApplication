package de.blinkt.openvpn.api;


interface IOpenVPNStatusCallback {

    oneway void newStatus(in String uuid, in String state, in String message, in String level);
}
