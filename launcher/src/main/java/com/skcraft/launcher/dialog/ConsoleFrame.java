/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher.dialog;

import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.swing.LinedBoxPanel;
import com.skcraft.launcher.swing.MessageLog;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.PastebinPoster;
import com.skcraft.launcher.util.SharedLocale;
import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.skcraft.launcher.util.SharedLocale.tr;

/**
 * A frame capable of showing messages.
 */
public class ConsoleFrame extends JFrame {

    private static ConsoleFrame globalFrame;

    @Getter private final Image trayIcon;

    @Getter private final MessageLog messageLog;
    @Getter private LinedBoxPanel buttonsPanel;

    private boolean registeredGlobalLog = false;

    /**
     * Construct the frame.
     *
     * @param numLines number of lines to show at a time
     * @param colorEnabled true to enable a colored console
     */
    public ConsoleFrame(int numLines, boolean colorEnabled) {
        this(SharedLocale.tr("console.title"), numLines, colorEnabled);
    }

    /**
     * Construct the frame.
     * 
     * @param title the title of the window
     * @param numLines number of lines to show at a time
     * @param colorEnabled true to enable a colored console
     */
    public ConsoleFrame(@NonNull String title, int numLines, boolean colorEnabled) {
        messageLog = new MessageLog(numLines, colorEnabled);
        trayIcon = SwingHelper.createImage(Launcher.class, "icon.png");

        setTitle(title);
        setIconImage(trayIcon);

        setSize(new Dimension(650, 400));
        initComponents();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                performClose();
            }
        });
    }

    /**
     * Add components to the frame.
     */
    private void initComponents() {
        buttonsPanel = new LinedBoxPanel(true);

        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(buttonsPanel, BorderLayout.NORTH);
        add(messageLog, BorderLayout.CENTER);

    }

    /**
     * Register the global logger if it hasn't been registered.
     */
    private void registerLoggerHandler() {
        if (!registeredGlobalLog) {
            getMessageLog().registerLoggerHandler();
            registeredGlobalLog = true;
        }
    }

    /**
     * Attempt to perform window close.
     */
    protected void performClose() {
        messageLog.detachGlobalHandler();
        messageLog.clear();
        registeredGlobalLog = false;
        dispose();
    }

    public static void showMessages() {
        ConsoleFrame frame = globalFrame;
        if (frame == null) {
            frame = new ConsoleFrame(10000, false);
            globalFrame = frame;
            frame.setTitle(SharedLocale.tr("console.launcherConsoleTitle"));
            frame.registerLoggerHandler();
            frame.setVisible(true);
        } else {
            frame.setVisible(true);
            frame.registerLoggerHandler();
            frame.requestFocus();
        }
    }

    public static void hideMessages() {
        ConsoleFrame frame = globalFrame;
        if (frame != null) {
            frame.setVisible(false);
        }
    }

}
