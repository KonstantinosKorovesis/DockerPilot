package gr.aueb.dmst.ProjectPr;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import com.github.dockerjava.api.model.Container;

public class Database {
    //set app directory name and location, set database name
    public static boolean setUpComplete = false;
    public static final String APP_DIRECTORY_NAME = ".dockerpilot";
    public static final String APP_DIRECTORY_PATH = System.getProperty("user.home") + File.separator + APP_DIRECTORY_NAME;
    public static final String DATABASE_NAME = "database" + ".db";
    public static final String DATABASE_PATH = APP_DIRECTORY_PATH + File.separator + DATABASE_NAME;
    public static final String TABLE_NAME_1 = "MEASUREMENT";
    public static final String TABLE_NAME_2 = "CONTAINER";
    public static final String JDBC_URL = "jdbc:sqlite:" + DATABASE_PATH;
    
    public static void setUpDatabase() {
        if (createDirectory()) {
            if (createDatabase()) {
                if (createTables()) {
                    setUpComplete = true;
                }
            }
        }
    }
    public static boolean createDirectory() {
        //check if the directory exists in the given path and if not, create it
        //true: directory was created or it already exists
        //false: failed to create directory
        File directoryFile = new File(APP_DIRECTORY_PATH);
        if (!directoryFile.exists()) {
            boolean created = directoryFile.mkdir();
            if (created) {
                System.out.println("The " + APP_DIRECTORY_NAME + " directory has been created at " + APP_DIRECTORY_PATH);
                return true;
            } else {
                System.err.println("[ERROR] Failed to create " + APP_DIRECTORY_NAME + " directory at " + APP_DIRECTORY_PATH);
                return false;
            }
        } else {
            System.out.println("The " + APP_DIRECTORY_NAME + " directory already exists at " + APP_DIRECTORY_PATH);
            return true;
        }
    }
    public static boolean createDatabase() {
        //create a database inside the given file path
        //if the database already exists, do nothing
        //true: database was created or already exists
        //false: failed to create the database
        File databaseFile = new File(DATABASE_PATH);
        if (!databaseFile.exists()) {
            try (Connection conn = DriverManager.getConnection(JDBC_URL);) {
                if (conn != null) {
                    System.out.println("The " + DATABASE_NAME + " database has been created at " + DATABASE_PATH);
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                System.err.println("[ERROR] Failed to create " + DATABASE_NAME + " database at " + DATABASE_PATH);
                System.err.println(e);
                return false;
            }
        } else {
            System.out.println("The " + DATABASE_NAME + " database already exists at " + DATABASE_PATH);
            return true;
        }
    }
    public static boolean createTables() {
        //creates the two tables
        //true: tables have been created or already exist
        //false: failed to create the tables
        String createTableContainerPack = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_1 + " (\n"
                            + "	measurementId       INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                            + "	measurementDate     DATE NOT NULL\n"
                            + ");";
        String createTableContainerData = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_2 + " (\n"
                            + " containerId     TEXT NOT NULL,\n"
                            + " image           TEXT NOT NULL,\n"
                            + " name            TEXT NOT NULL,\n"
                            + " dateCreated     DATE NOT NULL,\n"
                            + " state           TEXT NOT NULL,\n"
                            + " measurementId   INTEGER NOT NULL,\n"
                            + " FOREIGN KEY(measurementId) REFERENCES "  + TABLE_NAME_1 + "(measurementId),\n"
                            + " PRIMARY KEY(containerId, measurementId)\n"
                            + ");";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
            Statement stmt1 = conn.createStatement();
            Statement stmt2 = conn.createStatement();) {
            stmt1.execute(createTableContainerPack);
            stmt2.execute(createTableContainerData);
            System.out.println("The database tables " + TABLE_NAME_1 + " and " + TABLE_NAME_2 + " have been created or already exist.");
            return true;
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to create the database tables.");
            System.err.println(e);
            return false;
        }
    }
    //creates a new entry in the first table and returns the measurementId of the entry
    public static int insertMeasurement() {
        String sqlInsert = "INSERT INTO " + TABLE_NAME_1 + "(measurementDate) VALUES(DATE(\"now\"))"; //yyyy-MM-dd
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
            Statement stmt = conn.createStatement();) {
            stmt.executeUpdate(sqlInsert);
            //get and return number of current measurement
            String selectLastInsertedPackId = "SELECT * FROM " + TABLE_NAME_1 + " WHERE measurementId = last_insert_rowid()";
            try (ResultSet rs = stmt.executeQuery(selectLastInsertedPackId);) {
                int measurementId = rs.getInt("measurementId");
                return measurementId;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to insert into " + TABLE_NAME_1 + " table.");
            System.err.println(e);
        }
        return -1;
    }
    //creates a new entry for each of the existing containers and records their data at that time
    public static void insertContainers(int measurementId) {
        List<Container> containers = Monitor.getAllContainers();
        for (Container container : containers) {
            String[] row = ContainerModel.getContainerEntry(container);
            String sqlInsert = "INSERT INTO " + TABLE_NAME_2 +
                "(containerId, image, name, dateCreated, state, measurementId) VALUES (?,?,?,?,?,?)";
            try (Connection conn = DriverManager.getConnection(JDBC_URL);
                PreparedStatement pstmt = conn.prepareStatement(sqlInsert);) {
                int i = 0;
                pstmt.setString(1, row[i++]);
                pstmt.setString(2, row[i++]);
                pstmt.setString(3, row[i++]);
                pstmt.setString(4, row[i++]);
                pstmt.setString(5, row[i++]);
                pstmt.setInt(6, measurementId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("[ERROR] Failed to insert into " + TABLE_NAME_2 + " table.");
                System.err.println(e);
            }
        }
    }

    //SELECTS
    public static String[] selectAllDates() {
        List<String> datesList = new ArrayList<String>();
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
            Statement stmt = conn.createStatement();) {
            String selectAllDates = "SELECT DISTINCT measurementDate FROM " + TABLE_NAME_1;
            try (ResultSet rs = stmt.executeQuery(selectAllDates);) {
                while (rs.next()) {
                    datesList.add(rs.getString("measurementDate"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to select all distinct dates.");
            System.err.println(e);
        }
        String[] s = datesList.toArray(new String[0]);
        return s;
    }

    public static String[] selectMeasurementIdsOnDate(String date) {
        List<String> idList = new ArrayList<String>();
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
            Statement stmt = conn.createStatement();) {
            String selectIds = "SELECT DISTINCT measurementId FROM " + TABLE_NAME_1 + "\n"
                             + "WHERE measurementDate = \"" + date + "\"";
            try (ResultSet rs = stmt.executeQuery(selectIds);) {
                while (rs.next()) {
                    idList.add(rs.getString("measurementId"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to select all measurement IDs based on a specific date.");
            System.err.println(e);
        }
        String[] s = idList.toArray(new String[0]);
        return s;
    }

    public static int selectRunningContainerCount(String measurementId) {
        int count = 0;
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
            Statement stmt = conn.createStatement();) {
            String selectRunningContainerCount = "SELECT COUNT(*) AS count FROM " + TABLE_NAME_2 + "\n"
                             + "WHERE measurementId = " + measurementId + " AND state = \"running\"";
            try (ResultSet rs = stmt.executeQuery(selectRunningContainerCount);) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to select the running container count given a specific measurementId.");
            System.err.println(e);
        }
        return count;
    }

    public static int selectTotalContainerCount(String measurementId) {
        int count = 0;
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
            Statement stmt = conn.createStatement();) {
            String selectRunningContainerCount = "SELECT COUNT(*) AS count FROM " + TABLE_NAME_2 + "\n"
                             + "WHERE measurementId = " + measurementId;
            try (ResultSet rs = stmt.executeQuery(selectRunningContainerCount);) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to select the total container given a specific measurementId.");
            System.err.println(e);
        }
        return count;
    }

    public static List<String[]> selectContainerEntries(String measurementId) {
        List<String[]> containerEntries = new ArrayList<String[]>();
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
            Statement stmt = conn.createStatement();) {
            String selectIds = "SELECT * FROM " + TABLE_NAME_2 + " WHERE measurementId = " + measurementId;
            try (ResultSet rs = stmt.executeQuery(selectIds);) {
                while (rs.next()) {
                    String[] entry = new String[5];
                    int i = 0;
                    entry[i++] = rs.getString("name");
                    entry[i++] = rs.getString("containerId");
                    entry[i++] = rs.getString("image");
                    entry[i++] = rs.getString("dateCreated");
                    entry[i++] = rs.getString("state");
                    containerEntries.add(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to select the container entries given a specific measurementId");
            System.err.println(e);
        }
        return containerEntries;
    }
}