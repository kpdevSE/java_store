package controller;

import filesManager.BillIdFileManager;
import filesManager.BillItemFileManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tm.billItems.BillDetails;
import tm.billItems.BillItem;
import java.io.FileNotFoundException;
import java.util.List;

public class BillListController {

    public ListView<BillDetails> lstItemIds;
    public TableView<BillItem> tblItemList;
    public Label lblTotal;

    public void initialize() throws FileNotFoundException {
        loadList();
        tblItemList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BillItem>() {
            @Override
            public void changed(ObservableValue<? extends BillItem> observable, BillItem oldValue, BillItem newValue) {
                if (tblItemList.getSelectionModel().getSelectedItem()==null){
                    return;
                }
            }
        });

        lstItemIds.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BillDetails>() {
            @Override
            public void changed(ObservableValue<? extends BillDetails> observable, BillDetails oldValue, BillDetails newValue) {
                if (lstItemIds.getSelectionModel().getSelectedItems()==null){
                    return;
                }
                try {
                    loadTable();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        tblItemList.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemName"));
        tblItemList.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tblItemList.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("ratePrice"));
        tblItemList.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("price"));

    }

    public void loadList() throws FileNotFoundException {
        ObservableList<BillDetails> items = lstItemIds.getItems();
        items.clear();
        List<BillDetails> allBillIds = BillIdFileManager.getAllBillIds("billId.csv");
        for (BillDetails BillDetails :allBillIds){
            items.add(BillDetails);
        }
    }

    public void loadTable() throws FileNotFoundException {
        ObservableList<BillItem> items = tblItemList.getItems();
        items.clear();
        List<BillItem> allBillItems = BillItemFileManager.getAllBillItems("billItems.csv");
        double total=0;
        for (BillItem billItem:allBillItems){
            if (billItem.getBillId().equals(lstItemIds.getSelectionModel().getSelectedItem().getBillId())){
                items.add(billItem);
                total+=billItem.getPrice();
            }
        }
        lblTotal.setText(total+"");
    }
}
