package cn.edu.sustech.crawler;

import java.io.IOException;
import java.sql.SQLException;

public class CrawlerMain {
    
    private static final String SQL_HOST = "localhost";
    private static final String SQL_USER = "test";
    private static final String SQL_PASSWORD = "123456";
    private static final String SQL_DATABASE = "cs209a";
    private static final int SQL_PORT = 5432;
    
    public static void main(String[] args) throws SQLException, IOException {
            DatabaseService databaseService = new DatabaseService(
                SQL_HOST, SQL_PORT, SQL_USER,
                SQL_PASSWORD, SQL_DATABASE
            );
            DataCollector dataCollector = new DataCollector(
                    databaseService,100, 10000
            );
            databaseService.connect();
            databaseService.createTable();
            databaseService.disableForeignKeyCheck();
            dataCollector.collectData();
            databaseService.enableForeignKeyCheck();
            databaseService.close();
    }
}
