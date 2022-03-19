package com.firesoftitan.play.titanbox.rfp.fakes;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;

import javax.annotation.Nullable;

public class FakeNetworkManager  extends NetworkManager {
    public FakeNetworkManager(EnumProtocolDirection enumprotocoldirection) {
        super(enumprotocoldirection);
        this.m = new FakeChannel();
        this.n = this.m.remoteAddress();
        this.preparing = false;
    }
    
    // stopReading
    @Override
    public void m() {

    }
    
    //sendPacket
    @Override
    public void a(Packet<?> packet) {

    }
    
    //sendPacket
    @Override
    public void a(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {


    }

}
