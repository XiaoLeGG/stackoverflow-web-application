package cn.edu.sustech;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        DataResponse dataResponse = new DataResponse(
                "localhost", 5432, "postgres",
                "ljcfyh_123@99", "postgres",
                100, 1000, false
        );
        System.out.println("最新更新时间为：" + dataResponse.getLastUpdate());
        System.out.println("---Number of Answers---");
        System.out.println("没有答案的 questions 的百分比：" + dataResponse.getNoAnswerQuestionPercent());
        System.out.println("answer 数量的平均值：" + dataResponse.getAnswerNumAvg());
        System.out.println("answer 数量的最大值" + dataResponse.getAnswerNumMax());
        System.out.println("answer 数量的分布" + dataResponse.getAnswerNumDistribution());

        System.out.println("---Accepted Answers---");
        System.out.println("有 accepted answer 的问题的百分比：" + dataResponse.getAcceptedAnswerPercent());
        System.out.println("问题从提出到解决 (answer accepted time – question post time) 的时间间隔分布：" + dataResponse.getQuestionTimeDiffDistribution());
        System.out.println("含有 non-accepted answer 的 upvote 数高于 accepted answer 的问题的百分比：" + dataResponse.getNonAcceptedAnswerPercent());

        System.out.println("---Tags---");
        System.out.println("哪些 tags 经常和 Java tag 一起出现：" + dataResponse.getTagDistribution());
        System.out.println("哪些 tag groups 经常和 Java tag 一起出现：" + dataResponse.getTagGroupDistribution());
        System.out.println("哪些 tags得到最多的 upvotes：" + dataResponse.getTagScoreDistribution());
        System.out.println("哪些 tag groups 得到最多的 upvotes：" + dataResponse.getTagGroupScoreDistribution());
        System.out.println("哪些 tags得到最多的 views：" + dataResponse.getTagViewDistribution());
        System.out.println("哪些 tag groups 得到最多的 views：" + dataResponse.getTagGroupViewDistribution());

        System.out.println("---Users---");
        System.out.println("参与 Thread 讨论的用户数量的分布（提问）：" + dataResponse.getAccountQuestionTimes());
        System.out.println("参与 Thread 讨论的用户数量的分布（回答）：" + dataResponse.getAccountAnswerTimes());
        System.out.println("参与 Thread 讨论的用户数量的分布（评论）：" + dataResponse.getAccountCommentTimes());
        System.out.println("参与每个 Thread 讨论的用户数量的分布（1 + 提问不同用户数 + 评论不同用户数）：" + dataResponse.getQuestionDiscussUserNumDistribution());
        System.out.println("参与每个 Thread 讨论的回答用户数量的分布：" + dataResponse.getQuestionAnswerUserNumDistribution());
        System.out.println("参与每个 Thread 讨论的评论用户数量的分布：" + dataResponse.getQuestionCommentUserNumDistribution());
        System.out.println("参与 Thread 讨论的用户数量的分布（提问：回答：评论 = 1：1：1）获取活跃度：" + dataResponse.getAccountActivity(1, 1, 1));
        System.out.println("哪些用户参与 java thread 讨论最活跃：" + dataResponse.getActivateAccountID(1, 1, 1));

        System.out.println("---Frequently discussed Java APIs ---");
        System.out.println("哪些 Java APIs 在 Stack Overflow 上最频繁被讨论：" + dataResponse.getActivateJavaAPI(1, 1, 1));
    }
}
