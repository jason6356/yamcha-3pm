package entity;

import adt.ArrayList;
import adt.ListI;
import adt.QueueI;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Order {

    private String id;
    private ListI<Item> orderedItems;
    private double totalAmt;
    private OrderStatus status;
    private final static String PREFIX = "OD";
    private final static double SST = 0.06;
    private final static double SERVICECHARGE = 0.1;

    private static int orderCount = 1;

    public Order(String id, ListI<Item> orderedItems, double totalAmt, OrderStatus status) {
        this.id = id;
        this.orderedItems = orderedItems;
        this.totalAmt = totalAmt;
        this.status = status;
        orderCount = splitNumberFromID(id);
    }

    public Order() {
        this.id = genOrderID();
        this.orderedItems = new ArrayList<>();
        this.totalAmt = 0.0;
        this.status = OrderStatus.PENDING;
        orderCount++;
    }

    public Order(String id) {
        this(id,new ArrayList<>(),0.0,OrderStatus.PENDING);
    }

    public static String getPREFIX() {
        return PREFIX;
    }

    public static double getSST() {
        return SST;
    }

    public static double getSERVICECHARGE() {
        return SERVICECHARGE;
    }

    private int splitNumberFromID(String id){
        return Integer.parseInt(id.substring(id.length()-1)) + 1;
    }

    public static int getOrderCount() {
        return orderCount;
    }

    public static void setOrderCount(int orderCount) {
        Order.orderCount = orderCount;
    }

    private String genOrderID(){
        return PREFIX + orderCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ListI<Item> getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(ListI<Item> orderedItems) {
        this.orderedItems = orderedItems;
    }

    public double getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(double totalAmt) {
        this.totalAmt = totalAmt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void addItemFromOrder(Item entry){
        orderedItems.add(entry);
    }

    public void removeItemFromOrder(Item entry){

        OptionalInt indexOpt = IntStream.range(0, orderedItems.getNumberOfEntries())
                        .filter(i -> entry.equals(orderedItems.getEntry(i)))
                        .findFirst();

        if(indexOpt.isPresent()) {
            orderedItems.remove(indexOpt.getAsInt());
        }
        else
            System.out.println("No Such Element Found!");
    }

    private String lsToString(){
        StringBuilder b = new StringBuilder();
        orderedItems.forEach(b::append);
        return b.toString();
    }

    public double calculateTotalAmount(){
        return orderedItems.stream()
                .map(e -> e.getQuantity() * e.getPrice())
                .reduce(0.0,Double::sum);
    }

    public double calculateSST(){
        return calculateTotalAmount() * SST;
    }

    public double calculateServiceCharge(){
        return calculateTotalAmount() * SERVICECHARGE;
    }

    public double calculateSubTotal(){
        return calculateTotalAmount() + calculateSST() + calculateServiceCharge();
    }

    @Override
    public String toString() {
        return String.format("%38s Order ID - %s\n", "", id) +
                String.format("%38s Ordered Items \n", "") +
                String.format("%s", lsToString()) +
                String.format("%38s Total        - RM%.2f\n", "", calculateTotalAmount()) +
                String.format("%38s SST. TAX 6%%  - RM%.2f\n", "", calculateSST()) +
                String.format("%38s SERV TAX 10%% - RM%.2f\n", "", calculateServiceCharge()) +
                String.format("%38s SUB TOTAL    - RM%.2f\n", "", calculateSubTotal());
    }

    public String orderedItemsToFileFormat(){
        return orderedItems.stream()
                .map(e -> e.getId() + "~" + e.getQuantity() + "&")
                .reduce("",String::concat);
    }

    public String toFileFormat(){
        String orderedItemString = orderedItemsToFileFormat();
        int len = orderedItemString.length();
        //ID#ORDERED_ITEMS#STATUS
        return String.format("%s#%s#%s\n",id,orderedItemString.substring(0,len-1),status.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return id != null ? id.equals(order.id) : order.id == null;
    }

    public static void writeFile(QueueI<Order> orderQueue, QueueI<Order> servedQueue){

        Path path = Paths.get("src/txt/order.txt");

        String content = "";
        for (Object order : orderQueue)
            content += ((Order) order).toFileFormat();

        for (Object order :
                servedQueue) {
            content += ((Order) order).toFileFormat();
        }

        try {
            Files.write(path,content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readFile(QueueI<Order> orderQueue, QueueI<Order> servedQueue, ListI<Item> foodMenu, ListI<Item> beverageMenu){
        Path path = Paths.get("src/txt/order.txt");
        try {
            Stream<String> lines = Files.lines(path);
            lines.map(e -> e.split("#"))
                    .forEach(data -> {
                String id = data[0];
                String orderedString = data[1];
                OrderStatus orderStatus = data[2].equals("PENDING") ? OrderStatus.PENDING : OrderStatus.SERVED;
                Order order = new Order(id,new ArrayList<>(),0.0,orderStatus);
                String[] itemData = orderedString.split("&");
                for (String item : itemData) {
                    String[] dto = item.split("~");
                    //System.out.println(dto[0]);
                    Item key = new Item(dto[0]);
                    Item r1 = foodMenu.search(key) == null ? beverageMenu.search(key) : foodMenu.search(key);
                    r1 = (Item) r1.clone();
                    r1.setQuantity(Integer.parseInt(dto[1]));
                    order.addItemFromOrder(r1);
                }
                if(orderStatus == OrderStatus.PENDING)
                    orderQueue.enqueue(order);
                else
                    servedQueue.enqueue(order);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
