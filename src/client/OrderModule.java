package client;

import adt.ArrayList;
import adt.CircularLinkedQueue;
import adt.ListI;
import adt.QueueI;
import entity.Item;
import entity.Order;
import entity.OrderStatus;
import entity.Staff;

import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OrderModule {

    private Staff currentStaff;
    private ListI<Item> foodMenu;

    private ListI<Item> beverageMenu;
    private QueueI<Order> orderQueue;
    private QueueI<Order> servedQueue;

    private Scanner input;

    public OrderModule(Staff currentStaff) {
        this.currentStaff = currentStaff;
        this.foodMenu = new ArrayList<>();
        this.beverageMenu = new ArrayList<>();
        this.orderQueue = new CircularLinkedQueue<>();
        this.servedQueue = new CircularLinkedQueue<>();
        this.input = new Scanner(System.in);
    }

    public void run() {

        Item.readFile(foodMenu, beverageMenu);
        Order.readFile(orderQueue,servedQueue,foodMenu,beverageMenu);
        //Menu Loop
        int choice = 0;
        input.nextLine();
        do {
            //PrintingUtil.clearConsole();
            displayMenu();
            choice = input.nextInt();

            while (choice < 0 || choice > 6) {
                System.out.printf("%38sInvalid Choice !\n", "");
                displayMenu();
                choice = input.nextInt();
            }

            performChoice(choice);


        } while (choice != 6);

        Order.writeFile(orderQueue,servedQueue);
        Item.writeFile(foodMenu,beverageMenu);
        PrintingUtil.clearConsole();
    }

    private void displayOnComingOrder() {

        if (!orderQueue.isEmpty()) {
            System.out.printf("%40sIncoming Orders to be served!\n%40s", "", "");
            PrintingUtil.printLine(40);
            boolean first = true;
            for (Object item : orderQueue) {
                if (first) {
                    System.out.printf("%38s %5s", "", ((Order) item).getId());
                    first = false;
                } else
                    System.out.printf(" <-- %-4s", ((Order) item).getId());
            }
        }
        System.out.println();
    }

    private void displayServedOrder() {

        if (!servedQueue.isEmpty()) {
            System.out.printf("%40sServed Orders!\n%40s", "", "");
            PrintingUtil.printLine(40);
            boolean first = true;
            for (Object item : servedQueue) {
                if (first) {
                    System.out.printf("%38s %5s", "", ((Order) item).getId());
                    first = false;
                } else
                    System.out.printf(" <-- %-4s", ((Order) item).getId());
            }
        }
        System.out.println();
    }

    private void displayMenu() {
        displayOnComingOrder();
        displayServedOrder();
        System.out.printf("%38s Welcome %s for Using The System!!!\n\n", "", currentStaff.getName());
        System.out.printf("%38s Dear %s, Please enter your choice\n", "", currentStaff.getName());
        System.out.printf("%45s Menu \n%38s", "", "");
        PrintingUtil.printLine(20);
        System.out.printf("%38s 1. Make Order\n", "");
        System.out.printf("%38s 2. Serve Order\n", "");
        System.out.printf("%38s 3. View Details\n", "");
        System.out.printf("%38s 4. Update Order\n", "");
        System.out.printf("%38s 5. Generate Report\n", "");
        System.out.printf("%38s 6. Exit\n", "");
        System.out.printf("%38sEnter Your Choice - ", "");
    }

    private void performChoice(int choice) {

        switch (choice) {
            case 1:
                makeOrder();
                break;
            case 2:
                serveOrder();
                break;
            case 3:
                viewDetails();
                break;
            case 4:
                updateOrder();
                break;
            case 5:
                generateReport();
                break;
            default:
                break;
        }
    }

    private void serveOrder() {

        //Algorithm to perform serving the order

        if (!orderQueue.isEmpty()) {

            //Showing the current order queue
            displayOnComingOrder();

            //get Front and show the current order (toString())
            Order currentOrder = orderQueue.getFront();
            System.out.printf("%38s Current Order - \n", "");

            //show the staff are all the menu items ready to serve
            System.out.println(currentOrder);

            System.out.printf("%38s Are you sure all the items are ready to be served ? (Y/N) - ", "");

            char choice = input.next().charAt(0);
            //if yes
            if (Character.toUpperCase(choice) == 'Y') {
                //dequeue front order and enqueue into the served queue
                orderQueue.getFront().setStatus(OrderStatus.SERVED);
                servedQueue.enqueue(orderQueue.dequeue());
                System.out.printf("%38s Successfully served the order !\n", "");
            } else {
                //if no
                //go back to menu
                System.out.printf("%38s Successfully discarded !\n", "");
            }

        } else {
            System.out.printf("%38s The Current Queue is Empty, no Order to be served!\n", "");
        }
    }

    private void generateReport() {

        boolean session = true;

        do{
            reportMenu();
            System.out.printf("%38s Enter yoru choice - ", "");
            int choice = input.nextInt();

            while(choice < 1 || choice > 3){
                System.out.printf("%38s Invalid Choice!!! \n", "s");
                reportMenu();
                choice = input.nextInt();
            }

            switch (choice){
                case 1:
                    displayProfitReport();
                    break;
                case 2:
                    displayItemRanking();
                    break;
                default:
                    session = false;
            }

        }while(session);



    }

    private void displayItemRanking() {

        System.out.printf("%38s Item Ranking For Food And Beverage\n%38s", "", "");
        PrintingUtil.printLine(35);
        Stream.concat(foodMenu.stream(),beverageMenu.stream())
                .sorted((m1,m2) -> Integer.compare(m2.getQuantity(),m1.getQuantity()))
                .filter(e -> e.getQuantity() > 0)
                .map(Item::toRankingFormat)
                .forEach(System.out::println);
    }

    private void displayProfitReport() {

        double tax = 0.0;
        double tips = 0.0;
        double profit = 0.0;

        if(servedQueue.isEmpty())
            return;

        //id ordered items unit price quantity sum
        System.out.printf("%38s %-5s %-30s %-20s %-10s %-4s\n", "", "ID", "Ordered Items", "Price", "Quantity", "Sum");
        for(Object order : servedQueue) {
            System.out.printf("%38s %-5s %-30s %-20s %-10s %-4s\n", "", ((Order) order).getId(), "", "", "", "");
            ((Order) order).getOrderedItems()
                    .stream()
                    .forEach(e -> System.out.printf("%38s %5s %s", "", "",e.toReportFormat()));

            double sst = ((Order) order).calculateSST();
            double sv = ((Order) order).calculateServiceCharge();
            double subtotal = ((Order) order).calculateSubTotal();
            tax += sst;
            tips += sv;
            System.out.printf("%38s", "");
            PrintingUtil.printLine(75);
            System.out.printf("%38s %58s %-9s RM%.2f\n", "", "", "Tax", sst);
            System.out.printf("%38s %58s %-9s RM%.2f\n", "", "", "Service", sv);
            System.out.printf("%38s %58s %-9s RM%.2f\n", "", "", "Subtotal", subtotal);
            profit += ((Order) order).calculateTotalAmount();
        }

        System.out.printf("%38s Total Amount Tax     gather from system - %.2f\n","", tax);
        System.out.printf("%38s Total Amount Tips    gather from system - %.2f\n","", tips);
        System.out.printf("%38s Total Amount Profit  gather from system - %.2f\n","", profit);

        System.out.println();
        System.out.println();
        System.out.println();
    }

    private void reportMenu(){
        System.out.printf("%38s Which report would like to generate ? \n","");
        System.out.printf("%38s 1. Profit Report \n", "");
        System.out.printf("%38s 2. Item Ranking \n", "");
    }

    private void updateOrder() {

        boolean session = true;

        displayOnComingOrder();
        System.out.printf("%38s Enter Order ID to Update the Order ID - ", "");
        String id = input.next();
        input.nextLine();
        Order key = new Order(id);
        Order result = null;

        for(Object order : orderQueue)
            if(((Order) order).equals(key))
                result = (Order) order;


        if (result != null) {
            do {
                System.out.println(result);
                System.out.printf("%38s Enter commands to update the order\n", "");
                System.out.printf("%38s For Example : Add <Food Name> <Quantity>\n", "");
                System.out.printf("%38s For Example : Remove <Food Name> <Quantity>\n", "");
                System.out.printf("%38s For Example : Menu Food \n", "");
                System.out.printf("%38s For Example : Menu Beverage \n", "");
                System.out.printf("%38s For Example : Exit \n", "");
                System.out.printf("%38s Enter your command - ", "");

                String command = input.nextLine();
                String args[] = command.split(" ");
                int len = args.length;

                performCommand(result, args);

                if(args[0].equalsIgnoreCase("exit"))
                    session = false;

            }while(session);
        }
        else{
            System.out.printf("%38s No Such Order Found!!!\n", "");
        }
        PrintingUtil.clearConsole();

    }

    private void performCommand(Order order,String[] args){
        int len = args.length;
        String firstArgument = args[0];
        String lastArg = args[args.length - 1];
        //check if is menu or add || remove
        if(firstArgument.equalsIgnoreCase("menu")){
            if(len < 3){
                if(lastArg.equalsIgnoreCase("food") || lastArg.equalsIgnoreCase("beverage"))
                {
                    String type = lastArg.toLowerCase();
                    System.out.println(type);
                    displayItems(type);
                }
            }
            else
                System.out.printf("%38s Invalid Command!\n", "");
        }
        else if(firstArgument.equalsIgnoreCase("add") || firstArgument.equalsIgnoreCase("remove")){
            if(isNumeric(lastArg))
            {
                int quantity = Integer.parseInt(lastArg);
                String[] subArr = Arrays.copyOfRange(args,1,args.length-1);
                String foodName = String.join(" ", subArr);
                System.out.println(foodName);
                String type = firstArgument.toLowerCase();
                modifyOrder(order,type,foodName,quantity);
            }
            else{
                System.out.printf("%38s Invalid Command!\n", "");
            }
        }
        else if(firstArgument.equalsIgnoreCase("exit")){
            System.out.printf("%38s Successfully Update The Order !", "");
        }
        else{
            System.out.printf("%38s Invalid Command!\n", "");
        }
    }

    private void modifyOrder(Order order, String type,String foodName, int quantity){

        ListI<Item> orderedItems = order.getOrderedItems();

        Optional<Item> result = orderedItems.stream()
                .filter(item -> item.getName().toLowerCase().contains(foodName))
                .findFirst();

        if(type.equals("add")){

            if(result.isPresent()){
                Item ref = result.get();
                ref.setQuantity(ref.getQuantity() + quantity);
            }
            else{
                Item newItem = searchAllMenusByName(foodName);
                if(newItem != null){
                    newItem = (Item) newItem.clone();
                    newItem.setQuantity(quantity);
                    order.addItemFromOrder(newItem);
                }
                else
                    System.out.printf("%38s Item not found from menu!!\n", "");
            }
        }
        else if(type.equals("remove")){
            if(result.isPresent()){
                Item item = result.get();
                System.out.println(item);
                if(quantity >= item.getQuantity()) {
                    if(orderedItems.getNumberOfEntries() < 2){
                        System.out.printf("%38s Please any items to ensure we don't meet any empty order!!!\n", "");
                    }
                    else
                        order.removeItemFromOrder(item);
                }
                else {
                    Item menuItem = searchAllMenusByName(foodName);
                    item.setQuantity(item.getQuantity() - quantity);
                    menuItem.setQuantity(menuItem.getQuantity() - quantity);
                }
            }
            else
                System.out.printf("%38s Item not found from order!\n!", "");
        }
    }

    private Item searchAllMenusByName(String name){

        Optional<Item> foodResult = foodMenu.stream()
                .filter(item -> item.getName().toLowerCase().contains(name))
                .findFirst();
        Optional<Item> beverageResult = beverageMenu.stream()
                .filter(item -> item.getName().toLowerCase().contains(name))
                .findFirst();

        if(foodResult.isPresent())
            return foodResult.get();
        else if(beverageResult.isPresent())
            return beverageResult.get();
        else
            return null;
    }



    private boolean isNumeric(String arg){
        try{
            int val = Integer.parseInt(arg);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private void displayViewMenu(){

        System.out.printf("%38s View Details\n%38s", "","");
        PrintingUtil.printLine(20);
        System.out.printf("%38s 1. View All Order Details\n", "");
        System.out.printf("%38s 2. View Specific Order\n","");
        System.out.printf("%38s 3. View Menus\n", "");
        System.out.printf("%38s 4. Exit      \n", "");
    }


    private void viewDetails() {

        int choice = 0;

        displayViewMenu();
        System.out.printf("%38s Enter your choice - ", "");
        choice = input.nextInt();

        switch(choice){
            case 1:
                viewAllOrders();
                break;
            case 2:
                viewSpecificOrder();
                break;
            case 3:
                viewMenus();
                break;
            default:
                break;
        }


    }

    private void viewMenus(){

        System.out.printf("%38s Enter which menu would u like to view (FOOD/BEVERAGE) - ", "");
        if(input.hasNextLine())
            input.nextLine();

        String choice = input.next();

        if(choice.equalsIgnoreCase("Food") || choice.equalsIgnoreCase("Beverage")){
            String type = choice.equalsIgnoreCase("Food") ? "Food" : "Beverage";
            displayItems(type);
        }
        else
            System.out.printf("%38s No Such Menu !!! \n", "");

    }

    private void viewAllOrders(){
        orderQueue.forEach( e -> System.out.println(e));
    }

    private void viewSpecificOrder(){
        System.out.printf("%38s Enter order id - ", "");

        if(input.hasNextLine()){
            input.nextLine();
        }
        String id = input.next();

        Order result = null;
        Order key = new Order(id);

        for(Object item : orderQueue)
            if(((Order) item).equals(key))
                result = ((Order) item);

        if (result != null) {
            System.out.println(result);
        }
        else{
            System.out.printf("%38s No Such Order ID !", "");
        }
    }

    private void displayMenuSelection() {
        System.out.printf("%45s Menu \n%38s", "", "");
        PrintingUtil.printLine(20);
        System.out.printf("%38s 1. Food\n", "");
        System.out.printf("%38s 2. Beverage\n", "");
        System.out.printf("%38s 3. Discard\n", "");
        System.out.printf("%38sEnter Your Choice - ", "");
    }

    private void makeOrder() {
        Order order = new Order();
        int choice = 0;
        do {
            System.out.printf("%38s Current Order ID - %s\n", "", order.getId());
            displayMenuSelection();
            choice = input.nextInt();

            while (choice < 0 || choice > 3) {
                System.out.printf("%38sInvalid Choice !\n", "");
                displayMenuSelection();
                choice = input.nextInt();
            }
            String type;
            if (choice == 1)
                type = "Food";
            else if (choice == 2)
                type = "Beverage";
            else
                break;

            getItem(type, order);
            System.out.println(order.getOrderedItems().getNumberOfEntries());
        } while (choice != 3);
        if (!order.getOrderedItems().isEmpty()){
            System.out.printf("%38s Successfully added Order Into The Queue!!!\n", "");
            orderQueue.enqueue(order);
        }
        else
            Order.setOrderCount(Order.getOrderCount() - 1);
    }

    private void displayItems(String type) {

        ListI<Item> menu = type.equalsIgnoreCase("food") ? foodMenu : beverageMenu;

        System.out.printf("%45s Menu \n%38s", "", "");
        PrintingUtil.printLine(20);
        IntStream.range(0, menu.getNumberOfEntries())
                .forEach(i ->
                        System.out.printf("%38s %d. %-30s\t%5.2f\n", "", i + 1, menu.getEntry(i).getName(), menu.getEntry(i).getPrice())
                );
        System.out.printf("%38s %d. Exit\n", "", menu.getNumberOfEntries() + 1);
    }

    private void getItem(String type, Order order) {

        ListI<Item> menu = type.equals("Food") ? foodMenu : beverageMenu;

        int choice = 0;
        do {
            displayItems(type);
            System.out.printf("%38sEnter Your Choice - ", "");
            choice = input.nextInt();
            while (choice < 1 || choice > menu.getNumberOfEntries() + 1) {
                System.out.printf("%38sInvalid Choice !\n", "");
                displayItems(type);
                System.out.printf("%38sEnter Your Choice - ", "");
                choice = input.nextInt();
            }
            if (choice == menu.getNumberOfEntries() + 1)
                break;

            Item result = menu.getEntry(choice - 1);
            System.out.printf("%38sEnter Your Quantity - ", "");
            int quantity = input.nextInt();
            Item itemClone = ((Item) result.clone());
            itemClone.setQuantity(quantity);
            result.setQuantity(result.getQuantity() + quantity);
            System.out.println(itemClone);
            order.addItemFromOrder(itemClone);
            System.out.printf("%38sSuccessfully added the item to Order!!!\n", "");

        } while (choice != foodMenu.getNumberOfEntries() + 1);
    }

    public QueueI<Order> getOrderQueue() {
        return orderQueue;
    }

    public QueueI<Order> getServedQueue() {
        return servedQueue;
    }
}
