import model.Bill;
import model.Product;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Inventory {
    private static final String FILE_NAME = "products.csv";
    private static final Integer PRODUCTS_NUMBER = 1000;
    private static final Integer MAX_PRODUCTS_IN_BILL = 5;

    private final List<Product> products;
    private List<Bill> bills;
    private final ReadWriteLock billsLock;
    private final List<ReadWriteLock> productLocks;

    public Inventory() {
        this.products = this.readProducts();
        this.bills = new ArrayList<>();
        this.productLocks = new ArrayList<>();
        this.billsLock = new ReentrantReadWriteLock();

        for(int i = 0; i < PRODUCTS_NUMBER; i++) {
            this.productLocks.add(new ReentrantReadWriteLock());
        }
    }

    public void generateBills(int billCount) {
        for(int i = 0; i < billCount; i++) {
            final Random random = new Random();

            final HashMap<Integer, Integer> productList = new HashMap<>();
            int productCount = random.nextInt(MAX_PRODUCTS_IN_BILL - 1) + 1;
            int totalPrice = 0;
            List<Integer> productIndexes = new ArrayList<>();
            List<ReadWriteLock> currentLocks = new ArrayList<>();

            while (productIndexes.size() < productCount) {
                int productIndex = random.nextInt(PRODUCTS_NUMBER);
                if (!productIndexes.contains(productIndex)) {
                    productIndexes.add(productIndex);
                }
            }

            Collections.sort(productIndexes);
            for(Integer productIndex : productIndexes) {
                ReadWriteLock lock = productLocks.get(productIndex);

                lock.writeLock().lock();
                currentLocks.add(lock);
            }

            for(Integer productIndex: productIndexes) {
                Product product = products.get(productIndex);

                if(product.getQuantity() > 0) {
                    int quantity;
                    if (product.getQuantity() == 1) quantity = 1;
                    else {
                        quantity = random.nextInt(product.getQuantity() - 1) + 1;
                    }

                    totalPrice += quantity * product.getPrice();

                    productList.put(product.getId(), quantity);
                }
            }


            for (Map.Entry<Integer, Integer> mapEntry : productList.entrySet()) {
                int productId = mapEntry.getKey();
                int quantity = mapEntry.getValue();

                Product product = products.get(productId);
                product.setQuantity(product.getQuantity() - quantity);
            }

            this.billsLock.writeLock().lock();
            for (ReadWriteLock lock : currentLocks) {
                lock.writeLock().unlock();
            }
            Bill bill = new Bill(totalPrice, productList);
            this.bills.add(bill);
            this.billsLock.writeLock().unlock();
        }
    }

    public void audit() {
        int totalPrice = 0;
        int expectedTotalPrice = 0;

        HashMap<Integer, Integer> expectedQuantities = new HashMap<>();

        for(ReadWriteLock lock : productLocks) {
            lock.readLock().lock();
        }
        billsLock.readLock().lock();
        System.out.println("Finished locking for audit");
        System.out.println("There are " + bills.size() + " bills");
        for (Product product : products) {
            expectedQuantities.put(product.getId(), product.getInitialQuantity());
        }

        for (Bill bill : bills) {
            totalPrice += bill.getTotalPrice();

            for (Map.Entry<Integer, Integer> entrySet : bill.getProductList().entrySet()) {
                int productId = entrySet.getKey();
                int quantity = entrySet.getValue();

                expectedQuantities.put(productId, expectedQuantities.get(productId) - quantity);
                Optional<Product> optProduct = products.stream().filter((Product p) -> p.getId() == productId).findFirst();
                if(optProduct.isEmpty()) {
                    throw new RuntimeException("Unexpected product id " + productId);
                }
                Product product = optProduct.get();

                expectedTotalPrice += product.getPrice() * quantity;
            }
        }

        if(totalPrice != expectedTotalPrice) {
            throw new RuntimeException("The money resulted from all bills do not add up! Expected: " + expectedTotalPrice + " Received: " + totalPrice);
        }

        for(Map.Entry<Integer, Integer> entrySet : expectedQuantities.entrySet()) {
            int productId = entrySet.getKey();
            int expectedQuantity = entrySet.getValue();

            Optional<Product> optProduct = products.stream().filter((Product p) -> p.getId() == productId).findFirst();
            if(optProduct.isEmpty()) {
                throw new RuntimeException("Unexpected product id " + productId);
            }
            Product product = optProduct.get();

            if(product.getQuantity() != expectedQuantity) {
                System.out.println(product);
                for(Bill bill : bills) {
                    if(bill.getProductList().containsKey(productId)) {
                        System.out.println(bill);
                        System.out.println(bill.getProductList().get(productId));
                    }
                }
                throw new RuntimeException("Product(id=" + product.getId() + ") has an unexpected quantity! Expected: " + expectedQuantity + " Received: " + product.getQuantity());
            }
        }

        billsLock.readLock().unlock();
        for(ReadWriteLock lock : productLocks) {
            lock.readLock().unlock();
        }
//        productLocks.forEach(Lock::unlock);
    }

    private List<Product> readProducts() {
        final List<Product> products = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(FILE_NAME));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for(int index = 0; index < PRODUCTS_NUMBER; index++) {
            final Integer id = scanner.nextInt();
            final Integer price = scanner.nextInt();
            final Integer quantity = scanner.nextInt();
            final Product product = new Product(id - 1, price, quantity);
            products.add(product);
        }

        return products;
    }

}
