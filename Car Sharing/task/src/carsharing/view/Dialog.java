package carsharing.view;

import carsharing.entity.Car;
import carsharing.entity.Company;
import carsharing.entity.Customer;
import carsharing.repository.CarRepository;
import carsharing.repository.CustomerRepository;
import carsharing.repository.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Dialog {
    private static final Scanner SCANNER = new Scanner(System.in);
    Repository<Company> companyRepository;
    CarRepository carRepository;
    CustomerRepository customerRepository;
    Company company;
    Customer customer;

    public Dialog(Repository<Company> companyRepository, CarRepository carRepository, CustomerRepository customerRepository) {
        this.companyRepository = companyRepository;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
    }

    private String readLn() {
        return SCANNER.nextLine();
    }

    public void run() {
        State state = main;
        while (state != null) {
            state = state.get();
        }
    }

    Menu main = new Menu(
            new MenuItem(1, "Log in as a manager", () -> this.manager),
            new MenuItem(2, "Log in as a customer", () -> this.getAllCustomers(() -> this.customerMenu)),
            new MenuItem(3, "Create a customer", () -> this.createNewCustomer(() -> this.main)),
            new MenuItem(0, "Exit", null)
    );


    Menu manager = new Menu(
            new MenuItem(1, "Company list", () -> getAllCompanies(() -> this.car)),
            new MenuItem(2, "Create a company", () -> createCompany(() -> this.manager)),
            new MenuItem(0, "Back", main)
    );

    Menu car = new Menu(
            new MenuItem(1, "Car list", () -> getAllCarsByCompany(() -> this.car)),
            new MenuItem(2, "Create a car", () -> createCar(company, () -> this.car)),
            new MenuItem(0, "Back", () -> this.manager)
    );

    Menu customerMenu = new Menu(
            new MenuItem(1, "Rent a car", () -> rentACar(() -> this.customerMenu)),
            new MenuItem(2, "Return a rented car", () -> returnACar(() -> this.customerMenu)),
            new MenuItem(3, "My rented car", () -> rentedCar(() -> this.customerMenu)),
            new MenuItem(0, "Back", () -> this.main)
    );

    private State createCompany(State next) {
        System.out.println("Enter the company name:");
        String name = readLn();
        try {
            companyRepository.add(new Company(name));
            System.out.printf("The company was created!%n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return next;
    }

    private State getAllCompanies(State next) {
        List<Company> list = new ArrayList<>();
        int choice = 0;
        try {
            list = companyRepository.getAll();

            if (list.isEmpty()) {
                System.out.println("The company list is empty!");
            } else {
                System.out.println("Choose the company:");

                int count = 1;
                for (Company company : list) {
                    System.out.printf("%d. %s%n", count, company.getName());
                    count++;
                }

                System.out.println("0. Back");

                choice = Integer.parseInt(readLn());

                Company c;
                if (choice != 0 && list.size() == 1) {
                    c = list.get(0);
                    company = c;
                } else if (choice != 0 && list.size() > 1) {
                    c = list.get(choice - 1);
                    company = c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (list.isEmpty() || choice == 0) {
            return this.manager;
        }
        return next;
    }

    private State createCar(Company company, State next) {
        System.out.println("Enter the car name:");
        String name = readLn();
        try {
            carRepository.add(new Car(name, company.getId()));
            System.out.printf("The car was created!%n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return next;
    }

    private State getAllCarsByCompany(State next) {
        try {
            List<Car> list = carRepository.getCarsByCompany(company.getId());
            if (list.isEmpty()) {
                System.out.println("The car list is empty!");
            } else {
                System.out.printf("%s car list:%n", company.getName());
            }
            int count = 1;
            List<Customer> customers = customerRepository.getAll();
            List<Car> rentedCars = new ArrayList<>();
            for (Customer c : customers) {
                if (c.getRentedCarId() != 0) {
                    rentedCars.add(carRepository.getById(c.getRentedCarId()));
                }
            }

            for (Car car : list) {
                boolean notEqual = true;
                for (Car rentedCar : rentedCars) {
                    if (Objects.equals(car.getId(), rentedCar.getId())) {
                        notEqual = false;
                        break;
                    }
                }
                if (notEqual) {
                    System.out.printf("%d. %s%n", count, car.getName());
                    count++;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return next;
    }

    private State createNewCustomer(State next) {
        System.out.println("Enter the customer name:");
        String name = readLn();
        try {
            customerRepository.add(new Customer(name));
            System.out.println("Created customer: " + name);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return next;
    }

    private State getAllCustomers(State next) {
        List<Customer> list = new ArrayList<>();
        try {
            list = customerRepository.getAll();
            if (list.isEmpty()) {
                System.out.println("The customer list is empty!");
            } else {
                System.out.println("Choose a customer:");
                int count = 1;
                for (Customer customer : list) {
                    System.out.printf("%d. %s%n", count, customer.getName());
                    count++;
                }

                System.out.println("0. Back");

                int choice = Integer.parseInt(readLn());

                getCustomer(list, choice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (list.isEmpty()) {
            return this.main;
        }

        return next;
    }

    private void getCustomer(List<Customer> list, int choice) {
        Customer c;
        if (choice != 0 && list.size() == 1) {
            c = list.get(0);
            customer = c;
        } else if (choice != 0 && list.size() > 1) {
            c = list.get(choice - 1);
            customer = c;
        }
    }

    private State rentACar(State next) {
        if (customer.getRentedCarId() != 0) {
            System.out.println("You've already rented a car!");
            return this.customerMenu;
        }
        getAllCompanies(() -> this.car);
        System.out.println("Choose a car:");
        getAllCarsByCompany(() -> null);
        System.out.println("0. Back");

        int choice = Integer.parseInt(readLn());
        Car car;
        if (choice != 0) {
            try {
                List<Car> list = carRepository.getCarsByCompany(company.getId());
                if (list.size() > 1) {
                    car = list.get(choice - 1);
                } else {
                    car = list.get(0);
                }
                customer.setRentedCarId(car.getId());
                customerRepository.updateCustomer(customer);
                System.out.println("You rented '" + car.getName() + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return next;
    }

    private State returnACar(State next) {

        Long rentedCarId = customer.getRentedCarId();
        if (rentedCarId != 0) {
            customer.setRentedCarId(null);
            try {
                customerRepository.updateCustomer(customer);
                System.out.println("You've returned a rented car!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("You didn't rent a car!");
        }

        return next;
    }

    private State rentedCar(State next) {
        Long rentedCarId = customer.getRentedCarId();
        if (rentedCarId != 0) {
            try {
                Car car = carRepository.getById(rentedCarId);
                System.out.println("Your rented car:");
                System.out.println(car.getName());
                System.out.println("Company:");

                Company company = companyRepository.getById(car.getCompanyId());
                System.out.println(company.getName());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("You didn't rent a car!");
        }

        return next;
    }
}
