package controller;

import filesManager.ItemFileManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import tm.item.item;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class DeletedItemsController {
    public TableView<item> tblList;
    public Pane root1;
    public TextField txtQuantity;
    public Label lblItemName;
    public Label lblManufacture;
    public ChoiceBox<String> chcCategoryFilter;
    public TextField txtSearchItem;
    public Label lblPrice;
    public Label lblCategory;
    public Label lblPleaseUpdateQuantity;

    public void initialize() throws FileNotFoundException {
        root1.setDisable(true);
        lblPleaseUpdateQuantity.setVisible(false);
        tblList.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblList.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("manufacture"));
        tblList.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("price"));
        tblList.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("category"));
        tblList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<item>() {
            @Override
            public void changed(ObservableValue<? extends item> observable, item oldValue, item newValue) {
                if(tblList.getSelectionModel().getSelectedItem()==null){
                    return;
                }else {
                    root1.setDisable(false);
                    item selectedItem = tblList.getSelectionModel().getSelectedItem();
                    lblItemName.setText(selectedItem.getName());
                    lblManufacture.setText(selectedItem.getManufacture());
                    txtQuantity.clear();
                    txtQuantity.setText(selectedItem.getQuantity()+"");
                    lblPrice.setText(selectedItem.getPrice()+"");
                    lblCategory.setText(selectedItem.getCategory());
                }

            }
        });
        loadTable();

        chcCategoryFilter.getItems().addAll("All","AirPods","IPad","Iphone","Mac","Watch");
        chcCategoryFilter.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (chcCategoryFilter.getSelectionModel().getSelectedItem()==null){
                    return;
                }
                try {
                    categoryFilterLoadTable();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) throws IOException {
        if (txtQuantity.getText().equals("0")){
            lblPleaseUpdateQuantity.setVisible(true);
        }else {
            lblPleaseUpdateQuantity.setVisible(false);
            List<item> allItems = ItemFileManager.getAllItems("items.csv");
            item selectedItem = tblList.getSelectionModel().getSelectedItem();
            for (item item : allItems){
                if (selectedItem.getCategory().equals(item.getCategory())&&selectedItem.getName().equals(item.getName())&&selectedItem.getManufacture().equals(item.getManufacture())&&(selectedItem.getQuantity()== item.getQuantity())&&(selectedItem.getPrice()== item.getPrice())){
                    item.setQuantity(Integer.parseInt(txtQuantity.getText()));
                }
            }
            ItemFileManager.addAllBooks(allItems, "items.csv");
            loadTable();
        }
    }

    public void txtSearchItemOnKeyReleased(KeyEvent keyEvent) throws FileNotFoundException {
        tblList.getItems().clear();
        List<item> items = ItemFileManager.getAllItems("items.csv");
        for (item item :items){
            if (item.getName().contains(txtSearchItem.getText())&& item.getQuantity()==0){
                tblList.getItems().add(item);
            }
        }
    }

    void loadTable() throws FileNotFoundException {
        root1.setDisable(true);
        lblPrice.setText("");
        lblCategory.setText("");
        lblItemName.setText("");
        lblManufacture.setText("");
        txtQuantity.clear();
        ObservableList<item> items = tblList.getItems();
        items.clear();
        List<item> allItems = ItemFileManager.getAllItems("items.csv");
        for (item item : allItems){
            if (item.getQuantity()==0){
                items.add(item);
            }
        }
        tblList.refresh();
    }

    public void categoryFilterLoadTable() throws FileNotFoundException {
        String selectedItem = chcCategoryFilter.getSelectionModel().getSelectedItem();
        List<item> allItems = ItemFileManager.getAllItems("items.csv");
        ObservableList<item> items = tblList.getItems();
        items.clear();
        for (item item : allItems){
            String category = item.getCategory();
            if (selectedItem.equals("All")){
                loadTable();
            }else if (category.equals(selectedItem)&& item.getQuantity()==0){
                items.add(item);
            }
        }
        tblList.refresh();
    }

}
