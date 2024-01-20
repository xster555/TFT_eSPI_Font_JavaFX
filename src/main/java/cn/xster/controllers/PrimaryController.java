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

  List<String> fontList = new ArrayList<>();
  Font[] allFonts;


  /**
   * 初始化方法，当前fxml控件完成加载后调用
   */
  @FXML
  public void initialize() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    allFonts = ge.getAllFonts();
    _setupListView();
    _setupFontSizeSpinner();
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

  private void _updatePreviewWindow() {
    previewContainer.getChildren().clear();

    SwingNode swingNode = new SwingNode();
    JTextArea jTextArea = new JTextArea("\n  ABCDEFGHIJKLMNOPQRSTUVWXYZ\n\n  abcdefghijklmnopqrstuvwxyz\n\n  0123456789");

    Font font = allFonts[fontListView.getSelectionModel().getSelectedIndex()];
    Font font1 = font.deriveFont(fontSizeSpinner.getValue().floatValue());

    jTextArea.setFont(font1);

    swingNode.setContent(jTextArea);
    previewContainer.getChildren().add(swingNode);
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
      fontCreator.getHFont(fontListView.getSelectionModel().getSelectedIndex(),
          fontSizeSpinner.getValue(),
          StringUtils.isBlank(charToExport.getText()) ? "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789": charToExport.getText(),
          a,
          StringUtils.isBlank(outputFileName.getText()) ? file.getAbsolutePath() + "\\output" : file.getAbsolutePath() + "\\" + outputFileName.getText());
    }
  }
}

