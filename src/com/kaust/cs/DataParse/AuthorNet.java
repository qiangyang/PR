package com.kaust.cs.DataParse;
import java.io.*;
import java.util.*;
import com.kaust.cs.PaperPOJO.Paper;
import java.util.Map;
import java.util.Map.Entry;
import com.kaust.cs.Tools.GraphModel;
import com.kaust.cs.Tools.Weight;

/**
 * Created by yangq0a on 16/8/31.
 */
public class AuthorNet {
    public static final int FREQUENCY = 4;
    String file = "/Users/yangq0a/Documents/DBLPData/dblp.txt";
    String outPutPath = "./Data/authorResults.txt";
    Vector<Paper> v = new DataImport().dataImport(file);
    HashMap<String, ArrayList<String>> author = new HashMap<String, ArrayList<String>>();
    public ArrayList<String> vertices = new ArrayList<String>();
    public ArrayList<Weight> weights = new ArrayList<Weight>();
    public int edgeNum = 0;
    //find authors who are always cooperating
    public void authorInfoAnalysis(){
        Enumeration enums = v.elements();
        while(enums.hasMoreElements()){
            Paper p = (Paper) enums.nextElement();
            String pId = p.getPaperID();
            for(int i=0; p.getAuthorList()!= null && i<p.getAuthorList().size();  i++){
                if(author.containsKey(p.getAuthorList().get(i).toString())){
                    author.get(p.getAuthorList().get(i).toString()).add(pId);
                }else{
                    ArrayList<String> pid = new ArrayList<String>();
                    pid.add(pId);
                    author.put(p.getAuthorList().get(i).toString(), pid);
                }
            }
        }
//        for(String key: author.keySet()){
//            ArrayList<String> pid = author.get(key);
//            if (pid.size()>1){
//                System.out.println(key+"   : "+ pid);
//            }
//        }
//        System.out.println("b: "+author.size());
        Iterator<Map.Entry<String, ArrayList<String>>> it = author.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<String, ArrayList<String>> entry= it.next();
            String key= entry.getKey();
            ArrayList<String> plist = entry.getValue();
            if(plist == null || plist.size()<2)
            {
                it.remove();
            }
        }
//                for(String key: author.keySet()){
//
//                System.out.println(key);
//        }
    }

    public HashMap<String, ArrayList<String>> findFrequentAuthors(){
        authorInfoAnalysis();
        for(String key: author.keySet()){
            ArrayList<String> pid = author.get(key);
            if (pid.size()>FREQUENCY){
                System.out.println(key+"   : "+ pid);
            }
        }
        return author;
    }

    //求两个数组的交集
    public static ArrayList<String> intersect(ArrayList<String> arr1, ArrayList<String> arr2) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        ArrayList<String> list = new ArrayList<String>();
        for (String str : arr1) {
            if (!map.containsKey(str)) {
                map.put(str, Boolean.FALSE);
            }
        }
        for (String str : arr2) {
            if (map.containsKey(str)) {
                map.put(str, Boolean.TRUE);
            }
        }

        for (Entry<String, Boolean> e : map.entrySet()) {
            if (e.getValue().equals(Boolean.TRUE)) {
                list.add(e.getKey());
            }
        }
        return list;
    }

    public void findCooperationBetAuthor(){
        //findCooperationBetAuthor(String fileName)
        authorInfoAnalysis();
        for(String key0: author.keySet()){
            ArrayList<String> pid0 = author.get(key0);
            for(String key1: author.keySet()){
                ArrayList<String> pid1 = author.get(key1);
                if(key0.equals(key1)){
                    continue;
                }else{
                    //System.out.println(key0+"==>"+pid0+"  "+key1+"==>"+pid1);

                        ArrayList<String> arr = intersect(pid0,pid1);
                        if(!vertices.contains(key0)){
                            vertices.add(key0);
                        }
                        if(!vertices.contains(key1)){
                            vertices.add(key1);
                        }
                        if(arr.size()>0){
                            edgeNum++;
                            Weight w = new Weight(vertices.indexOf(key0),vertices.indexOf(key1),arr.size());
                            weights.add(w);
                            //write the inofrmation into a file
//                            try {
//                                File file=new File(fileName);
//                                if(!file.exists()||file.isDirectory()) throw new FileNotFoundException();
//                                FileOutputStream out = new FileOutputStream(file,true);
//                                StringBuffer sb = new StringBuffer();
//                                sb.append(key0+" "+key1+" "+arr.size());
//                                for(int i=0; i<arr.size(); i++){
//                                    sb.append(" #"+arr.get(i));
//                                }
//                                sb.append("\n");
//                                out.write(sb.toString().getBytes("utf-8"));
//                                out.close();
////                                System.out.println("author["+key0+"] and author["+ key1+ "] cooperate "+ arr.size()+" papers, including"+arr);
//                            }catch (Exception e){
//                                System.out.println("Data writing Errors:"+e.toString());
//                            }
                        }
                    }
                }
            }
        }

    public void createAuthorNet(){
        findCooperationBetAuthor();
        int vecs = vertices.size();
        int edegs = edgeNum;
        GraphModel g = new GraphModel(vecs);
        String[] vertice = (String[]) vertices.toArray(new String [vertices.size()]);
        Weight[] weight = (Weight[]) weights.toArray(new Weight [weights.size()]);
        try {
            buildAdjGraphic(g, vertice,vecs,weight,edegs);
        }catch (Exception e){
            System.out.println("Builing AuthorNet ERRORS"+e.toString());
        }
//        if(edegs > 1) {
//            System.out.println("--------该邻接矩阵如下---------");
//            g.print();
//        }
    }

    /**
     * 构建邻接矩阵
     * */
    public void buildAdjGraphic(GraphModel g, String[] vertices, int n,Weight[] weight,int e) throws Exception{
        Weight.createAdjGraphic(g, vertices, n, weight, e);
    }

    public static void main(String[] args){
        AuthorNet an = new AuthorNet();
        an.createAuthorNet();
        Vector<Paper> vec = new Vector<Paper>();
        //an.findCooperationBetAuthor(outPutPath);
    }
}
