package cn.edu.sustech;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class DataCollector {
    private int pageSize; // 分页获取数据时每页的大小 [1, 100]
    private int pageStep; // 分页获取数据时每次获取所隔的页数
    private List<JSONObject> questionList; // 获取问题JSON列表
    private List<JSONObject> answerList;  // 获取回答JSON列表
    private List<JSONObject> commentList; // 获取评论JSON列表
    private int QuestionTotal; // 当前StackOverflow上的问题总数（用来衡量数据爬取的普适性）
    private int NoAnsQuestionTotal; // 当前StackOverflow上的无回答问题总数（计算比例，用来衡量数据爬取的普适性）
    private DatabaseService databaseService; // 数据库服务

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    } // 设置每页大小
    public void setPageStep(int pageStep) {
        this.pageStep = pageStep;
    } // 设置每次获取所隔的页数
    public DataCollector (DatabaseService databaseService, int pageSize, int pageStep) throws IOException {
        // 自定义每页大小和每次获取所隔的页数
        this.databaseService = databaseService;
        questionList = new ArrayList<>();
        answerList = new ArrayList<>();
        commentList = new ArrayList<>();
        this.pageSize = pageSize;
        this.pageStep = pageStep;
        refresh();
    }
    public void refresh() throws IOException {
        // 刷新数据，主要更新问题总数和无回答问题总数
        String url = "https://api.stackexchange.com/2.3/questions";
        String params = "filter=total&tagged=java&site=stackoverflow&key=gqjiH6ExBbic7NaMoFxC)w((";
        String apiURL = url + "?" + params;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(apiURL);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String responseBody = EntityUtils.toString(entity);
            JSONObject data = JSON.parseObject(responseBody);
            QuestionTotal = data.getInteger("total");
        }
        response.close();
        httpClient.close();
        url = "https://api.stackexchange.com/2.3/questions/no-answers";
        params = "filter=total&tagged=java&site=stackoverflow&key=gqjiH6ExBbic7NaMoFxC)w((";
        apiURL = url + "?" + params;
        httpClient = HttpClients.createDefault();
        httpGet = new HttpGet(apiURL);
        response = httpClient.execute(httpGet);
        entity = response.getEntity();
        if (entity != null) {
            String responseBody = EntityUtils.toString(entity);
            JSONObject data = JSON.parseObject(responseBody);
            NoAnsQuestionTotal = data.getInteger("total");
        }
        response.close();
        httpClient.close();
    }
    public int getQuestionTotal() throws IOException {
        // 获取stackOverflow上准确的问题总数
        return QuestionTotal;
    }
    public int getNoAnsQuestionTotal() throws IOException {
        // 获取stackOverflow上准确的无回答问题总数
        return NoAnsQuestionTotal;
    }

    public double getNoAnsPercent() throws IOException {
        // 获取stackOverflow上准确的无回答问题比例
        QuestionTotal = getQuestionTotal();
        NoAnsQuestionTotal = getNoAnsQuestionTotal();
        return (double) NoAnsQuestionTotal / QuestionTotal;
    }
    public void collectData() throws IOException, SQLException {
        // 数据爬取
        int pageTotal = QuestionTotal / pageSize; // 设置总页数
        for (int i = 1; i <= pageTotal; i += pageStep) {
            // 按照activity排序，获取每隔pageStep页的pageSize个问题
            String redColorCode = "\u001B[31m";
            String resetColorCode = "\u001B[0m";
            System.out.println(redColorCode + "获取所有提问数据ing：" + (int)(100 * ((double)i / pageTotal)) + "%" + resetColorCode);
            String url = "https://api.stackexchange.com/2.3/questions";
            String params = String.format("page=%d&pagesize=%d&order=desc&sort=activity&tagged=java&filter=withbody&site=stackoverflow&key=gqjiH6ExBbic7NaMoFxC)w((", i, pageSize);
            String apiURL = url + "?" + params;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(apiURL);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String responseBody = EntityUtils.toString(entity);
                JSONObject data = JSON.parseObject(responseBody);
                if (data.getJSONArray("items") == null) continue;
                for (int j = 0; j < pageSize; j++) {
                    JSONObject item = data.getJSONArray("items").getJSONObject(j);
                    questionList.add(item);
                }
            }
            response.close();
            httpClient.close();
        }
        // 获取每个问题的回答
        List<Integer> nowQuestionIdList = new ArrayList<>();
        List<Integer> AnswerIDList = new ArrayList<>();
        for (int i = 0; i < questionList.size(); i++) {
            if (i % 100 == 0 && i > 0) {
                String redColorCode = "\u001B[31m";
                String resetColorCode = "\u001B[0m";
                System.out.println(redColorCode + "获取所有回答数据ing：" + (int)(100 * ((double)i / questionList.size())) + "%" + resetColorCode);
                String ids = "";
                for (int j = 0; j < nowQuestionIdList.size(); j++) {
                    ids += nowQuestionIdList.get(j);
                    if (j != nowQuestionIdList.size() - 1) {
                        ids += ";";
                    }
                }
                List<JSONObject> jsonObjectList = getAnswers(ids);
                for (JSONObject jsonObject: jsonObjectList) {
                    JSONArray answers = jsonObject.getJSONArray("items");
                    for (int j = 0; j < answers.size(); j++) {
                        JSONObject item = answers.getJSONObject(j);
                        if (item != null) {
                            answerList.add(item);
                            AnswerIDList.add(item.getInteger("answer_id"));
                        }
                    }
                }
                nowQuestionIdList.clear();
            }
            JSONObject item = questionList.get(i);
            int questionId = item.getInteger("question_id");
            nowQuestionIdList.add(questionId);
        }
        if (nowQuestionIdList.size() > 0) {
            String ids = "";
            for (int j = 0; j < nowQuestionIdList.size(); j++) {
                ids += nowQuestionIdList.get(j);
                if (j != nowQuestionIdList.size() - 1) {
                    ids += ";";
                }
            }
            List<JSONObject> jsonObjectList = getAnswers(ids);
            for (JSONObject jsonObject: jsonObjectList) {
                JSONArray answers = jsonObject.getJSONArray("items");
                for (int j = 0; j < answers.size(); j++) {
                    JSONObject item = answers.getJSONObject(j);
                    if (item != null) {
                        answerList.add(item);
                        AnswerIDList.add(item.getInteger("answer_id"));
                    }
                }
            }
            nowQuestionIdList.clear();
        }
        // 获取每个回答的评论
        List<Integer> nowAnswerIdList = new ArrayList<>();
        for (int i = 0; i < AnswerIDList.size(); i++) {
            if (i % 100 == 0 && i > 0) {
                String redColorCode = "\u001B[31m";
                String resetColorCode = "\u001B[0m";
                System.out.println(redColorCode + "获取所有评论数据ing：" + (int)(100 * ((double)i / AnswerIDList.size())) + "%" + resetColorCode);
                String ids = "";
                for (int j = 0; j < nowAnswerIdList.size(); j++) {
                    ids += nowAnswerIdList.get(j);
                    if (j != nowAnswerIdList.size() - 1) {
                        ids += ";";
                    }
                }
                List<JSONObject> jsonObjectList = getComments(ids);
                for (JSONObject jsonObject: jsonObjectList) {
                    JSONArray answers = jsonObject.getJSONArray("items");
                    for (int j = 0; j < answers.size(); j++) {
                        JSONObject item = answers.getJSONObject(j);
                        if (item != null) commentList.add(item);
                    }
                }
                nowAnswerIdList.clear();
            }
            JSONObject item = answerList.get(i);
            int answerId = item.getInteger("answer_id");
            nowAnswerIdList.add(answerId);
        }
        if (nowAnswerIdList.size() > 0) {
            String ids = "";
            for (int j = 0; j < nowAnswerIdList.size(); j++) {
                ids += nowAnswerIdList.get(j);
                if (j != nowAnswerIdList.size() - 1) {
                    ids += ";";
                }
            }
            List<JSONObject> jsonObjectList = getComments(ids);
            for (JSONObject jsonObject: jsonObjectList) {
                JSONArray answers = jsonObject.getJSONArray("items");
                for (int j = 0; j < answers.size(); j++) {
                    JSONObject item = answers.getJSONObject(j);
                    if (item != null) commentList.add(item);
                }
            }
            nowAnswerIdList.clear();
        }
        System.out.println("爬取Question总数：" + questionList.size());
        System.out.println("爬取Answer总数：" + answerList.size());
        System.out.println("爬取Comment总数：" + commentList.size());
        // 将数据插入数据库
        String redColorCode = "\u001B[31m";
        String resetColorCode = "\u001B[0m";
        System.out.println(redColorCode + "数据插入数据库" + resetColorCode);
        for (int i = 0; i < questionList.size(); i++) {
            JSONObject questionItem = questionList.get(i);
            databaseService.insertQuestionRecord(questionItem);
        }
        System.out.println(redColorCode + "Question已经插入数据库!" + resetColorCode);
        for (int i = 0; i < answerList.size(); i++) {
            JSONObject answerItem = answerList.get(i);
            databaseService.insertAnswerRecord(answerItem);
        }
        System.out.println(redColorCode + "Answer已经插入数据库!" + resetColorCode);
        for (int i = 0; i < commentList.size(); i++) {
            JSONObject commentItem = commentList.get(i);
            databaseService.insertCommentRecord(commentItem);
        }
        System.out.println(redColorCode + "Comment已经插入数据库!" + resetColorCode);
        System.out.println(redColorCode + "数据插入数据库完成！" + resetColorCode);
    }
    public List<JSONObject> getComments(String ids) {
        // 获取每个回答的评论，ids为answer_id的字符串，以分号分隔
        List<JSONObject> jsonObjectList = new ArrayList<>();
        int page = 1;
        while (true) {
            String url = "https://api.stackexchange.com/2.3/answers/" + ids + "/comments";
            String params = String.format("page=%d&pagesize=%d&filter=withbody&order=desc&sort=creation&site=stackoverflow&key=gqjiH6ExBbic7NaMoFxC)w((", page, pageSize);
            String apiURL = url + "?" + params;
            System.out.println(apiURL);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(apiURL);
            try {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    JSONObject data = JSON.parseObject(responseBody);
                    jsonObjectList.add(data);
                    if (!data.getBoolean("has_more")) break; // 获取所有评论数据
                }
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            page++;
        }
        return jsonObjectList;
    }
    public List<JSONObject> getAnswers(String ids) {
        // 获取每个问题的回答，ids为question_id的字符串，以分号分隔
        List<JSONObject> jsonObjectList = new ArrayList<>();
        int page = 1;
        while (true) {
            String url = "https://api.stackexchange.com/2.3/questions/" + ids + "/answers";
            String params = String.format("page=%d&pagesize=%d&filter=withbody&order=desc&sort=activity&site=stackoverflow&key=gqjiH6ExBbic7NaMoFxC)w((", page, pageSize);
            String apiURL = url + "?" + params;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(apiURL);
            try {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    JSONObject data = JSON.parseObject(responseBody);
                    jsonObjectList.add(data);
                    if (!data.getBoolean("has_more")) break; // 获取所有回答数据
                }
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            page++;
        }
        return jsonObjectList;
    }

}
