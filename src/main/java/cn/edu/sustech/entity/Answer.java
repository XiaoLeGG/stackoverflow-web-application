package cn.edu.sustech.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@TableName("answer")
public class Answer {

	@TableId("answer_id")
	private int answerId;
	
	@TableField("last_activity_date")
	private Date lastActivityDate;
	
	@TableField("last_edit_date")
	private Date lastEditDate;
	
	@TableField("creation_date")
	private Date creationDate;
	
	@TableField("score")
	private int score;
	
	@TableField("is_accepted")
	private boolean isAccepted;
	
	@TableField("content_license")
	private String contentLicense;
	
	@TableField("question_id")
	private int questionId;
	
	@TableField("body")
	private String body;
	
	@TableField("account_id")
	private int accountId;
	
	
}
