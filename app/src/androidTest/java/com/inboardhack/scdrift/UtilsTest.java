package com.inboardhack.scdrift;

import junit.framework.TestCase;

/**
 * Created by benjaminran on 1/30/16.
 */
public class UtilsTest extends TestCase {

    public void testChecksum() throws Exception {
        checksum(new byte[]{111,120,55,127,'_',157});//413
    }
}