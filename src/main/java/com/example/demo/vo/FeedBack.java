package com.example.demo.vo;

import com.example.demo.constants.FeedbackType;
import com.google.common.base.Enums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.ser.EnumMapSerializer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedBack {
    private Long userId;
    private String type;
    private String passtemplateId;
    private String comment;
    public boolean validate(){
        FeedbackType feedbackType = Enums.getIfPresent(FeedbackType.class,this.type.toUpperCase()).orNull();
        return !(null==feedbackType||null==comment);
    }
}
