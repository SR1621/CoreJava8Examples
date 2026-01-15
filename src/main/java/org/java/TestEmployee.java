import java.util.*;
import java.util.stream.*;
class Employee {
    int id;
    String name;
    int age;
    String gender;
    String department;
    int yearOfJoining;
    double salary;

    public String getGender() {
        return this.gender;
    }
    public String getDepartment() {
        return this.department;
    }
    public int getAge() {
        return this.age;
    }
    public double getSalary() {
        return this.salary;
    }
    public String getName() {
        return this.name;
    }
    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Employee(int id, String name, int age, String gender, String dept, int yoj, double salary) {
        super();
        this.id=id;
        this.name=name;
        this.salary=salary;
    }
}
public class TestEmployee {
    public static void main(String[] args) {
        List<Employee> employeeList = new ArrayList<Employee>();

        employeeList.add(new Employee(111, "Jiya Brein", 32, "Female", "HR", 2011, 25000.0));
        employeeList.add(new Employee(122, "Paul Niksui", 25, "Male", "Sales And Marketing", 2015, 13500.0));
        employeeList.add(new Employee(133, "Martin Theron", 29, "Male", "Infrastructure", 2012, 18000.0));
        employeeList.add(new Employee(144, "Murali Gowda", 28, "Male", "Product Development", 2014, 32500.0));
        employeeList.add(new Employee(155, "Nima Roy", 27, "Female", "HR", 2013, 22700.0));
        employeeList.add(new Employee(166, "Iqbal Hussain", 43, "Male", "Security And Transport", 2016, 10500.0));
        System.out.println("Array list"+employeeList.get(1).getName());
        // employeeList.stream().forEach(e-> System.out.println(e.getSalary()));
        //incrementSalaryforLessThanA(employeeList,2,19000);
        incrementSalaryforLessThanA1(employeeList);

    }

    //write a function to retrieve Total salary and average salary of all employees

    //write a function to retrieve the youngest male employee from the "Security And Transport" department

    //Evaluate the output
	/*private static void incrementSalaryforLessThanA(List<Employee> employeeList, double incFactor, double lessThan) {
            final List<Employee> incrementedList = employeeList.stream()
                .filter(emp -> {
                        System.out.println("fitering employee " + emp.getName());
                      return emp.getSalary() >1;
                    })
                .map(emp -> {
                        System.out.println("Mapping employee " + emp.getName());
                        double newSal = emp.getSalary() * incFactor;
                        emp.setSalary(newSal);
                        return emp;
                    })
                .collect(Collectors.toList());
        }*/

    public static void incrementSalaryforLessThanA1(List<Employee> list)
    {
        list.stream().forEach((l -> System.out.println("Names"+l.getName())));
    }
}
