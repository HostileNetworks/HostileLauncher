/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher.swing;

import com.skcraft.launcher.Instance;
import com.skcraft.launcher.InstanceList;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.util.SharedLocale;

import lombok.Data;
import lombok.extern.java.Log;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FilenameUtils;

@Log
public class InstanceTableModel extends AbstractTableModel {

    private final InstanceList instances;
    private Map<Integer, InstanceIcon> instanceIcons;
    private Icon welcomeIcon;

    public InstanceTableModel(InstanceList instances) {
        this.instances = instances;
        instanceIcons = new ConcurrentHashMap<Integer, InstanceIcon>();
        welcomeIcon = SwingHelper.createIcon(Launcher.class, "welcome_icon.png", 96, 64);
        
        // add an empty entry for "welcome" screen
        instanceIcons.put(0, new InstanceIcon());
    }

    public void update() {
        instances.sort();
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "";
            case 1:
                return SharedLocale.tr("launcher.modpackColumn");
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return ImageIcon.class;
            case 1:
                return String.class;
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                instances.get(rowIndex).setSelected((boolean) (Boolean) value);
                break;
            case 1:
            default:
                break;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return false;
            case 1:
                return false;
            default:
                return false;
        }
    }

    @Override
    public int getRowCount() {
        return instances.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final Instance instance = instances.get(rowIndex);
        switch (columnIndex) {
            case 0:
                if (rowIndex == 0)
                    return welcomeIcon;
                InstanceIcon instanceIcon = instanceIcons.get(rowIndex);
                if (instanceIcon == null) {
                    // freshly-requested icon. First set some defaults
                    Icon iconLocal = SwingHelper.createIcon(Launcher.class, "instance_icon.png", 96, 64);
                    instanceIcon = new InstanceIcon();
                    instanceIcon.setIconLocal(iconLocal);
                    try {
                        instanceIcon.setIconRemote(buildDownloadIcon(ImageIO.read(Launcher.class.getResource("instance_icon.png"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    instanceIcons.put(rowIndex, instanceIcon);
                }
                final InstanceIcon instanceIconLoaded = instanceIcons.get(rowIndex);
                
                if (!instanceIcon.isLoaded()) {
                    // attempt to load instance-specific icon
                    instanceIconLoaded.setLoaded(true);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            String modpackUrlDir = FilenameUtils.getFullPath(instance.getManifestURL().toString());
                            URL instanceIconUrl = null;
                            try {
                                instanceIconUrl = new URL(modpackUrlDir + "icon.png");
                                //HttpURLConnection conn = (HttpURLConnection) instanceIconUrl.openConnection();
                                //conn.setDoOutput(false);
                                //BufferedImage downloadedIcon = ImageIO.read(conn.getInputStream());
                                BufferedImage downloadedIcon = ImageIO.read(instanceIconUrl);
                                instanceIconLoaded.setIconLocal(new ImageIcon(downloadedIcon));
                                instanceIconLoaded.setIconRemote(buildDownloadIcon(downloadedIcon));
                                fireTableDataChanged();
                            } catch (IOException e) {
                                log.warning("Could not download remote icon for instance '" + instance.getName() + "' from " + instanceIconUrl.toString());
                            }
                        }
                    });
                }
                if (instance.isLocal()) {
                    return instanceIcon.getIconLocal();
                } else {
                    return instanceIcon.getIconRemote();
                }
            case 1:
                return instance.getTitle();
            default:
                return null;
        }
    }
    
    private ImageIcon buildDownloadIcon(BufferedImage instanceIcon) {
        try {
            BufferedImage iconBg = instanceIcon;
            BufferedImage iconFg = ImageIO.read(Launcher.class.getResource("download_icon_overlay.png"));
            BufferedImage iconCombined = new BufferedImage(iconBg.getWidth(), iconBg.getHeight(), BufferedImage.TYPE_INT_ARGB );
            Graphics2D canvas = iconCombined.createGraphics();
            canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            canvas.drawImage(iconBg, 0, 0, null);
            canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            canvas.drawImage(iconFg, iconBg.getWidth() - iconFg.getWidth(), iconBg.getHeight() - iconFg.getHeight(), null);
            canvas.dispose();
            return new ImageIcon(iconCombined);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Data
    public class InstanceIcon {
        private Icon iconLocal;
        private Icon iconRemote;
        private boolean isLoaded = false;
    }
}
