package carsharing.entity;

public class Car extends BaseEntity {
    Long companyId;

    public Car(String name, Long companyId) {
        super(name);
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
