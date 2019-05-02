package com.example.subscriber.entities;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("parsedPackets")
public class LocalPacket extends Packet {
    private String fingerprint;

    public LocalPacket(long timestamp, String snifferMac, String deviceMac, boolean global, int sequenceNumber, String ssid, int ssidLen, String fingerprint) {
        super(timestamp, snifferMac, deviceMac, global, sequenceNumber, ssid, ssidLen);
        this.fingerprint = fingerprint;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
