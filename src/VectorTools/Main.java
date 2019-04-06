package VectorTools;

import VectorTools.android.AndroidFormatTools;
import VectorTools.svg.SvgFormatTools;
import javafx.application.Application;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    private TextArea inputArea;
    private TextArea outputArea;
    private RadioButton relativeRB;
    private RadioButton absoluteRB;
    private RadioButton convertRB;
    private RadioButton androidRB;
    private RadioButton svgRB;
    private WebView webViewInput;
    private File lastInput;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
        primaryStage.setTitle("SvgGui");
        Scene main = new Scene(root, 900, 400);
        primaryStage.setScene(main);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(900);
//        ((GridPane)root.lookup("#grid")).setPadding(new Insets(10));
        inputArea = (TextArea) root.lookup("#inputArea");
        outputArea = (TextArea) root.lookup("#outputArea");
        relativeRB = (RadioButton) root.lookup("#relative");
        absoluteRB = (RadioButton) root.lookup("#absolute");
        convertRB = (RadioButton) root.lookup("#convert");
        androidRB = (RadioButton) root.lookup("#android");
        svgRB = (RadioButton) root.lookup("#svg");
        ToggleGroup modeGroup = new ToggleGroup();
        relativeRB.setToggleGroup(modeGroup);
        absoluteRB.setToggleGroup(modeGroup);
        convertRB.setToggleGroup(modeGroup);
        ToggleGroup typeGroup = new ToggleGroup();
        androidRB.setToggleGroup(typeGroup);
        svgRB.setToggleGroup(typeGroup);

        ((Button) root.lookup("#input_info")).setOnAction(event -> {
            Dialog d = new Dialog();
            d.setTitle("Input image info");
            DialogPane pane = new DialogPane();

            double maxX = SvgFormatTools.getSizeInfo(inputArea.getText()).get(0);
            double maxY = SvgFormatTools.getSizeInfo(inputArea.getText()).get(1);
            double minX = SvgFormatTools.getSizeInfo(inputArea.getText()).get(2);
            double minY = SvgFormatTools.getSizeInfo(inputArea.getText()).get(3);

            Label max = new Label("Max:");
            Label min = new Label("Min:");
            Label maxHeight = new Label("Height: " + maxY);
            Label minHeight = new Label("Height: " + minY);
            Label maxWidth = new Label("Width: " + maxX);
            Label minWidth = new Label("Width: " + minX);
            maxHeight.setPadding(new Insets(0, 0, 0, 10));
            minHeight.setPadding(new Insets(0, 0, 0, 10));
            maxWidth.setPadding(new Insets(0, 0, 0, 10));
            minWidth.setPadding(new Insets(0, 0, 0, 10));
            HBox content = new HBox();
            VBox maxBox = new VBox();
            VBox minBox = new VBox();
            maxBox.getChildren().addAll(max, maxHeight, maxWidth);
            minBox.getChildren().addAll(min, minHeight, minWidth);
            content.getChildren().addAll(maxBox, minBox);
            content.setSpacing(50);
            pane.setContent(content);
            pane.getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
            d.setDialogPane(pane);
            d.show();
        });

        ((MenuBar) root.lookup("#menu")).getMenus().get(0).setOnAction(event -> {
            Utils.PrintUtils.writeln("click");
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Resize dialog");
            DialogPane pane = new DialogPane();
            Parent dialogRoot = null;
            try {
                pane.setContent(dialogRoot = FXMLLoader.load(getClass().getResource("ResizeDialog.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ButtonType resize = new ButtonType("Resize", ButtonBar.ButtonData.OK_DONE);
//            ButtonBar.
            Parent finalDialogRoot = dialogRoot;
            dialog.setResultConverter(param -> {
                if (param == resize) {

                    TextField height = (TextField) finalDialogRoot.lookup("#height");
                    TextField width = (TextField) finalDialogRoot.lookup("#width");
                    Utils.PrintUtils.writeln("hei - " + height.getText());
                    Utils.PrintUtils.writeln("wid - " + width.getText());
                    Utils.PrintUtils.writeln("res - " + SvgFormatTools.resize(inputArea.getText(), "pathData", Double.parseDouble(height.getText())));
                    outputArea.setText(SvgFormatTools.resize(inputArea.getText(), "pathData", Double.parseDouble(height.getText())));
                    return SvgFormatTools.resize(inputArea.getText(), "pathData", Double.parseDouble(height.getText())/100);
                }
                return null;
            });
            pane.getButtonTypes().addAll(new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE), resize);
//            pane.getButtonTypes().
            dialog.setDialogPane(pane);
            dialog.show();
            dialog.setOnCloseRequest(event1 -> {
                outputArea.setText(dialog.getResult());
                Utils.PrintUtils.writeln(dialog.getResult());
            });
        });

        webViewInput = (WebView) root.lookup("#webInput");
        WebView webViewOutput = (WebView) root.lookup("#webOutput");
        lastInput = new File("input.svg");
        File lastOutput = new File("output.svg");
        if (lastInput.exists())
            webViewInput.getEngine().load(lastInput.toURI().toString());
        if (lastOutput.exists())
            webViewOutput.getEngine().load(lastOutput.toURI().toString());

        androidRB.setOnAction(event -> {
            convertRB.setText("Convert to SVG");
        });
        svgRB.setOnAction(event -> {
            convertRB.setText("Convert to Android Vector");
        });

        ((Button) root.lookup("#out_copy")).setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            HashMap<DataFormat, Object> map = new HashMap<>();
            map.put(DataFormat.PLAIN_TEXT, outputArea.getText());
            clipboard.setContent(map);
        });

        ((Button) root.lookup("#in_paste")).setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if(clipboard.hasString())
                inputArea.setText(clipboard.getString());
        });

        modeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> modify());
        typeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> modify());

        outputArea.textProperty().addListener((observable, oldValue, newValue) -> {
            String output;
            if (androidRB.isSelected())
                if (convertRB.isSelected()) output = outputArea.getText();
                else output = AndroidFormatTools.convertToSVG(outputArea.getText());
            else if (convertRB.isSelected()) {
                output = AndroidFormatTools.convertToSVG(outputArea.getText());
            } else {
                output = outputArea.getText();
            }
            BufferedWriter outputWriter = null;
            try {
                outputWriter = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(lastOutput), StandardCharsets.UTF_8));
                outputWriter.write(output);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (outputWriter != null) {
                    try {
                        outputWriter.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            webViewOutput.getEngine().load(lastOutput.toURI().toString());
        });

        inputArea.textProperty().addListener((observable, oldValue, newValue) -> modify());
        primaryStage.show();
    }

    private void modify(){
        if (inputArea.getText().trim().length() == 0)
            return;
        BufferedWriter inputWriter = null;
        String input;
        if (androidRB.isSelected()){
            if (relativeRB.isSelected())
                outputArea.setText(AndroidFormatTools.makeRelative(inputArea.getText()));
            if (absoluteRB.isSelected())
                outputArea.setText(AndroidFormatTools.makeAbsolute(inputArea.getText()));
            if (convertRB.isSelected())
                outputArea.setText(AndroidFormatTools.convertToSVG(inputArea.getText()));
            input = AndroidFormatTools.convertToSVG(inputArea.getText());
        } else {
            if (relativeRB.isSelected())
                outputArea.setText(SvgFormatTools.makeRelative(inputArea.getText()));
            if (absoluteRB.isSelected())
                outputArea.setText(SvgFormatTools.makeAbsolute(inputArea.getText()));
            if (convertRB.isSelected())
                outputArea.setText(SvgFormatTools.convertToAndroidVector(inputArea.getText()));
            input = inputArea.getText();
        }
        try {
            inputWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(lastInput), StandardCharsets.UTF_8));
            inputWriter.write(input);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputWriter != null) {
                try {
                    inputWriter.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        for (String s : input.split(">")) {
            if (s.contains("<path")){
                Utils.PrintUtils.writeln(s);
                Utils.PrintUtils.writeln("Tags in line:");
                for (String tag : SvgFormatTools.getTags(s)) {
                    Utils.PrintUtils.writeln(tag);
                }
            }
        }
        webViewInput.getEngine().load(lastInput.toURI().toString());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
