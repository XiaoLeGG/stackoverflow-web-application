package cn.edu.sustech.crawler;

import java.io.IOException;
import java.sql.SQLException;

public class CrawlerMain {
    
    private static final String SQL_HOST = "localhost";
    private static final String SQL_USER = "postgres";
    private static final String SQL_PASSWORD = "ljcfyh_123@99";
    private static final String SQL_DATABASE = "postgres";
    private static final int SQL_PORT = 5432;
    
    public static void main(String[] args) throws SQLException, IOException {
            DatabaseService databaseService = new DatabaseService(
                SQL_HOST, SQL_PORT, SQL_USER,
                SQL_PASSWORD, SQL_DATABASE
            );
            DataCollector dataCollector = new DataCollector(
                    databaseService,100, 1000
            );
            databaseService.connect();
            databaseService.createTable();
            databaseService.disableForeignKeyCheck();
            dataCollector.collectData();
            databaseService.enableForeignKeyCheck();
            databaseService.close();
    }
}
