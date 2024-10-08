import java.util.*;

// Enum representing different item categories
enum ItemCategory {
    ELECTRONICS, FURNITURE, CLOTHING, FOOD;
}

// Enum representing different vehicle types
enum VehicleType {
    TRUCK, VAN, BIKE;
}

// Abstract Vehicle class with common attributes
abstract class Vehicle {
    String licensePlate;
    double capacity;
    VehicleType vehicleType;

    public Vehicle(String licensePlate, double capacity, VehicleType vehicleType) {
        this.licensePlate = licensePlate;
        this.capacity = capacity;
        this.vehicleType = vehicleType;
    }

    public abstract boolean canAccessRoute(Set<VehicleType> restrictedVehicles);

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}
// Specific vehicle types extending the abstract Vehicle class
class Truck extends Vehicle {
    public Truck(String licensePlate, double capacity) {
        super(licensePlate, capacity, VehicleType.TRUCK);
    }

    @Override
    public boolean canAccessRoute(Set<VehicleType> restrictedVehicles) {
        return !restrictedVehicles.contains(VehicleType.TRUCK);
    }
}

class Van extends Vehicle {
    public Van(String licensePlate, double capacity) {
        super(licensePlate, capacity, VehicleType.VAN);
    }

    @Override
    public boolean canAccessRoute(Set<VehicleType> restrictedVehicles) {
        return !restrictedVehicles.contains(VehicleType.VAN);
    }
}

class Bike extends Vehicle {
    public Bike(String licensePlate, double capacity) {
        super(licensePlate, capacity, VehicleType.BIKE);
    }

    @Override
    public boolean canAccessRoute(Set<VehicleType> restrictedVehicles) {
        return !restrictedVehicles.contains(VehicleType.BIKE);
    }
}
// Class representing an item in the warehouse with categories
class Item {
    private String name;
    private int quantity;
    private double weight;
    private ItemCategory category;

    public Item(String name, int quantity, double weight, ItemCategory category) {
        this.name = name;
        this.quantity = quantity;
        this.weight = weight;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public double getWeight() {
        return weight;
    }

    public String toString() {
        return name + " (Qty: " + quantity + ", Weight: " + weight + "kg, Category: " + category + ")";
    }
}

// Class representing the warehouse and inventory management
// Class representing the warehouse and inventory management
class Warehouse {
    private Map<String, Item> inventory = new HashMap<>();

    // Add or update item in the warehouse
    public void addItem(String name, int quantity, double weight, ItemCategory category) {
        inventory.putIfAbsent(name, new Item(name, quantity, weight, category));
    }

    public void removeItem(String name, int quantity) {
        if (inventory.containsKey(name)) {
            Item item = inventory.get(name);
            if (item.getQuantity() >= quantity) {
                item.setQuantity(item.getQuantity() - quantity);
                System.out.println("Removed " + quantity + " of " + name + " from inventory.");
            } else {
                System.out.println("Insufficient quantity of " + name + " in stock.");
            }
        } else {
            System.out.println(name + " not found in inventory.");
        }
    }

    public void displayInventory() {
        System.out.println("Current Inventory:");
        for (Item item : inventory.values()) {
            System.out.println(item);
        }
    }

    // New method to get item
    public Item getItem(String name) {
        return inventory.get(name);
    }
}


// Class representing a shipping route
class ShippingRoute {
    private String from;
    private String to;
    private double distance;
    private Set<VehicleType> restrictedVehicles;

    public ShippingRoute(String from, String to, double distance, Set<VehicleType> restrictedVehicles) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.restrictedVehicles = restrictedVehicles;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getDistance() {
        return distance;
    }

    public Set<VehicleType> getRestrictedVehicles() {
        return restrictedVehicles;
    }

    public boolean isAccessibleByVehicle(Vehicle vehicle) {
        return vehicle.canAccessRoute(restrictedVehicles);
    }
}

// Shipping Manager to manage shipping operations
class ShippingManager {
    private Map<String, List<ShippingRoute>> routes = new HashMap<>();

    // Add route
    public void addRoute(String from, String to, double distance, Set<VehicleType> restrictedVehicles) {
        routes.putIfAbsent(from, new ArrayList<>());
        routes.putIfAbsent(to, new ArrayList<>());
        routes.get(from).add(new ShippingRoute(from, to, distance, restrictedVehicles));
        routes.get(to).add(new ShippingRoute(to, from, distance, restrictedVehicles));
    }

    // Find the shortest route using Dijkstra's algorithm
    public void findShortestRoute(String start, String end, Vehicle vehicle) {
        PriorityQueue<ShippingRoute> pq = new PriorityQueue<>(Comparator.comparingDouble(ShippingRoute::getDistance));
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();

        distances.put(start, 0.0);
        pq.add(new ShippingRoute(start, start, 0.0, new HashSet<>()));

        while (!pq.isEmpty()) {
            ShippingRoute currentRoute = pq.poll();
            String currentLocation = currentRoute.getTo();

            if (!visited.add(currentLocation)) {
                continue;
            }

            if (currentLocation.equals(end)) {
                printRoute(end, previous);
                System.out.println("Total Distance: " + distances.get(end) + " km");
                return;
            }

            for (ShippingRoute neighbor : routes.getOrDefault(currentLocation, new ArrayList<>())) {
                if (!visited.contains(neighbor.getTo()) && neighbor.isAccessibleByVehicle(vehicle)) {
                    double newDist = distances.get(currentLocation) + neighbor.getDistance();
                    if (newDist < distances.getOrDefault(neighbor.getTo(), Double.MAX_VALUE)) {
                        distances.put(neighbor.getTo(), newDist);
                        previous.put(neighbor.getTo(), currentLocation);
                        pq.add(new ShippingRoute(currentLocation, neighbor.getTo(), newDist, neighbor.getRestrictedVehicles()));
                    }
                }
            }
        }
        System.out.println("No available route for " + vehicle.getVehicleType() + " from " + start + " to " + end);
    }

    // Print the shortest route
    private void printRoute(String end, Map<String, String> previous) {
        Stack<String> path = new Stack<>();
        String current = end;
        while (current != null) {
            path.push(current);
            current = previous.get(current);
        }
        System.out.print("Shortest Path: ");
        while (!path.isEmpty()) {
            System.out.print(path.pop());
            if (!path.isEmpty()) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }
}

// Main class
public class WarehouseShippingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Create warehouse and items
        Warehouse warehouse = new Warehouse();
        warehouse.addItem("Laptop", 10, 2.5, ItemCategory.ELECTRONICS);
        warehouse.addItem("Sofa", 5, 50.0, ItemCategory.FURNITURE);
        warehouse.addItem("T-shirt", 100, 0.2, ItemCategory.CLOTHING);

        warehouse.displayInventory();

        // Create a shipping manager and add routes
        ShippingManager shippingManager = new ShippingManager();
        shippingManager.addRoute("Warehouse", "Customer1", 10, new HashSet<>(Arrays.asList(VehicleType.TRUCK)));
        shippingManager.addRoute("Warehouse", "Customer2", 20, new HashSet<>());
        shippingManager.addRoute("Customer1", "Customer2", 15, new HashSet<>(Arrays.asList(VehicleType.BIKE)));

        // Vehicles
        Truck truck = new Truck("TRK123", 1000);
        Van van = new Van("VAN456", 500);

        // Taking user input for the order
        System.out.println("\nEnter the item name:");
        String itemName = scanner.nextLine();
        System.out.println("Enter the quantity:");
        int quantityOrdered = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.println("Enter the customer name:");
        String customerName = scanner.nextLine();

        // Check if the item exists in the warehouse using the new method
        Item orderedItem = warehouse.getItem(itemName);
        if (orderedItem == null) {
            System.out.println("Item not found in inventory.");
            return;
        }

        double totalWeight = quantityOrdered * orderedItem.getWeight(); // Calculate total weight

        // Check if the vehicle can carry the load
        Vehicle vehicle = van; // You can change to truck or bike as needed
        if (totalWeight <= vehicle.capacity) {
            // Remove items from inventory
            warehouse.removeItem(itemName, quantityOrdered);

            // Find the shortest route to the customer
            shippingManager.findShortestRoute("Warehouse", customerName, vehicle);
        } else {
            System.out.println("Order exceeds the vehicle's capacity. Total weight: " + totalWeight + "kg, Vehicle capacity: " + vehicle.capacity + "kg.");
        }

        // Close the scanner
        scanner.close();
    }
}
