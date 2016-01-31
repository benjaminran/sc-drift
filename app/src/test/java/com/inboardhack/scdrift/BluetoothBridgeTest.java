package com.inboardhack.scdrift;

import junit.framework.TestCase;

/**
 * Created by benjaminran on 1/31/16.
 */
public class BluetoothBridgeTest extends TestCase {

    public void testParseMessage() throws Exception {
        String message1 = "acc:100,200,321";
        BluetoothBridge.getInstance().parseMessage(message1);
        System.out.println(Utils.join(",", BluetoothBridge.getInstance().getRealAccel()));
    }

    public void testGetInstance() throws Exception {

    }

    public void testGetInstance1() throws Exception {

    }
}