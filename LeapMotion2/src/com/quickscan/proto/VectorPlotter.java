package com.quickscan.proto;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Vector;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * @author Jonathan Crow
 */
public class VectorPlotter extends JPanel
{
    private ArrayList<Double> rightAngles, leftAngles;
    private AngleTable angleTable;
    private FingerPlot2D[] fingers;
    private float scale;
    private String msg;
    private Finger.Type measureType;
    private JointAngle measureJoint;
    private double leftAngle, rightAngle, avgRight, avgLeft;
    private boolean freeze;
    private BufferedImage cam;
    
    public VectorPlotter(float scale)
    {
        rightAngles = new ArrayList<>();
        leftAngles = new ArrayList<>();
        fingers = new FingerPlot2D[0];
        this.scale = scale;
        msg = "";
        measureType = Finger.Type.TYPE_INDEX;
        measureJoint = JointAngle.JOINT_1;
        leftAngle = rightAngle = avgLeft = avgRight = 0;
        freeze = false;
        setPreferredSize(new Dimension(800, 500));
    }
    /**
     * Converts the provided HandList into data that is easily displayable by
     * the associated JPanel
     * @param hands The most recent list of hands from the leap motion API
     */
    public void plotHand(HandList hands)
    {
        ArrayList<FingerPlot2D> fins = new ArrayList<>();
        leftAngle = rightAngle = 0;
        for(Hand hand : hands)
        {
            for(Finger fin : hand.fingers())
            {
                //Projecting 2d
                FingerPlot2D plot = new FingerPlot2D(hand.isLeft(), fin.type());
                //plot.addVector(hand.wristPosition().times(scale));
                plot.addVector(fin.bone(Bone.Type.TYPE_METACARPAL).prevJoint().times(scale));
                plot.addVector(fin.bone(Bone.Type.TYPE_METACARPAL).nextJoint().times(scale));
                plot.addVector(fin.bone(Bone.Type.TYPE_PROXIMAL).nextJoint().times(scale));
                plot.addVector(fin.bone(Bone.Type.TYPE_INTERMEDIATE).nextJoint().times(scale));
                plot.addVector(fin.bone(Bone.Type.TYPE_DISTAL).nextJoint().times(scale));
                fins.add(plot);
                
                //calculating angle
                if(fin.type() == measureType)
                {
                    Vector dir1 = fin.bone(measureJoint.bone1).direction(),
                           dir2 = fin.bone(measureJoint.bone2).direction();
                    double angle = 180-Math.toDegrees(dir1.angleTo(dir2));
                    if(plot.left) leftAngle = angle;
                    else rightAngle = angle;
                }
            }
        }
        fingers = fins.toArray(new FingerPlot2D[0]);
        setMessage(String.format("Left Angle: %.2f     Right Angle: %.2f",leftAngle,rightAngle));
    }
    /**
     * Changes the message to be displayed on the main panel
     * @param msg the message to be displayed
     */
    public void setMessage(String msg)
    {
        this.msg = msg;
    }
    /**
     * Changes the finger type to measure
     * @param measure the finger type from the leap motion API
     */
    public void setMeasureType(Finger.Type measure)
    {
        this.measureType = measure;
    }
    /**
     * Changes the joint to be measured
     * @param measureJoint 
     */
    public void setMeasureJoint(JointAngle measureJoint)
    {
        this.measureJoint = measureJoint;
    }
    public void setFrozen(boolean freeze)
    {
        this.freeze = freeze;
    }
    public boolean isFrozen()
    {
        return freeze;
    }
    public void capture()
    {
        while(rightAngles.size() >= 10)
        {
            rightAngles.remove((int)0);
            leftAngles.remove((int)0);
        }
        rightAngles.add(rightAngle);
        leftAngles.add(leftAngle);
        for(int i = 0; i < rightAngles.size(); i++)
        {
            avgRight += rightAngles.get(i);
            avgLeft += leftAngles.get(i);
        }
        avgRight /= rightAngles.size();
        avgLeft /= leftAngles.size();
        if(angleTable == null)
        {
            Object[][] nums = new Object[10][2];
            for(int x = 0; x < 10; x++)
                for(int y = 0; y < 2; y++)
                    nums[x][y] = "-";
            angleTable = new AngleTable(nums, new String[]{"Left Hand","Right Hand"},leftAngles,rightAngles);
            UI.openSubFrame(new JScrollPane(angleTable));
        }
        updateTable();
    }
    public void clearAngles()
    {
        rightAngles.clear();
        leftAngles.clear();
        updateTable();
    }
    public void updateTable()
    {
        if(angleTable != null) angleTable.updateTable();
    }
    public void setCameraImage(BufferedImage cam)
    {
        this.cam = cam;
    }
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(cam != null) g.drawImage(cam, 0, 0, getWidth(), getHeight(), this);
        else
        {
            g.setColor(Color.BLACK);
            g.fillRect(0,0,getWidth(),getHeight());
        }
        g.setColor(Color.WHITE);
        g.drawString(msg, 25, 25);
        if(avgLeft != 0 || avgRight != 0) g.drawString(String.format("Average Angles: Right Angle = %.2f        Left Angle = %.2f", avgRight, avgLeft), 25, 50);
        try
        {
            int cenX = getWidth()/2, cenY = getHeight()/2;
            for(int i = 0; i < fingers.length; i++)
            {
                if(fingers[i] == null) return;
                if(fingers[i].type == measureType) g.setColor(Color.BLUE);
                else g.setColor(fingers[i].left ? Color.YELLOW : Color.RED);
                Joint j = fingers[i].headJoint;
                int jCount = 0;
                if(j == null) return;
                Color prevClr = g.getColor();
                while(j.next != null)
                {
                    if(fingers[i].type == measureType && measureJoint.joint == jCount)
                    {
                        prevClr = g.getColor();
                        g.setColor(Color.WHITE);
                    }
                    g.fillOval(j.x+cenX-5, j.y+cenY-5, 10, 10);
                    g.setColor(prevClr);
                    g.drawLine(j.x+cenX, j.y+cenY, j.next.x+cenX, j.next.y+cenY);
                    j = j.next;
                    jCount++;
                }
                g.fillOval(j.x+cenX-5, j.y+cenY-5, 10, 10);
            }
        }
        catch(Exception err)
        {
            System.err.println(err.getMessage());
        }
    }
    
    private class FingerPlot2D
    {
        private boolean left;
        private Finger.Type type;
        private Joint headJoint;
        
        private FingerPlot2D(boolean left, Finger.Type type)
        {
            this.left = left;
            this.type = type;
            headJoint = null;
        }
        private void addVector(Vector v)
        {
            addJoint(v.getX(), v.getZ());
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