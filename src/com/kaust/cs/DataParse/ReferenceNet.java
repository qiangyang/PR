package com.kaust.cs.DataParse;

import com.kaust.cs.PaperPOJO.Paper;
import com.kaust.cs.Tools.GraphModel;
import com.kaust.cs.Tools.ImprovedWeight;
import com.kaust.cs.Tools.SIMFUNC.SimFunction;
import com.kaust.cs.Tools.Weight;
import com.kaust.cs.Tools.ImprovedGraphModel;
import java.io.IOException;
import java.util.*;

/**
 * Created by yangq0a on 16/8/31.
 */
public class ReferenceNet {
    String file = "/Users/yangq0a/Documents/DBLPData/dblp.txt";
    Vector<Paper> v = new DataImport().dataImport(file);
    HashMap<Long, GraphModel> clusters = new HashMap<>();
    HashMap<Long, ImprovedGraphModel> edgeReinforcedClusters = new HashMap<>();
    HashMap<String, LinkedList<Integer>> citationYear = new HashMap<>();

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

    //对map按照value排序
    public Object getMinValue(Map<Integer, Integer> map) {
        if (map == null)
            return null;
        Collection<Integer> c = map.values();
        Object[] obj = c.toArray();
        Arrays.sort(obj);
        return obj[0];
    }
    public Object getMaxValue(Map<Integer, Integer> map) {
        if (map == null)
            return null;
        int length =map.size();
        Collection<Integer> c = map.values();
        Object[] obj = c.toArray();
        Arrays.sort(obj);
        return obj[length-1];
    }
    public int getSumValue(Map<Integer, Integer> map) {
        int sum = 0;
        if (map != null) {
            int length = map.size();
            Collection<Integer> c = map.values();
            Object[] obj = c.toArray();
            for (int i = 0; i < obj.length; i++) {
                sum += (int) obj[i];
            }
        }
        return sum;
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
    public HashMap<Long, GraphModel>  createReferenceNetwork(){
        HashMap<Long, ArrayList<ReferenceRelation>> tempClusters = getConnectedReference();
        //HashMap<Long, GraphModel> clusters = new HashMap<Long, GraphModel>();
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
//            if(edegs > 1) {
//                System.out.println("--------The Reference Network of No.【"+id+"】is shown as following"+"---------");
//                g.print();
//            }
            clusters.put(id, g);
        }
        System.out.println("===============Seperator===================");

//        long id = -6;
//        GraphModel gm = clusters.get(id);
//        System.out.println(gm.getValueOfVertice());
//        System.out.println("The size of clusters is: "+clusters.size());
        return clusters;
    }

    /**
     * 构建邻接矩阵
     * */
    public void buildAdjGraphic(GraphModel g, String[] vertices, int n,Weight[] weight,int e) throws Exception{
        Weight.createAdjGraphic(g, vertices, n, weight, e);
    }
    public void buildImprovedAdjGraphic(ImprovedGraphModel g, String[] vertices, int n,ImprovedWeight[] weight,int e) throws Exception{
        ImprovedWeight.createAdjGraphic(g, vertices, n, weight, e);
    }
    /**
     * 利用citation构建索引,进行聚类
     * */
    public HashMap<Long, LinkedList<String>> constrcutIndexByCitation()throws IOException {
        //HashMap<Long, GraphModel> clusters =
        createReferenceNetwork();
        HashMap<Long, HashMap<String, Integer>> cluster = new HashMap<>();
        HashMap<Long, LinkedList<String>> newcluster = new HashMap<>();

        int i = 0;
        System.out.println(clusters.size());
        for(long id: clusters.keySet()){
            i++;
            HashMap<String, Integer> wordcount = new HashMap<>();
            GraphModel gm = clusters.get(id);
            ArrayList<String> list  = gm.getValueOfVertice();
                for(String pID: list) {
                    for(Paper p: v){
                    if (p.getPaperID().equals(pID)){
                            String pTitle = p.getPaperTitle();
                             //System.out.println("ssss"+pTitle);
                            ArrayList<String> words = SimFunction.cutWord(pTitle);
                            for (String word : words) {
                                if (word != null) {
                                    if (wordcount.containsKey(word)) {
                                        int num = wordcount.get(word);
                                        num += 1;
                                        wordcount.put(word, num);
                                    } else {
                                        wordcount.put(word, 1);
                                    }
                                }
                            }
                            break;
                        }
                }
            }
//            if(i>30) {
//                break;
//            }else {
                HashMap<String, Integer> wordcount1 = new HashMap<>();
                LinkedList<String> listFreWords = new LinkedList<>();
                for (String str : wordcount.keySet()){
                    if(wordcount.get(str) >1){
                        wordcount1.put(str, wordcount.get(str));
                        listFreWords.add(str);
                    }
                }
                if(!wordcount1.isEmpty()){
                    //System.out.println(wordcount1);
                    cluster.put(id, wordcount1);
                    newcluster.put(id, listFreWords);
                }

//            }
        }
        return newcluster;
    }
    public HashMap<String, Double> simTitelPaper(String title)throws IOException{
        HashMap<String, Double> similarity = new HashMap<>();
        HashMap<Long, LinkedList<String>> cluster = constrcutIndexByCitation();
        ArrayList<String> words = SimFunction.cutWord(title);
        for (long id: cluster.keySet()){
            if(!intersect(new LinkedList<String>(words), cluster.get(id)).isEmpty()){
                for(long clusterId: clusters.keySet()){
                    if(id == clusterId){
                        GraphModel gm = clusters.get(id);
                        ArrayList<String> list  = gm.getValueOfVertice();
                        for(String pID: list) {
                            for (Paper p : v) {
                                if (p.getPaperID().equals(pID)) {
                                    String pTitle = p.getPaperTitle();
                                    double sim = SimFunction.simByJaccard(pTitle, title);
                                    if(sim > 0 && !pTitle.equals(title))
                                      similarity.put(pTitle, sim);
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
//        for(String str: similarity.keySet()) {
//            System.out.println("titel:[" + title + "] 与 ["+str+"]的相似性为: "+similarity.get(str));
//        }
        return similarity;
    }
    //获取文章的引用时间列表
    public HashMap<String, LinkedList<Integer>> getCitationYearList(){
        //HashMap<String, LinkedList<Integer>> citationYear = new HashMap<>();
        for(long id: clusters.keySet()){
            GraphModel gm = clusters.get(id);
            int edges[][] = gm.getEdges();
            for(int i=0; i<edges[0].length; i++){
                String pID = gm.getValueOfVertice(i);
                LinkedList<Integer> cYear = new LinkedList<>();
                for(int j=0; j<edges.length; j++){
                    String pID1 = gm.getValueOfVertice(j);
                    if(edges[j][i] == 1){
                        for(Paper p: v){
                            if(p.getPaperID().equals(pID1)){
                                cYear.add(Integer.parseInt(p.getYear()));
                                break;
                            }
                        }
                    }
                }
                if(!cYear.isEmpty())
                  citationYear.put(pID, cYear);
            }
        }
        System.out.println(citationYear);
        return citationYear;
    }
    // 获取LC值
    public HashMap<String, Double> getLC(){
        HashMap<String, Double> lc = new HashMap<>();
        getCitationYearList();
        int D = 2016;
        for(String pID: citationYear.keySet()){
            LinkedList<Integer> years = citationYear.get(pID);
            int last = Collections.max(years);
            int first = Collections.min(years);
            double lcValue = (last - first)*1.0/(D-first+1);
            lc.put(pID, lcValue);
        }
        return lc;
    }

    //获取FC值
    public HashMap<String, Double> getFC(){
        HashMap<String, Double> fc = new HashMap<>();
        getCitationYearList();
        int T = 5;
        for(String pID: citationYear.keySet()){
            double pcValue = 0;
            LinkedList<Integer> years = citationYear.get(pID);
            LinkedList<LinkedList<Integer>> gapList = new LinkedList<>();
            LinkedList<HashMap<Integer, Integer>> yearList = new LinkedList<>();
            int last = Collections.max(years);
            int first = Collections.min(years);
            int gap = 0;
            if((last-first)%T != 0 ){
                gap = (last-first)/T+1;
            }else {
                gap = (last - first) / T;
            }
            Collections.sort(years, Collections.reverseOrder());
            LinkedList<Integer> l1 = new LinkedList<>();
            LinkedList<Integer> l2 = new LinkedList<>();
            LinkedList<Integer> l3 = new LinkedList<>();
            LinkedList<Integer> l4 = new LinkedList<>();
            LinkedList<Integer> l5 = new LinkedList<>();
            HashMap<Integer, Integer> yearsTimes1 = new HashMap<>();
            HashMap<Integer, Integer> yearsTimes2 = new HashMap<>();
            HashMap<Integer, Integer> yearsTimes3 = new HashMap<>();
            HashMap<Integer, Integer> yearsTimes4 = new HashMap<>();
            HashMap<Integer, Integer> yearsTimes5 = new HashMap<>();
            for(int year: years){
                if(first <= year && year <= (first+gap)){
                    l1.add(year);
                }
                if(first+gap < year && year <= first+2*gap){
                    l2.add(year);
                }
                if(first+2*gap < year && year<= first+3*gap){
                    l3.add(year);
                }
                if(first+3*gap < year && year<= first+4*gap){
                    l4.add(year);
                }
                if(first+4*gap < year && year<= last){
                    l5.add(year);
                }
            }
            gapList.add(l1);
            gapList.add(l2);
            gapList.add(l3);
            gapList.add(l4);
            gapList.add(l5);
            yearList.add(yearsTimes1);
            yearList.add(yearsTimes2);
            yearList.add(yearsTimes3);
            yearList.add(yearsTimes4);
            yearList.add(yearsTimes5);
            for(int y: l1){
                if(!yearsTimes1.containsKey(y)){
                    yearsTimes1.put(y, 1);
                }else{
                    int times = yearsTimes1.get(y);
                    times++;
                    yearsTimes1.put(y, times);
                }
            }

            for(int y: l2){
                if(!yearsTimes2.containsKey(y)){
                    yearsTimes2.put(y, 1);
                }else{
                    int times = yearsTimes2.get(y);
                    times++;
                    yearsTimes2.put(y, times);
                }
            }for(int y: l3){
                if(!yearsTimes3.containsKey(y)){
                    yearsTimes3.put(y, 1);
                }else{
                    int times = yearsTimes3.get(y);
                    times++;
                    yearsTimes3.put(y, times);
                }
            }for(int y: l4){
                if(!yearsTimes4.containsKey(y)){
                    yearsTimes4.put(y, 1);
                }else{
                    int times = yearsTimes4.get(y);
                    times++;
                    yearsTimes4.put(y, times);
                }
            }
            for(int y: l5){
                if(!yearsTimes5.containsKey(y)){
                    yearsTimes5.put(y, 1);
                }else{
                    int times = yearsTimes5.get(y);
                    times++;
                    yearsTimes5.put(y, times);
                }
            }

            for(int i=0; i<gapList.size(); i++){
                pcValue+=gapList.get(i).size()*1.0/years.size()*((int)getMaxValue(yearList.get(i)) - (int)getMinValue((yearList.get(i))))/(getSumValue(yearList.get(i))/yearList.get(i).size());
            }
            fc.put(pID, pcValue);
        }
        return fc;
    }
    //获取vDeg值
    public HashMap<String, Double> getVDeg() {
        HashMap<String, Double> vDeg = new HashMap<>();
        VenueFactor vf = new VenueFactor();
        HashMap<String, Double> lc = getLC();
        HashMap<String, Double> fc = getFC();
        HashMap<String, Double> imf = vf.getVenuesImpactFactor();
        double PHI = 0.5;
        for(String pID: fc.keySet()){
            double vdeg = 0;
            for(Paper p: v){
                if(p.getPaperID().equals(pID)){
                    vdeg = PHI*imf.get(p.getVenue())+(1-PHI)*lc.get(pID)*fc.get(pID);
                    break;
                }
            }
            vDeg.put(pID, vdeg);
        }
        return  vDeg;
    }
    //构建ERCN
    public HashMap<Long, ImprovedGraphModel> constructEdgeReinforcedCN(){
        HashMap<Long, ArrayList<ReferenceRelation>> tempClusters = getConnectedReference();
        HashMap<String, Double> vDeg = getVDeg();
        for(long id: tempClusters.keySet()){
            ArrayList<ReferenceRelation> rrList = tempClusters.get(id);
            ArrayList<String> vertices = new ArrayList<String>();
            ArrayList<ImprovedWeight> weights = new ArrayList<ImprovedWeight>();
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
                    ImprovedWeight w = new ImprovedWeight(vertices.indexOf(startPaperId), vertices.indexOf(endPaperId), (vDeg.get(startPaperId)+vDeg.get(endPaperId))/2);
                    weights.add(w);
                }

            }
            //generate author networks for every cluster
            int vecs = vertices.size();
            int edegs = edgeNum;
            ImprovedGraphModel g = new ImprovedGraphModel(vecs);
            String[] vertice = (String[]) vertices.toArray(new String [vertices.size()]);
            ImprovedWeight[] weight = (ImprovedWeight[]) weights.toArray(new ImprovedWeight [weights.size()]);
            try {
                buildImprovedAdjGraphic(g, vertice,vecs,weight,edegs);
            }catch (Exception e){
                System.out.println("Builing Reference Network ERRORS"+e.toString());
            }
//            if(edegs > 1) {
//                System.out.println("--------The Reference Network of No.【"+id+"】is shown as following"+"---------");
//                g.print();
//            }
            edgeReinforcedClusters.put(id, g);
        }
        System.out.println("===============Seperator===================");

//        long id = -6;
//        GraphModel gm = clusters.get(id);
//        System.out.println(gm.getValueOfVertice());
//        System.out.println("The size of clusters is: "+clusters.size());
        return edgeReinforcedClusters;
    }
    public static void main(String[] args)throws IOException {
        ReferenceNet rn = new ReferenceNet();
        //rn.createReferenceNetwork();
        //rn.constrcutIndexByCitation();
        rn.simTitelPaper("A Computational-Intelligence-Based Approach for Detection of Exudates in Diabetic Retinopathy Images.");
        rn.getCitationYearList();
    }
}
