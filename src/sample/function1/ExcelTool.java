package sample.function1;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dyp on 2018/3/15.
 */
public class ExcelTool {
    private static LogCallBack logCallBack;

    private static Map<String, String> languageMap = new HashMap<>();

    public static List<Info> getInfoContent(String path) throws Exception {
        List<Info> infoList = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            log("文件 " + path + " 不存在");
            return infoList;
        }

        InputStream inputStream = new FileInputStream(file.getAbsoluteFile());

        WorkbookSettings workbookSettings = new WorkbookSettings();
        workbookSettings.setEncoding("ISO-8859-1");
//        workbookSettings.setEncoding("GBK");
        Workbook workbook = Workbook.getWorkbook(inputStream, workbookSettings);

        Sheet sheet = workbook.getSheet(0);
        //行列
        log("rows = " + sheet.getRows() + " columns = " + sheet.getColumns());
        int rows = sheet.getRows(); //行
        int columns = sheet.getColumns(); //列
        Cell[] keyCell = sheet.getColumn(0);
        for (int column = 1; column < columns; column++) {
            System.out.println("生成第" + column + "个");
            Info info = new Info();
            info.setTitle(sheet.getCell(column, 0).getContents());
            if (sheet.getCell(column, 0).getContents() != null && sheet.getCell(column, 0).getContents().length() > 0) {
                for (int row = 0; row < rows; row++) {
                    if (!sheet.getCell(column, row).isHidden() && sheet.getCell(column, row).getContents().length() > 0) {
                        log(sheet.getCell(column, row).getContents());
                        info.add(keyCell[row].getContents(), sheet.getCell(column, row).getContents());
                    }
                }
                infoList.add(info);
                log("第" + column + "个生成完成, size = " + info.getSize());
                log("\n==========================================================================\n");
            } else {
                System.out.println("第" + column + "列无效，跳过");
            }
        }

        return infoList;
    }

    public static List<Info> getInfoContentFromTxt(String path) throws Exception {
        List<Info> infoList = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            log("文件 " + path + " 不存在");
            return infoList;
        }

        Info info = new Info();
        Reader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String tempStr;
        tempStr = bufferedReader.readLine();
        info.setTitle(tempStr.split("\t")[1]);
        while ((tempStr = bufferedReader.readLine()) != null) {
            log(tempStr);
            String[] ret = tempStr.split("\t");
            switch (ret.length) {
                case 1:

                    break;
                case 2:
                    info.add(ret[0], ret[1]);
                    break;
                default:
                    log("格式异常: " + tempStr);
                    break;
            }
        }

        infoList.add(info);
        return infoList;
    }

    public static List<String> getSourceContent(String sourcePath) throws Exception {
        File file = new File(sourcePath);
        if (!file.exists()) {
            log("文件 " + sourcePath + " 不存在");
            return null;
        }
        File translateFiles = new File(file.getParent() + "/translate");
        if (translateFiles.exists()) {
            translateFiles.delete();
        }
        translateFiles.mkdirs();

        //读取源文件并且缓存起来
        InputStream inputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        Reader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String tempStr = null;
        List<String> stringBuffer = new ArrayList<>();

        while ((tempStr = bufferedReader.readLine()) != null) {
            stringBuffer.add(tempStr);
        }

        return stringBuffer;
    }

    public static void generateFiles(String path, List<String> sourceFile, List<Info> infoList, List<String> ignoreList) throws Exception {
        int generateFiles = 0;
        for (Info info : infoList) {
            if (generateFile(path, info, sourceFile, ignoreList)) {
                generateFiles++;
            }
        }
        log("一共生成" + generateFiles + "个文件");
    }

    public static void generateFiles(String path, List<String> sourceFile, List<Info> infoList) throws Exception {
        generateFiles(path, sourceFile, infoList, null);
    }

    public static List<String> getIgnoreListContent(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            log("文件 " + path + " 不存在");
            return null;
        }

        //读取源文件并且缓存起来
        InputStream inputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        Reader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String tempStr = null;
        List<String> stringBuffer = new ArrayList<>();
        while ((tempStr = bufferedReader.readLine()) != null) {
            stringBuffer.add(tempStr);
        }
        log("ignore list size : " + stringBuffer.size());
        return stringBuffer;
    }

    public static void generateTextFile(String path, List<String> info) throws Exception {
        File file = new File(path + "/" + "string.txt");
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        Writer writer = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        for (String s : info) {
            bufferedWriter.write(s + "\n");
        }

        bufferedWriter.flush();
        bufferedWriter.close();
        writer.close();
        log("文件" + file.getAbsolutePath() + "写入完成");
    }

    private static boolean generateIosFile(String sourcePath, Info info, List<String> source, List<String> ignoreList) throws Exception {
        //创建文件
//        File file = new File(sourcePath);
        //创建文件夹
        if (languageMap.get(info.getTitle()) == null) {
            log("未知语言类型: " + info.getTitle() + ", 跳过");
            return false;
        }
        File targetFiles = new File(sourcePath + "/" + languageMap.get(info.getTitle()));
        if (targetFiles.exists()) {
            targetFiles.delete();
        }
        targetFiles.mkdirs();

        File targetFile = new File(targetFiles.getPath() + "/Localizable.strings");
        if (targetFile.exists()) {
            targetFile.delete();
        }
        boolean ret = targetFile.createNewFile();
        log("创建文件" + targetFile.getAbsolutePath() + "结果: " + ret);

        //创建输出流
        Writer writer = new FileWriter(targetFile);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        int unreplayCount = 0;
        int ignoreCount = 0;
        for (String s : source) {
            if (s.contains("\"") && !s.contains("?xml")) {
                //要替换的部分
                String tempKey = s.substring(s.indexOf("=") + 3, s.lastIndexOf("\""));
                //判断该key是否被忽略
                if (ignoreList != null && ignoreList.contains(tempKey)) {
                    ignoreCount++;
                    bufferedWriter.write(s + "\n");
                    continue;
                }
                //获取value
                String value = info.get(tempKey);
                if (value != null && value.length() > 0) {
                    bufferedWriter.write(s.replace("\"" + tempKey + "\"", "\"" + value + "\"") + "\n");
                } else {
                    unreplayCount++;
                    log(unreplayCount + " 未找到key : " + tempKey + " 所对应的value");
//                    bufferedWriter.write(s.replace(tempKey, "未替换") + "\n");
                    bufferedWriter.write(s + "\n");
                }
            } else {
                //不需要替换的部分
                bufferedWriter.write(s + "\n");
            }
        }
        log("未替换数: " + unreplayCount);
        log("忽略数: " + ignoreCount);
        bufferedWriter.flush();
        bufferedWriter.close();
        writer.close();
        log(targetFile.getName() + "写入完成");
        log("\n==========================================================================\n");
        return true;

    }

    private static boolean generateFile(String sourcePath, Info info, List<String> source, List<String> ignoreList) throws Exception {
        //创建文件
//        File file = new File(sourcePath);
        //创建文件夹
        if (languageMap.get(info.getTitle()) == null) {
            log("未知语言类型: " + info.getTitle() + ", 跳过");
            return false;
        }
        File targetFiles = new File(sourcePath + "/" + languageMap.get(info.getTitle()));
        if (targetFiles.exists()) {
            targetFiles.delete();
        }
        targetFiles.mkdirs();

        File targetFile = new File(targetFiles.getPath() + "/strings.xml");
        if (targetFile.exists()) {
            targetFile.delete();
        }
        boolean ret = targetFile.createNewFile();
        log("创建文件" + targetFile.getAbsolutePath() + "结果: " + ret);

        //创建输出流
        Writer writer = new FileWriter(targetFile);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        int unreplayCount = 0;
        int ignoreCount = 0;
        for (String s : source) {
            if (s.contains("\"") && !s.contains("?xml")) {
                //要替换的部分 Android
                String tempKey = s.substring(s.indexOf(">") + 1, s.lastIndexOf("<"));
//                log(tempKey);
                //判断该key是否被忽略
                if (ignoreList != null && ignoreList.contains(tempKey)) {
                    ignoreCount++;
                    bufferedWriter.write(s + "\n");
                    continue;
                }
                //获取value
                String value = info.get(tempKey);
                if (value != null && value.length() > 0) {
                    bufferedWriter.write(s.replace(">" + tempKey + "<", ">" + value + "<") + "\n");
                } else {
                    unreplayCount++;
                    log(unreplayCount + " 未找到key : " + tempKey + " 所对应的value");
//                    bufferedWriter.write(s.replace(tempKey, "未替换") + "\n");
                    bufferedWriter.write(s + "\n");
                }
            } else {
                //不需要替换的部分
                bufferedWriter.write(s + "\n");
            }
        }
        log("未替换数: " + unreplayCount);
        log("忽略数: " + ignoreCount);
        bufferedWriter.flush();
        bufferedWriter.close();
        writer.close();
        log(targetFile.getName() + "写入完成");
        log("\n==========================================================================\n");
        return true;
    }

    //读取解析规则文件
    public static void setRule(String path) throws IOException {
        List<String> strings = getFileContent(path);
        //解析语言翻译规则
        Map<String, String> languageRule = new HashMap<>();
        for (int i = strings.indexOf("language set start"); i < strings.indexOf("language set end"); i++) {
            String info = strings.get(i);
            System.out.println(info);
            //忽略 //开头和空输入的内容
            if (info.length() == 0 || info.indexOf("//") == 0) {
                continue;
            }
            String[] ret = info.split(" : ");
            if (ret.length == 2) {
                languageRule.put(ret[0], ret[1]);
            }
        }
        languageMap = languageRule;
        log("解析文件名规则完成");
    }

    private static List<String> getFileContent(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            log("文件 " + path + " 不存在");
            return null;
        }

        //读取源文件并且缓存起来
        InputStream inputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        Reader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String tempStr = null;
        List<String> stringBuffer = new ArrayList<>();
        while ((tempStr = bufferedReader.readLine()) != null) {
            stringBuffer.add(tempStr);
        }
        return stringBuffer;
    }

    private static void log(String info) {
        System.out.println(info);
        if (logCallBack != null) {
            logCallBack.log(info);
        }
    }

    public static void setLogCallBack(LogCallBack logCallBack) {
        ExcelTool.logCallBack = logCallBack;
    }

    public interface LogCallBack {
        void log(String info);
    }
}
