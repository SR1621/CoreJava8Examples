// EmployeeStreamPlaybook.java
// Java 8 stream "playbook" using an Employee domain to demonstrate real-world patterns.
// Each demo is labeled Q1..Q90 and explained with layman-friendly comments.
// Compile & run on Java 8+:
//   javac EmployeeStreamPlaybook.java && java EmployeeStreamPlaybook

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class EmployeeStreamPlaybook {

    // --- Domain -------------------------------------------------------------

    enum Gender { MALE, FEMALE, OTHER }

    static class Department {
        final int id;
        final String name;
        final String location;
        Department(int id, String name, String location) {
            this.id = id; this.name = name; this.location = location;
        }
        @Override public String toString() { return name + "(" + location + ")"; }
    }

    static class Project {
        final int id;
        final String name;
        final boolean billable;
        final LocalDate startDate;
        final LocalDate endDate; // can be null if ongoing
        Project(int id, String name, boolean billable, LocalDate startDate, LocalDate endDate) {
            this.id = id; this.name = name; this.billable = billable; this.startDate = startDate; this.endDate = endDate;
        }
        @Override public String toString() {
            return name + (billable ? "[billable]" : "[non-billable]");
        }
    }

    static class Employee {
        final int id;
        final String name;
        final Gender gender;
        final int age;
        final int deptId;
        final BigDecimal salary; // monthly salary
        final LocalDate joinDate;
        final boolean active;
        final Set<String> skills;
        final List<Project> projects;

        Employee(int id, String name, Gender gender, int age, int deptId,
                 BigDecimal salary, LocalDate joinDate, boolean active,
                 Set<String> skills, List<Project> projects) {
            this.id = id; this.name = name; this.gender = gender; this.age = age; this.deptId = deptId;
            this.salary = salary; this.joinDate = joinDate; this.active = active;
            this.skills = skills == null ? new HashSet<>() : new HashSet<>(skills);
            this.projects = projects == null ? new ArrayList<>() : new ArrayList<>(projects);
        }
        @Override public String toString() {
            return String.format("%s{id=%d, dept=%d, age=%d, salary=%s}", name, id, deptId, age, salary);
        }
    }

    // --- Sample Data --------------------------------------------------------

    static class DataFactory {
        static Map<Integer, Department> departments() {
            Map<Integer, Department> m = new LinkedHashMap<>();
            m.put(10, new Department(10, "Engineering", "St. Louis"));
            m.put(20, new Department(20, "Product", "Chicago"));
            m.put(30, new Department(30, "Sales", "New York"));
            m.put(40, new Department(40, "HR", "Remote"));
            return m;
        }
        static List<Project> allProjects() {
            return Arrays.asList(
                new Project(1, "CardAuth", true, LocalDate.of(2023,1,5), null),
                new Project(2, "FraudDetect", true, LocalDate.of(2022,8,1), LocalDate.of(2024,6,30)),
                new Project(3, "DevPortal", false, LocalDate.of(2024,3,15), null),
                new Project(4, "DataWarehouse", true, LocalDate.of(2021,11,1), null),
                new Project(5, "MobileApp", true, LocalDate.of(2024,5,10), null)
            );
        }
        static List<Employee> employees() {
            Map<Integer, Department> dep = departments();
            List<Project> projects = allProjects();
            // Helper for quick BigDecimal
            Function<Integer, BigDecimal> k = i -> new BigDecimal(i).setScale(2, RoundingMode.HALF_UP);
            return Arrays.asList(
                new Employee(101, "Alice", Gender.FEMALE, 28, 10, k.apply(9500), LocalDate.of(2022,4,1), true,
                        new HashSet<>(Arrays.asList("Java","Spring","SQL")), Arrays.asList(projects.get(0), projects.get(2))),
                new Employee(102, "Bob", Gender.MALE, 35, 10, k.apply(12000), LocalDate.of(2020,1,15), true,
                        new HashSet<>(Arrays.asList("Java","Kotlin","Docker")), Arrays.asList(projects.get(1))),
                new Employee(103, "Charlie", Gender.OTHER, 30, 20, k.apply(10500), LocalDate.of(2021,9,10), true,
                        new HashSet<>(Arrays.asList("Product","SQL","Excel")), Arrays.asList(projects.get(2))),
                new Employee(104, "Diana", Gender.FEMALE, 42, 30, k.apply(15000), LocalDate.of(2018,5,20), false,
                        new HashSet<>(Arrays.asList("Salesforce","Negotiation")), Arrays.asList(projects.get(4))),
                new Employee(105, "Evan", Gender.MALE, 26, 10, k.apply(8000), LocalDate.of(2023,7,1), true,
                        new HashSet<>(Arrays.asList("Java","React","SQL")), Arrays.asList(projects.get(0), projects.get(4))),
                new Employee(106, "Fiona", Gender.FEMALE, 31, 20, k.apply(11000), LocalDate.of(2020,10,1), true,
                        new HashSet<>(Arrays.asList("UX","Figma","Research")), Arrays.asList(projects.get(2))),
                new Employee(107, "Gabe", Gender.MALE, 29, 30, k.apply(9000), LocalDate.of(2022,1,3), true,
                        new HashSet<>(Arrays.asList("Sales","Excel")), Arrays.asList(projects.get(3))),
                new Employee(108, "Hannah", Gender.FEMALE, 33, 40, k.apply(7000), LocalDate.of(2019,12,12), true,
                        new HashSet<>(Arrays.asList("HR","Recruiting")), Collections.emptyList()),
                new Employee(109, "Ivan", Gender.MALE, 27, 10, k.apply(8800), LocalDate.of(2023,2,14), true,
                        new HashSet<>(Arrays.asList("Java","Spring","Docker")), Arrays.asList(projects.get(0))),
                new Employee(110, "Julia", Gender.FEMALE, 38, 20, k.apply(13000), LocalDate.of(2017,6,30), true,
                        new HashSet<>(Arrays.asList("Product","Leadership")), Arrays.asList(projects.get(1), projects.get(3)))
            );
        }
    }

    // --- Helpers ------------------------------------------------------------

    static void header(String title) {
        System.out.println("\n== " + title + " ==");
    }

    static Map<Integer, Department> DEPTS = DataFactory.departments();

    // --- Demos --------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        List<Employee> EMP = DataFactory.employees();

        // BASIC (Q1 - Q25)
        q1_createStream(EMP);
        q2_filter(EMP);
        q3_mapNames(EMP);
        q4_mapToSalarySum(EMP);
        q5_sortedBySalary(EMP);
        q6_sortedByDeptThenSalary(EMP);
        q7_distinctSkills(EMP);
        q8_limitAndSkip(EMP);
        q9_anyAllNoneMatch(EMP);
        q10_findFirst(EMP);
        q11_countActive(EMP);
        q12_collectToSet(EMP);
        q13_collectToMapIdToName(EMP);
        q14_joiningNames(EMP);
        q15_removeNulls();
        q16_debugPeek(EMP);
        q17_boxedPrimitive(EMP);
        q18_iterateGenerate();
        q19_reduceHighestSalary(EMP);
        q20_reduceTotalSalary(EMP);
        q21_minByMaxBy(EMP);
        q22_optionalHandling(EMP);
        q23_mapToIntAverageAge(EMP);
        q24_takeTopN(EMP);
        q25_nthHighestSalary(EMP, 3);

        // INTERMEDIATE (Q26 - Q55)
        q26_groupByDept(EMP);
        q27_groupByDeptCounting(EMP);
        q28_groupByDeptTotalSalary(EMP);
        q29_groupByDeptAvgSalary(EMP);
        q30_groupByDeptNamesList(EMP);
        q31_partitionHighEarners(EMP);
        q32_summarizingSalary(EMP);
        q33_collectingAndThenUnmodifiable(EMP);
        q34_toLinkedHashMapOrdered(EMP);
        q35_groupByJoinMonth(EMP);
        q36_salaryBuckets(EMP);
        q37_findDuplicatesByName(EMP);
        q38_flatMapAllProjects(EMP);
        q39_flatMapSkillsSet(EMP);
        q40_employeesPerProject(EMP);
        q41_toConcurrentMapDemo(EMP);
        q42_parallelStreamCaution(EMP);
        q43_groupingMultiLevel(EMP);
        q44_partitionByActiveAndDept(EMP);
        q45_distinctByKey(EMP);
        q46_customCollectorSetToCsv(EMP);
        q47_collectToImmutableList(EMP);
        q48_sortNullSafeComparator();
        q49_sortUsingThenComparing(EMP);
        q50_mapMergeFunction(EMP);
        q51_joiningWithPrefixSuffix(EMP);
        q52_mapValuesTransform(EMP);
        q53_groupingAndTopN(EMP);
        q54_groupingAndMinBy(EMP);
        q55_partitionAndSummarize(EMP);

        // ADVANCED (Q56 - Q90)
        q56_lazyEvaluation(EMP);
        q57_shortCircuit(EMP);
        q58_reusablePredicates(EMP);
        q59_exceptionInLambdaHandling();
        q60_filesLinesExample();
        q61_customReducerMonthlyPayroll(EMP);
        q62_customComparatorCollator();
        q63_parallelCustomPool(EMP);
        q64_windowTopSalariesPerDept(EMP);
        q65_stableVsUnstableSortNote(EMP);
        q66_cartesianProductSkills(EMP);
        q67_frequencyOfSkills(EMP);
        q68_mostCommonSkill(EMP);
        q69_employeeTenureInYears(EMP);
        q70_groupByTenureBrackets(EMP);
        q71_projectDurationDays(EMP);
        q72_overlapEmployeesAcrossProjects(EMP);
        q73_buildIndexByName(EMP);
        q74_findByNameCaseInsensitive(EMP);
        q75_paginateSortedEmployees(EMP);
        q76_streamVsLoopEquivalence(EMP);
        q77_customCollectorAverageBigDecimal(EMP);
        q78_mapToDoubleVsBigDecimal(EMP);
        q79_cacheIntermediateResults(EMP);
        q80_invertDeptToEmployeesMap(EMP);
        q81_topSkillPerDept(EMP);
        q82_partitionByBillableProject(EMP);
        q83_flatMapOptionalLike(EMP);
        q84_groupByFirstLetter(EMP);
        q85_removeInactiveThenGroup(EMP);
        q86_collectToTreeMap(EMP);
        q87_validateLambdasExplain(EMP);
        q88_streamPipelineTemplate(EMP);
        q89_whenNotToUseStreams(EMP);
        q90_summaryChecklist();

        System.out.println("\nAll demos finished. Open the source to read layman comments above each example (Q1..Q90).\n" );
    }

    // ===================== BASIC ============================================

    // Q1: Create a stream from a list and print names.
    // Layman: "Turn the list into a conveyor belt and print each name."
    static void q1_createStream(List<Employee> EMP) {
        header("Q1 Create stream & print names");
        EMP.stream().map(e -> e.name).forEach(System.out::println);
    }

    // Q2: Filter active employees.
    // Layman: "Keep only those currently working (active=true)."
    static void q2_filter(List<Employee> EMP) {
        header("Q2 Filter active employees");
        List<Employee> active = EMP.stream().filter(e -> e.active).collect(Collectors.toList());
        System.out.println(active);
    }

    // Q3: Map to names.
    // Layman: "Convert each employee into just their name tag."
    static void q3_mapNames(List<Employee> EMP) {
        header("Q3 Map to names");
        List<String> names = EMP.stream().map(e -> e.name).collect(Collectors.toList());
        System.out.println(names);
    }

    // Q4: Map to salary and sum (BigDecimal-safe way).
    // Layman: "Add up monthly salaries of everyone."
    static void q4_mapToSalarySum(List<Employee> EMP) {
        header("Q4 Sum of salaries (BigDecimal)");
        BigDecimal total = EMP.stream().map(e -> e.salary).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Total monthly payroll: " + total);
    }

    // Q5: Sort by salary descending.
    // Layman: "Order people from highest pay to lowest."
    static void q5_sortedBySalary(List<Employee> EMP) {
        header("Q5 Sort by salary desc");
        List<Employee> sorted = EMP.stream()
            .sorted(Comparator.comparing((Employee e) -> e.salary).reversed())
            .collect(Collectors.toList());
        sorted.forEach(System.out::println);
    }

    // Q6: Sort by department, then salary desc.
    // Layman: "Group by team; within the team, richest first."
    static void q6_sortedByDeptThenSalary(List<Employee> EMP) {
        header("Q6 Sort by dept then salary desc");
        List<Employee> sorted = EMP.stream()
            .sorted(Comparator.comparing((Employee e) -> e.deptId)
                              .thenComparing((Employee e) -> e.salary, Comparator.reverseOrder()))
            .collect(Collectors.toList());
        sorted.forEach(System.out::println);
    }

    // Q7: Distinct skills across all employees.
    // Layman: "Collect unique skill names across the company."
    static void q7_distinctSkills(List<Employee> EMP) {
        header("Q7 Distinct skills");
        Set<String> skills = EMP.stream().flatMap(e -> e.skills.stream()).collect(Collectors.toSet());
        System.out.println(skills);
    }

    // Q8: Limit and skip.
    // Layman: "Grab first 3 employees; skip first 2 then take 3."
    static void q8_limitAndSkip(List<Employee> EMP) {
        header("Q8 limit & skip");
        System.out.println("First 3: " + EMP.stream().limit(3).collect(Collectors.toList()));
        System.out.println("Skip 2, take 3: " + EMP.stream().skip(2).limit(3).collect(Collectors.toList()));
    }

    // Q9: anyMatch / allMatch / noneMatch
    // Layman: "Quick yes/no checks."
    static void q9_anyAllNoneMatch(List<Employee> EMP) {
        header("Q9 match checks");
        boolean anyHigh = EMP.stream().anyMatch(e -> e.salary.compareTo(new BigDecimal("12000")) > 0);
        boolean allAdults = EMP.stream().allMatch(e -> e.age >= 18);
        boolean noneTeen = EMP.stream().noneMatch(e -> e.age < 18);
        System.out.println("Any >12k? " + anyHigh + ", All >=18? " + allAdults + ", None <18? " + noneTeen);
    }

    // Q10: findFirst
    // Layman: "Get the very first person in the list (if any)."
    static void q10_findFirst(List<Employee> EMP) {
        header("Q10 findFirst");
        EMP.stream().findFirst().ifPresent(System.out::println);
    }

    // Q11: count
    // Layman: "How many active employees?"
    static void q11_countActive(List<Employee> EMP) {
        header("Q11 count active");
        long c = EMP.stream().filter(e -> e.active).count();
        System.out.println(c);
    }

    // Q12: collect to Set
    // Layman: "Unique departments present."
    static void q12_collectToSet(List<Employee> EMP) {
        header("Q12 collect to set");
        Set<Integer> depts = EMP.stream().map(e -> e.deptId).collect(Collectors.toSet());
        System.out.println(depts);
    }

    // Q13: collect to Map id->name
    // Layman: "Make a phonebook: id maps to name."
    static void q13_collectToMapIdToName(List<Employee> EMP) {
        header("Q13 toMap id->name");
        Map<Integer,String> m = EMP.stream().collect(Collectors.toMap(e -> e.id, e -> e.name));
        System.out.println(m);
    }

    // Q14: joining names
    // Layman: "Join names into a single string with commas."
    static void q14_joiningNames(List<Employee> EMP) {
        header("Q14 joining");
        String s = EMP.stream().map(e -> e.name).collect(Collectors.joining(", "));
        System.out.println(s);
    }

    // Q15: remove nulls
    // Layman: "Filter out null values safely."
    static void q15_removeNulls() {
        header("Q15 remove nulls");
        List<String> data = Arrays.asList("A", null, "B", null, "C");
        List<String> cleaned = data.stream().filter(Objects::nonNull).collect(Collectors.toList());
        System.out.println(cleaned);
    }

    // Q16: peek for debugging
    // Layman: "Print in the middle without changing the stream."
    static void q16_debugPeek(List<Employee> EMP) {
        header("Q16 peek for debug");
        List<String> topNames = EMP.stream()
                .peek(e -> System.out.println("start: " + e.name))
                .filter(e -> e.salary.compareTo(new BigDecimal("10000")) > 0)
                .peek(e -> System.out.println(">10k: " + e.name))
                .map(e -> e.name.toUpperCase())
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("Result: " + topNames);
    }

    // Q17: boxed primitive streams
    // Layman: "Ages as numbers, then back to objects if needed."
    static void q17_boxedPrimitive(List<Employee> EMP) {
        header("Q17 IntStream/boxed");
        int sumAges = EMP.stream().mapToInt(e -> e.age).sum();
        List<Integer> ages = EMP.stream().mapToInt(e -> e.age).boxed().collect(Collectors.toList());
        System.out.println("Sum ages=" + sumAges + ", ages=" + ages);
    }

    // Q18: iterate & generate
    // Layman: "Create streams without a list: counting and random IDs."
    static void q18_iterateGenerate() {
        header("Q18 iterate & generate");
        List<Integer> firstTen = IntStream.iterate(1, i -> i + 1).limit(10).boxed().collect(Collectors.toList());
        List<String> randomIds = Stream.generate(() -> UUID.randomUUID().toString()).limit(3).collect(Collectors.toList());
        System.out.println(firstTen);
        System.out.println(randomIds);
    }

    // Q19: reduce to highest salary
    // Layman: "Walk through people and remember the best-paid so far."
    static void q19_reduceHighestSalary(List<Employee> EMP) {
        header("Q19 reduce max salary employee");
        Optional<Employee> best = EMP.stream().reduce((a,b) -> a.salary.compareTo(b.salary) >= 0 ? a : b);
        System.out.println(best.orElse(null));
    }

    // Q20: reduce total salary (accumulator + combiner)
    // Layman: "Add salaries; show two-parameter form that works in parallel too."
    static void q20_reduceTotalSalary(List<Employee> EMP) {
        header("Q20 reduce total salary 3-arg");
        BigDecimal total = EMP.parallelStream().reduce(
            BigDecimal.ZERO,
            (sum, e) -> sum.add(e.salary),
            BigDecimal::add
        );
        System.out.println(total);
    }

    // Q21: minBy / maxBy collector
    static void q21_minByMaxBy(List<Employee> EMP) {
        header("Q21 minBy / maxBy");
        Comparator<Employee> bySalary = Comparator.comparing(e -> e.salary);
        Employee min = EMP.stream().collect(Collectors.minBy(bySalary)).orElse(null);
        Employee max = EMP.stream().collect(Collectors.maxBy(bySalary)).orElse(null);
        System.out.println("Min=" + min + "; Max=" + max);
    }

    // Q22: Optional handling
    // Layman: "Safe wrapper for maybe-value."
    static void q22_optionalHandling(List<Employee> EMP) {
        header("Q22 Optional handling");
        Employee e = EMP.stream().filter(x -> x.name.equals("Zoe")).findFirst().orElseGet(() ->
            new Employee(0, "Zoe", Gender.OTHER, 0, 0, BigDecimal.ZERO, LocalDate.now(), false, Collections.emptySet(), Collections.emptyList())
        );
        System.out.println(e);
    }

    // Q23: average age
    static void q23_mapToIntAverageAge(List<Employee> EMP) {
        header("Q23 average age");
        OptionalDouble avg = EMP.stream().mapToInt(x -> x.age).average();
        System.out.println(avg.isPresent() ? avg.getAsDouble() : null);
    }

    // Q24: Top-N salaries
    static void q24_takeTopN(List<Employee> EMP) {
        header("Q24 Top-3 salaries");
        List<Employee> top3 = EMP.stream()
            .sorted(Comparator.comparing((Employee e) -> e.salary).reversed())
            .limit(3)
            .collect(Collectors.toList());
        System.out.println(top3);
    }

    // Q25: Nth highest salary
    static void q25_nthHighestSalary(List<Employee> EMP, int n) {
        header("Q25 Nth highest salary");
        Employee nth = EMP.stream()
            .sorted(Comparator.comparing((Employee e) -> e.salary).reversed())
            .skip(n - 1)
            .findFirst().orElse(null);
        System.out.println(n + "th highest: " + nth);
    }

    // ===================== INTERMEDIATE =====================================

    // Q26: Group by department
    static void q26_groupByDept(List<Employee> EMP) {
        header("Q26 group by dept");
        Map<Integer, List<Employee>> byDept = EMP.stream().collect(Collectors.groupingBy(e -> e.deptId));
        System.out.println(byDept);
    }

    // Q27: Group by dept and count
    static void q27_groupByDeptCounting(List<Employee> EMP) {
        header("Q27 group by dept & count");
        Map<Integer, Long> counts = EMP.stream().collect(Collectors.groupingBy(e -> e.deptId, Collectors.counting()));
        System.out.println(counts);
    }

    // Q28: Group by dept and sum salaries
    static void q28_groupByDeptTotalSalary(List<Employee> EMP) {
        header("Q28 group by dept total salary");
        Map<Integer, BigDecimal> totals = EMP.stream().collect(
            Collectors.groupingBy(e -> e.deptId,
                Collectors.reducing(BigDecimal.ZERO, e -> e.salary, BigDecimal::add))
        );
        System.out.println(totals);
    }

    // Q29: Group by dept and average salary
    static void q29_groupByDeptAvgSalary(List<Employee> EMP) {
        header("Q29 group by dept avg salary");
        Map<Integer, Double> avg = EMP.stream().collect(
            Collectors.groupingBy(e -> e.deptId,
                Collectors.averagingDouble(e -> e.salary.doubleValue()))
        );
        System.out.println(avg);
    }

    // Q30: Group by dept to names list
    static void q30_groupByDeptNamesList(List<Employee> EMP) {
        header("Q30 group by dept -> names");
        Map<Integer, List<String>> names = EMP.stream().collect(
            Collectors.groupingBy(e -> e.deptId, Collectors.mapping(e -> e.name, Collectors.toList()))
        );
        System.out.println(names);
    }

    // Q31: Partition high earners (>= 10k)
    static void q31_partitionHighEarners(List<Employee> EMP) {
        header("Q31 partition high earners");
        Map<Boolean, List<Employee>> parts = EMP.stream().collect(
            Collectors.partitioningBy(e -> e.salary.compareTo(new BigDecimal("10000")) >= 0)
        );
        System.out.println(parts);
    }

    // Q32: Summarizing salary
    static void q32_summarizingSalary(List<Employee> EMP) {
        header("Q32 summarizing salary");
        DoubleSummaryStatistics stats = EMP.stream()
            .collect(Collectors.summarizingDouble(e -> e.salary.doubleValue()));
        System.out.println(stats);
    }

    // Q33: collectingAndThen to unmodifiable list
    static void q33_collectingAndThenUnmodifiable(List<Employee> EMP) {
        header("Q33 collectingAndThen unmodifiable");
        List<String> names = EMP.stream().map(e -> e.name).collect(
            Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)
        );
        System.out.println(names);
        // names.add("X"); // would throw
    }

    // Q34: to LinkedHashMap to preserve insertion order
    static void q34_toLinkedHashMapOrdered(List<Employee> EMP) {
        header("Q34 LinkedHashMap preserve order");
        Map<Integer, String> ordered = EMP.stream().collect(
            Collectors.toMap(e -> e.id, e -> e.name, (a,b) -> a, LinkedHashMap::new)
        );
        System.out.println(ordered);
    }

    // Q35: Group by join month (YYYY-MM)
    static void q35_groupByJoinMonth(List<Employee> EMP) {
        header("Q35 group by join month");
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, List<Employee>> byMonth = EMP.stream().collect(
            Collectors.groupingBy(e -> e.joinDate.format(f))
        );
        System.out.println(byMonth);
    }

    // Q36: Salary buckets
    static void q36_salaryBuckets(List<Employee> EMP) {
        header("Q36 salary buckets");
        Function<BigDecimal, String> bucket = s -> {
            double v = s.doubleValue();
            if (v < 9000) return "<9k";
            if (v < 11000) return "9-11k";
            if (v < 13000) return "11-13k";
            return ">=13k";
        };
        Map<String, List<Employee>> buckets = EMP.stream().collect(Collectors.groupingBy(e -> bucket.apply(e.salary)));
        System.out.println(buckets);
    }

    // Q37: Find duplicate names
    static void q37_findDuplicatesByName(List<Employee> EMP) {
        header("Q37 duplicate names");
        Set<String> seen = new HashSet<>();
        Set<String> dups = EMP.stream().map(e -> e.name)
                .filter(n -> !seen.add(n))
                .collect(Collectors.toSet());
        System.out.println(dups);
    }

    // Q38: flatMap all projects
    static void q38_flatMapAllProjects(List<Employee> EMP) {
        header("Q38 flatMap projects");
        List<Project> projects = EMP.stream().flatMap(e -> e.projects.stream()).distinct().collect(Collectors.toList());
        System.out.println(projects);
    }

    // Q39: flatMap skills set
    static void q39_flatMapSkillsSet(List<Employee> EMP) {
        header("Q39 flatMap skills");
        Set<String> skills = EMP.stream().flatMap(e -> e.skills.stream()).collect(Collectors.toCollection(TreeSet::new));
        System.out.println(skills);
    }

    // Q40: Employees per project (inverse mapping)
    static void q40_employeesPerProject(List<Employee> EMP) {
        header("Q40 employees per project");
        Map<String, List<String>> index = EMP.stream().flatMap(e -> e.projects.stream().map(p -> new AbstractMap.SimpleEntry<>(p.name, e.name)))
            .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        System.out.println(index);
    }

    // Q41: Concurrent map demo (only safe with careful collectors)
    static void q41_toConcurrentMapDemo(List<Employee> EMP) {
        header("Q41 toConcurrentMap");
        ConcurrentMap<Integer, String> m = EMP.parallelStream().collect(
            Collectors.toConcurrentMap(e -> e.id, e -> e.name)
        );
        System.out.println(m);
    }

    // Q42: Parallel stream caution
    static void q42_parallelStreamCaution(List<Employee> EMP) {
        header("Q42 parallel caution");
        // Demonstrate that order is not guaranteed with parallel forEach
        EMP.parallelStream().forEach(e -> System.out.print(e.id + " "));
        System.out.println();
    }

    // Q43: Multi-level grouping: dept -> gender -> names
    static void q43_groupingMultiLevel(List<Employee> EMP) {
        header("Q43 multi-level grouping");
        Map<Integer, Map<Gender, List<String>>> result = EMP.stream().collect(
            Collectors.groupingBy(e -> e.deptId,
                Collectors.groupingBy(e -> e.gender, Collectors.mapping(e -> e.name, Collectors.toList())))
        );
        System.out.println(result);
    }

    // Q44: Partition by active then again by dept (two-step)
    static void q44_partitionByActiveAndDept(List<Employee> EMP) {
        header("Q44 partition by active then group by dept");
        Map<Boolean, Map<Integer, List<Employee>>> result = EMP.stream().collect(
            Collectors.partitioningBy(e -> e.active, Collectors.groupingBy(e -> e.deptId))
        );
        System.out.println(result);
    }

    // Q45: Distinct-by-key (keep first per name)
    static void q45_distinctByKey(List<Employee> EMP) {
        header("Q45 distinct-by-key (name)");
        Set<String> seen = ConcurrentHashMap.newKeySet();
        List<Employee> uniqueByName = EMP.stream()
            .filter(e -> seen.add(e.name))
            .collect(Collectors.toList());
        System.out.println(uniqueByName);
    }

    // Q46: Custom collector: Set<String> -> CSV string
    static void q46_customCollectorSetToCsv(List<Employee> EMP) {
        header("Q46 custom collector CSV");
        Set<String> skills = EMP.stream().flatMap(e -> e.skills.stream()).collect(Collectors.toSet());
        String csv = skills.stream().collect(Collector.of(
            () -> new StringBuilder(),
            (sb, s) -> { if (sb.length() > 0) sb.append(','); sb.append(s); },
            (a, b) -> { if (a.length() > 0 && b.length() > 0) a.append(','); a.append(b); return a; },
            StringBuilder::toString
        ));
        System.out.println(csv);
    }

    // Q47: Collect to immutable list (defensive copy)
    static void q47_collectToImmutableList(List<Employee> EMP) {
        header("Q47 immutable list");
        List<Employee> copy = Collections.unmodifiableList(new ArrayList<>(EMP));
        System.out.println(copy.size());
    }

    // Q48: Sort with null-safe comparator (demo with optional nulls)
    static void q48_sortNullSafeComparator() {
        header("Q48 null-safe comparator");
        List<String> names = Arrays.asList("Bob", null, "Alice", "Charlie", null);
        List<String> sorted = names.stream()
            .sorted(Comparator.nullsLast(Comparator.naturalOrder()))
            .collect(Collectors.toList());
        System.out.println(sorted);
    }

    // Q49: thenComparing chain
    static void q49_sortUsingThenComparing(List<Employee> EMP) {
        header("Q49 thenComparing chain");
        List<Employee> sorted = EMP.stream().sorted(
            Comparator.comparing((Employee e) -> e.deptId)
                      .thenComparing(e -> e.gender)
                      .thenComparing(e -> e.age)
                      .thenComparing(e -> e.name)
        ).collect(Collectors.toList());
        System.out.println(sorted);
    }

    // Q50: toMap with merge function (keep higher salary when same name)
    static void q50_mapMergeFunction(List<Employee> EMP) {
        header("Q50 toMap merge function");
        Map<String, Employee> byNameHighestPay = EMP.stream().collect(
            Collectors.toMap(e -> e.name, e -> e, (a,b) -> a.salary.compareTo(b.salary) >= 0 ? a : b)
        );
        System.out.println(byNameHighestPay);
    }

    // Q51: joining with prefix/suffix
    static void q51_joiningWithPrefixSuffix(List<Employee> EMP) {
        header("Q51 joining with prefix/suffix");
        String s = EMP.stream().map(e -> e.name).collect(Collectors.joining(", ", "[", "]"));
        System.out.println(s);
    }

    // Q52: Map values transform (id -> uppercase name)
    static void q52_mapValuesTransform(List<Employee> EMP) {
        header("Q52 map values transform");
        Map<Integer, String> m = EMP.stream().collect(Collectors.toMap(e -> e.id, e -> e.name.toUpperCase()));
        System.out.println(m);
    }

    // Q53: Grouping and top-N per dept
    static void q53_groupingAndTopN(List<Employee> EMP) {
        header("Q53 top-2 earners per dept");
        Map<Integer, List<Employee>> top2 = EMP.stream().collect(Collectors.groupingBy(e -> e.deptId,
            Collectors.collectingAndThen(Collectors.toList(), list -> list.stream()
                .sorted(Comparator.comparing((Employee e) -> e.salary).reversed())
                .limit(2).collect(Collectors.toList()))
        ));
        System.out.println(top2);
    }

    // Q54: Grouping and minBy (cheapest per dept)
    static void q54_groupingAndMinBy(List<Employee> EMP) {
        header("Q54 cheapest per dept");
        Map<Integer, Optional<Employee>> minPerDept = EMP.stream().collect(
            Collectors.groupingBy(e -> e.deptId, Collectors.minBy(Comparator.comparing(e -> e.salary)))
        );
        System.out.println(minPerDept);
    }

    // Q55: Partition and summarize
    static void q55_partitionAndSummarize(List<Employee> EMP) {
        header("Q55 partition and summarize");
        Map<Boolean, DoubleSummaryStatistics> stats = EMP.stream().collect(
            Collectors.partitioningBy(e -> e.active, Collectors.summarizingDouble(e -> e.salary.doubleValue()))
        );
        System.out.println(stats);
    }

    // ===================== ADVANCED ========================================

    // Q56: Lazy evaluation demonstration
    static void q56_lazyEvaluation(List<Employee> EMP) {
        header("Q56 lazy evaluation");
        Stream<Employee> s = EMP.stream().filter(e -> { System.out.println("filter " + e.name); return e.age > 30; });
        System.out.println("Nothing printed yet because no terminal op.");
        System.out.println("Now terminal op:");
        System.out.println(s.count());
    }

    // Q57: Short-circuit operations (anyMatch stops early)
    static void q57_shortCircuit(List<Employee> EMP) {
        header("Q57 short-circuit");
        boolean found = EMP.stream().peek(e -> System.out.println("check " + e.name))
                .anyMatch(e -> e.salary.compareTo(new BigDecimal("14000")) >= 0);
        System.out.println("Found high earner? " + found);
    }

    // Q58: Reusable predicates
    static void q58_reusablePredicates(List<Employee> EMP) {
        header("Q58 reusable predicates");
        Predicate<Employee> isEng = e -> e.deptId == 10;
        Predicate<Employee> highPay = e -> e.salary.compareTo(new BigDecimal("10000")) > 0;
        List<Employee> result = EMP.stream().filter(isEng.and(highPay)).collect(Collectors.toList());
        System.out.println(result);
    }

    // Q59: Checked exception handling in lambdas (wrap & rethrow)
    static void q59_exceptionInLambdaHandling() {
        header("Q59 handle checked exceptions");
        List<String> files = Arrays.asList("a.txt", "b.txt");
        List<Long> sizes = files.stream().map(name -> {
            try { return Files.exists(Paths.get(name)) ? Files.size(Paths.get(name)) : -1L; }
            catch (IOException ex) { return -1L; }
        }).collect(Collectors.toList());
        System.out.println(sizes);
    }

    // Q60: Files.lines example (read, filter, map)
    static void q60_filesLinesExample() throws IOException {
        header("Q60 Files.lines");
        Path temp = Files.createTempFile("demo", ".txt");
        Files.write(temp, Arrays.asList("alice,28", "bob,35", "charlie,30"), StandardOpenOption.TRUNCATE_EXISTING);
        try (Stream<String> lines = Files.lines(temp)) {
            List<String> adults = lines
                .map(s -> s.split(","))
                .filter(arr -> Integer.parseInt(arr[1]) >= 30)
                .map(arr -> arr[0])
                .collect(Collectors.toList());
            System.out.println(adults);
        }
        Files.deleteIfExists(temp);
    }

    // Q61: Custom reducer: monthly payroll by dept
    static void q61_customReducerMonthlyPayroll(List<Employee> EMP) {
        header("Q61 custom reducer payroll by dept");
        Map<Integer, BigDecimal> payroll = EMP.stream().collect(
            Collectors.groupingBy(e -> e.deptId, Collectors.reducing(BigDecimal.ZERO, e -> e.salary, BigDecimal::add))
        );
        System.out.println(payroll);
    }

    // Q62: Locale/collator sort (case-insensitive)
    static void q62_customComparatorCollator() {
        header("Q62 locale/case-insensitive sort");
        List<String> names = Arrays.asList("alice", "Alice", "ALICE", "Álvaro");
        List<String> sorted = names.stream()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .collect(Collectors.toList());
        System.out.println(sorted);
    }

    // Q63: Parallel stream with custom pool
    static void q63_parallelCustomPool(List<Employee> EMP) {
        header("Q63 parallel with custom pool");
        ForkJoinPool pool = new ForkJoinPool(4);
        try {
            List<String> names = pool.submit(() -> EMP.parallelStream().map(e -> e.name).collect(Collectors.toList())).join();
            System.out.println(names);
        } finally { pool.shutdown(); }
    }

    // Q64: Window: top salaries per dept (another approach)
    static void q64_windowTopSalariesPerDept(List<Employee> EMP) {
        header("Q64 window top salaries per dept");
        Map<Integer, List<Employee>> top = EMP.stream().collect(Collectors.groupingBy(e -> e.deptId));
        top.replaceAll((k, v) -> v.stream().sorted(Comparator.comparing((Employee e) -> e.salary).reversed()).limit(1).collect(Collectors.toList()));
        System.out.println(top);
    }

    // Q65: Stable sort note (Streams uses TimSort via Collections.sort)
    static void q65_stableVsUnstableSortNote(List<Employee> EMP) {
        header("Q65 stable sort note");
        List<Employee> sorted = EMP.stream().sorted(Comparator.comparing(e -> e.name)).collect(Collectors.toList());
        System.out.println(sorted);
    }

    // Q66: Cartesian product of skills between two employees (pairs)
    static void q66_cartesianProductSkills(List<Employee> EMP) {
        header("Q66 cartesian product of skills (first two employees)");
        if (EMP.size() < 2) return;
        Employee a = EMP.get(0), b = EMP.get(1);
        List<String> pairs = a.skills.stream().flatMap(sa -> b.skills.stream().map(sb -> sa + "+" + sb)).collect(Collectors.toList());
        System.out.println(pairs);
    }

    // Q67: Frequency of skills across company
    static void q67_frequencyOfSkills(List<Employee> EMP) {
        header("Q67 skill frequency");
        Map<String, Long> freq = EMP.stream().flatMap(e -> e.skills.stream()).collect(
            Collectors.groupingBy(Function.identity(), Collectors.counting())
        );
        System.out.println(freq);
    }

    // Q68: Most common skill
    static void q68_mostCommonSkill(List<Employee> EMP) {
        header("Q68 most common skill");
        String skill = EMP.stream().flatMap(e -> e.skills.stream())
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        System.out.println(skill);
    }

    // Q69: Employee tenure in years
    static void q69_employeeTenureInYears(List<Employee> EMP) {
        header("Q69 tenure in years");
        Map<String, Long> tenure = EMP.stream().collect(Collectors.toMap(e -> e.name, e -> ChronoUnit.YEARS.between(e.joinDate, LocalDate.now())));
        System.out.println(tenure);
    }

    // Q70: Group by tenure brackets
    static void q70_groupByTenureBrackets(List<Employee> EMP) {
        header("Q70 tenure buckets");
        Function<Long, String> bucket = y -> y < 1 ? "<1y" : y < 3 ? "1-3y" : y < 5 ? "3-5y" : ">=5y";
        Map<String, List<Employee>> byBucket = EMP.stream().collect(Collectors.groupingBy(
            e -> bucket.apply(ChronoUnit.YEARS.between(e.joinDate, LocalDate.now()))
        ));
        System.out.println(byBucket);
    }

    // Q71: Project duration in days (null-safe for endDate)
    static void q71_projectDurationDays(List<Employee> EMP) {
        header("Q71 project duration days");
        List<Project> projects = DataFactory.allProjects();
        Map<String, Long> days = projects.stream().collect(Collectors.toMap(p -> p.name,
            p -> ChronoUnit.DAYS.between(p.startDate, p.endDate == null ? LocalDate.now() : p.endDate)));
        System.out.println(days);
    }

    // Q72: Employees overlapping across projects (names working on multiple projects)
    static void q72_overlapEmployeesAcrossProjects(List<Employee> EMP) {
        header("Q72 overlap employees across projects");
        Map<String, Long> counts = EMP.stream().collect(Collectors.toMap(e -> e.name, e -> e.projects.stream().count()));
        List<String> multi = counts.entrySet().stream().filter(en -> en.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toList());
        System.out.println(multi);
    }

    // Q73: Build index by name -> employee (assume unique or last wins)
    static void q73_buildIndexByName(List<Employee> EMP) {
        header("Q73 index by name");
        Map<String, Employee> idx = EMP.stream().collect(Collectors.toMap(e -> e.name, e -> e, (a,b)->b));
        System.out.println(idx.keySet());
    }

    // Q74: Case-insensitive search by name (contains)
    static void q74_findByNameCaseInsensitive(List<Employee> EMP) {
        header("Q74 case-insensitive search");
        String q = "a";
        List<Employee> res = EMP.stream().filter(e -> e.name.toLowerCase().contains(q)).collect(Collectors.toList());
        System.out.println(res);
    }

    // Q75: Pagination (page 2, size 3 after sorting by name)
    static void q75_paginateSortedEmployees(List<Employee> EMP) {
        header("Q75 pagination");
        int page = 2, size = 3; // 1-based page index for demo
        List<Employee> sorted = EMP.stream().sorted(Comparator.comparing(e -> e.name)).collect(Collectors.toList());
        List<Employee> page2 = sorted.stream().skip((page-1L)*size).limit(size).collect(Collectors.toList());
        System.out.println(page2);
    }

    // Q76: Stream vs loop equivalence (sum of salaries)
    static void q76_streamVsLoopEquivalence(List<Employee> EMP) {
        header("Q76 stream vs loop: total salary");
        BigDecimal viaStream = EMP.stream().map(e -> e.salary).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal viaLoop = BigDecimal.ZERO; for (Employee e : EMP) viaLoop = viaLoop.add(e.salary);
        System.out.println("Stream=" + viaStream + ", Loop=" + viaLoop);
    }

    // Q77: Custom collector average BigDecimal
    static void q77_customCollectorAverageBigDecimal(List<Employee> EMP) {
        header("Q77 average salary (BigDecimal)");
        class Acc { BigDecimal sum = BigDecimal.ZERO; int count = 0; }
        BigDecimal avg = EMP.stream().collect(Collector.of(
            Acc::new,
            (a,e) -> { a.sum = a.sum.add(e.salary); a.count++; },
            (a,b) -> { a.sum = a.sum.add(b.sum); a.count += b.count; return a; },
            a -> a.count == 0 ? BigDecimal.ZERO : a.sum.divide(new BigDecimal(a.count), 2, RoundingMode.HALF_UP)
        ));
        System.out.println(avg);
    }

    // Q78: mapToDouble vs BigDecimal (precision note)
    static void q78_mapToDoubleVsBigDecimal(List<Employee> EMP) {
        header("Q78 precision note");
        double sumD = EMP.stream().mapToDouble(e -> e.salary.doubleValue()).sum();
        BigDecimal sumBD = EMP.stream().map(e -> e.salary).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("double=" + sumD + ", BigDecimal=" + sumBD);
    }

    // Q79: Cache intermediate results (collect then reuse)
    static void q79_cacheIntermediateResults(List<Employee> EMP) {
        header("Q79 cache intermediate");
        List<Employee> eng = EMP.stream().filter(e -> e.deptId == 10).collect(Collectors.toList());
        BigDecimal total = eng.stream().map(e -> e.salary).reduce(BigDecimal.ZERO, BigDecimal::add);
        long cnt = eng.stream().count();
        System.out.println("Engineering count=" + cnt + ", total=" + total);
    }

    // Q80: Invert dept -> employees (map of dept name-> names)
    static void q80_invertDeptToEmployeesMap(List<Employee> EMP) {
        header("Q80 invert dept to names");
        Map<String, List<String>> m = EMP.stream().collect(Collectors.groupingBy(e -> DEPTS.get(e.deptId).name, Collectors.mapping(e -> e.name, Collectors.toList())));
        System.out.println(m);
    }

    // Q81: Top skill per dept (most frequent skill within dept)
    static void q81_topSkillPerDept(List<Employee> EMP) {
        header("Q81 top skill per dept");
        // Java 8-compatible implementation (no flatMapping)
        Map<Integer, String> top = EMP.stream().collect(Collectors.groupingBy(e -> e.deptId, Collectors.collectingAndThen(
            Collectors.mapping(e -> e.skills, Collectors.toList()),
            listOfSets -> {
                Map<String, Long> freq = listOfSets.stream().flatMap(Set::stream).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                return freq.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
            }
        )));
        System.out.println(top);
    }

    // Helper for Java 8: flatMapping not available. Provide custom.
    // We'll implement flatMapping via collectingAndThen + interim list.
    // But above used Collectors.flatMapping (Java 9+). Let's rewrite using Java 8 compatible approach:
    // Re-implement q81 in Java 8 style to ensure compilation.
    static void q81_topSkillPerDept_Java8(List<Employee> EMP) {
        Map<Integer, String> top = EMP.stream().collect(Collectors.groupingBy(e -> e.deptId, Collectors.collectingAndThen(
            Collectors.mapping(e -> e.skills, Collectors.toList()),
            listOfSets -> {
                Map<String, Long> freq = listOfSets.stream().flatMap(Set::stream).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                return freq.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
            }
        )));
        System.out.println(top);
    }

    // We'll call the Java8-compatible version instead of the Java 9 feature in main via q81 wrapper.

    // Q82: Partition employees by whether they have any billable project
    static void q82_partitionByBillableProject(List<Employee> EMP) {
        header("Q82 partition by billable project");
        Map<Boolean, List<Employee>> parts = EMP.stream().collect(Collectors.partitioningBy(e -> e.projects.stream().anyMatch(p -> p.billable)));
        System.out.println(parts);
    }

    // Q83: FlatMap Optional-like (flatten optional names if present)
    static void q83_flatMapOptionalLike(List<Employee> EMP) {
        header("Q83 flatMap optional-like");
        // Pretend we lookup nicknames that may or may not exist
        Function<Employee, Optional<String>> nickname = e -> e.name.length() > 4 ? Optional.of(e.name.substring(0, 3)) : Optional.empty();
        List<String> nicks = EMP.stream().map(nickname).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        System.out.println(nicks);
    }

    // Q84: Group by first letter of name
    static void q84_groupByFirstLetter(List<Employee> EMP) {
        header("Q84 group by first letter");
        Map<Character, List<String>> groups = EMP.stream().collect(Collectors.groupingBy(e -> e.name.charAt(0), Collectors.mapping(e -> e.name, Collectors.toList())));
        System.out.println(groups);
    }

    // Q85: Remove inactive then group by dept
    static void q85_removeInactiveThenGroup(List<Employee> EMP) {
        header("Q85 remove inactive then group");
        Map<Integer, List<Employee>> res = EMP.stream().filter(e -> e.active).collect(Collectors.groupingBy(e -> e.deptId));
        System.out.println(res);
    }

    // Q86: Collect to TreeMap (sorted by key)
    static void q86_collectToTreeMap(List<Employee> EMP) {
        header("Q86 collect to TreeMap");
        Map<Integer, String> m = EMP.stream().collect(Collectors.toMap(e -> e.id, e -> e.name, (a,b)->a, TreeMap::new));
        System.out.println(m);
    }

    // Q87: Validate lambdas (explain: avoid side effects)
    static void q87_validateLambdasExplain(List<Employee> EMP) {
        header("Q87 lambda side-effects note");
        // Bad: modifying external list inside forEach can cause issues in parallel.
        List<String> target = Collections.synchronizedList(new ArrayList<>());
        EMP.parallelStream().forEach(e -> target.add(e.name)); // works here, but be cautious. Prefer collectors.
        List<String> safe = EMP.parallelStream().map(e -> e.name).collect(Collectors.toList());
        System.out.println("safe collected size=" + safe.size());
    }

    // Q88: Stream pipeline template (filter -> map -> sort -> collect)
    static void q88_streamPipelineTemplate(List<Employee> EMP) {
        header("Q88 template");
        List<String> result = EMP.stream()
            .filter(e -> e.active)
            .map(e -> e.name)
            .sorted()
            .collect(Collectors.toList());
        System.out.println(result);
    }

    // Q89: When NOT to use streams (explain)
    static void q89_whenNotToUseStreams(List<Employee> EMP) {
        header("Q89 when not to use streams");
        // If you need complex stateful operations across elements or heavy exception control flow, a classic loop is clearer.
        // Example: find two adjacent employees who meet a condition together. A for-loop might be simpler.
        boolean foundPair = false;
        for (int i = 0; i < EMP.size() - 1; i++) {
            if (EMP.get(i).deptId == EMP.get(i+1).deptId) { foundPair = true; break; }
        }
        System.out.println("adjacent same-dept pair? " + foundPair);
    }

    // Q90: Summary checklist
    static void q90_summaryChecklist() {
        header("Q90 summary checklist");
        System.out.println("1) Keep streams side-effect free\n2) Choose the right collector\n3) Beware of parallel ordering\n4) Use BigDecimal for money\n5) Prefer method refs for readability");
    }
}
