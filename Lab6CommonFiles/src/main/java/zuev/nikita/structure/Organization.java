package zuev.nikita.structure;

import java.io.Serializable;
import java.util.*;

/**
 * Data storage structure
 */
public class Organization implements Comparable<Organization>, Serializable {
    private static final long serialVersionUID = 1L;

    public Organization(Integer id, String name, Coordinates coordinates, Date creationDate, Double annualTurnover, OrganizationType type, Address postalAddress) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.annualTurnover = annualTurnover;
        this.creationDate = creationDate;
        this.type = type;
        this.postalAddress = postalAddress;
    }

    /**
     * The field cannot be null, The field value must be greater than 0, The value of this field must be unique, The value of this field must be generated automatically
     */
    private Integer id;
    /**
     * The field cannot be null, String cannot be empty
     */
    private String name;
    /**
     * The field cannot be null
     */
    private Coordinates coordinates;
    /**
     * The field cannot be null, the value of this field must be generated automatically
     */
    private Date creationDate;
    /**
     * The field can be null, the field value must be greater than 0
     */
    private Double annualTurnover;
    /**
     * The field cannot be null
     */
    private OrganizationType type;
    /**
     * The field cannot be null
     */
    private Address postalAddress;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Address getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(Address postalAddress) {
        this.postalAddress = postalAddress;
    }

    public Double getAnnualTurnover() {
        return annualTurnover;
    }

    public void setAnnualTurnover(Double annualTurnover) {
        this.annualTurnover = annualTurnover;
    }

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    @Override
    public int compareTo(Organization o) {
        int compare = name.compareTo(o.getName());
        if (compare == 0) {
            compare = coordinates.compareTo(o.getCoordinates());
            if (compare == 0) {
                compare = annualTurnover.compareTo(o.getAnnualTurnover());
                if (compare == 0) {
                    compare = postalAddress.compareTo(o.getPostalAddress());
                    if (compare == 0) {
                        compare = type.compareTo(o.getType());
                    }
                }
            }
        }
        return compare;
    }

    @Override
    public String toString() {
        return "-- ID:\t\t\t" + id + '\n' +
                "-- Название:\t\t" + name + '\n' +
                "-- Координаты:\n" +
                "-- -- X:\t\t" + coordinates.getX() + '\n' +
                "-- -- Y:\t\t" + coordinates.getY() + '\n' +
                "-- Дата создания:\t" + creationDate + '\n' +
                "-- Годовой оборот:\t" + annualTurnover + '\n' +
                "-- Тип организации:\t" + type + '\n' +
                "-- Адрес:\t\t" + postalAddress.getZipCode();
    }

    /**
     * Method for a user to enter a structure through the input stream.
     *
     * @return Organization
     */
    public static Organization organizationInput() {
        System.out.println("Вводите данные об организации:");
        return new Organization(-1, nameInput(), coordinatesInput(), new Date(), annualTurnoverInput(), organizationTypeInput(), addressInput());
    }

    private static String nameInput() {
        Scanner inputScanner = new Scanner(System.in);
        boolean flag = true;
        String name = null;
        while (flag) {
            System.out.print("Название: ");
            name = inputScanner.nextLine().trim();
            if (!name.equals("")) flag = false;
            else System.out.println("name не может быть пустым.");
        }
        return name;
    }

    private static Coordinates coordinatesInput() {
        Scanner inputScanner = new Scanner(System.in);
        long x = 0;
        double y = 0;
        boolean flag = true;
        System.out.println("Координаты:");
        while (flag) {
            System.out.print("X: ");
            try {
                x = Long.parseLong(inputScanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("X дожен быть целым числом.");
                continue;
            }
            if (x <= 923) flag = false;
            else System.out.println("X дожен быть не больше 923.");
        }
        flag = true;
        while (flag) {
            System.out.print("Y: ");
            try {
                y = Double.parseDouble(inputScanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Y дожен быть дробным числом. Дробное число пишется через точку.");
                continue;
            }
            flag = false;
        }
        return new Coordinates(x, y);
    }

    private static double annualTurnoverInput() {
        Scanner inputScanner = new Scanner(System.in);
        double annualTurnover = 0;
        boolean flag = true;
        while (flag) {
            System.out.print("Годовой оборот: ");
            try {
                annualTurnover = Double.parseDouble(inputScanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Годовой оборот дожен быть дробным числом. Дробное число пишется через точку.");
                continue;
            }
            if (annualTurnover > 0) flag = false;
            else System.out.println("Годовой оборот должен быть больше 0.");
        }
        return annualTurnover;
    }

    private static OrganizationType organizationTypeInput() {
        Scanner inputScanner = new Scanner(System.in);
        OrganizationType type = null;
        String orgTypeString;
        boolean flag = true;
        while (flag) {
            flag = false;
            System.out.print("Тип организации (COMMERCIAL - 1, PUBLIC - 2, TRUST - 3): ");
            orgTypeString = inputScanner.nextLine().trim();
            try {
                type = OrganizationType.valueOf(orgTypeString);
            } catch (Exception e) {
                try {
                    int index = Integer.parseInt(orgTypeString);
                    type = OrganizationType.values()[index - 1];
                } catch (NumberFormatException | IndexOutOfBoundsException e2) {
                    System.out.println("Нет такого типа");
                    flag = true;
                }
            }
        }
        return type;
    }

    private static Address addressInput() {
        Scanner inputScanner = new Scanner(System.in);
        System.out.print("Адрес: ");
        String zipCode = inputScanner.nextLine().trim();
        if (zipCode.equals("")) zipCode = null;
        return new Address(zipCode);
    }
}