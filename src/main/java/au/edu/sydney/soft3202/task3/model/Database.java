package au.edu.sydney.soft3202.task3.model;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Database {


    private static final String dbName = "game.db";
    private static final String dbURL = "jdbc:sqlite:" + dbName;

    public void createDB() {
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            System.out.println("Database already created");
            return;
        }
        try (Connection ignored = DriverManager.getConnection(dbURL)) {
            // If we get here that means no exception raised from getConnection - meaning it worked
            System.out.println("A new database has been created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void removeDB() {
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            boolean result = dbFile.delete();
            if (!result) {
                System.out.println("Couldn't delete existing db file");
                System.exit(-1);
            } else {
                System.out.println("Removed existing DB file.");
            }
        } else {
            System.out.println("No existing DB file.");
        }
    }

    public void setupDB() {
        String createUserTableSQL =
                """
                CREATE TABLE IF NOT EXISTS users (
                    id integer PRIMARY KEY,
                    username text NOT NULL
                );
                """;

        String createRecordTableSQL =
                """
                CREATE TABLE IF NOT EXISTS games (
                    id integer PRIMARY KEY,
                    userid integer NOT NULL,
                    gamename text NOT NULL,
                    serialisation text NOT NULL,
                    CONSTRAINT fk_username
                    FOREIGN KEY (userid)
                    REFERENCES users(id)

                );
                """;


        try (Connection conn = DriverManager.getConnection(dbURL);
             Statement statement = conn.createStatement()) {
            statement.execute(createUserTableSQL);
            statement.execute(createRecordTableSQL);

            System.out.println("Created tables");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void addStartingData() {
        String addUserDataSQL =
                """
                INSERT INTO users(username) VALUES
                     ("testuser"),
                     ("testuser1"),
                     ("testuser2"),
                     ("testuser3");
                """;

        String addGameDataSQL =
                """
                insert into games(userid,gamename,serialisation) VALUES
                	(1,"testname1","White|false|.,.,.,.,.,.,.,w;b,.,.,.,.,.,.,.;.,.,.,b,.,.,.,w;b,.,.,.,.,.,w,.;.,b,.,.,.,w,.,w;b,.,b,.,.,.,w,.;.,b,.,.,.,w,.,w;b,.,b,.,.,.,w,."),
                    (2,"testname2","Black|false|.,b,.,.,.,.,.,w;b,.,b,.,w,.,w,.;.,b,.,.,.,w,.,w;b,.,.,.,.,.,w,.;.,b,.,b,.,w,.,w;b,.,b,.,.,.,w,.;.,b,.,.,.,w,.,w;b,.,b,.,.,.,w,."),
                    (3,"testname3","Black|false|.,b,.,.,.,w,.,w;b,.,b,.,.,.,w,.;.,b,.,.,.,w,.,w;b,.,b,.,.,.,w,.;.,b,.,.,.,w,.,w;b,.,b,.,.,.,w,.;.,b,.,.,.,w,.,w;b,.,b,.,.,.,w,."),
                    (4,"testname4","Black|false|.,b,.,.,.,w,.,w;b,.,b,.,.,.,w,.;.,b,.,.,.,w,.,w;b,.,w,.,.,.,w,.;.,b,.,.,.,.,.,w;b,.,.,.,.,.,w,.;.,b,.,b,.,w,.,w;b,.,b,.,.,.,w,.");

                """;

        try (Connection conn = DriverManager.getConnection(dbURL);
             Statement statement = conn.createStatement()) {
            statement.execute(addUserDataSQL);
            statement.execute(addGameDataSQL);


            System.out.println("Added starting data");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public int getIdByName(String username){
        String checkUserIdByName =
                """
                SELECT id
                FROM users
                WHERE username =?
                """;

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement preparedStatement = conn.prepareStatement(checkUserIdByName)) {
            preparedStatement.setString(1, username);
            ResultSet results = preparedStatement.executeQuery();
            System.out.println();
            int result = Integer.parseInt(results.getString("id"));
            return result;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return -1;
    }

    public void addUserFromQuestionableSource(String username) {
        String addSingleUserWithParametersSQL =
                """
                INSERT INTO users(username) VALUES
                    (?)
                """;

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement preparedStatement = conn.prepareStatement(addSingleUserWithParametersSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();

            System.out.println("Added questionable data");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    public void addGameFromQuestionableSource(String username,String gameName, String serialisation) {
        int userid = getIdByName(username);
        String addSingleGameWithParametersSQL =
                """
                INSERT INTO games(userid,gamename,serialisation) VALUES
                    (?,?,?);
                """;

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement preparedStatement = conn.prepareStatement(addSingleGameWithParametersSQL)) {
            preparedStatement.setInt(1, userid);
            preparedStatement.setString(2, gameName);
            preparedStatement.setString(3, serialisation);
            preparedStatement.executeUpdate();

            System.out.println("Added questionable data");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    public List<GameRecord> getAllRecords() {
        List<GameRecord> records = new ArrayList<>();
        String gamesSQL =
                """
                select A.id, A.username, B.gamename,B.serialisation from users A join games B on (A.id = B.userid);
                """;

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement preparedStatement = conn.prepareStatement(gamesSQL)) {
            ResultSet results = preparedStatement.executeQuery();

            while (results.next()) {
                records.add(new GameRecord(results.getString("username"),results.getString("gamename"),results.getString("serialisation")));
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return records;
    }
    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        String gamesSQL =
                """
                SELECT username
                FROM users
                """;

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement preparedStatement = conn.prepareStatement(gamesSQL)) {
            ResultSet results = preparedStatement.executeQuery();

            while (results.next()) {
                users.add(results.getString("username"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return users;
    }

    public void updateGame(String username, String gameName, String serialisation){
        int id = getIdByName(username);
        String updateSQL =
                """
                UPDATE games
                SET serialisation = ?
                WHERE userid = ? and gamename = ?
                """;
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement preparedStatement = conn.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, serialisation);
            preparedStatement.setInt(2, id);
            preparedStatement.setString(3, gameName);
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }

}
