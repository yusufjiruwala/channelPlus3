/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.generic;

/**
 *
 * @author yusuf
 */
public interface itableModel {

    public int getRowCount();

    public int getColumnCount();

    public String getColumnName(int columnIndex);

    public Class<?> getColumnClass(int columnIndex);

    public boolean isCellEditable(int rowIndex, int columnIndex);

    public Object getValueAt(int rowIndex, int columnIndex);

    public void setValueAt(Object aValue, int rowIndex, int columnIndex);

    public void addTableModelListener(itableModelListener l);

    public void removeTableModelListener(itableModelListener l);
}
