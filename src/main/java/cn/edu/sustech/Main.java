package cn.edu.sustech;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        // TODO: 在这里更改本地数据库连接信息
        DatabaseService databaseService = new DatabaseService(
                "localhost", 5432, "postgres",
                "ljcfyh_123@99", "postgres"
        );
        databaseService.connect(); // 连接数据库
        databaseService.createTable(); // 创建表
        DataCollector dataCollector = new DataCollector(databaseService, 100, 10000);
        // 创建数据收集器，这里的参数可以自行调整，爬取更大或更小的数据集
        databaseService.disableForeignKeyCheck(); // 关闭外键约束
        dataCollector.collectData(); // 收集数据并存储到数据库中
        databaseService.enableForeignKeyCheck(); // 开启外键约束

        // 所有API测试
        System.out.println(dataCollector.getNoAnsPercent());
        System.out.println(databaseService.queryNoAnswerQuestionPercent());
        System.out.println(databaseService.queryAnswerNumAvg());
        System.out.println(databaseService.queryAnswerNumMax());
        System.out.println(databaseService.queryAnswerNumDistribution());
        System.out.println(databaseService.queryAcceptedAnswerPercent());
        System.out.println(databaseService.queryQuestionTimeDiffDistribution());
        System.out.println(databaseService.queryNonAcceptedAnswerPercent());
        System.out.println(databaseService.queryTagDistribution());
        System.out.println(databaseService.queryTagScoreDistribution());
        System.out.println(databaseService.queryTagViewDistribution());
        System.out.println(databaseService.queryAccountAskTotal());
        System.out.println(databaseService.queryAccountAnswerTotal());
        System.out.println(databaseService.queryAccountCommentTotal());
        System.out.println(databaseService.queryActivateAccountID(1, 1, 1));
        System.out.println(databaseService.queryQuestionAppearTimes("java"));
        System.out.println(databaseService.queryAnswerAppearTimes("java"));
        System.out.println(databaseService.queryCommentAppearTimes("java"));
        databaseService.close(); // 关闭数据库连接
    }
}
