package sample.util;

import java.io.*;
import java.util.Properties;

public class PropertiesUtil {
    private static final String KEY_FILE_PATH_SOURCE = "kfps";
    private static final String KEY_FILE_PATH_XLS = "kfpx";
    private static final String KEY_FILE_PATH_IGNORE = "kfpi";
    private static final String KEY_FILE_PATH_RULE = "kfpr";

    private String properiesName = "";
    private File file;

    public PropertiesUtil() {

    }

    public PropertiesUtil(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.file = file;
        this.properiesName = file.getName();
    }

    /**
     * 按key获取值
     *
     * @param key
     * @return
     */
    public String readProperty(String key) {
        String value = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file.getAbsolutePath());
            if (is != null){
                InputStreamReader reader = new InputStreamReader(is);
                Properties properties = new Properties();
                properties.load(reader);
                value = properties.getProperty(key);

                reader.close();
            }

        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * 获取整个配置信息
     *
     * @return
     */
    public Properties getProperties() {
        Properties p = new Properties();
        InputStream is = null;
        try {
            is = PropertiesUtil.class.getClassLoader().getResourceAsStream(properiesName);
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return p;
    }

    /**
     * key-value写入配置文件
     *
     * @param key
     * @param value
     */
    public void writeProperty(String key, String value) {
        InputStream is = null;
        OutputStream os = null;
        Properties p = new Properties();
        try {
            is = new FileInputStream(properiesName);
//            is = PropertiesUtil.class.getClassLoader().getResourceAsStream(properiesName);
            p.load(is);
//            os = new FileOutputStream(PropertiesUtil.class.getClassLoader().getResource(properiesName).getFile());
            os = new FileOutputStream(properiesName);

            p.setProperty(key, value);
            p.store(os, key);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is)
                    is.close();
                if (null != os)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setFilePathSource(String path) {
        writeProperty(KEY_FILE_PATH_SOURCE, path);
    }

    public String getFilePathSource() {
        return readProperty(KEY_FILE_PATH_SOURCE);
    }

    public void setFilePathXls(String path) {
        writeProperty(KEY_FILE_PATH_XLS, path);
    }

    public String getFilePathXls() {
        return readProperty(KEY_FILE_PATH_XLS);
    }

    public void setFilePathIgnore(String path) {
        writeProperty(KEY_FILE_PATH_IGNORE, path);
    }

    public String getFilePathIgnore() {
        return readProperty(KEY_FILE_PATH_IGNORE);
    }

    public void setFilePathRule(String path) {
        writeProperty(KEY_FILE_PATH_RULE, path);
    }

    public String getFilePathRule() {
        return readProperty(KEY_FILE_PATH_RULE);
    }
}
