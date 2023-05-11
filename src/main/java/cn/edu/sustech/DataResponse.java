package cn.edu.sustech;
import org.postgresql.util.PGInterval;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataResponse {
    /*
        这是一个数据响应类，用于存储数据分析的结果，相应类可以在后端对数据库进行更新，并获得最新的统计数据。
        前端请通过实例化后的对象获得统计数据。
    */
    private Timestamp lastUpdate; // 最后更新时间
    private DataCollector dataCollector; // 爬虫
    private DatabaseService databaseService; // 数据库服务
    private double noAnswerQuestionPercent; // 无回答问题比例
    private double AnswerNumAvg; // 平均回答数
    private int AnswerNumMax; // 最大回答数
    private Map<Integer, Integer> answerNumDistribution; // 回答数分布
    private double AcceptedAnswerPercent; // 接受回答比例
    private Map<Integer, PGInterval> questionTimeDiffDistribution; // 问题时间间隔分布
    private double NonAcceptedAnswerPercent; // 未接受回答比例
    private Map<String, Integer> tagDistribution; // 标签分布
    private Map<String, Integer> tagScoreDistribution; // 标签分数分布
    private Map<String, Integer> tagViewDistribution; // 标签浏览量分布

    private Map<String, Integer> tagGroupDistribution; // 标签组分布
    private Map<String, Integer> tagGroupScoreDistribution; // 标签组分数分布
    private Map<String, Integer> tagGroupViewDistribution; // 标签组浏览量分布



    private int accountAskTotal; // 提问总数
    private int accountAnswerTotal; // 回答总数
    private int accountCommentTotal; // 评论总数


    private Map<String, Integer> javaAPIAppearanceInQuestion; // 问题中javaAPI出现次数
    private Map<String, Integer> javaAPIAppearanceInAnswer; // 回答中javaAPI出现次数
    private Map<String, Integer> javaAPIAppearanceInComment; // 评论中javaAPI出现次数

    private Map<String, Integer> accountQuestionTimes; // 用户提问次数
    private Map<String, Integer> accountAnswerTimes; // 用户回答次数
    private Map<String, Integer> accountCommentTimes; // 用户评论次数
    public double getNoAnswerQuestionPercent() {
        // 返回所有问题没有回答的问题的比例
        return noAnswerQuestionPercent;
    }
    public double getAnswerNumAvg() {
        // 返回所有问题的平均回答数
        return AnswerNumAvg;
    }
    public int getAnswerNumMax() {
        // 返回所有问题的最大回答数
        return AnswerNumMax;
    }
    public Map<Integer, Integer> getAnswerNumDistribution() {
        // 返回所有问题的回答数分布
        return answerNumDistribution;
    }
    public double getAcceptedAnswerPercent() {
        // 返回所有问题中有被接受回答的比例
        return AcceptedAnswerPercent;
    }
    public Map<Integer, PGInterval> getQuestionTimeDiffDistribution() {
        // 返回所有有接受回答的问题中，从提问到接受回答的时间间隔分布
        return questionTimeDiffDistribution;
    }
    public double getNonAcceptedAnswerPercent() {
        // 返回所有有接受回答的问题中，存在非接受答案的比例
        return NonAcceptedAnswerPercent;
    }
    public Map<String, Integer> getTagDistribution() {
        // 返回所有问题的标签分布
        return tagDistribution;
    }
    public Map<String, Integer> getTagScoreDistribution() {
        // 返回所有问题的标签分数分布
        return tagScoreDistribution;
    }
    public Map<String, Integer> getTagViewDistribution() {
        // 返回所有问题的标签浏览量分布
        return tagViewDistribution;
    }
    public Map<String, Integer> getTagGroupDistribution() {
        // 返回所有问题的标签组分布
        return tagGroupDistribution;
    }
    public Map<String, Integer> getTagGroupScoreDistribution() {
        // 返回所有问题的标签组分数分布
        return tagGroupScoreDistribution;
    }
    public Map<String, Integer> getTagGroupViewDistribution() {
        // 返回所有问题的标签组浏览量分布
        return tagGroupViewDistribution;
    }
    public int getAccountQuestionTotal() {
        // 返回所有提问的不同用户数目
        return accountAskTotal;
    }
    public int getAccountAnswerTotal() {
        // 返回所有回答的不同用户数目
        return accountAnswerTotal;
    }
    public int getAccountCommentTotal() {
        // 返回所有评论的不同用户数目
        return accountCommentTotal;
    }
    public List<String> getActivateAccountID(int question, int answer, int comment) {
        // 返回活跃用户的ID, question, answer, comment分别代表问题，回答，评论的权重，权重越大，该用户越活跃，返回列表按照活跃度降序
        Map<String, Integer> accountIDAppearTimes = new HashMap<>();
        for (String key : accountQuestionTimes.keySet()) {
            if (accountIDAppearTimes.containsKey(key)) {
                accountIDAppearTimes.put(key, accountIDAppearTimes.get(key) + accountQuestionTimes.get(key) * question);
            } else {
                accountIDAppearTimes.put(key, accountQuestionTimes.get(key) * question);
            }
        }
        for (String key : accountAnswerTimes.keySet()) {
            if (accountIDAppearTimes.containsKey(key)) {
                accountIDAppearTimes.put(key, accountIDAppearTimes.get(key) + accountAnswerTimes.get(key) * answer);
            } else {
                accountIDAppearTimes.put(key, accountAnswerTimes.get(key) * answer);
            }
        }
        for (String key : accountCommentTimes.keySet()) {
            if (accountIDAppearTimes.containsKey(key)) {
                accountIDAppearTimes.put(key, accountIDAppearTimes.get(key) + accountCommentTimes.get(key) * comment);
            } else {
                accountIDAppearTimes.put(key, accountCommentTimes.get(key) * comment);
            }
        }
        List<String> activateAccountID = new ArrayList<>();
        accountIDAppearTimes.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .forEach(entry -> activateAccountID.add(entry.getKey()));
        return activateAccountID;
    }
    public Map<String, Integer> getJavaAPIAppearanceInQuestion() {
        // 返回问题中javaAPI出现次数
        return javaAPIAppearanceInQuestion;
    }
    public Map<String, Integer> getJavaAPIAppearanceInAnswer() {
        // 返回回答中javaAPI出现次数
        return javaAPIAppearanceInAnswer;
    }
    public Map<String, Integer> getjavaAPIAppearanceInComment() {
        // 返回评论中javaAPI出现次数
        return javaAPIAppearanceInComment;
    }

    public Map<String, Integer> getAccountQuestionTimes() {
        // 返回用户提问次数
        return accountQuestionTimes;
    }
    public Map<String, Integer> getAccountAnswerTimes() {
        // 返回用户回答次数
        return accountAnswerTimes;
    }
    public Map<String, Integer> getAccountCommentTimes() {
        // 返回用户评论次数
        return accountCommentTimes;
    }
    public Map<String, Integer> getActivateJavaAPI(int question, int answer, int comment) {
        // 返回<活跃javaAPI,活跃度>, question, answer, comment分别代表问题，回答，评论的权重，权重越大，该javaAPI越活跃，返回列表按照活跃度降序
        Map<String, Integer> javaAPIAppearTimes = new HashMap<>();
        for (String key : javaAPIAppearanceInQuestion.keySet()) {
            if (javaAPIAppearTimes.containsKey(key)) {
                javaAPIAppearTimes.put(key, javaAPIAppearTimes.get(key) + javaAPIAppearanceInQuestion.get(key) * question);
            } else {
                javaAPIAppearTimes.put(key, javaAPIAppearanceInQuestion.get(key) * question);
            }
        }
        for (String key : javaAPIAppearanceInAnswer.keySet()) {
            if (javaAPIAppearTimes.containsKey(key)) {
                javaAPIAppearTimes.put(key, javaAPIAppearTimes.get(key) + javaAPIAppearanceInAnswer.get(key) * answer);
            } else {
                javaAPIAppearTimes.put(key, javaAPIAppearanceInAnswer.get(key) * answer);
            }
        }
        for (String key : javaAPIAppearanceInComment.keySet()) {
            if (javaAPIAppearTimes.containsKey(key)) {
                javaAPIAppearTimes.put(key, javaAPIAppearTimes.get(key) + javaAPIAppearanceInComment.get(key) * comment);
            } else {
                javaAPIAppearTimes.put(key, javaAPIAppearanceInComment.get(key) * comment);
            }
        }
        return javaAPIAppearTimes;
    }
    public Map<String, Integer> getAccountActivity(int question, int answer, int comment) {
        // 返回<用户ID,活跃度>, question, answer, comment分别代表问题，回答，评论的权重，权重越大，该用户越活跃，返回列表按照活跃度降序
        Map<String, Integer> accountIDAppearTimes = new HashMap<>();
        for (String key : accountQuestionTimes.keySet()) {
            if (accountIDAppearTimes.containsKey(key)) {
                accountIDAppearTimes.put(key, accountIDAppearTimes.get(key) + accountQuestionTimes.get(key) * question);
            } else {
                accountIDAppearTimes.put(key, accountQuestionTimes.get(key) * question);
            }
        }
        for (String key : accountAnswerTimes.keySet()) {
            if (accountIDAppearTimes.containsKey(key)) {
                accountIDAppearTimes.put(key, accountIDAppearTimes.get(key) + accountAnswerTimes.get(key) * answer);
            } else {
                accountIDAppearTimes.put(key, accountAnswerTimes.get(key) * answer);
            }
        }
        for (String key : accountCommentTimes.keySet()) {
            if (accountIDAppearTimes.containsKey(key)) {
                accountIDAppearTimes.put(key, accountIDAppearTimes.get(key) + accountCommentTimes.get(key) * comment);
            } else {
                accountIDAppearTimes.put(key, accountCommentTimes.get(key) * comment);
            }
        }
        return accountIDAppearTimes;
    }
    public Timestamp getLastUpdate() {
        return lastUpdate;
    } // 获取最新更新时间
    public DataResponse(String host, int port, String user, String password, String database, int pageSize, int pageStep) throws IOException, SQLException {
        // host 数据库地址, port 数据库端口, user 数据库用户名, password 数据库密码, database 数据库名, pageSize 每页大小, pageStep 每次查询的页数
        databaseService = new DatabaseService(host, port, user, password, database);
        dataCollector = new DataCollector(databaseService, pageSize, pageStep);
        refresh();
    }
    public void refresh() throws SQLException, IOException {
        // 重新抓取，并更新数据
        databaseService.connect(); // 连接数据库
        databaseService.createTable(); // 创建表
        databaseService.disableForeignKeyCheck(); // 关闭外键约束
        dataCollector.collectData(); // 收集数据并存储到数据库中
        databaseService.enableForeignKeyCheck(); // 开启外键约束
        noAnswerQuestionPercent = databaseService.queryNoAnswerQuestionPercent(); // 查询无回答问题比例
        AnswerNumAvg = databaseService.queryAnswerNumAvg(); // 查询平均回答数
        AnswerNumMax = databaseService.queryAnswerNumMax(); // 查询最大回答数
        answerNumDistribution = databaseService.queryAnswerNumDistribution(); // 查询回答数分布
        AcceptedAnswerPercent = databaseService.queryAcceptedAnswerPercent(); // 查询被接受回答比例
        questionTimeDiffDistribution = databaseService.queryQuestionTimeDiffDistribution(); // 查询问题时间差分布
        NonAcceptedAnswerPercent = databaseService.queryNonAcceptedAnswerPercent(); // 查询未被接受回答比例
        tagDistribution = databaseService.queryTagDistribution(); // 查询标签分布
        tagScoreDistribution = databaseService.queryTagScoreDistribution(); // 查询标签分数分布
        tagViewDistribution = databaseService.queryTagViewDistribution(); // 查询标签浏览量分布
        tagGroupDistribution = databaseService.queryTagGroupDistribution(); // 查询标签组分布
        tagGroupScoreDistribution = databaseService.queryTagGroupScoreDistribution(); // 查询标签组分数分布
        tagGroupViewDistribution = databaseService.queryTagGroupViewDistribution(); // 查询标签组浏览量分布
        accountAskTotal = databaseService.queryAccountAskTotal(); // 查询用户提问总数
        accountAnswerTotal = databaseService.queryAccountAnswerTotal(); // 查询用户回答总数
        accountCommentTotal = databaseService.queryAccountCommentTotal(); // 查询用户评论总数
        accountAnswerTimes = databaseService.queryAccountAnswerNum(); // 查询用户回答次数
        accountQuestionTimes = databaseService.queryAccountQuestionNum(); // 查询用户提问次数
        accountCommentTimes = databaseService.queryAccountCommentNum(); // 查询用户评论次数
        String redColorCode = "\u001B[31m"; String resetColorCode = "\u001B[0m";
        System.out.println(redColorCode + "正在查询javaAPI在Question中出现次数，请稍等..." + resetColorCode);
        javaAPIAppearanceInQuestion = databaseService.queryJavaAPIAppearanceInQuestion(); // 查询javaAPI在问题中出现次数
        System.out.println(redColorCode + "正在查询javaAPI在Answer中出现次数，请稍等..." + resetColorCode);
        javaAPIAppearanceInAnswer = databaseService.queryJavaAPIAppearanceInAnswer(); // 查询javaAPI在回答中出现次数
        System.out.println(redColorCode + "正在查询javaAPI在Comment中出现次数，请稍等..." + resetColorCode);
        javaAPIAppearanceInComment = databaseService.queryJavaAPIAppearanceInComment(); // 查询javaAPI在评论中出现次数
        databaseService.close(); // 关闭数据库连接
        lastUpdate = new Timestamp(System.currentTimeMillis()); // 更新最后更新时间
    }
}
