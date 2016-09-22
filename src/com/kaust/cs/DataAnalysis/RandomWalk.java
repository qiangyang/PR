package com.kaust.cs.DataAnalysis;

import com.kaust.cs.DataParse.ReferenceNet;
import com.kaust.cs.Tools.GraphModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
/**
 * Created by yangq0a on 16/9/11.
 */
public class RandomWalk {
    public static final long MAX_ITERATIOM_TIMES = 1000;
    public static final double MIN_ERRORS = 0.0001;
    public HashMap<Long, double[][]> getTransMatrix(){
        ReferenceNet rn = new ReferenceNet();
        HashMap<Long, GraphModel> oringalClusters = rn.createReferenceNetwork();
        HashMap<Long, double[][]> transMatrix = new HashMap<Long, double[][]>();
        for(long id: oringalClusters.keySet()){
            GraphModel gm = oringalClusters.get(id);
//            GraphModel newGM = new GraphModel(gm.getNumOfVertice());
            ArrayList<Integer> sumList = gm.getSumOfCol();
//            System.out.println("sum:"+ sumList);
            double edges[][] = new double[sumList.size()][sumList.size()];
            for(int i=0; i< gm.getEdges().length; i++){
                for(int j=0; j<gm.getEdges()[0].length; j++){
                    if(gm.getEdges()[j][i]==1){
                        edges[j][i] = sumList.get(i)*1.0/gm.getEdges()[0].length;
                    }else{
                        edges[j][i] = 0.0;
                    }
                }
            }
            transMatrix.put(id, edges);
//            System.out.println("Print The Transfomation Matrix:");
//            for(int i=0; i< gm.getEdges()[0].length; i++) {
//                for (int j = 0; j < gm.getEdges().length; j++) {
//                    System.out.print(edges[i][j]+"  ");
//                }
//                System.out.println();
//            }
        }
        return transMatrix;
    }
    //RWR
    public void randomWalkRestart(float alpha, int startPoint, long maxIterationTimes, double minErrors, double transMatrix[][]){
        int iterationTimes = 0;
        double[] rank_sp = new double[transMatrix[0].length];
        double[] e = new double[transMatrix[0].length];
        //init rank_sp, set identify vector
        for(int i=0; i<transMatrix[0].length; i++){
            if(i == startPoint){
                rank_sp[i] = 1.0;
                e[i] = 1.0;
            }else{
                rank_sp[i] =0.0;
                e[i] =0.0;
            }
        }
        boolean flag = true;
        while(iterationTimes < maxIterationTimes){
            if(flag == true){
                for(int i= 0; i<transMatrix.length; i++){
                    double[] temp = rank_sp;
                    for(int j=0; j<transMatrix[0].length; j++){
                        rank_sp[i]+= alpha*transMatrix[i][j]*rank_sp[j];
                        rank_sp[i]+= (1-alpha)*e[i];
                    }
                    for(int k=0; k<rank_sp.length; k++){
                        if(judge(temp,rank_sp,minErrors)){
                            flag = false;
                        }
                    }
                }
            }else
                break;
        }
        System.out.println("迭代后节点["+startPoint+"]的rank score为:");
        for(int i=0; i<rank_sp.length; i++){
            System.out.println(rank_sp[i]);
        }
    }
    //judge the difference between two interations
    public boolean judge(double a[], double b[], double minErrors){
        boolean flag = true;
        for(int i=0; i<a.length; i++){
            if(Math.abs(a[i]-b[i])<minErrors)
                continue;
            else{
                flag = false;
                break;
            }
        }
        return flag;
    }
    public static void main(String[] args){
        RandomWalk rw = new RandomWalk();
        rw.getTransMatrix();
    }
}




























































