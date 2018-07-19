package com.esotericcoder.www.thenewyorktimes.model;

public class FilterSetting {
    private int beginDate;
    private String sort;
    private String newsDesk;

    public FilterSetting(int beginDate, String sort, String newsDesk){
        this.beginDate = beginDate;
        this.sort = sort;
        this.newsDesk = newsDesk;
    }

    public int getBeginDate(){
        return beginDate;
    }

    public String getSort(){
        return sort;
    }

    public String getNewsDesk(){
        return newsDesk;
    }
}
