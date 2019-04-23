package app;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ItemController {
    @FXML
    private Text text;

    @FXML
    private Text sub_text;

    public void setText(String text){
        this.text.setText(text);
    }

    public void setSub_text(String sub_text){
        this.sub_text.setText(sub_text);
    }
}
