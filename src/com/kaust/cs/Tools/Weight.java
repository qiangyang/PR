package com.kaust.cs.Tools;
import com.kaust.cs.Tools.GraphModel;
//插入的边的类
public class Weight {

	int row;  //起点
    int col;  //终点
    int weight; //权值
    
    public Weight(int row,int col,int weight)
    {
        this.row = row;
        this.col = col;
        this.weight = weight;
    }
    
    public static void createAdjGraphic(GraphModel g, String[] vertices, int n,Weight[] weight,int e)
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
