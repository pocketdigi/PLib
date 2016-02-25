package com.pocketdigi.plib.validator;

import android.widget.EditText;

import com.pocketdigi.plib.core.PToast;

import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 输入验证
 * Created by fhp on 16/2/22.
 */
public class Validator {
    EditText inputView;
    //存储规则
    TreeMap<String, String> rules = new TreeMap<>();
    //失败提示
    String failureNotice = "";

    public Validator(EditText inputView) {
        this.inputView = inputView;
    }

    /**
     * 开始校验,如果失败，可以用getFailureNotice获取失败信息
     *
     * @param toast 是否弹出 Toast
     * @return
     */
    public boolean validate(boolean toast) {
        String inputString = inputView.getText().toString();
        Set<String> ruleKeys = rules.keySet();
        for (String key : ruleKeys) {
            Pattern pattern = Pattern.compile(key);
            Matcher matcher = pattern.matcher(inputString);
            if (!matcher.lookingAt()) {
                //没有通过正则
                failureNotice = rules.get(key);
                if (toast)
                    PToast.show(failureNotice);
                return false;
            }

        }
        return true;
    }

    /**
     * 校验 ，弹Toast
     *
     * @return
     */
    public boolean validate() {
        return validate(true);
    }

    /**
     * 添加规则
     *
     * @param rule   正则规则,参考 {@link #RULE_NOTEMPTY},{@link #RULE_EMAIL},{@link #RULE_PLATENUMBER}
     * @param notice 验证失败时，Toast提示
     */
    public void addRule(String rule, String notice) {
        rules.put(rule, notice);
    }

    /**
     * 获取校验失败的提示，当不
     *
     * @return
     */
    public String getFailureNotice() {
        return failureNotice;
    }

    /**
     * 非空
     */
    public static final String RULE_NOTEMPTY = "[\\S]+";
    /**
     * Email
     */
    public static final String RULE_EMAIL = "/^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\\.][a-z]{2,3}([\\.][a-z]{2})?$/i";
    /**
     * 车牌号码
     * 第一位为中文，后跟一个字母，5个非空字符(可能含中文)
     */
    public static final String RULE_PLATENUMBER = "^[\\u4e00-\\u9fa5]{1}[A-Z]{1}[\\S]{5}$";
    /**
     * 手机号码
     * 13x,14x,15x,17x,18x
     */
    public static final String RULE_MOBILEPHONE = "^1[3|4|5|7|8][\\d]{9}$";
    /**
     * 限制字符在5-8位之间
     */
    public static final String RULE_LENGTH_5_TO_8 = "[\\S]{5,8}";

}
