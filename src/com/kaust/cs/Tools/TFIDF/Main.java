package com.kaust.cs.Tools.TFIDF;

/**
 * Created by yangq0a on 16/9/5.
 */
import com.kaust.cs.PaperPOJO.Paper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Main {

    public static void main(String[] args) throws IOException {

        Vector<Paper> vec = new Vector<Paper>();
        Map<String, HashMap<String, Integer>> normal = ReadFiles.NormalTFOfAll(vec);
        for (String filename : normal.keySet()) {
            System.out.println("fileName " + filename);
            System.out.println("TF " + normal.get(filename).toString());
        }

        System.out.println("-----------------------------------------");

        Map<String, HashMap<String, Float>> notNarmal = ReadFiles.tfOfAll(vec);
        for (String filename : notNarmal.keySet()) {
            System.out.println("fileName " + filename);
            System.out.println("TF " + notNarmal.get(filename).toString());
        }

        System.out.println("-----------------------------------------");

        Map<String, Float> idf = ReadFiles.idf(vec);
        for (String word : idf.keySet()) {
            System.out.println("keyword :" + word + " idf: " + idf.get(word));
        }

        System.out.println("-----------------------------------------");

        Map<String, HashMap<String, Float>> tfidf = ReadFiles.tfidf(vec);
        for (String filename : tfidf.keySet()) {
            System.out.println("fileName " + filename);
            System.out.println(tfidf.get(filename));
        }
    }
}