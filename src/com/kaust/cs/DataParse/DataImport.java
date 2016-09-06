package com.kaust.cs.DataParse;

import com.kaust.cs.PaperPOJO.Paper;
import java.io.*;
import java.util.LinkedList;
import java.util.Vector;
import com.kaust.cs.Tools.TimeCost;
/**
 * Created by yangq0a on 16/8/30.
 */
public class DataImport {
    public DataImport(){
    }

    Vector<Paper> vector = new Vector<Paper>();
    //Parse Data and filter papers the number of their refernces less than 2
    public Vector<Paper> dataImport(String fileName){
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
            String line  = "";
            StringBuffer tempPaper = new StringBuffer();
            int isFirstTimes = 0;
            int i = 0;
            while((line = br.readLine()) != null && i < 2000){
                if(isFirstTimes == 0){
                    tempPaper.append(line);
                }else{
                    if(line.length() == 0){
                        String allContents = tempPaper.toString();
                        if(allContents.contains("%")){
                            LinkedList<String> referenceList = new LinkedList<String>();
                            Paper p = new Paper();
                            String[] con = allContents.split("#");
                            for(int j=0; j<con.length; j++){
                                if(con[j].startsWith("*")){
//                                    if(con[j].substring(1).startsWith(" ")){
//
//                                    }
                                    p.setPaperTitle(con[j].substring(1));
                                }
                                if(con[j].startsWith("@")){
                                    String authors[] = con[j].substring(1).split(",");
                                    LinkedList<String> authorList = new LinkedList<String>();
                                    for(int num=0; num< authors.length; num++){
                                        if(authors[num].startsWith(" ")){
                                            authorList.add(authors[num].substring(1));
                                        }else{
                                            authorList.add(authors[num]);
                                        }
                                    }
                                    p.setAuthorList(authorList);
                                }
                                if(con[j].startsWith("t")){
                                    p.setYear(con[j].substring(1));
                                }
                                if(con[j].startsWith("c")){
                                    p.setVenue(con[j].substring(1));
                                }
                                if(con[j].startsWith("index")){
                                    p.setPaperID(con[j].substring(5));
                                }
                                if(con[j].startsWith("%")){
                                    referenceList.add(con[j].substring(1));
                                }
                        }
                            p.setReferenceList(referenceList);
                            if(referenceList.size()>2){
                                vector.add(p);
//                                if(i<1000){
//                                    System.out.println("Title:"+p.getPaperTitle()+" ID:"+p.getPaperID()+" Year:"+p.getYear()+" Venue:"+p.getVenue()+"  Author:"+p.getAuthorList()+"  Ref:"+p.getReferenceList());
//                                }
                            }
                        }
                        tempPaper.setLength(0);
                        //System.out.println("Title:"+vector.size());
                }else {
                        tempPaper.append(line);
                    }
                }
                isFirstTimes = 1;
                i++;
            }
            br.close();
        }catch(Exception e) {
            System.out.println("Exception:" + e.toString());
        }
        System.out.println("The number of papers is :"+vector.size());
        return vector;
    }

    public static void main(String args[]){
        DataImport di = new DataImport();
        long startTime = TimeCost.getTime();
        di.dataImport("/Users/yangq0a/Documents/DBLPData/dblp.txt");
        long endTime = TimeCost.getTime();
        System.out.println("The time of parsing papers is :"+TimeCost.getTimeCost(startTime, endTime)+" ms");
    }
}
