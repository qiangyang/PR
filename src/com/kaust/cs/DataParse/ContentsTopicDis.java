package com.kaust.cs.DataParse;

import com.kaust.cs.PaperPOJO.Paper;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import com.kaust.cs.Tools.TFIDF.ContentsParsing;
/**
 * Created by yangq0a on 16/9/26.
 */
public class ContentsTopicDis {
    public static String file = "/Users/yangq0a/Documents/DBLPData/dblp.txt";
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

    public static void main(String[] args) throws Exception{
        ContentsTopicDis ctd = new ContentsTopicDis();
        System.out.println(ctd.getKeyInfoByTitle());
    }
}
