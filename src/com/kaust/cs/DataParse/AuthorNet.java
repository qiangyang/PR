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

    class AuthorRelationPairs{
        public String author1;
        public String author2;
        public ArrayList<String> COPaperIDList;
        public long ownerShip;

        public String getAuthor1() {
            return author1;
        }

        public void setAuthor1(String author1) {
            this.author1 = author1;
        }

        public String getAuthor2() {
            return author2;
        }

        public void setAuthor2(String author2) {
            this.author2 = author2;
        }

        public ArrayList<String> getCOPaperID() {
            return COPaperIDList;
        }

        public void setCOPaperIDList(ArrayList<String> COPaperIDList) {
            this.COPaperIDList = COPaperIDList;
        }

        public long getOwnerShip() {
            return ownerShip;
        }

        public void setOwnerShip(long ownerShip) {
            this.ownerShip = ownerShip;
        }
    }

    public void findCooperationBetAuthor(){
        //findCooperationBetAuthor(String fileName)
        authorInfoAnalysis();
        HashMap<Integer, AuthorRelationPairs> relMap = new HashMap<Integer,AuthorRelationPairs>();
        int numOfRel = 0;
        for(String key0: author.keySet()){
            ArrayList<String> pid0 = author.get(key0);
            for(String key1: author.keySet()){
                ArrayList<String> pid1 = author.get(key1);
                if(key0.equals(key1)){
                    continue;
                }else{
                    ArrayList<String> arr = intersect(pid0,pid1);
                        if(arr.size()>0){
                            AuthorRelationPairs authorsRel = new AuthorRelationPairs();
                            authorsRel.setAuthor1(key0);
                            authorsRel.setAuthor2(key1);
                            authorsRel.setCOPaperIDList(arr);
                            authorsRel.setOwnerShip(0);
                            numOfRel++;
                            relMap.put(numOfRel,authorsRel);
                        }
                    }

                }
            }
        //generate flag for every pair
        for(int key0: relMap.keySet()){
            for(int key1: relMap.keySet()){
                if(key0 != key1){
                    AuthorRelationPairs a0 = relMap.get(key0);
                    AuthorRelationPairs a1 = relMap.get(key1);
                    if(a0.getAuthor1().equals(a1.getAuthor1()) || a0.getAuthor1().equals(a1.getAuthor2()) || a0.getAuthor2().equals(a1.getAuthor1()) || a0.getAuthor2().equals(a1.getAuthor2())){
                        if(a0.getOwnerShip() == 0 && a1.getOwnerShip() == 0) {
                            a0.setOwnerShip(key0);
                            a1.setOwnerShip(key0);
                        }else if(a0.getOwnerShip() == 0 && a1.getOwnerShip() != 0){
                            a0.setOwnerShip(a1.getOwnerShip());
                        }else if(a0.getOwnerShip() != 0 && a1.getOwnerShip() == 0){
                            a1.setOwnerShip(a0.getOwnerShip());
                        }
                    }
                }
            }
        }
        // scan the map to cluster
        HashMap<Long, ArrayList<AuthorRelationPairs>> authorRelMap = new HashMap<Long, ArrayList<AuthorRelationPairs>>();
        for(int key0: relMap.keySet()){
            AuthorRelationPairs a0 = relMap.get(key0);
            long flag0 = a0.getOwnerShip();
            for(int key1: relMap.keySet()){
                AuthorRelationPairs a1 = relMap.get(key0);
                long flag1 = a1.getOwnerShip();
                if(key0 != key1){
                    if(flag0 == flag1){
                        if(!authorRelMap.containsKey(flag0)){
                        ArrayList<AuthorRelationPairs> simAuthors = new ArrayList<AuthorRelationPairs>();
                        simAuthors.add(a0);
                        simAuthors.add(a1);
                        authorRelMap.put(flag0, simAuthors);
                        }else{
                            if(authorRelMap.get(flag0).contains(a1)){
                                authorRelMap.get(flag0).add(a0);
                            }else if(authorRelMap.get(flag0).contains(a0)){
                                authorRelMap.get(flag0).add(a1);
                            }else if(!authorRelMap.get(flag0).contains(a1) && authorRelMap.get(flag0).contains(a0)){
                                authorRelMap.get(flag0).add(a0);
                                authorRelMap.get(flag0).add(a1);
                            }
                        }
                    }
                }
            }
        }
    }


    public void createAuthorNet(){
//        if(!vertices.contains(key0)){
//            vertices.add(key0);
//        }
//        if(!vertices.contains(key1)){
//            vertices.add(key1);
//        }
//        if(arr.size()>0) {
//            edgeNum++;
//            Weight w = new Weight(vertices.indexOf(key0), vertices.indexOf(key1), arr.size());
//            weights.add(w);
//        }
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
//        Vector<Paper> vec = new Vector<Paper>();
//        an.findCooperationBetAuthor(outPutPath);
    }
}
