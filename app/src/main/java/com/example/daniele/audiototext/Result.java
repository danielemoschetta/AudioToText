package com.example.daniele.audiototext;

public class Result {

    private int id;
    private String text;
    private String time;

    public Result(){}

    public Result(String text,String time) {
        super();
        this.text=text;
        this.time=time;
    }

    //getters & setters
    public int getId()  {
        return id;
    }
    public String getText()    {
        return text;
    }
    public String getTime()   {
        return time;
    }


    public void setId(int i)    {
        id = i;
    }
    public void setText(String s)  {
        text = s;
    }
    public void setTime(String s) {
        time = s;
    }

    @Override
    public String toString() {
        return text + "\n" + time;
    }
}
