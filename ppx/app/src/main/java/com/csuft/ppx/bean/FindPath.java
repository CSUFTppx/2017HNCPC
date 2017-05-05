package com.csuft.ppx.bean;

/**
 * Created by ling on 2017/3/29.
 */

//寻路信息
public class FindPath {
    //方向  0-left;1-top;2-right;3-button
    public String direction;
    //开始位置
    public int startPath;
    //结束位置
    public int endPath;
    //路程
    public int val;
    //坐标位置；
    public int endLocation;

    public FindPath(int startPath, int endPath, String direction, int val, int endLocation){
        this.startPath = startPath;
        this.endPath = endPath;
        this.direction = direction;
        this.val = val;
        this.endLocation = endLocation;
    }

    @Override
    public String toString() {
        return direction+startPath+endPath;
    }
}
