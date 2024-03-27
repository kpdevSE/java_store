package filesManager;

import com.opencsv.CSVWriter;
import tm.item.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ItemFileManager {
    public static List<item> getAllItems(String fileName) throws FileNotFoundException {
        List<item> items =new ArrayList<>();
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()){
            String line=scanner.nextLine();
            String[] parts=line.split(",");
            String name=parts[0];
            String manufacture=parts[1];
            String price=parts[2];
            String quantity=parts[3];
            String category=parts[4];
            item item =null;
            if (category.equals("Iphone")){
                item =new Iphone(name,manufacture,Double.parseDouble(price),Integer.parseInt(quantity),"Iphone");
            }else if (category.equals("Watch")){
                item =new Watch(name,manufacture,Double.parseDouble(price),Integer.parseInt(quantity),"Watch");
            }else if (category.equals("IPad")){
                item =new IPad(name,manufacture,Double.parseDouble(price),Integer.parseInt(quantity),"IPad");
            }else if (category.equals("AirPods")){
                item =new AirPods(name,manufacture,Double.parseDouble(price),Integer.parseInt(quantity),"AirPods");
            }else if (category.equals("Mac")){
                item =new Mac(name,manufacture,Double.parseDouble(price),Integer.parseInt(quantity),"Mac");
            }
            if (item !=null){
                items.add(item);
            }
        }
        scanner.close();
        return items;
    }

    public static void addBook(item item, String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName, true);
        CSVWriter csvWriter=new CSVWriter(fileWriter,',',CSVWriter.NO_QUOTE_CHARACTER,CSVWriter.DEFAULT_ESCAPE_CHARACTER,CSVWriter.DEFAULT_LINE_END);
        csvWriter.writeNext(new String[]{item.getName(), item.getManufacture(),""+ item.getPrice(),""+ item.getQuantity(), item.getCategory()});
        csvWriter.close();
    }

    public static void addAllBooks(List<item> items, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file);
        CSVWriter csvWriter = new CSVWriter(fileWriter,',',CSVWriter.NO_QUOTE_CHARACTER,CSVWriter.DEFAULT_ESCAPE_CHARACTER,CSVWriter.DEFAULT_LINE_END);
        List<String[]> list=new ArrayList<>();
        for (item item : items){
            list.add(new String[]{item.getName(), item.getManufacture(), item.getPrice()+"", item.getQuantity()+"", item.getCategory()});
        }
        csvWriter.writeAll(list);
        csvWriter.close();
    }
}
