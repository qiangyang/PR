package com.kaust.cs.Tools;

//插入的边的类
public class ImprovedWeight {

	int row;  //起点
    int col;  //终点
    double weight; //权值

    public ImprovedWeight(int row, int col, double weight)
    {
        this.row = row;
        this.col = col;
        this.weight = weight;
    }
    
    public static void createAdjGraphic(ImprovedGraphModel g, String[] vertices, int n, ImprovedWeight[] weight, int e)
    throws Exception
    {
       //初始化结点
       for(int i=0;i<n;i++)
       {
           g.insertVertice(vertices[i]);
       }
       //初始化所有的边
       for(int i=0;i<e;i++)
       {
           g.insertEdges(weight[i].row, weight[i].col, weight[i].weight);
       }
    }
}
