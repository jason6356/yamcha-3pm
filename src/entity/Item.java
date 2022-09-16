package entity;

import adt.ListI;
import client.BackupServer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Item implements Cloneable{

    private String id;
    private String name;
    private double price;
    private int quantity;
    private ItemType itemType;
    private static int itemCount = 0;
    private final static String prefix = "IT";

    public Item(String id){
        this(id,"",0.0,ItemType.FOOD,0);
    }

    public Item(String id, String name, double price, ItemType itemType,int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.itemType = itemType;
        this.quantity = quantity;
        itemCount = splitNumberFromID(id);
    }

    public Item(String name, double price, int quantity) {
        this.id = genItemID();
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        itemCount++;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public static int getItemCount() {
        return itemCount;
    }

    public static void setItemCount(int itemCount) {
        Item.itemCount = itemCount;
    }

    public static String getPrefix() {
        return prefix;
    }

    private String genItemID(){
        return prefix + itemCount;
    }

    private int splitNumberFromID(String id){
        return Integer.parseInt(id.substring(id.length()-1)) + 1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id.equals(((Item) obj).getId());
    }

    @Override
    public Object clone() {
        Object obj = null;
        try{
            obj = super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return obj;
    }

    public String toFileFormat(){
        //IT16#Milo King Kong#5.0#BEVERAGE#0
        return String.format("%s#%s#%.2f#%s#%d\n",id,name,price,itemType.toString(),quantity);
    }

    public String toReportFormat(){
        //%-30s %-20s %-10s %-4s
        //name price quantity price * quantity
        return String.format("%-30s RM%-16.2f %-10d RM%-2.2f\n", name,price,quantity,price * quantity);
    }

    public String toRankingFormat(){
        return String.format("%38s %30s %d\n","", name, quantity);
    }

    @Override
    public String toString() {
        return String.format("%38s Item Name  : %s\n", "", name) +
                String.format("%38s Item Type : %s\n", "", itemType.toString())+
                String.format("%38s Item Price : %.2f\n", "", price) +
                String.format("%38s Quantity   : %d\n", "", quantity);
    }

    public static void readFile(ListI<Item> foodMenu, ListI<Item> beverageMenu){

        String filename = "src/txt/item.txt";

        File f = new File(filename);
        if(f.exists()) {

            try(Stream<String> stream = Files.lines(Paths.get(filename))){

                stream.forEach(e -> {

                    String[] split = e.split("#");
                    String id = split[0];
                    String name = split[1];
                    double price = Double.parseDouble(split[2]);
                    ItemType type = split[3].equals("FOOD") ? ItemType.FOOD : ItemType.BEVERAGE;
                    int quantity = Integer.parseInt(split[4]);

                    Item item = new Item(id,name,price,type,quantity);
                    if(item.getItemType() == ItemType.FOOD)
                        foodMenu.add(item);
                    else
                        beverageMenu.add(item);
                });

            }catch(IOException e){
                e.printStackTrace();
            }

        }
        else {
            String content = null;
            try {
                content = BackupServer.GET_REQUEST_FOOD_TEXT();
                Files.write(Paths.get(filename), content.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            readFile(foodMenu, beverageMenu);
        }
    }

    public static void writeFile(ListI<Item> foodMenu, ListI<Item> beverageMenu){

        Path path = Paths.get("src/txt/item.txt");
        String content = Stream.concat(foodMenu.stream(),beverageMenu.stream())
                .map(Item::toFileFormat)
                .reduce("", String::concat);

        try {
            Files.write(path,content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
