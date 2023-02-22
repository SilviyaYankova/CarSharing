package carsharing.entity;

public class Customer extends BaseEntity {
    Long rentedCarId;

    public Customer(String name) {
        super(name);
    }

    public Long getRentedCarId() {
        return rentedCarId;
    }

    public void setRentedCarId(Long rentedCarId) {
        this.rentedCarId = rentedCarId;
    }
}
