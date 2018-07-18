package sample.function1;

import java.util.HashMap;
import java.util.Map;

/**
 * 对应一种语言
 * Created by dyp on 2018/3/15.
 */
public class Info {
    private String title;
    private Map<String, String> infoMap = new HashMap<>();

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void add(String key, String value) {
        key = dealwithString(key);
        value = dealwithString(value);

        infoMap.put(key, value);
    }

    public String get(String key) {
        return infoMap.get(key);
    }

    public int getSize() {
        return infoMap.size();
    }

    public void showInfo() {
        System.out.println(title + " 长度为: " + getSize() + " 包含信息为: ");
        for (String key : infoMap.keySet()) {
            System.out.println(key + " : " + infoMap.get(key));
        }
    }

    private String dealwithString(String value) {
        value = value.replace("'", "\\'");
        value = value.replace("&", "&amp;");
        value = value.replace("\"", "\\\"");
        value = value.replace("\n", "");
        return value;
    }
}
