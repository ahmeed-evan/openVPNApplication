
package de.blinkt.openvpn.api;


interface ExternalCertificateProvider {

    byte[] getSignedData(in String alias, in byte[] data);


    byte[] getCertificateChain(in String alias);


    Bundle getCertificateMetaData(in String alias);
}
