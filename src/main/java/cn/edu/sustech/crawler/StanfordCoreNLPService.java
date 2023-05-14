package cn.edu.sustech.crawler;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class StanfordCoreNLPService {
    private Properties props;
    private StanfordCoreNLP pipeline;
    public StanfordCoreNLPService() {
        props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        props.setProperty("ner.useSUTime", "false");
        pipeline = new StanfordCoreNLP(props);
    }
    public Map<String, Integer> getAllJavaAPI(String htmlText) {
        Map<String, Integer> javaAPIs = new HashMap<>();
        Annotation annotation = new Annotation(htmlText);
        pipeline.annotate(annotation);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for (CoreLabel token : tokens) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // word 以 java. 开头
                if (word.startsWith("java.")) {
                    if (javaAPIs.containsKey(word)) {
                        javaAPIs.put(word, javaAPIs.get(word) + 1);
                    } else {
                        javaAPIs.put(word, 1);
                    }
                }
            }
        }
        return javaAPIs;
    }
}
