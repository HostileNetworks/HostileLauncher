/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class InstanceTable extends DefaultTable {

    public InstanceTable() {
        super();
        setTableHeader(null);
    }

    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        try {
            getColumnModel().getColumn(0).setPreferredWidth(64);
        } catch (ArrayIndexOutOfBoundsException e) {}
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        ((JComponent)component).setBorder(new MatteBorder(0, 0, (row == 0 ? 1 : 0), 0, Color.GRAY) );
        return component;
    }

    @Override
    public void setRowHeight() {
        setRowHeight(64);
    }
}
