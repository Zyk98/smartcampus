package com.cumtb.mp.vo;

import com.cumtb.mp.entity.Question;
import com.cumtb.mp.entity.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class QuestionTagVO implements Serializable{

    private Question question;
    private ArrayList<Tag> tagList;

}
