package sample.function1;

/**
 * Created by dyp on 2018/3/26.
 */
public class Utils {
    public static String getDirName(String type) {
        String ret = null;
        switch (type) {
            case "Česky"://捷克语
                ret = "values-cs";
                break;
            case "Dansk":   //丹麦语
                ret = "values-da";
                break;
            case "Deutsch": //德语
                ret = "values-de";
                break;
            case "Español": //西班牙语
                ret = "values-es";
                break;
            case "Finnish": //芬兰语
                ret = "values-fi";
                break;
            case "Français": //法语
                ret = "values-fr";
                break;
            case "Norweigian":   //挪威
                ret = "values-nb";
                break;
            case "Polski":  //波兰
                ret = "values-pl";
                break;
            case "Português": //葡萄牙
                ret = "values-pt";
                break;
            case "SWEDISH": //瑞典
                ret = "values-sv";
                break;
            case "Русский": //俄语
                ret = "values-ru";
                break;
            case "Swedish": //瑞典
                ret = "values-sv";
                break;
            case "Türkçe": //土耳其
                ret = "values–tr";
                break;
            case "Greek": //希娜语
                ret = "values-el";
                break;
            case "Nederlands": //荷兰
                ret = "values-nl";
                break;
            case "Arabic": //阿拉伯
                ret = "values-ar";
                break;
            case "Indonesia language ": //印尼语
                ret = "values-in";
                break;
            case "中文":
                ret = "values-zh";
                break;
            case "Thailand":
                ret = "Thailand";
                break;
        }
        return ret;
    }
}
