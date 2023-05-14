package cn.edu.sustech.crawler;
import com.alibaba.fastjson.JSONObject;
import org.postgresql.util.PGInterval;


import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseService {
    private Connection connection; // 数据库连接
    private String host; // 数据库主机
    private int port; // 数据库端口
    private String user; // 数据库用户名
    private String password; // 数据库密码
    private String database; // 数据库名

    private StanfordCoreNLPService stanfordCoreNLPService;

    public DatabaseService(String host, int port, String user, String password, String database) {
        // 初始化数据库连接配置
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
        connection = null;
        stanfordCoreNLPService = new StanfordCoreNLPService();
    }
    public boolean connect() {
        // 连接数据库
        try {
            connection = java.sql.DriverManager.getConnection(
                    "jdbc:postgresql://" + host + ":" + port + "/" + database,
                    user,
                    password
            );
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Cannot connect to the database.");
            e.printStackTrace();
            return false;
        }
    }
    public boolean isConnected() {
        return connection!=null;
    } // 判断是否连接到数据库
    public void close() {
        // 关闭数据库连接
        try {
            connection.close();
        } catch (java.sql.SQLException e) {
            System.out.println("Cannot close the connection.");
            e.printStackTrace();
        }
    }
    private PreparedStatement prepareStatement(String sql) {
        // 准备 SQL 语句
        try {
            return connection.prepareStatement(sql);
        } catch (java.sql.SQLException e) {
            System.out.println("Cannot prepare the statement.");
            e.printStackTrace();
            return null;
        }
    }
    public void createTable() throws SQLException {
        // 创建数据库表
        Statement statement = this.connection.createStatement();
        statement.executeUpdate("drop table if exists comment;\n" +
                "drop table if exists answer;\n" +
                "drop table if exists connection_tag_and_question;\n" +
                "drop table if exists tag;\n" +
                "drop table if exists question;\n" +
                "drop table if exists owner;\n" +
                "create table owner(\n" +
                "  account_id int primary key,\n" +
                "  user_id int not null,\n" +
                "  profile_image text,\n" +
                "  link text not null,\n" +
                "  user_type text not null,\n" +
                "  display_name text not null,\n" +
                "  reputation int not null\n" +
                ");\n" +
                "create table question (\n" +
                "    question_id int primary key,\n" +
                "    score int not null,\n" +
                "    link text not null,\n" +
                "    answer_count int not null,\n" +
                "    view_count int not null,\n" +
                "    content_license text,\n" +
                "    title text not null,\n" +
                "    last_activity_date timestamp not null,\n" +
                "    last_edit_date timestamp,\n" +
                "    creation_date timestamp not null,\n" +
                "    account_id int not null,\n" +
                "    body text not null,\n" +
                "    foreign key (account_id) references owner(account_id)\n" +
                ");\n" +
                "create table tag (\n" +
                "    tag_name text primary key\n" +
                ");\n" +
                "create table answer(\n" +
                "    answer_id int primary key,\n" +
                "    last_activity_date timestamp not null,\n" +
                "    last_edit_date timestamp,\n" +
                "    creation_date timestamp not null,\n" +
                "    score int not null,\n" +
                "    is_accepted bool not null,\n" +
                "    content_license text,\n" +
                "    question_id int not null,\n" +
                "    body text not null,\n" +
                "    account_id int not null,\n" +
                "    foreign key (question_id) references question(question_id),\n" +
                "    foreign key (account_id) references owner(account_id)\n" +
                ");\n" +
                "create table connection_tag_and_question (\n" +
                "    tag_name text not null,\n" +
                "    question_id int not null,\n" +
                "    foreign key (tag_name) references tag (tag_name),\n" +
                "    foreign key (question_id) references question(question_id)\n" +
                ");\n" +
                "create table comment (\n" +
                "    comment_id int primary key,\n" +
                "    edited bool not null,\n" +
                "    post_id int not null,\n" +
                "    body text not null,\n" +
                "    creation_date timestamp not null,\n" +
                "    score int not null,\n" +
                "    content_license text,\n" +
                "    account_id int not null,\n" +
                "    foreign key (account_id) references owner(account_id),\n" +
                "    foreign key (post_id) references  answer(answer_id)\n" +
                ")");
    }
    
    public void disableForeignKeyCheck() throws SQLException {
        // 关闭外键约束
        Statement statement = connection.createStatement();
        statement.execute("SET session_replication_role = replica;");
        statement.close();
    }
    
    public void enableForeignKeyCheck() throws SQLException {
        // 开启外键约束
        Statement statement = connection.createStatement();
        statement.execute("SET session_replication_role = DEFAULT;");
        statement.close();
    }
    
    public void insertQuestion(int question_id, Timestamp last_activity_date, Timestamp last_edit_date, Timestamp creation_date, int score, String link, int answer_count, int view_count, String content_license, String title, int account_id, String body) throws SQLException {
        // 在Question表中插入一条记录
        PreparedStatement statement = this.prepareStatement("insert into question values (?,?,?,?,?,?,?,?,?,?,?,?);");
        statement.setInt(1, question_id);
        statement.setInt(2, score);
        statement.setString(3, link);
        statement.setInt(4, answer_count);
        statement.setInt(5, view_count);
        statement.setString(6, content_license);
        statement.setString(7, title);
        statement.setTimestamp(8, last_activity_date);
        statement.setTimestamp(9, last_edit_date);
        statement.setTimestamp(10, creation_date);
        statement.setInt(11, account_id);
        statement.setString(12, body);
        statement.executeUpdate();
    }
    
    public void insertTag(String tag_name) throws SQLException {
        // 在Tag表中插入一条记录
        PreparedStatement statement = this.prepareStatement("select count(*) from tag where tag_name = ?");
        statement.setString(1, tag_name);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) > 0) {
            return;
        }
        statement = this.prepareStatement("insert into tag values (?)");
        statement.setString(1, tag_name);
        statement.executeUpdate();
    }
    
    public void insertAnswer(int answer_id, Timestamp last_activity_date, Timestamp last_edit_date, Timestamp creation_date, int score, boolean is_accepted, String content_license, int question_id, String body, int account_id) throws SQLException {
        // 在Answer表中插入一条记录
        PreparedStatement statement = this.prepareStatement("insert into answer values (?,?,?,?,?,?,?,?,?,?)");
        statement.setInt(1, answer_id);
        statement.setTimestamp(2, last_activity_date);
        statement.setTimestamp(3, last_edit_date);
        statement.setTimestamp(4, creation_date);
        statement.setInt(5, score);
        statement.setBoolean(6, is_accepted);
        statement.setString(7, content_license);
        statement.setInt(8, question_id);
        statement.setString(9, body);
        statement.setInt(10, account_id);
        statement.executeUpdate();
    }
    
    public void insertOwner(int account_id, int user_id, String profile_image, String link, String user_type, String display_name, int reputation) throws SQLException {
        // 在Owner表中插入一条记录
        PreparedStatement statement = this.prepareStatement("select count(*) from owner where account_id = ?");
        statement.setInt(1, account_id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) > 0) {
            return;
        }
        statement = this.prepareStatement("insert into owner values (?,?,?,?,?,?,?)");
        statement.setInt(1, account_id);
        statement.setInt(2, user_id);
        statement.setString(3, profile_image);
        statement.setString(4, link);
        statement.setString(5, user_type);
        statement.setString(6, account_id == -1 ? "does_not_exist" : display_name);
        statement.setInt(7, reputation);
        statement.executeUpdate();
    }
    
    public void insertComment(int comment_id, boolean edited, int post_id, String body, Timestamp creation_date, int score, String content_license, int account_id) throws SQLException {
        // 在Comment表中插入一条记录
        PreparedStatement statement = this.prepareStatement("insert into comment values (?,?,?,?,?,?,?,?)");
        statement.setInt(1, comment_id);
        statement.setBoolean(2, edited);
        statement.setInt(3, post_id);
        statement.setString(4, body);
        statement.setTimestamp(5, creation_date);
        statement.setInt(6, score);
        statement.setString(7, content_license);
        statement.setInt(8, account_id);
        statement.execute();
    }
    
    public void insertConnectionTagAndQuestion(String tag_name, int question_id) throws SQLException {
        // 在ConnectionTagAndQuestion表中插入一条记录（为了多对多关系额外创建的数据表，以符合第三范式）
        PreparedStatement statement = this.prepareStatement("insert into connection_tag_and_question values (?,?)");
        statement.setString(1, tag_name);
        statement.setInt(2, question_id);
        statement.executeUpdate();
    }
    
    private Timestamp convertDate(Integer date) {
        // 将Unix时间戳转换为Timestamp
        if (date == null) {
            return null;
        }
        return new Timestamp(date * 1000L);
    }
    
    public void insertQuestionRecord(JSONObject questionJSON) throws SQLException {
        // 将一个问题的JSON数据插入到数据库中
        JSONObject ownerJson = questionJSON.getJSONObject("owner");
        Owner owner = new Owner(
                ownerJson.getString("profile_image"),
                ownerJson.getInteger("account_id") == null ? -1: ownerJson.getInteger("account_id"),
                ownerJson.getString("user_type"),
                ownerJson.getInteger("user_id") == null ? -1: ownerJson.getInteger("user_id"),
                ownerJson.getString("link") == null ? "": ownerJson.getString("link"),
                ownerJson.getString("display_name"),
                ownerJson.getInteger("reputation") == null ? -1: ownerJson.getInteger("reputation")
        );
        insertQuestion(
                questionJSON.getInteger("question_id"),
                convertDate(questionJSON.getInteger("last_activity_date")),
                convertDate(questionJSON.getInteger("last_edit_date")),
                convertDate(questionJSON.getInteger("creation_date")),
                questionJSON.getInteger("score"),
                questionJSON.getString("link"),
                questionJSON.getInteger("answer_count"),
                questionJSON.getInteger("view_count"),
                questionJSON.getString("content_license"),
                questionJSON.getString("title"),
                owner.getAccountId(),
                questionJSON.getString("body")
        );
        for (Object tag : questionJSON.getJSONArray("tags")) {
            insertTag((String) tag);
        }
        for (Object tag: questionJSON.getJSONArray("tags")) {
            insertConnectionTagAndQuestion((String) tag, questionJSON.getInteger("question_id"));
        }
        insertOwner(
            owner.getAccountId(),
            owner.getUserId(),
            owner.getProfileImage(),
            owner.getLink(),
            owner.getUserType(),
            owner.getDisplayName(),
            owner.getReputation()
        );
    }
    public void insertAnswerRecord(JSONObject answerJSON) throws SQLException {
        // 将一个回答的JSON数据插入到数据库中
        JSONObject ownerJson = answerJSON.getJSONObject("owner");
        Owner owner = new Owner(
            ownerJson.getString("profile_image"),
            ownerJson.getInteger("account_id") == null ? -1: ownerJson.getInteger("account_id"),
            ownerJson.getString("user_type"),
            ownerJson.getInteger("user_id") == null ? -1: ownerJson.getInteger("user_id"),
            ownerJson.getString("link") == null ? "": ownerJson.getString("link"),
            ownerJson.getString("display_name"),
            ownerJson.getInteger("reputation") == null ? -1: ownerJson.getInteger("reputation")
        );
        insertAnswer(
            answerJSON.getInteger("answer_id"),
            convertDate(answerJSON.getInteger("last_activity_date")),
            convertDate(answerJSON.getInteger("last_edit_date")),
            convertDate(answerJSON.getInteger("creation_date")),
            answerJSON.getInteger("score"),
            answerJSON.getBoolean("is_accepted"),
            answerJSON.getString("content_license"),
            answerJSON.getInteger("question_id"),
            answerJSON.getString("body"),
            owner.getAccountId()
        );
        insertOwner(
            owner.getAccountId(),
            owner.getUserId(),
            owner.getProfileImage(),
            owner.getLink(),
            owner.getUserType(),
            owner.getDisplayName(),
            owner.getReputation()
        );
    }
    void insertCommentRecord(JSONObject commentJSON) throws SQLException {
        // 将一个评论的JSON数据插入到数据库中
        JSONObject ownerJson = commentJSON.getJSONObject("owner");
        Owner owner = new Owner(
            ownerJson.getString("profile_image"),
            ownerJson.getInteger("account_id") == null ? -1: ownerJson.getInteger("account_id"),
            ownerJson.getString("user_type"),
            ownerJson.getInteger("user_id") == null ? -1: ownerJson.getInteger("user_id"),
            ownerJson.getString("link") == null ? "": ownerJson.getString("link"),
            ownerJson.getString("display_name"),
            ownerJson.getInteger("reputation") == null ? -1: ownerJson.getInteger("reputation")
        );
        // public void insertComment(int comment_id, boolean edited, int post_id, String body, Timestamp creation_date, int score, String content_license, int account_id) throws SQLException {
        insertComment(
            commentJSON.getInteger("comment_id"),
            commentJSON.getBoolean("edited"),
            commentJSON.getInteger("post_id"),
            commentJSON.getString("body"),
            convertDate(commentJSON.getInteger("creation_date")),
            commentJSON.getInteger("score"),
            commentJSON.getString("content_license"),
            owner.getAccountId()
        );
        insertOwner(
            owner.getAccountId(),
            owner.getUserId(),
            owner.getProfileImage(),
            owner.getLink(),
            owner.getUserType(),
            owner.getDisplayName(),
            owner.getReputation()
        );
    }
    public double queryNoAnswerQuestionPercent() throws SQLException {
        // 查询没有回答的问题的比例
        PreparedStatement statement = this.prepareStatement("select (1.0 * (select count(*) " +
                "from question where answer_count = 0)) / (select count(*) from question) as no_answer_precent;");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getDouble("no_answer_precent");
        }
        return -1;
    }
    public double queryAnswerNumAvg() throws SQLException {
        // 查询回答数的平均值
        PreparedStatement statement = this.prepareStatement("select 1.0 * sum(answer_count) / (select count(*) from question) as average_answer from question;");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getDouble("average_answer");
        }
        return -1;
    }
    public int queryAnswerNumMax() throws SQLException {
        // 查询回答数的最大值
        PreparedStatement statement = this.prepareStatement("select max(answer_count) as max_answer from question;");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("max_answer");
        }
        return -1;
    }
    public Map<Integer, Integer> queryAnswerNumDistribution() throws SQLException {
        // 查询回答数的分布, 返回一个map, key为回答数, value为回答数为key的问题的个数
        PreparedStatement statement = this.prepareStatement("select answer_count as num, count(answer_count) as count  from question group by answer_count order by count desc;");
        ResultSet resultSet = statement.executeQuery();
        Map<Integer, Integer> result = new HashMap<>();
        int index = 0;
        while (resultSet.next()) {
            result.put(resultSet.getInt("num"), resultSet.getInt("count"));
            index++;
        }
        return result;
    }
    public double queryAcceptedAnswerPercent() throws SQLException {
        // 查询有接受回答的问题的比例
        PreparedStatement statement = this.prepareStatement("select (select 1.0 * count(distinct question_id) from answer where is_accepted = true) / (select count(*) from question) as accept_question_precent;");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getDouble("accept_question_precent");
        }
        return -1;
    }
    public Map<Integer, PGInterval> queryQuestionTimeDiffDistribution() throws SQLException {
        // 查询问题和最早被接受回答的时间差的分布, 返回一个map, key为问题的id, value为时间差
        PreparedStatement statement = this.prepareStatement("select question.question_id, (foo.creation_date - question.creation_date) as date_diff from question\n" +
                "right join (select question_id, creation_date from answer where is_accepted = true) foo\n" +
                "on question.question_id = foo.question_id;");
        ResultSet resultSet = statement.executeQuery();
        Map<Integer, PGInterval> result = new HashMap<>();
        int index = 0;
        while (resultSet.next()) {
            PGInterval interval = (PGInterval) resultSet.getObject("date_diff");
            result.put(resultSet.getInt("question_id"), interval);
            index++;
        }
        return result;
    }
    public double queryNonAcceptedAnswerPercent() throws SQLException {
        // 查询拥有非接受答案问题的比例
        PreparedStatement statement = this.prepareStatement("select 1.0 * count(*) / (select count(*) from (select answer.score, answer.question_id from answer where is_accepted = true) as foo3) as non_accept_question_precent from\n" +
                "(select answer.score, answer.question_id from answer where is_accepted = true) as foo2\n" +
                "left join (select max(score) as max_score, question_id from answer where is_accepted = false group by question_id) as foo1\n" +
                "on foo1.question_id = foo2.question_id\n" +
                "where foo1.max_score is not null and foo1.max_score > foo2.score;");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getDouble("non_accept_question_precent");
        }
        return -1;
    }

    public Map<String, Integer> queryTagDistribution() throws SQLException {
        // 查询标签的分布, 返回一个map, key为标签名, value为标签名为key的问题的个数
        PreparedStatement statement = this.prepareStatement("select tag_name, count(*) as cnt from connection_tag_and_question\n" +
                "group by tag_name\n" +
                "order by cnt desc;");
        ResultSet resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        int index = 0;
        while (resultSet.next()) {
            result.put(resultSet.getString("tag_name"), resultSet.getInt("cnt"));
            index++;
        }
        return result;
    }
    public Map<String, Integer> queryTagScoreDistribution() throws SQLException {
        // 查询标签的分数分布, 返回一个map, key为标签名, value为标签名为key的问题的分数总和
        PreparedStatement statement = this.prepareStatement("select tag_name, sum(score) as sum from (select connection_tag_and_question.tag_name, score from question\n" +
                "left join connection_tag_and_question\n" +
                "on question.question_id = connection_tag_and_question.question_id) as foo\n" +
                "group by tag_name\n" +
                "order by sum desc;");
        ResultSet resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        int index = 0;
        while (resultSet.next()) {
            result.put(resultSet.getString("tag_name"), resultSet.getInt("sum"));
            index++;
        }
        return result;
    }
    public Map<String, Integer> queryTagViewDistribution() throws SQLException {
        // 查询标签的浏览量分布, 返回一个map, key为标签名, value为标签名为key的问题的浏览量总和
        PreparedStatement statement = this.prepareStatement("select tag_name, sum(view_count) as sum from (select connection_tag_and_question.tag_name, view_count from question\n" +
                "left join connection_tag_and_question\n" +
                "on question.question_id = connection_tag_and_question.question_id) as foo\n" +
                "group by tag_name\n" +
                "order by sum desc;");
        ResultSet resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        int index = 0;
        while (resultSet.next()) {
            result.put(resultSet.getString("tag_name"), resultSet.getInt("sum"));
            index++;
        }
        return result;
    }
    public Map<String, Integer> queryTagGroupDistribution() throws SQLException {
        // 查询标签的分组分布, 返回一个map, key为标签名, value为标签名为key的问题的分组总和
        PreparedStatement statement = this.prepareStatement("select tag_names, count(*) as count from\n" +
                "(select question_id, string_agg(tag_name, ',') as tag_names\n" +
                "from connection_tag_and_question\n" +
                "group by question_id) as foo\n" +
                "group by tag_names;");
        ResultSet resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        int index = 0;
        while (resultSet.next()) {
            result.put(resultSet.getString("tag_names"), resultSet.getInt("count"));
            index++;
        }
        return result;
    }
    public Map<String, Integer> queryTagGroupScoreDistribution() throws SQLException {
        // 查询标签的分组分数分布, 返回一个map, key为标签名, value为标签名为key的问题的分组分数总和
        PreparedStatement statement = this.prepareStatement("select tag_names, sum(score) from (select tag_names, score from (select question_id, string_agg(tag_name, ',') as tag_names\n" +
                "from connection_tag_and_question\n" +
                "group by question_id) as foo1\n" +
                "left join question\n" +
                "on foo1.question_id = question.question_id) as foo2\n" +
                "group by tag_names;\n");
        ResultSet resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        int index = 0;
        while (resultSet.next()) {
            result.put(resultSet.getString("tag_names"), resultSet.getInt("sum"));
            index++;
        }
        return result;
    }
    public Map<String, Integer> queryTagGroupViewDistribution() throws SQLException {
        // 查询标签的分组浏览量分布, 返回一个map, key为标签名, value为标签名为key的问题的分组浏览量总和
        PreparedStatement statement = this.prepareStatement("select tag_names, sum(view_count) from (select tag_names, view_count from (select question_id, string_agg(tag_name, ',') as tag_names\n" +
                "from connection_tag_and_question\n" +
                "group by question_id) as foo1\n" +
                "left join question\n" +
                "on foo1.question_id = question.question_id) as foo2\n" +
                "group by tag_names;\n");
        ResultSet resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        int index = 0;
        while (resultSet.next()) {
            result.put(resultSet.getString("tag_names"), resultSet.getInt("sum"));
            index++;
        }
        return result;
    }
    public int queryAccountAskTotal() throws SQLException {
        // 查询提问的不同用户的个数
        PreparedStatement statement = this.prepareStatement("select count(distinct account_id) as cnt from question;");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("cnt");
    }
    public int queryAccountAnswerTotal() throws SQLException {
        // 查询回答的不同用户的个数
        PreparedStatement statement = this.prepareStatement("select count(distinct account_id) as cnt from answer;");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("cnt");
    }
    public int queryAccountCommentTotal() throws SQLException {
        // 查询评论的不同用户的个数
        PreparedStatement statement = this.prepareStatement("select count(distinct account_id) as cnt from comment;");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("cnt");
    }
    public Map<String, Integer> queryAccountQuestionNum() throws SQLException {
        // 查询不同用户的提问数, 返回一个map, key为用户id, value为该用户的提问数
        PreparedStatement statement = this.prepareStatement("select account_id, count(*) as count from question \n" +
                "where account_id<>-1\n" +
                "group by account_id;");
        ResultSet resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        int index = 0;
        while (resultSet.next()) {
            result.put(resultSet.getString("account_id"), resultSet.getInt("count"));
            index++;
        }
        return result;
    }
    public Map<String, Integer> queryAccountAnswerNum() throws SQLException {
        PreparedStatement statement = this.prepareStatement("select account_id, count(*) as count from answer \n" +
                "where account_id<>-1\n" +
                "group by account_id;");
        ResultSet resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        int index = 0;
        while (resultSet.next()) {
            result.put(resultSet.getString("account_id"), resultSet.getInt("count"));
            index++;
        }
        return result;
    }
    public Map<String, Integer> queryAccountCommentNum() throws SQLException {
        // 查询不同用户的评论数, 返回一个map, key为用户id, value为该用户的评论数
        PreparedStatement statement = this.prepareStatement("select account_id, count(*) as count from comment \n" +
                "where account_id<>-1\n" +
                "group by account_id;");
        ResultSet resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        int index = 0;
        while (resultSet.next()) {
            result.put(resultSet.getString("account_id"), resultSet.getInt("count"));
            index++;
        }
        return result;
    }


    public Map<String, Integer> queryJavaAPIAppearanceInQuestion() throws SQLException {
        // 查询问题中出现的java api的次数, 返回一个map, key为java api的名字, value为该api出现的次数
        PreparedStatement statement = this.prepareStatement("select count(*) from question;");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int nowCnt = 0, len = resultSet.getInt(1);
        statement = this.prepareStatement("select title, body from question;");
        resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        while (resultSet.next()) {
            nowCnt ++;
            if (nowCnt % (len / 10) == 0) {
                String redColorCode = "\u001B[31m"; String resetColorCode = "\u001B[0m";
                System.out.println("已完成" + redColorCode + (nowCnt * 100 / len) + "%" + resetColorCode);
            }
            String body = resultSet.getString("title");
            Map<String, Integer> temp = stanfordCoreNLPService.getAllJavaAPI(body);
            for (String key : temp.keySet()) {
                if (result.containsKey(key)) {
                    result.put(key, result.get(key) + temp.get(key));
                } else {
                    result.put(key, temp.get(key));
                }
            }
            body = resultSet.getString("body");
            temp = stanfordCoreNLPService.getAllJavaAPI(body);
            for (String key : temp.keySet()) {
                if (result.containsKey(key)) {
                    result.put(key, result.get(key) + temp.get(key));
                } else {
                    result.put(key, temp.get(key));
                }
            }
        }
        return result;
    }
    public Map<String, Integer> queryJavaAPIAppearanceInAnswer() throws SQLException {
        // 查询回答中出现的java api的次数, 返回一个map, key为java api的名字, value为该api出现的次数
        PreparedStatement statement = this.prepareStatement("select count(*) from answer;");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int nowCnt = 0, len = resultSet.getInt(1);
        statement = this.prepareStatement("select body from answer;");
        resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        while (resultSet.next()) {
            nowCnt ++;
            if (nowCnt % (len / 15) == 0) {
                String redColorCode = "\u001B[31m"; String resetColorCode = "\u001B[0m";
                System.out.println("已完成" + redColorCode + (nowCnt * 100 / len) + "%" + resetColorCode);
            }
            String body = resultSet.getString("body");
            Map<String, Integer> temp = stanfordCoreNLPService.getAllJavaAPI(body);
            for (String key : temp.keySet()) {
                if (result.containsKey(key)) {
                    result.put(key, result.get(key) + temp.get(key));
                } else {
                    result.put(key, temp.get(key));
                }
            }
        }
        return result;
    }
    public Map<String, Integer> queryJavaAPIAppearanceInComment() throws SQLException {
        // 查询评论中出现的java api的次数, 返回一个map, key为java api的名字, value为该api出现的次数
        PreparedStatement statement = this.prepareStatement("select count(*) from comment;");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int nowCnt = 0, len = resultSet.getInt(1);
        statement = this.prepareStatement("select body from comment;");
        resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        while (resultSet.next()) {
            nowCnt ++;
            if (nowCnt % (len / 5) == 0) {
                String redColorCode = "\u001B[31m"; String resetColorCode = "\u001B[0m";
                System.out.println("已完成" + redColorCode + (nowCnt * 100 / len) + "%" + resetColorCode);
            }
            String body = resultSet.getString("body");
            Map<String, Integer> temp = stanfordCoreNLPService.getAllJavaAPI(body);
            for (String key : temp.keySet()) {
                if (result.containsKey(key)) {
                    result.put(key, result.get(key) + temp.get(key));
                } else {
                    result.put(key, temp.get(key));
                }
            }
        }
        return result;
    }

    public Map<String, Integer> queryQuestionAnswerUserNumDistribution() throws SQLException {
        PreparedStatement statement = this.prepareStatement("select question_id, count(distinct account_id) as cnt from (select question.question_id, answer.account_id from question\n" +
                "left join answer\n" +
                "on answer.question_id = question.question_id) as foo\n" +
                "group by question_id;");
        ResultSet resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        while (resultSet.next()) {
            result.put(resultSet.getString("question_id"), resultSet.getInt("cnt"));
        }
        return result;
    }
    public Map<String, Integer> queryQuestionCommentUserNumDistribution() throws SQLException {
        PreparedStatement statement = this.prepareStatement("select question_id, count(distinct comment_id) as cnt from (select question_id, comment_id from (select question.question_id, answer.answer_id from question\n" +
                "left join answer\n" +
                "on answer.question_id = question.question_id) as foo\n" +
                "left join comment\n" +
                "on (foo.answer_id = comment.post_id)) as foo2\n" +
                "group by question_id;\n");
        ResultSet resultSet = statement.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        while (resultSet.next()) {
            result.put(resultSet.getString("question_id"), resultSet.getInt("cnt"));
        }
        return result;
    }
}
