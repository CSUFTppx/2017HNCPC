package com.csuft.ppx.bean;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ling on 2017/4/21.
 */

public class Method {
    //最大权值
    final int max_val = Integer.MAX_VALUE;
    //线路
    private List<Path> pathList;
    int pointCount = 16;
    int currentPath = 0;

    //寻路
    List<FindPath> findPathList = new ArrayList<FindPath>();
    //位置信息
    List<Place> placeList;

    public Method(){
        placeList = getPlaceList();
        pathList = getPath();
    }
    public List<Path> getPath(){
        List<Path> pathList = new ArrayList<Path>();
        Path path = new Path(1,2,190);
        pathList.add(path);
        path = new Path(2,3,250);
        pathList.add(path);
        path = new Path(2,1,190);
        pathList.add(path);
        path = new Path(3,2,250);
        pathList.add(path);
        path = new Path(3,4,70);
        pathList.add(path);
        path = new Path(3,10,90);
        pathList.add(path);
        path = new Path(4,3,70);
        pathList.add(path);
        path = new Path(4,6,70);
        pathList.add(path);
        path = new Path(4,5,60);
        pathList.add(path);
        path = new Path(4,8,80);
        pathList.add(path);
        path = new Path(5,4,60);
        pathList.add(path);
        path = new Path(5,7,70);
        pathList.add(path);
        path = new Path(5,9,80);
        pathList.add(path);
        path = new Path(6,4,70);
        pathList.add(path);
        path = new Path(7,5,70);
        pathList.add(path);
        path = new Path(8,4,80);
        pathList.add(path);
        path = new Path(9,5,80);
        pathList.add(path);
        path = new Path(10,3,90);
        pathList.add(path);
        path = new Path(10,11,50);
        pathList.add(path);
        path = new Path(10,12,70);
        pathList.add(path);
        path = new Path(10,14,80);
        pathList.add(path);
        path = new Path(11,13,70);
        pathList.add(path);
        path = new Path(11,15,70);
        pathList.add(path);
        path = new Path(12,10,70);
        pathList.add(path);
        path = new Path(13,11,70);
        pathList.add(path);
        path = new Path(14,10,80);
        pathList.add(path);
        path = new Path(15,11,80);
        pathList.add(path);

        return pathList;

    }
    public List<Place> getPlaceList(){
        List<Place> list = new ArrayList<Place>();
        Place place = new Place(0,0,0);
        list.add(place);
        place = new Place(1,20,0);
        list.add(place);
        place = new Place(2,20,190);
        list.add(place);
        place = new Place(3,250,190);
        list.add(place);
        place = new Place(4,250,120);
        list.add(place);
        place = new Place(5,240,60);
        list.add(place);
        place = new Place(6,180,120);
        list.add(place);
        place = new Place(7,180,60);
        list.add(place);
        place = new Place(8,330,120);
        list.add(place);
        place = new Place(9,330,60);
        list.add(place);
        place = new Place(10,250,380);
        list.add(place);
        place = new Place(11,250,430);
        list.add(place);
        place = new Place(12,180,380);
        list.add(place);
        place = new Place(13,280,430);
        list.add(place);
        place = new Place(14,330,380);
        list.add(place);
        place = new Place(15,330,430);


        return list;
    }
    //获得路径
    public List<FindPath> getShortPath(int n, ImageView car, float density){
        findPathList.clear();
        int x = (int)(car.getLeft()/density);
        int y = (int)(car.getTop()/density);
        float firstMin = max_val;
        float secondMin = max_val;
        int firstLocaltion = 0;
        int secondLocaltion = 0;
        String s = "";
        for(int i=1;i<placeList.size();i++){
            Place place = placeList.get(i);
            float sum = Math.abs(x-place.x)+ Math.abs(y-place.y);
//            s += place.;
            if(sum<firstMin){
                secondMin = firstMin;
                secondLocaltion = firstLocaltion;
                firstMin = sum;
                firstLocaltion = place.localtion;
            }else if(sum<secondMin){
                secondMin = sum;
                secondLocaltion = place.localtion;
            }
        }

        int pathAim[] = new int[pointCount];
        float val[] = new float[pointCount];
        for(int i=0;i<pathAim.length;i++){
            pathAim[i] = currentPath;
            val[i] = max_val;
        }
        val[firstLocaltion] = firstMin;
        val[secondLocaltion] = secondMin;

        Deque<Integer> queue = new LinkedList<Integer>();
        queue.push(firstLocaltion);
        queue.push(secondLocaltion);

        while(!queue.isEmpty()){
            int current = queue.pop();
//            for(int i=0;i<val.length;i++){
//                if(path[current][i]!=max_val){
//                    if(val[current]+path[current][i]<val[i]) {
//                        val[i] = val[current] + path[current][i];
//                        pathAim[i] = current;
//                        queue.push(i);
//                    }
//                }
//            }
            for(int i=0;i<pathList.size();i++){
                if(current==pathList.get(i).startPath){
                    if(val[current]+pathList.get(i).distance<val[pathList.get(i).endPath]){
                        val[pathList.get(i).endPath] = val[current] + pathList.get(i).distance;
                        pathAim[pathList.get(i).endPath] = current;
                        queue.push(pathList.get(i).endPath);
                    }
                }
            }
        }
        String str = "";
        for(int i:pathAim)
            str += i;

        FindPath findPath = null;
        while(pathAim[n]!=currentPath){
            //开始为bl结束为n
            int bl = pathAim[n];
            Place startPlace = placeList.get(bl);
            Place endPlace = placeList.get(n);

            int isxy = Math.abs(startPlace.x-endPlace.x)- Math.abs(startPlace.y-endPlace.y);
            if(isxy>0) {
                if (startPlace.x > endPlace.x) {
                    int valPlace = startPlace.x - endPlace.x;
                    int endLocaltion = endPlace.x;
                    findPath = new FindPath(bl, n, "left", valPlace, endLocaltion);
                } else if (startPlace.x < endPlace.x) {
                    int valPlace = endPlace.x - startPlace.x;
                    int endLocaltion = endPlace.x;
                    findPath = new FindPath(bl, n, "right", valPlace, endLocaltion);
                }
            }else {
                if (startPlace.y < endPlace.y) {
                    int valPlace = endPlace.y - startPlace.y;
                    int endLocaltion = endPlace.y;
                    findPath = new FindPath(bl, n, "button", valPlace, endLocaltion);
                } else if (startPlace.y > endPlace.y) {
                    int valPlace = startPlace.y - endPlace.y;
                    int endLocaltion = endPlace.y;
                    findPath = new FindPath(bl, n, "top", valPlace, endLocaltion);
                }
            }
            findPathList.add(findPath);
            n = bl;
        }
        Place endPlace = placeList.get(n);

        if(x!=endPlace.x || y!=endPlace.y) {
            int isxy = Math.abs(x-endPlace.x)- Math.abs(y-endPlace.y);
            if(isxy>0) {
                if (x > endPlace.x) {
                    int valPlace = x - endPlace.x;
                    int endLocaltion = endPlace.x;
                    findPath = new FindPath(currentPath, n, "left", valPlace, endLocaltion);
                } else if (x < endPlace.x) {
                    int valPlace = endPlace.x - x;
                    int endLocaltion = endPlace.x;
                    findPath = new FindPath(currentPath, n, "right", valPlace, endLocaltion);
                }
            }else {
                if (y < endPlace.y) {
                    int valPlace = endPlace.y - y;
                    int endLocaltion = endPlace.y;
                    findPath = new FindPath(currentPath, n, "button", valPlace, endLocaltion);
                } else if (y > endPlace.y) {
                    int valPlace = y - endPlace.y;
                    int endLocaltion = endPlace.y;
                    findPath = new FindPath(currentPath, n, "top", valPlace, endLocaltion);
                }
            }
            findPathList.add(findPath);

        }
        return findPathList;
    }
}

class Path{
    public int startPath;
    public int endPath;
    public int distance;
    public Path(int startPath,int endPath,int distance){
        this.startPath = startPath;
        this.endPath = endPath;
        this.distance = distance;
    }
}
