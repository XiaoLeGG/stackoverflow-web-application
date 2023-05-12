package cn.edu.sustech;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;
import java.util.Properties;

public class StanfordCoreNLPExample {
    public static void main(String[] args) {
        // 创建Properties对象，并设置需要的注解
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        props.setProperty("ner.useSUTime", "false");
        // 创建StanfordCoreNLP对象
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // 要处理的HTML文本
        String htmlText = "java.add and java.add is my favorite API.";
        // 创建Annotation对象，并设置要处理的文本
        Annotation annotation = new Annotation(htmlText);

        // 使用Stanford CoreNLP进行注解处理
        pipeline.annotate(annotation);

        // 获取所有句子
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        // 遍历每个句子，并提取Java API
        for (CoreMap sentence : sentences) {
            // 获取句子的标记（tokens）
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            // 遍历每个标记，提取包含"java."的标记作为Java API
            for (CoreLabel token : tokens) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                if (word.contains("java.")) {
                    System.out.println("Java API: " + word);
                }
            }
        }
    }
}
