package com.quickscan.proto;

import com.leapmotion.leap.Bone;

public enum JointAngle
{
    JOINT_1(Bone.Type.TYPE_METACARPAL,Bone.Type.TYPE_PROXIMAL,1),
    JOINT_2(Bone.Type.TYPE_PROXIMAL,Bone.Type.TYPE_INTERMEDIATE,2),
    JOINT_3(Bone.Type.TYPE_INTERMEDIATE,Bone.Type.TYPE_DISTAL,3);
    
    public final Bone.Type bone1, bone2;
    public final int joint;
    
    private JointAngle(Bone.Type bone1, Bone.Type bone2, int joint)
    {
        this.bone1 = bone1;
        this.bone2 = bone2;
        this.joint = joint;
    }
}