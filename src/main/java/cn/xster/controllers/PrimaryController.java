package cn.xster.controllers;

import cn.xster.kernel.fontutils.FontCreator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;

import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;

import javafx.geometry.Insets;
import javafx.scene.control.*;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * primary.fxml 的控制类
 *
 */
public class PrimaryController  {
  private static final Logger LOGGER = LoggerFactory.getLogger(PrimaryController.class);

  @FXML private VBox mainPane;
  @FXML private ListView<String> fontListView;
  @FXML private TextArea previewWindow;
  @FXML private Label fontsNum;
  @FXML private Spinner<Integer> fontSizeSpinner;
  @FXML private TextField charToExport, outputFileName;
  @FXML private VBox previewContainer;
  @FXML private TextField searchTextField;

  List<String> fontList = new ArrayList<>();
  Font[] allFonts;
  String textToDisplay = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\nabcdefghijklmnopqrstuvwxyz\n0123456789\n.~!@#$%^&*()_+{}|:\"<>?";
  JTextArea jTextArea = null;

  /**
   * 初始化方法，当前fxml控件完成加载后调用
   */
  @FXML
  public void initialize() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    allFonts = ge.getAllFonts();
    _setupListView();
    _setupFontSizeSpinner();
    _setupTextArea();
    _setupSearch();
  }

  private void _setupSearch() {
    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if(newValue == null) return;

      if(StringUtils.isBlank(newValue)) {
        fontListView.setItems(FXCollections.observableList(fontList));
        return;
      }

      List<String> filteredList = new ArrayList<>();
      for(String fontName: fontList) {
        if(fontName.toLowerCase().contains(newValue.toLowerCase())) {
          filteredList.add(fontName);
        }
      }

      fontListView.setItems(FXCollections.observableList(filteredList));
    });
  }

  public void _setupFontSizeSpinner() {
    SpinnerValueFactory<Integer> svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, 20, 1);
    fontSizeSpinner.setValueFactory(svf);

    fontSizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
      if(newValue == null || fontListView.getSelectionModel().getSelectedItem() == null) return;

      _updatePreviewWindow();
    });
  }

  private void _setupListView() {
    FontCreator fontCreator = new FontCreator();
    String[] fontArr = fontCreator.getFontList();
    fontList = Arrays.asList(fontArr);

    fontsNum.setText("(" + fontList.size() + ")");

    fontListView.setItems(FXCollections.observableList(fontList));

    fontListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if(newValue == null) return;

      _updatePreviewWindow();
    });
  }

  private void _setupTextArea() {
    SwingNode swingNode = new SwingNode();
    jTextArea = new JTextArea();
    jTextArea.setBounds(0,0,250, 500);
    jTextArea.setRows(100);

    jTextArea.setLineWrap(true);
    jTextArea.setWrapStyleWord(true);

    swingNode.setContent(jTextArea);
    previewContainer.getChildren().add(swingNode);
  }

  private void _updatePreviewWindow() {
    textToDisplay = StringUtils.isBlank(jTextArea.getText())
        ? "ABCDEFGHIJKLMNOPQRSTUVWXYZ\nabcdefghijklmnopqrstuvwxyz\n0123456789\n.~!@#$%^&*()_+{}|:\"<>?"
        : jTextArea.getText().trim();
    jTextArea.setText(textToDisplay);

    Font font = allFonts[fontListView.getSelectionModel().getSelectedIndex()];
    Font font1 = font.deriveFont(fontSizeSpinner.getValue().floatValue());

    jTextArea.setFont(font1);
    jTextArea.setText(textToDisplay);
  }

  @FXML
  public void handleExport() {
    // create a Directory chooser
    DirectoryChooser dir_chooser = new DirectoryChooser();
    Stage thisStage = (Stage) mainPane.getScene().getWindow();
    File file = dir_chooser.showDialog(thisStage);

    if (file != null) {
      FontCreator fontCreator = new FontCreator();
      int[] a = {};
      String outputPath;
      if(_isWindowsOS()) {
        outputPath = StringUtils.isBlank(outputFileName.getText()) ? file.getAbsolutePath() + "\\output" : file.getAbsolutePath() + "\\" + outputFileName.getText();
      } else {
        outputPath = StringUtils.isBlank(outputFileName.getText()) ? file.getAbsolutePath() + "/output" : file.getAbsolutePath() + "/" + outputFileName.getText();
      }

      fontCreator.getHFont(fontListView.getSelectionModel().getSelectedIndex(),
          fontSizeSpinner.getValue(),
          StringUtils.isBlank(charToExport.getText()) ? "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.~!@#$%^&*()_+{}|:\"<>?": charToExport.getText(),
          a,
          outputPath);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Export Done", ButtonType.OK);
        alert.showAndWait();
    }
  }

  private boolean _isWindowsOS() {
    String os = System.getProperty("os.name");
    return os.toLowerCase().contains("win");
  }
}

