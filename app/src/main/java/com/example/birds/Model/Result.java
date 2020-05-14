package com.example.birds.Model;

public class Result {

    String name;
    String Conf;

    public Result(String name, String conf) {
        this.name = name;
        Conf = conf;
    }

    public Result() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConf() {
        return Conf;
    }

    public void setConf(String conf) {
        Conf = conf;
    }
}
