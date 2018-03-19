package com.quickscan.proto;

import com.leapmotion.leap.Finger;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

public class HandTabbedPane extends JTabbedPane
{
    private JTabbedPane left, right;
    
    private HandTabbedPane()
    {
        addTab("Left Hand", left = new JTabbedPane());
        addTab("Right Hand", right = new JTabbedPane());
        for(Finger.Type finType : Finger.Type.values())
        {
            left.addTab(finType.toString().substring(5).toLowerCase(), new JTable());
            right.addTab(finType.toString().substring(5).toLowerCase(), new JTable());
        }
    }
    
    
    private static final HandTabbedPane inst = new HandTabbedPane();
    
    public static HandTabbedPane getInstance()
    {
        return inst;
    }
}