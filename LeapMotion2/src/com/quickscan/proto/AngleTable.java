package com.quickscan.proto;

import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class AngleTable extends JTable
{
    private ArrayList<Double> left, right;
    private TableModel table;
    private boolean srcEdit;
    
    public AngleTable(Object[][] data, String[] titles, ArrayList<Double> left, ArrayList<Double> right)
    {
        super(data, titles);
        table = getModel();
        this.left = left;
        this.right = right;
    }
    public void updateTable()
    {
        srcEdit = true;
        for(int i = 0; i < getRowCount(); i++)
        {
            if(i < right.size())
            {
                table.setValueAt(String.format("%.2f",left.get(i)), i, 0);
                table.setValueAt(String.format("%.2f",right.get(i)), i, 1);
            }
            else
            {
                table.setValueAt("-", i, 0);
                table.setValueAt("-", i, 1);
            }
        }
        updateUI();
    }
    private double parse(String str)
    {
        try
        {
            return Double.parseDouble(str);
        }
        catch(Exception err)
        {}
        return Double.NaN;
    }
}