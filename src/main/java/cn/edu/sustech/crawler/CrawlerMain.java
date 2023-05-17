package cn.edu.sustech.crawler;

import java.io.IOException;
import java.sql.SQLException;

public class CrawlerMain {
    public static void main(String[] args) throws SQLException, IOException {
            DatabaseService databaseService = new DatabaseService(
                    "localhost", 5432, "postgres",
                    "ljcfyh_123@99", "postgres"
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
