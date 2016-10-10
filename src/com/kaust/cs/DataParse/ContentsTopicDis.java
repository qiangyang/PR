package com.kaust.cs.DataParse;

import com.kaust.cs.PaperPOJO.Paper;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import com.kaust.cs.Tools.SIMFUNC.SimFunction;
import com.kaust.cs.Tools.TFIDF.ContentsParsing;
/**
 * Created by yangq0a on 16/9/26.
 */
public class ContentsTopicDis {
    public static String file = "/Users/yangq0a/Documents/DBLPData/dblp.txt";
    public static double Threshold = 0.1;
    Vector<Paper> v = new DataImport().dataImport(file);
    public Map<String, HashMap<String, Float>>  getKeyInfoByTitle() throws Exception{
        ContentsParsing cp = new ContentsParsing();
        Map<String, HashMap<String, Float>> tfidf = cp.tfidf(v);
        return tfidf;
    }

    public HashMap<Long, Vector<String>> getKeyInfoByKeyWords(){

        return null;
    }

    public HashMap<Long, Vector<String>> getKeyInfoByAbstract(){

        return null;
    }

    public double simByContents(String title)throws Exception{
        double sim = 0;
        //Map<String, HashMap<String, Float>> tfidf = getKeyInfoByTitle();
        for(Paper p: v){
            sim  = SimFunction.simByJaccard(title, p.getPaperTitle());
            if(sim > 0.2) {
                System.out.println("The similarity of [" + title + "] and [" + p.getPaperTitle() + "] is " + sim);
            }
        }
        return sim;
    }

    public static void main(String[] args) throws Exception{
        String inputTitle = "Encryption Techniques for Secure Database Outsourcing";
        ContentsTopicDis ctd = new ContentsTopicDis();
        ctd.simByContents(inputTitle);
    }
}
