package controller;

import filesManager.BillIdFileManager;
import filesManager.BillItemFileManager;
import filesManager.ItemFileManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import payment.Payment;
import payment.PaymentAlert;
import tm.billItems.BillDetails;
import tm.billItems.BillItem;
import tm.item.item;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SaleController {

    public TableView<item> tblList;
    public ChoiceBox<String> chcCategoryFilter;
    public TextField txtSearchItem;
    public Spinner<Integer> spnQuantity;
    public Label lblItemName;
    public Button btnDeleteFromBill;
    public Button btnUpDateQuantity;
    public Label lblBillId;
    public Label lblTotal;
    public TableView<BillItem> tblBillList;
    public Label lblPrice;
    public Button btnPayment;
    public AnchorPane root;
    public Label lblItemBalance;
    public Label lblItemTotalPrice;
    public TextField txtCashPrice;
    public ChoiceBox<String> chcPaymentOption;
    public Pane root2;
    public Pane root1;
    public Pane cashRoot;
    public Button btnAddToBill;
    public Label lblTotalRs;
    public Button btnCancelBill;


    boolean paymentSuccessfully=false;

    public void initialize() throws IOException {
        btnCancelBill.setDisable(true);
        btnPayment.setDisable(true);
        btnDeleteFromBill.setDisable(true);
        btnUpDateQuantity.setDisable(true);
        cashRoot.setVisible(false);
        lblItemTotalPrice.setDisable(true);
        tblBillList.setDisable(true);
        List<item> allItems = ItemFileManager.getAllItems("items.csv");
        ItemFileManager.addAllBooks(allItems, "tem.csv");
        loadTable();
        lblTotalRs.setVisible(false);
        root1.setDisable(true);
        root2.setDisable(true);
        root2.setVisible(false);
        lblBillId.setText(autoGenerateId());
        tblList.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblList.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("manufacture"));
        tblList.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("price"));
        tblList.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tblList.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("category"));

        tblBillList.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("bookName"));
        tblBillList.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tblBillList.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("ratePrice"));
        tblBillList.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("price"));

        tblList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<item>() {
            @Override
            public void changed(ObservableValue<? extends item> observable, item oldValue, item newValue) {
                if(tblList.getSelectionModel().getSelectedItem()==null){
                    return;
                }
                btnAddToBill.setDisable(false);
                btnDeleteFromBill.setDisable(true);
                btnUpDateQuantity.setDisable(true);
                root1IsHide(false);
                SpinnerValueFactory<Integer> spinnerValueFactory=new SpinnerValueFactory.IntegerSpinnerValueFactory(0,tblList.getSelectionModel().getSelectedItem().getQuantity());
                spinnerValueFactory.setValue(1);
                spnQuantity.setValueFactory(spinnerValueFactory);
                lblItemName.setText(tblList.getSelectionModel().getSelectedItem().getName());
                lblPrice.setText(tblList.getSelectionModel().getSelectedItem().getPrice()+"");
                spnQuantity.setDisable(false);
            }
        });

        tblBillList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BillItem>() {
            @Override
            public void changed(ObservableValue<? extends BillItem> observable, BillItem oldValue, BillItem newValue) {
                if(tblBillList.getSelectionModel().getSelectedItem()==null){
                    return;
                }
                btnAddToBill.setDisable(true);
                btnDeleteFromBill.setDisable(false);
                btnUpDateQuantity.setDisable(false);
                int quantity = 0;
                try {
                    List<item> allItems = ItemFileManager.getAllItems("tem.csv");
                    for (item item : allItems){
                        if (tblBillList.getSelectionModel().getSelectedItem().getItemName().equals(item.getName())){
                            quantity= item.getQuantity()+tblBillList.getSelectionModel().getSelectedItem().getQuantity();
                        }
                    }
                    SpinnerValueFactory<Integer> spinnerValueFactory=new SpinnerValueFactory.IntegerSpinnerValueFactory(0,quantity);
                    spinnerValueFactory.setValue(tblBillList.getSelectionModel().getSelectedItem().getQuantity());
                    spnQuantity.setValueFactory(spinnerValueFactory);
                    lblItemName.setText(tblBillList.getSelectionModel().getSelectedItem().getItemName());
                    lblPrice.setText(tblBillList.getSelectionModel().getSelectedItem().getPrice()+"");
                    spnQuantity.setDisable(false);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                
            }
        });
        chcCategoryFilter.getItems().addAll("All","AirPods","IPad","Iphone","Mac","Watch");
        chcCategoryFilter.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(chcCategoryFilter.getSelectionModel().getSelectedItem()==null){
                    return;
                }else {
                    try {
                        categoryFilterLoadTable();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        chcPaymentOption.getItems().addAll("Online Payment","Cash Payment");
        chcPaymentOption.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (chcPaymentOption.getSelectionModel().getSelectedItem()==null){
                    return;
                }
                else if (chcPaymentOption.getSelectionModel().getSelectedItem().equals("Online Payment")){
                    lblItemTotalPrice.setDisable(false);
                    cashRoot.setVisible(false);
                }else if (chcPaymentOption.getSelectionModel().getSelectedItem().equals("Cash Payment")){
                    lblItemTotalPrice.setDisable(false);
                    cashRoot.setVisible(true);
                }
            }
        });
    }

    private void loadTable() throws FileNotFoundException {
        clearDetails();
        tblList.getItems().clear();
        List<item> allItems = ItemFileManager.getAllItems("tem.csv");
        for (item item : allItems){
            if (item.getQuantity()!=0){
                tblList.getItems().addAll(item);
            }
        }
    }

    public void categoryFilterLoadTable() throws FileNotFoundException {
        String selectedItem = chcCategoryFilter.getSelectionModel().getSelectedItem();
        List<item> allItems = ItemFileManager.getAllItems("tem.csv");
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

    public void txtSearchItemOnKeyReleased(KeyEvent keyEvent) throws FileNotFoundException {
        List<item> allItems = ItemFileManager.getAllItems("tem.csv");
        ObservableList<item> items = tblList.getItems();
        items.clear();
        String searchBook = txtSearchItem.getText();
        for (item item : allItems){
            if (item.getName().contains(searchBook)&& item.getQuantity()!=0){
                items.add(item);
            }
        }
    }

    public void btnAddToBillOnAction(ActionEvent actionEvent) throws IOException {
        btnAddToBill.setDisable(true);
        if (tblList.getSelectionModel().getSelectedItem()==null){
            return;
        }else {
            ObservableList<BillItem> items = tblBillList.getItems();
            boolean alReadyAdded=false;
            for (BillItem book:items){
                if (book.getItemName().equals(lblItemName.getText())){
                    alReadyAdded=true;
                }
            }
            if (!alReadyAdded){
                btnCancelBill.setDisable(false);
                btnPayment.setDisable(false);
                item selectedItem = tblList.getSelectionModel().getSelectedItem();
                BillItem billItem = new BillItem(lblBillId.getText(), selectedItem.getName(), spnQuantity.getValue(), selectedItem.getPrice(), selectedItem.getPrice() *  spnQuantity.getValue());
                tblBillList.getItems().add(billItem);
                totalPrice();
                tblBillList.setDisable(false);
                lblTotalRs.setVisible(true);
                List<item> allItems = ItemFileManager.getAllItems("tem.csv");
                for (item item : allItems){
                    if (selectedItem.getName().equals(item.getName())){
                        item.setQuantity(item.getQuantity()-spnQuantity.getValue());
                    }
                }
                ItemFileManager.addAllBooks(allItems,"tem.csv");
                loadTable();
            }else {
                Alert alert=new Alert(Alert.AlertType.INFORMATION,"Already Added to Bill list",ButtonType.OK);
                alert.showAndWait();
            }
            clearDetails();
        }
    }

    public void btnDeleteFromBillOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete Item from bill?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get().equals(ButtonType.YES)){
            ObservableList<BillItem> items = tblBillList.getItems();
            List<BillItem> tmList=new ArrayList<>();
            BillItem selectedItem = tblBillList.getSelectionModel().getSelectedItem();
            for (BillItem billItem:items){
                if (!selectedItem.equals(billItem)){
                    tmList.add(billItem);
                }
            }
            List<item> allItems = ItemFileManager.getAllItems("tem.csv");
            for (item item : allItems){
                if (item.getName().equals(selectedItem.getItemName())){
                    item.setQuantity(item.getQuantity()+selectedItem.getQuantity());
                }
            }
            ItemFileManager.addAllBooks(allItems,"tem.csv");
            loadTable();
            loadBillTable(tmList);
        }
    }

    public void btnUpDateQuantityOnAction(ActionEvent actionEvent) throws IOException {
        List<item> allItems = ItemFileManager.getAllItems("tem.csv");
        BillItem selectedItem = tblBillList.getSelectionModel().getSelectedItem();
        for (item item : allItems){
            if (selectedItem.getItemName().equals(item.getName())){
                item.setQuantity(item.getQuantity()+selectedItem.getQuantity()-spnQuantity.getValue());
            }
        }
        ItemFileManager.addAllBooks(allItems,"tem.csv");
        ObservableList<BillItem> items = tblBillList.getItems();
        List<BillItem> listBill=new ArrayList<>();
        for (BillItem billItem:items){
            if (billItem.getItemName().equals(selectedItem.getItemName())){
                billItem.setQuantity(spnQuantity.getValue());
            }
            listBill.add(billItem);
        }
        loadBillTable(listBill);
        loadTable();
        clearDetails();
    }

    public void totalPrice(){
        ObservableList<BillItem> items = tblBillList.getItems();
        double total=0;
        for(BillItem book:items){
            total+=book.getPrice();
        }
        lblTotal.setText(""+total);
    }

    public String autoGenerateId() throws FileNotFoundException {
        String id;
        List<BillDetails> allBillDetails = BillIdFileManager.getAllBillIds("billId.csv");
        if (!allBillDetails.isEmpty()){
            BillDetails BillDetails = allBillDetails.get(allBillDetails.size() - 1);
            String billId = BillDetails.getBillId();
            String substring = billId.substring(1, billId.length());
            int intId = Integer.parseInt(substring);
            intId++;
            if(intId<10){
                id="B00"+intId;
            } else if (intId<100) {
                id="B0"+intId;
            } else{
                id="B"+intId;
            }
        }else {
            id="B001";
        }
        return id;
    }

    public void btnPaymentOnAction(ActionEvent actionEvent) throws IOException {
        root1.setDisable(true);
        root1.setVisible(false);
        root2.setDisable(false);
        root2.setVisible(true);
        lblItemTotalPrice.setText(lblTotal.getText());
    }

    public void loadBillTable(List<BillItem> billItems){
        clearDetails();
        tblBillList.getItems().clear();
        for (BillItem billItem:billItems){
            tblBillList.getItems().add(billItem);
        }
        tblBillList.refresh();
    }

    private void clearDetails(){
        lblItemName.setText("");
        lblPrice.setText("");
        SpinnerValueFactory<Integer> i=new SpinnerValueFactory.IntegerSpinnerValueFactory(0,0);
        spnQuantity.setValueFactory(i);
    }

    public void btnOkOnAction(ActionEvent actionEvent) throws IOException {
        if (chcPaymentOption.getSelectionModel().getSelectedItem().equals("Cash Payment")&&txtCashPrice.getText().isEmpty()){
            txtCashPrice.setStyle("-fx-border-color:red;");
            txtCashPrice.requestFocus();
        }else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You Want to pay?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get().equals(ButtonType.YES)){
                Payment payment=new PaymentAlert();
                if (chcPaymentOption.getSelectionModel().getSelectedItem().equals("Online Payment")){
                    payment.onlinePayment();
                    paymentSuccessfully=true;
                    writeData();
                }else if (chcPaymentOption.getSelectionModel().getSelectedItem().equals("Cash Payment")){
                    if (Double.parseDouble(lblTotal.getText())<=Double.parseDouble(txtCashPrice.getText())){
                        payment.cashPayment();
                        paymentSuccessfully=true;
                        writeData();
                    }else {
                        txtCashPrice.setStyle("-fx-border-color:red;");
                    }
                }
                if (paymentSuccessfully){
                    BillIdFileManager.addBillId("billId.csv",new BillDetails(lblBillId.getText(),chcPaymentOption.getSelectionModel().getSelectedItem()));
                    ObservableList<BillItem> items = tblBillList.getItems();
                    BillItemFileManager.addBillItem("billItems.csv",items);
                    tblBillList.getItems().clear();
                    lblBillId.setText(autoGenerateId());
                }
            }else {
                clearPayment();
            }
        }
    }

    public void txtCashOnKeyReleased(KeyEvent keyEvent) {
        double cash = Double.parseDouble(txtCashPrice.getText());
        double total = Double.parseDouble(lblItemTotalPrice.getText());
        lblItemBalance.setText((cash-total)+"");
    }

    public void clearPayment() throws IOException {
        lblTotal.setText("");
        clearDetails();
        root1IsHide(false);
        List<item> allItems = ItemFileManager.getAllItems("items.csv");
        ItemFileManager.addAllBooks(allItems, "tem.csv");
        loadTable();
    }

    public void btnCancelBillOnAction(ActionEvent actionEvent) throws IOException {
        clearPayment();
        tblBillList.getItems().clear();
        tblBillList.refresh();
        btnCancelBill.setDisable(true);
        btnPayment.setDisable(true);
    }

    private void writeData() throws IOException {
        List<item> allItems = ItemFileManager.getAllItems("items.csv");
        ObservableList<item> items = tblList.getItems();
        for (item item : allItems){
            for (item tblItem :items){
                if (item.getName().equals(tblItem.getName())){
                    item.setQuantity(tblItem.getQuantity());
                }
            }
        }
        ItemFileManager.addAllBooks(allItems, "items.csv");
        loadTable();
        clearPayment();
        lblBillId.setText(autoGenerateId());
    }

    public void root1IsHide(boolean a){
        root2.setDisable(!a);
        root2.setVisible(a);
        root1.setDisable(a);
        root1.setVisible(!a);
    }
}