/**
Region
Copyright (c) 2020 rnlin
This software is released under the MIT License.
http://opensource.org/licenses/mit-license.php
*/
package com.github.rnlin.rnlibrary;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConsoleLog {

    static final String red    = "\u001b[00;31m";
    static final String green  = "\u001b[00;32m";
    static final String yellow = "\u001b[00;33m";
    static final String purple = "\u001b[00;34m";
    static final String pink   = "\u001b[00;35m";
    static final String cyan   = "\u001b[00;36m";
    static final String end    = "\u001b[00m";

    private static String pluginName = null;
    private static boolean enableDebugMessage = false;

    public static void writeLine(@Nullable String text) {
        //if (!enableMessage) return;
        System.out.println(pluginName + end + text + end); }

    public static void sendCaution(@Nullable String text) {
        //if (!enableMessage) return;
        System.out.println("[" + pluginName + ":Caution] " + yellow + text + end);
    }

    public static void sendWarning(@Nullable String text) {
        //if (!enableMessage) return;
        System.out.println("[" + pluginName + ":Warming] " + yellow + text + end);
    }

    public static void sendDescription(@Nullable String text) {
        //if (!enableMessage) return;
        System.out.println("[" + pluginName + ":info] " + cyan + text + end);
    }

    public static void sendDebugMessage(@Nullable String text) {
        if (!enableDebugMessage) return;
        System.out.println("[" + pluginName + ":Debug] " + pink + text + end);
    }

    public static void setPluginName(String name) {
        pluginName = name;
    }

    public static void setEnableDebugMessage(boolean b) {
        enableDebugMessage = b;
    }

    @NotNull
    public static String getGreenMessage(String message) { return green + message + end; }
    @NotNull
    public static String getRedMessage(String message) { return red + message + end; }
    @NotNull
    public static String getYellowMessage(String message) { return yellow + message + end; }
    @NotNull
    public static String getPurpleMessage(String message) { return purple + message + end; }
    @NotNull
    public static String getPinkMessage(String message) { return pink + message + end; }
    @NotNull
    public static String getCyanMessage(String message) { return cyan + message + end; }
}
