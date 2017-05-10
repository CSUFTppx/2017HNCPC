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
    int currentPath = 0;

    //寻路
    private List<FindPath> findPathList = new ArrayList<FindPath>();
    //位置信息
    private List<Place> placeList;

    public Method(){
        placeList = getPlaceList();
        pathList = getPath();
    }
    //点所能到达的路径
    public List<Path> getPath(){
        List<Path> pathList = new ArrayList<Path>();
        Path path = new Path(1,20);
        pathList.add(path);
        path = new Path(20,21);
        pathList.add(path);
        path = new Path(21,22);
        pathList.add(path);
        path = new Path(22,23);
        pathList.add(path);
        path = new Path(23,2);
        pathList.add(path);

        path = new Path(2,24);
        pathList.add(path);
        path = new Path(24,25);
        pathList.add(path);
        path = new Path(25,26);
        pathList.add(path);
        path = new Path(26,14);
        pathList.add(path);
        path = new Path(2,3);
        pathList.add(path);

        path = new Path(3,4);
        pathList.add(path);
        path = new Path(3,6);
        pathList.add(path);
        path = new Path(4,5);
        pathList.add(path);
        path = new Path(5,17);
        pathList.add(path);
        path = new Path(17,16);
        pathList.add(path);
        path = new Path(6,18);
        pathList.add(path);
        path = new Path(18,15);
        pathList.add(path);
        path = new Path(8,19);
        pathList.add(path);
        path = new Path(19,13);
        pathList.add(path);
        path = new Path(9,27);
        pathList.add(path);
        path = new Path(27,12);
        pathList.add(path);
        path = new Path(10,28);
        pathList.add(path);
        path = new Path(28,11);
        pathList.add(path);
        path = new Path(11,12);
        pathList.add(path);
        path = new Path(12,13);
        pathList.add(path);
        path = new Path(13,14);
        pathList.add(path);
        path = new Path(14,15);
        pathList.add(path);
        path = new Path(15,16);
        pathList.add(path);

        return pathList;

    }
    //各个点的坐标
    public List<Place> getPlaceList(){
        List<Place> list = new ArrayList<Place>();
        Place place = new Place(0,0,0);
        list.add(place);
        place = new Place(1,300,48);//300 - 260
        list.add(place);
        place = new Place(2,300,305);
        list.add(place);
        place = new Place(3,300,408);
        list.add(place);
        place = new Place(4,300,488);
        list.add(place);
        place = new Place(5,190,488);
        list.add(place);
        place = new Place(6,190,408);
        list.add(place);
        place = new Place(7,190,303);
        list.add(place);
        place = new Place(8,190,200);
        list.add(place);
        place = new Place(9,190,150);
        list.add(place);
        place = new Place(10,190,58);
        list.add(place);
        place = new Place(11,60,58);
        list.add(place);
        place = new Place(12,60,150);
        list.add(place);
        place = new Place(13,60,200);
        list.add(place);
        place = new Place(14,60,305);
        list.add(place);
        place = new Place(15,60,408);
        list.add(place);
        place = new Place(16,60,488);
        list.add(place);


        //辅助坐标
        place = new Place(17,160,488);
        list.add(place);
        place = new Place(18,160,408);
        list.add(place);
        place = new Place(19,160,200);
        list.add(place);

        place = new Place(20,300,98);
        list.add(place);
        place = new Place(21,300,150);
        list.add(place);
        place = new Place(22,300,200);
        list.add(place);
        place = new Place(23,300,250);
        list.add(place);

        place = new Place(24,240,305);
        list.add(place);
        place = new Place(25,180,305);
        list.add(place);
        place = new Place(26,120,305);
        list.add(place);

        place = new Place(27,160,150);
        list.add(place);
        place = new Place(28,160,58);
        list.add(place);


        return list;
    }
    //获得路径
    public List<FindPath> getShortPath(int n, ImageView car, float density){
        int pointCount = placeList.size();
        List<FindPath> findPathList = new ArrayList<FindPath>();
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

        //a到b
        int pathAim[] = new int[pointCount];
        //距离
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
            for(int i=0;i<pathList.size();i++){
                if(current==pathList.get(i).startPath){
                    if(val[current]+pathList.get(i).distance<val[pathList.get(i).endPath]){
                        val[pathList.get(i).endPath] = val[current] + pathList.get(i).distance;
                        pathAim[pathList.get(i).endPath] = current;
                        queue.push(pathList.get(i).endPath);
                    }
                }else if(current==pathList.get(i).endPath){
                    if(val[current]+pathList.get(i).distance<val[pathList.get(i).startPath]){
                        val[pathList.get(i).startPath] = val[current] + pathList.get(i).distance;
                        pathAim[pathList.get(i).startPath] = current;
                        queue.push(pathList.get(i).startPath);
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
    class Path{
        public int startPath;
        public int endPath;
        public int distance;
        public Path(int startPath,int endPath){
            this.startPath = startPath;
            this.endPath = endPath;
            List<Place> places = getPlaceList();
            Place ps = null;
            Place pe = null;
            for(Place p:places){
                if(p.localtion==startPath)
                    ps = p;
                if(p.localtion==endPath)
                    pe = p;

            }
            if(ps!=null&&pe!=null){
                if(Math.abs(ps.x-pe.x)>Math.abs(ps.y-pe.y)){
                    this.distance = Math.abs(ps.x-pe.x);
                }else{
                    this.distance = Math.abs(ps.y-pe.y);
                }
            }

        }
    }
}


