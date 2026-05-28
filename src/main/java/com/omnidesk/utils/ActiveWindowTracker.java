package com.omnidesk.utils;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

public class ActiveWindowTracker {

    // Gets the exact, raw text from the Windows Title Bar
    public static String getRawActiveWindowTitle() {
        HWND foregroundWindow = User32.INSTANCE.GetForegroundWindow();
        if (foregroundWindow == null) return "Unknown";

        char[] windowText = new char[512];
        User32.INSTANCE.GetWindowText(foregroundWindow, windowText, 512);

        return Native.toString(windowText).trim();
    }

    // Strips it down for the Time Tracker (e.g., just "Google Chrome")
    public static String simplifyTitle(String fullTitle) {
        if (fullTitle.isEmpty()) return "Unknown";
        if (fullTitle.contains("Google Chrome")) return "Google Chrome";
        if (fullTitle.contains("Edge")) return "Microsoft Edge";
        if (fullTitle.contains("Code")) return "VS Code";
        if (fullTitle.contains("Discord")) return "Discord";

        return fullTitle;
    }
}