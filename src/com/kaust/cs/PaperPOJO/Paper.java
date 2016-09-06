package com.kaust.cs.PaperPOJO;

import java.util.LinkedList;

/**
 * Created by yangq0a on 16/8/30.
 */
public class Paper {
    public String paperID;
    public String paperTitle;
    public LinkedList authorList;
    public String year;
    public String venue;
    public LinkedList referenceList;

    @Override
    public String toString() {
        return "Paper{" +
                "paperID='" + paperID + '\'' +
                ", paperTitle='" + paperTitle + '\'' +
                ", authorList=" + authorList +
                ", year='" + year + '\'' +
                ", referenceList=" + referenceList +
                '}';
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getPaperID() {
        return paperID;
    }

    public void setPaperID(String paperID) {
        this.paperID = paperID;
    }

    public String getPaperTitle() {
        return paperTitle;
    }

    public void setPaperTitle(String paperTitle) {
        this.paperTitle = paperTitle;
    }

    public LinkedList getAuthorList() {
        return authorList;
    }

    public void setAuthorList(LinkedList authorList) {
        this.authorList = authorList;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public LinkedList getReferenceList() {
        return referenceList;
    }

    public void setReferenceList(LinkedList referenceList) {
        this.referenceList = referenceList;
    }
}
