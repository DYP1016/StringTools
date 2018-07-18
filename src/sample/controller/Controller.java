package sample.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import sample.Main;
import sample.util.PropertiesUtil;
import sample.function1.ExcelTool;
import sample.function1.Info;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button btRun;
    @FXML
    private Button btSelectSource;
    @FXML
    private Button btSelectXls;
    @FXML
    private Button btSelectIgnore;
    @FXML
    private Button btSelectRule;
    @FXML
    private TextArea taOut;
    @FXML
    private Button btRefresh;
    @FXML
    private ChoiceBox<String> cbType;

    private static final int TYPE_ANDROID = 0;
    private static final int TYPE_IOS = 1;
    private int type = TYPE_ANDROID;

    private String filePathSource;
    private String filePathXls;
    private String filePathIgnore;
    private String filePathRule;

    private StringBuilder sbOut = new StringBuilder();

    private List<Info> infoList;

    private PropertiesUtil propertiesUtil = new PropertiesUtil("toolsConfig.txt");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialize");


        initView();
        initListener();
        initData();
    }


    private void initView() {
        cbType.setItems(FXCollections.observableArrayList("Android", "IOS"));
        cbType.getSelectionModel().select(0);
    }

    private void initListener() {
        //日志打印
        ExcelTool.setLogCallBack(Controller.this::addMessage);

        cbType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println("changed: " + oldValue + " " + newValue);
                type = newValue.intValue();
            }
        });

        btSelectSource.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择源文件");
            File file = fileChooser.showOpenDialog(Main.getInstance().getPrimaryStage());
            if (file == null) {
                return;
            }
            btSelectSource.setText(file.getName());
            filePathSource = file.getAbsolutePath();
            propertiesUtil.setFilePathSource(filePathSource);
            System.out.println(filePathSource);
        });

        btSelectXls.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择表格文件");
            File file = fileChooser.showOpenDialog(Main.getInstance().getPrimaryStage());
            if (file == null) {
                return;
            }
            btSelectXls.setText(file.getName());
            filePathXls = file.getAbsolutePath();
            propertiesUtil.setFilePathXls(filePathXls);
            System.out.println(filePathXls);
        });

        btSelectIgnore.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择忽略文件");
            File file = fileChooser.showOpenDialog(Main.getInstance().getPrimaryStage());
            if (file == null) {
                return;
            }
            btSelectIgnore.setText(file.getName());
            filePathIgnore = file.getAbsolutePath();
            propertiesUtil.setFilePathIgnore(filePathIgnore);
            System.out.println(filePathIgnore);
        });

        btSelectRule.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择规则文件");
            File file = fileChooser.showOpenDialog(Main.getInstance().getPrimaryStage());
            if (file == null) {
                return;
            }
            btSelectRule.setText(file.getName());
            filePathRule = file.getAbsolutePath();
            propertiesUtil.setFilePathRule(filePathRule);
            System.out.println(filePathRule);
        });

        btRefresh.setOnAction(event -> clearMessage());

        btRun.setOnAction(event -> {
            System.out.println("event");
            new Thread(new Runnable() {
                @Override
                public void run() {

                }
            }).start();
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    try {
                        start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        addMessage(e.getMessage());
                    } finally {
                        Platform.runLater(() -> showMessage());
                    }
                    return null;
                }
            };
            new Thread(task).start();
        });
    }

    private void initData() {
        String sourcePath = propertiesUtil.getFilePathSource();
        String sourceName = getFileName(sourcePath);
        if (sourceName != null) {
            btSelectSource.setText(sourceName);
            filePathSource = sourcePath;
        }

        String xlsPath = propertiesUtil.getFilePathXls();
        String xlsName = getFileName(xlsPath);
        if (xlsName != null) {
            btSelectXls.setText(xlsName);
            filePathXls = xlsPath;
        }

        String ignorePath = propertiesUtil.getFilePathIgnore();
        String ignoreName = getFileName(ignorePath);
        if (ignoreName != null) {
            btSelectIgnore.setText(ignoreName);
            filePathIgnore = ignorePath;
        }

        String rulePath = propertiesUtil.getFilePathRule();
        String ruleName = getFileName(rulePath);
        if (ruleName != null) {
            btSelectRule.setText(ruleName);
            filePathRule = rulePath;
        }

    }

    private void start() throws Exception {
        List<String> ignoreList = null;
        List<String> source = null;

        //检测源文件是否存在
        if (!checkFileExist(filePathSource)) {
            addMessage("源文件不存在");
            return;
        }
        //检测替换Map是否存在
        if (!checkFileExist(filePathXls)) {
            addMessage("表格文件不存在");
            return;
        }
        //检测规则文件
        if (!checkFileExist(filePathRule)) {
            addMessage("规则文件不存在");
            return;
        }
        //检测忽略文件
        if (checkFileExist(filePathIgnore)) {
            addMessage("读取忽略文件");
            ignoreList = ExcelTool.getIgnoreListContent(filePathIgnore);
        } else {
            addMessage("忽略文件不存在");
        }


        addMessage("<------- 开始生成源文件对象 ---------->");
        source = ExcelTool.getSourceContent(filePathSource);

        addMessage("开始生成表格对象...");
        infoList = ExcelTool.getInfoContent(filePathXls);

        addMessage("开始设置规则文件...");
        ExcelTool.setRule(filePathRule);

        addMessage("开始生成文件");
        ExcelTool.generateFiles(filePathSource.substring(0, filePathSource.lastIndexOf("/")) + "/translate/", source, infoList, ignoreList);

    }

    private boolean checkFileExist(String path) {
        if (path == null || path.length() == 0) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    public String getFileName(String path) {
        File file = getFile(path);
        return file != null ? file.getName() : null;
    }

    private File getFile(String path) {
        if (path == null || path.length() == 0) {
            return null;
        }
        File file = new File(path);
        return file;
    }

    private void addMessage(String message) {
        sbOut.append(message).append("\n");
    }

    private void showMessage() {
        taOut.setText(sbOut.toString());
    }

    private void clearMessage() {
        sbOut.delete(0, sbOut.length());
        taOut.setText(sbOut.toString());
    }
}
