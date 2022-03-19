package com.firesoftitan.play.titanbox.rfp.managers;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;

public class FakeNetworkManager  extends NetworkManager {

    public FakeNetworkManager(EnumProtocolDirection enumprotocoldirection) {
        super(enumprotocoldirection);
    }

    // stopReading
    @Override
    public void m() {

    }

}
