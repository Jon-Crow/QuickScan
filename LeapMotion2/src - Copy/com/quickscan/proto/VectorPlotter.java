package com.quickscan.proto;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Vector;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.JPanel;

public class VectorPlotter extends JPanel
{
    private FingerPlot2D[] fingers;
    private float scale;
    private String msg;
    
    public VectorPlotter(float scale)
    {
        fingers = new FingerPlot2D[0];
        this.scale = scale;
        msg = "";
        setPreferredSize(new Dimension(800, 500));
    }
    private void plot2D(ArrayList<FingerPlot3D> fins)
    {
        double yAvg = 0;
        int points = 0;
        for(FingerPlot3D plot : fins)
        {
            for(Vector v : plot.bones)
            {
                yAvg += v.getY();
                points++;
            }
        }
        yAvg /= points*scale;
        fingers = new FingerPlot2D[fins.size()];
        FingerPlot3D fin;
        for(int i = 0; i < fingers.length; i++)
        {
            fin = fins.get(i);
            fingers[i] = new FingerPlot2D();
            for(Vector v : fin.bones)
                fingers[i].addJoint(v.getX(), v.getZ());
        }
    }
    public void plotHand(HandList hands)
    {
        ArrayList<FingerPlot3D> fins = new ArrayList<>();
        for(Hand hand : hands)
        {
            double yAvg;
            for(Finger fin : hand.fingers())
                fins.add(new FingerPlot3D(fin,scale));
            plot2D(fins);
        }
    }
    public void setMessage(String msg)
    {
        this.msg = msg;
    }
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setColor(Color.WHITE);
        g.drawString(msg, 25, 25);
        try
        {
            int cenX = getWidth()/2, cenY = getHeight()/2;
            for(int i = 0; i < fingers.length; i++)
            {
                if(fingers[i] == null) return;
                Joint j = fingers[i].headJoint;
                if(j == null) return;
                while(j.next != null)
                {
                    g.fillOval(j.x+cenX-5, j.y+cenY-5, 10, 10);
                    g.drawLine(j.x+cenX, j.y+cenY, j.next.x+cenX, j.next.y+cenY);
                    j = j.next;
                }
                g.fillOval(j.x+cenX-5, j.y+cenY-5, 10, 10);
            }
        }
        catch(Exception err)
        {
            System.err.println(err.getMessage());
        }
    }
    
    private class FingerPlot3D
    {
        private ArrayList<Vector> bones;
        
        private FingerPlot3D(Finger fin, float scale)
        {
            bones = new ArrayList<>();
            bones.add(fin.bone(Bone.Type.TYPE_METACARPAL).center().times(scale));
            bones.add(fin.bone(Bone.Type.TYPE_PROXIMAL).center().times(scale));
            bones.add(fin.bone(Bone.Type.TYPE_INTERMEDIATE).center().times(scale));
            bones.add(fin.bone(Bone.Type.TYPE_DISTAL).center().times(scale));
        }
    }
    private class FingerPlot2D
    {
        private Joint headJoint;
        
        private FingerPlot2D()
        {
            headJoint = null;
        }
        private void addJoint(double x, double y)
        {
            if(headJoint == null) headJoint = new Joint(x, y);
            else
            {
                Joint cur = headJoint;
                while(cur.next != null)
                    cur = cur.next;
                cur.next = new Joint(x, y);
            }
        }
    }
    private class Joint
    {
        private int x, y;
        private Joint next;
        
        private Joint(double x, double y)
        {
            this.x = (int)(x+0.5);
            this.y = (int)(y+0.5);
            next = null;
        }
    }
}