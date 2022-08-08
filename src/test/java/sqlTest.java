import au.edu.sydney.soft3202.task3.model.Database;
import au.edu.sydney.soft3202.task3.model.GameBoard;
import au.edu.sydney.soft3202.task3.model.GameRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

public class sqlTest {
    Database database = new Database();
    @Test
    public void testInit(){
        database.createDB();
        database.setupDB();
        database.addStartingData();
    }
    @Test
    public void testGetUsers(){
        for(String s:database.getAllUsers()){
            System.out.println(s);
        }
    }
    @Test
    public void findUser(){
        GameBoard gameBoard = new GameBoard(database);
        System.out.println(gameBoard.isExistedUser("testuser"));
        System.out.println(gameBoard.isExistedUser("1"));
    }
    @Test
    public void findGame(){
        GameBoard gameBoard = new GameBoard(database);
        System.out.println(gameBoard.isExistedGame("testuser","testgame"));
        System.out.println(gameBoard.isExistedGame("1","testgame"));
        System.out.println(gameBoard.isExistedGame("testuser","1"));
    }
    @Test
    public void userClaimTest(){
        testGetUsers();
        GameBoard gameBoard = new GameBoard(database);
        gameBoard.claimUser("testuser2");
        testGetUsers();
    }
    @Test
    public void updateGameTest(){
        GameBoard gameBoard = new GameBoard(database);
        database.updateGame("testuser","testgame2","White|false|.,b,.,.,.,w,.,w;b,.,b,.,.,.,w,.;.,b,.,b,.,w,.,w;b,.,.,.,b,.,w,.;.,b,.,.,.,.,.,w;b,.,.,.,.,.,w,.;.,b,.,.,.,w,.,w;b,.,b,.,.,.,w,.");
        check();
    }
    @Test
    public void saveGameTest(){
        GameBoard gameBoard = new GameBoard(database);
        gameBoard.saveGame("testuser","game1","testUpdate");
        gameBoard.saveGame("testuser2","game1","testUpdate");
        gameBoard.saveGame("testuser","game1","testUpdate1");
        List<GameRecord> list = database.getAllRecords();
        for(GameRecord gameRecord:list){
            System.out.println(gameRecord.getUsername()+"   " +gameRecord.getGameName()+"   "+gameRecord.getSerialisation());
        }
    }
    @Test
    public void removeAll(){
        database.removeDB();
        database.setupDB();
        database.createDB();
        database.addStartingData();
    }
    @Test
    public void check(){
        List<GameRecord> list = database.getAllRecords();
        for(GameRecord gameRecord:list){
            System.out.println(gameRecord.getUsername()+"   " +gameRecord.getGameName()+"   "+gameRecord.getSerialisation());
        }
    }
    @Test
    public void getRecordsByUser(){
        GameBoard gameBoard = new GameBoard(database);
        List<GameRecord> records = gameBoard.getRecords("testuser");
        for(GameRecord record: records){
            System.out.println(record.getUsername()+" "+record.getGameName()+" "+record.getSerialisation());
        }
    }
    @Test
    public void checkUSerId(){
        System.out.println(database.getIdByName("testuser2"));
    }
    @Test
    public void addAgame(){
        database.addGameFromQuestionableSource("testuser","testgame2","aaaa");
    }
}
