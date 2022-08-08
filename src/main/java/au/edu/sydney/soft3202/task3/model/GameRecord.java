package au.edu.sydney.soft3202.task3.model;

public class GameRecord {
    String username;
    String gameName;
    String serialisation;
    public GameRecord(String username,String gameName,String serialisation){
        this.username = username;
        this.gameName = gameName;
        this.serialisation = serialisation;
    }
    public String getUsername(){
        return username;
    }
    public String getGameName(){
        return gameName;
    }
    public String getSerialisation(){
        return serialisation;
    }
}
