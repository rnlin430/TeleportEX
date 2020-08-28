/**
Region
Copyright (c) 2020 rnlin
This software is released under the MIT License.
http://opensource.org/licenses/mit-license.php
*/
package com.github.rnlin.rnlibrary;

import org.bukkit.entity.Player;

public class Massage {
    private Player player;

    public void sendMessage(String text) {
        player.sendMessage(text);
    }
}
