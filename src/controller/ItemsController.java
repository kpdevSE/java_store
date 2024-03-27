package controller;

import filesManager.ItemFileManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import tm.item.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ItemsController {
    public TableView<item> tblList;
    public Pane root1;
    public TextField txtAddItemName;
    public TextField txtAddQuantity;
    public TextField txtAddManufacture;
    public Pane root2;
    public Label lblName;
    public TextField txtQuantity;
    public TextField txtPrice;
    public Label lblManufacture;
    public ChoiceBox<String> chcCategory;
    public ChoiceBox<String> chcAddCategory;
    public TextField txtAddPrice;
    public ChoiceBox<String> chcCategoryFilter;
    public TextField txtSearchItem;
    public Button btnAddItem;

    public void initialize() throws FileNotFoundException {
        root1.setDisable(true);
        root2.setDisable(true);
        chcAddCategory.getItems().addAll("AirPods","IPad","Iphone","Mac","Watch");
        chcCategory.getItems().addAll("AirPods","IPad","Iphone","Mac","Watch");

        tblList.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblList.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("manufacture"));
        tblList.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("price"));
        tblList.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tblList.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("category"));

        chcAddCategory.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (chcAddCategory.getSelectionModel().getSelectedItem()==null){
                    return;
                }
            }
        });
        loadTable();
        tblList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<item>() {
            @Override
            public void changed(ObservableValue<? extends item> observable, item oldValue, item newValue) {
                if(tblList.getSelectionModel().getSelectedItem()==null){
                    return;
                }else {
                    item selectedItem = tblList.getSelectionModel().getSelectedItem();
                    lblName.setText(selectedItem.getName());
                    lblManufacture.setText(selectedItem.getManufacture());
                    txtQuantity.clear();
                    txtQuantity.setText(selectedItem.getQuantity()+"");
                    txtPrice.clear();
                    txtPrice.setText(selectedItem.getPrice()+"");
                    chcCategory.setValue(selectedItem.getCategory());
                    root2.setDisable(false);
                    root1.setDisable(true);
                    txtAddItemName.clear();
                    txtAddManufacture.clear();
                    txtAddPrice.clear();
                    txtAddQuantity.clear();
                    chcAddCategory.getSelectionModel().clearSelection();
                    btnAddItem.setDisable(false);
                }

            }
        });
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

    public void btnAddBookOnAction(ActionEvent actionEvent) throws FileNotFoundException {
        root1.setDisable(false);
        root2.setDisable(true);
        btnAddItem.setDisable(true);
    }

    public void btnOkOnAction(ActionEvent actionEvent) throws IOException {
        addBook();
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) throws IOException {
        List<item> allItems = ItemFileManager.getAllItems("items.csv");
        item selectedItem = tblList.getSelectionModel().getSelectedItem();
        for (item item : allItems){
            if (selectedItem.getCategory().equals(item.getCategory())&&selectedItem.getName().equals(item.getName())&&selectedItem.getManufacture().equals(item.getManufacture())&&(selectedItem.getQuantity()== item.getQuantity())&&(selectedItem.getPrice()== item.getPrice())){
                item.setQuantity(Integer.parseInt(txtQuantity.getText()));
                item.setPrice(Double.parseDouble(txtPrice.getText()));
                item.setCategory(chcCategory.getSelectionModel().getSelectedItem());
            }
        }
        ItemFileManager.addAllBooks(allItems, "items.csv");
        loadTable();
        root2.setDisable(true);
        chcCategoryFilter.getSelectionModel().clearSelection();
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete Item?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get().equals(ButtonType.YES)){
            List<item> allItems = ItemFileManager.getAllItems("items.csv");
            item selectedItem = tblList.getSelectionModel().getSelectedItem();
            for (item item : allItems){
                if (selectedItem.getCategory().equals(item.getCategory())&&selectedItem.getName().equals(item.getName())&&selectedItem.getManufacture().equals(item.getManufacture())&&(selectedItem.getQuantity()== item.getQuantity())&&(selectedItem.getPrice()== item.getPrice())){
                    item.setQuantity(0);
                }
            }
            ItemFileManager.addAllBooks(allItems, "items.csv");
            tblList.getSelectionModel().clearSelection();
            loadTable();
            root2.setDisable(true);
            chcCategoryFilter.getSelectionModel().clearSelection();
        }
    }

    private void categoryFilterLoadTable() throws FileNotFoundException {
        String selectedItem = chcCategoryFilter.getSelectionModel().getSelectedItem();
        List<item> allItems = ItemFileManager.getAllItems("items.csv");
        ObservableList<item> items = tblList.getItems();
        items.clear();
        for (item item : allItems){
            String category = item.getCategory();
            if (selectedItem.equals("All")){
                loadTable();
            }else if (category.equals(selectedItem)){
                items.add(item);
            }
        }
        tblList.refresh();
    }

    public void txtSearchBookOnKeyReleased(KeyEvent keyEvent) throws FileNotFoundException {
        List<item> allItems = ItemFileManager.getAllItems("items.csv");
        ObservableList<item> items = tblList.getItems();
        items.clear();
        String searchBook = txtSearchItem.getText();
        for (item item : allItems){
            if (item.getName().contains(searchBook)&& item.getQuantity()!=0){
                items.add(item);
            }
        }
    }

    public void addBook() throws IOException {
        if (txtAddItemName.getText().isEmpty()){
            txtAddItemName.requestFocus();
        }else if (txtAddManufacture.getText().isEmpty()){
            txtAddManufacture.requestFocus();
        }else if (txtAddPrice.getText().isEmpty()){
            txtAddPrice.requestFocus();
        }else if (txtAddQuantity.getText().isEmpty()){
            txtAddQuantity.requestFocus();
        } else if (chcAddCategory.getSelectionModel().getSelectedItem().isEmpty()) {
            chcAddCategory.show();
        }else {
            Double price=Double.parseDouble(txtAddPrice.getText());
            int quantity=Integer.parseInt(txtAddQuantity.getText());
            String author = txtAddManufacture.getText();
            String name = txtAddItemName.getText();
            String selectedItem = chcAddCategory.getSelectionModel().getSelectedItem();
            item item =null;
            if(selectedItem.equals("Iphone")){
                item =new Iphone(name,author,price,quantity,"Iphone");
            }else if(selectedItem.equals("Watch")){
                item =new Watch(name,author,price,quantity,"Watch");
            }else if(selectedItem.equals("IPad")){
                item =new IPad(name,author,price,quantity,"IPad");
            }else if(selectedItem.equals("AirPods")){
                item =new AirPods(name,author,price,quantity,"AirPods");
            }else if(selectedItem.equals("Mac")){
                item =new Mac(name,author,price,quantity,"Mac");
            }
            if(item !=null){
                ItemFileManager.addBook(item, "items.csv");
            }
            loadTable();
        }
    }

    void loadTable() throws FileNotFoundException {
        btnAddItem.setDisable(false);
        lblManufacture.setText("");
        lblName.setText("");
        txtQuantity.clear();
        txtPrice.clear();
        chcCategory.getSelectionModel().clearSelection();
        txtAddItemName.clear();
        txtAddManufacture.clear();
        txtAddPrice.clear();
        txtAddQuantity.clear();
        chcAddCategory.getSelectionModel().clearSelection();
        root1.setDisable(true);
        root2.setDisable(true);
        ObservableList<item> items = tblList.getItems();
        items.clear();
        List<item> allItems = ItemFileManager.getAllItems("items.csv");
        for (item item : allItems){
            if (item.getQuantity()!=0){
                items.add(item);
            }
        }
        tblList.refresh();
    }

    public void txtSearchOnMouseClicked(MouseEvent mouseEvent) throws FileNotFoundException {
        if (txtSearchItem.getText().isEmpty()){
            chcCategoryFilter.getSelectionModel().clearSelection();
            loadTable();
        }
    }

    public void txtAddBookNameOnAction(ActionEvent actionEvent) {
        txtAddManufacture.requestFocus();
    }

    public void txtAddQuantityOnAction(ActionEvent actionEvent) {
        chcAddCategory.show();
    }

    public void txtAddPriceOnAction(ActionEvent actionEvent) {
        txtAddQuantity.requestFocus();
    }

    public void txtAddAuthorNameOnAction(ActionEvent actionEvent) {
        txtAddPrice.requestFocus();
    }
}
