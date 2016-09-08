package com.kaust.cs.DataParse;

import com.kaust.cs.PaperPOJO.Paper;
import com.kaust.cs.Tools.GraphModel;
import com.kaust.cs.Tools.Weight;

import java.util.*;

/**
 * Created by yangq0a on 16/8/31.
 */
public class ReferenceNet {
    String file = "/Users/yangq0a/Documents/DBLPData/dblp.txt";
    Vector<Paper> v = new DataImport().dataImport(file);
    HashMap<String, ArrayList <String>> author = new HashMap<String, ArrayList<String>>();

    //find the cluster for every paper including citing and cited
    class ReferenceRelation{
        public String startPId;
        public LinkedList<String> referenceList;
        public long point;

        public String getStartPId() {
            return startPId;
        }

        public void setStartPId(String startPId) {
            this.startPId = startPId;
        }

        public LinkedList<String> getReferenceList() {
            return referenceList;
        }

        public void setReferenceList(LinkedList<String> referenceList) {
            this.referenceList = referenceList;
        }

        public long getPoint() {
            return point;
        }

        public void setPoint(long point) {
            this.point = point;
        }
    }

    //求两个数组的交集
    public LinkedList<String> intersect(LinkedList<String> arr1, LinkedList<String> arr2) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        LinkedList<String> list = new LinkedList<String>();
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

        for (Map.Entry<String, Boolean> e : map.entrySet()) {
            if (e.getValue().equals(Boolean.TRUE)) {
                list.add(e.getKey());
            }
        }
        return list;
    }

    public HashMap<Long, ArrayList<ReferenceRelation>> getConnectedReference(){
        int stop = 0;
        HashMap<Long, ReferenceRelation>  refRelation = new HashMap<Long, ReferenceRelation>();
        long numOfPaper = 0;
        for(Paper p: v){
            numOfPaper++;
            ReferenceRelation rr = new ReferenceRelation();
            rr.setStartPId(p.getPaperID());
            rr.setReferenceList(p.getReferenceList());
            rr.setPoint(0);
            refRelation.put(numOfPaper, rr);
        }
        //get the citing and cited of papers
        for(long key0: refRelation.keySet()){
            for(long key1: refRelation.keySet()){
                if(key0 == key1){
                    continue;
                }else {
                    ReferenceRelation rr1 = refRelation.get(key0);
                    ReferenceRelation rr2 = refRelation.get(key1);
                    if(rr1.getReferenceList().indexOf(rr2.getStartPId()) != -1 || rr2.getReferenceList().indexOf(rr1.getStartPId()) != -1 || intersect(rr1.getReferenceList(), rr2.getReferenceList()).size()>0){
                        if(rr1.getPoint() == 0 && rr2.getPoint() == 0){
                            rr1.setPoint(key0);
                            rr2.setPoint(key0);
                        }else if(rr1.getPoint() == 0 && rr2.getPoint() != 0){
                            rr1.setPoint(rr2.getPoint());
                        }else if(rr1.getPoint() != 0 && rr2.getPoint() == 0){
                            rr2.setPoint(rr1.getPoint());
                        }
                    }

                }
            }
        }
        HashMap<Long, ArrayList<ReferenceRelation>> tempCluster = new HashMap<Long, ArrayList<ReferenceRelation>>();
        //fisrt, deal with refernece list in which it only has cited some papers while not being cited by others
        long negativeFlag = 0;
        for(long key: refRelation.keySet()){
            ReferenceRelation rr = refRelation.get(key);
//            if(rr.getStartPId().equals("53e99e21b7602d97026e5640")){
//                System.out.println("Start:==============="+rr.getReferenceList());
//            }
//            if(rr.getReferenceList().contains("53e9afd3b7602d9703a23215")){
//                System.out.println("End:==============="+rr.getStartPId()+" =========== "+rr.getReferenceList());
//            }
            long flag = rr.getPoint();
            if(flag == 0){
                negativeFlag--;
                ArrayList<ReferenceRelation> arr = new ArrayList<ReferenceRelation>();
                arr.add(rr);
                tempCluster.put(negativeFlag, arr);
            }
        }
        //scan the refRelation and cluster them based on their flag
        for(long key0: refRelation.keySet()){
            ReferenceRelation rr0 = refRelation.get(key0);
            long flag0 = rr0.getPoint();
            for(long key1: refRelation.keySet()){
                ReferenceRelation rr1 = refRelation.get(key1);
                long flag1 = rr1.getPoint();
                if(key0 != key1){
                    if(flag0 == flag1 && flag0 != 0){
                        if(!tempCluster.containsKey(flag0)){
                            ArrayList<ReferenceRelation> simRR = new ArrayList<ReferenceRelation>();
                            simRR.add(rr0);
                            simRR.add(rr1);
                            tempCluster.put(flag0, simRR);
                        }else{
                            if(tempCluster.get(flag0).contains(rr1)){
                                tempCluster.get(flag0).add(rr0);
                            }else if(tempCluster.get(flag0).contains(rr1)){
                                tempCluster.get(flag0).add(rr1);
                            }else if(!tempCluster.get(flag0).contains(rr1) && tempCluster.get(flag0).contains(rr0)){
                                tempCluster.get(flag0).add(rr0);
                                tempCluster.get(flag0).add(rr1);
                            }else if(tempCluster.get(flag0).contains(rr1) && tempCluster.get(flag0).contains(rr1)){
                                continue;
                            }
                        }
                    }
                }
            }
        }
        return tempCluster;
    }
    //create reference network
    public void createReferenceNetwork(){
        HashMap<Long, ArrayList<ReferenceRelation>> tempClusters = getConnectedReference();
        HashMap<Long, GraphModel> clusters = new HashMap<Long, GraphModel>();
        for(long id: tempClusters.keySet()){
            ArrayList<ReferenceRelation> rrList = tempClusters.get(id);
            ArrayList<String> vertices = new ArrayList<String>();
            ArrayList<Weight> weights = new ArrayList<Weight>();
            int edgeNum = 0;
            for(ReferenceRelation rr: rrList){
                String startPaperId = rr.getStartPId();
                LinkedList<String> referenceList = rr.getReferenceList();
                if(!vertices.contains(startPaperId)){
                    vertices.add(startPaperId);
                }
                for(String endPaperId: referenceList){
                    if(!vertices.contains(endPaperId)){
                        vertices.add(endPaperId);
                    }
                    edgeNum++;
                    Weight w = new Weight(vertices.indexOf(startPaperId), vertices.indexOf(endPaperId), 1);
                    weights.add(w);
                }

            }
            //generate author networks for every cluster
            int vecs = vertices.size();
            int edegs = edgeNum;
            GraphModel g = new GraphModel(vecs);
            String[] vertice = (String[]) vertices.toArray(new String [vertices.size()]);
            Weight[] weight = (Weight[]) weights.toArray(new Weight [weights.size()]);
            try {
                buildAdjGraphic(g, vertice,vecs,weight,edegs);
            }catch (Exception e){
                System.out.println("Builing Reference Network ERRORS"+e.toString());
            }
            if(edegs > 1) {
                System.out.println("--------The Reference Network of No.【"+id+"】is shown as following"+"---------");
                g.print();
            }
            clusters.put(id, g);
        }
        System.out.println("===============Seperator===================");

        long id = -6;
        GraphModel gm = clusters.get(id);
        System.out.println(gm.getValueOfVertice());
        System.out.println("The size of clusters is: "+clusters.size());
    }

    /**
     * 构建邻接矩阵
     * */
    public void buildAdjGraphic(GraphModel g, String[] vertices, int n,Weight[] weight,int e) throws Exception{
        Weight.createAdjGraphic(g, vertices, n, weight, e);
    }

    public static void main(String[] args){
        ReferenceNet rn = new ReferenceNet();
        rn.createReferenceNetwork();
    }
}
