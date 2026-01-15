// EmployeeStreamQ91to300.java
// Java 8 runnable demos for Q91..Q300 using Employee domain (standalone file).
// Compile & run examples:
//   javac EmployeeStreamQ91to300.java && java EmployeeStreamQ91to300 Q123
//   java EmployeeStreamQ91to300 range Q91-Q110
//   java EmployeeStreamQ91to300 all

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.*;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class EmployeeStreamQ91to300 {

    // --- Domain (same as base file) ----------------------------------------
    enum Gender { MALE, FEMALE, OTHER }

    static class Department {
        final int id; final String name; final String location;
        Department(int id, String name, String location) { this.id=id; this.name=name; this.location=location; }
        @Override public String toString(){ return name+"("+location+")"; }
    }

    static class Project {
        final int id; final String name; final boolean billable; final LocalDate startDate; final LocalDate endDate; // null if ongoing
        Project(int id, String name, boolean billable, LocalDate startDate, LocalDate endDate){ this.id=id; this.name=name; this.billable=billable; this.startDate=startDate; this.endDate=endDate; }
        @Override public String toString(){ return name + (billable?"[billable]":"[non-billable]"); }
    }

    static class Employee {
        final int id; final String name; final Gender gender; final int age; final int deptId; final BigDecimal salary; final LocalDate joinDate; final boolean active; final Set<String> skills; final List<Project> projects;
        Employee(int id, String name, Gender gender, int age, int deptId, BigDecimal salary, LocalDate joinDate, boolean active, Set<String> skills, List<Project> projects) {
            this.id=id; this.name=name; this.gender=gender; this.age=age; this.deptId=deptId; this.salary=salary; this.joinDate=joinDate; this.active=active;
            this.skills = skills==null? new HashSet<>() : new HashSet<>(skills);
            this.projects = projects==null? new ArrayList<>() : new ArrayList<>(projects);
        }
        @Override public String toString(){ return String.format("%s{id=%d, dept=%d, age=%d, salary=%s}", name, id, deptId, age, salary); }
    }

    static class DataFactory {
        static Map<Integer, Department> departments(){
            Map<Integer, Department> m = new LinkedHashMap<>();
            m.put(10, new Department(10, "Engineering", "St. Louis"));
            m.put(20, new Department(20, "Product", "Chicago"));
            m.put(30, new Department(30, "Sales", "New York"));
            m.put(40, new Department(40, "HR", "Remote"));
            return m;
        }
        static List<Project> allProjects(){
            return Arrays.asList(
                new Project(1, "CardAuth", true, LocalDate.of(2023,1,5), null),
                new Project(2, "FraudDetect", true, LocalDate.of(2022,8,1), LocalDate.of(2024,6,30)),
                new Project(3, "DevPortal", false, LocalDate.of(2024,3,15), null),
                new Project(4, "DataWarehouse", true, LocalDate.of(2021,11,1), null),
                new Project(5, "MobileApp", true, LocalDate.of(2024,5,10), null)
            );
        }
        static List<Employee> employees(){
            List<Project> P = allProjects();
            java.util.function.Function<Integer, BigDecimal> k = i -> new BigDecimal(i).setScale(2, RoundingMode.HALF_UP);
            return Arrays.asList(
                new Employee(101, "Alice", Gender.FEMALE, 28, 10, k.apply(9500), LocalDate.of(2022,4,1), true,
                        new HashSet<>(Arrays.asList("Java","Spring","SQL")), Arrays.asList(P.get(0), P.get(2))),
                new Employee(102, "Bob", Gender.MALE, 35, 10, k.apply(12000), LocalDate.of(2020,1,15), true,
                        new HashSet<>(Arrays.asList("Java","Kotlin","Docker")), Arrays.asList(P.get(1))),
                new Employee(103, "Charlie", Gender.OTHER, 30, 20, k.apply(10500), LocalDate.of(2021,9,10), true,
                        new HashSet<>(Arrays.asList("Product","SQL","Excel")), Arrays.asList(P.get(2))),
                new Employee(104, "Diana", Gender.FEMALE, 42, 30, k.apply(15000), LocalDate.of(2018,5,20), false,
                        new HashSet<>(Arrays.asList("Salesforce","Negotiation")), Arrays.asList(P.get(4))),
                new Employee(105, "Evan", Gender.MALE, 26, 10, k.apply(8000), LocalDate.of(2023,7,1), true,
                        new HashSet<>(Arrays.asList("Java","React","SQL")), Arrays.asList(P.get(0), P.get(4))),
                new Employee(106, "Fiona", Gender.FEMALE, 31, 20, k.apply(11000), LocalDate.of(2020,10,1), true,
                        new HashSet<>(Arrays.asList("UX","Figma","Research")), Arrays.asList(P.get(2))),
                new Employee(107, "Gabe", Gender.MALE, 29, 30, k.apply(9000), LocalDate.of(2022,1,3), true,
                        new HashSet<>(Arrays.asList("Sales","Excel")), Arrays.asList(P.get(3))),
                new Employee(108, "Hannah", Gender.FEMALE, 33, 40, k.apply(7000), LocalDate.of(2019,12,12), true,
                        new HashSet<>(Arrays.asList("HR","Recruiting")), Collections.emptyList()),
                new Employee(109, "Ivan", Gender.MALE, 27, 10, k.apply(8800), LocalDate.of(2023,2,14), true,
                        new HashSet<>(Arrays.asList("Java","Spring","Docker")), Arrays.asList(P.get(0))),
                new Employee(110, "Julia", Gender.FEMALE, 38, 20, k.apply(13000), LocalDate.of(2017,6,30), true,
                        new HashSet<>(Arrays.asList("Product","Leadership")), Arrays.asList(P.get(1), P.get(3)))
            );
        }
    }

    static Map<Integer, Department> DEPTS = DataFactory.departments();

    static void header(String t){ System.out.println("\n== "+t+" =="); }

    // --- Entry Point --------------------------------------------------------
    public static void main(String[] args) throws Exception {
        List<Employee> EMP = DataFactory.employees();

        if (args.length == 0) {
            System.out.println("Pass codes like Q123, or 'range Q91-Q110', or 'all'. For example:\n  java EmployeeStreamQ91to300 Q101 Q102\n  java EmployeeStreamQ91to300 range Q120-Q130\n  java EmployeeStreamQ91to300 all");
            return;
        }
        if ("all".equalsIgnoreCase(args[0])) {
            for (int i=91;i<=300;i++) run("Q"+i, EMP);
            return;
        }
        if ("range".equalsIgnoreCase(args[0]) && args.length>1 && args[1].matches("Q\\\d+-Q\\\d+")){
            String[] p = args[1].substring(1).split("-Q");
            int a = Integer.parseInt(p[0]); int b = Integer.parseInt(p[1]);
            for (int i=a;i<=b;i++) run("Q"+i, EMP);
            return;
        }
        for (String code: args) run(code, EMP);
    }

    static void run(String code, List<Employee> EMP) throws Exception {
        try {
            int n = Integer.parseInt(code.substring(1));
            new Qs().run(n, EMP);
        } catch(Exception ex){
            System.out.println("Bad code: "+code+" -> "+ex);
        }
    }

    // -------------------- IMPLEMENTATIONS Q91..Q300 ------------------------
    static class Qs {
        void run(int q, List<Employee> EMP) throws Exception {
            switch(q){
                case 91: q91(EMP); break; case 92: q92(EMP); break; case 93: q93(EMP); break; case 94: q94(); break; case 95: q95(); break;
                case 96: q96(EMP); break; case 97: q97(EMP); break; case 98: q98(EMP); break; case 99: q99(EMP); break; case 100: q100(EMP); break;
                case 101: q101(EMP); break; case 102: q102(EMP); break; case 103: q103(EMP); break; case 104: q104(EMP); break; case 105: q105(EMP); break;
                case 106: q106(EMP); break; case 107: q107(EMP); break; case 108: q108(EMP); break; case 109: q109(EMP); break; case 110: q110(EMP); break;
                case 111: q111(EMP); break; case 112: q112(EMP); break; case 113: q113(EMP); break; case 114: q114(EMP); break; case 115: q115(EMP); break;
                case 116: q116(EMP); break; case 117: q117(EMP); break; case 118: q118(EMP); break; case 119: q119(EMP); break; case 120: q120(EMP); break;
                case 121: q121(EMP); break; case 122: q122(EMP); break; case 123: q123(EMP); break; case 124: q124(EMP); break; case 125: q125(EMP); break;
                case 126: q126(EMP); break; case 127: q127(EMP); break; case 128: q128(EMP); break; case 129: q129(EMP); break; case 130: q130(EMP); break;
                case 131: q131(EMP); break; case 132: q132(EMP); break; case 133: q133(); break; case 134: q134(); break; case 135: q135(EMP); break;
                case 136: q136(EMP); break; case 137: q137(EMP); break; case 138: q138(EMP); break; case 139: q139(EMP); break; case 140: q140(EMP); break;
                case 141: q141(EMP); break; case 142: q142(EMP); break; case 143: q143(EMP); break; case 144: q144(EMP); break; case 145: q145(EMP); break;
                case 146: q146(EMP); break; case 147: q147(EMP); break; case 148: q148(EMP); break; case 149: q149(EMP); break; case 150: q150(); break;
                case 151: q151(EMP); break; case 152: q152(EMP); break; case 153: q153(EMP); break; case 154: q154(EMP); break; case 155: q155(EMP); break;
                case 156: q156(EMP); break; case 157: q157(); break; case 158: q158(EMP); break; case 159: q159(); break; case 160: q160(EMP); break;
                case 161: q161(EMP); break; case 162: q162(EMP); break; case 163: q163(EMP); break; case 164: q164(EMP); break; case 165: q165(EMP); break;
                case 166: q166(); break; case 167: q167(EMP); break; case 168: q168(EMP); break; case 169: q169(EMP); break; case 170: q170(); break;
                case 171: q171(EMP); break; case 172: q172(EMP); break; case 173: q173(EMP); break; case 174: q174(EMP); break; case 175: q175(EMP); break;
                case 176: q176(EMP); break; case 177: q177(); break; case 178: q178(EMP); break; case 179: q179(EMP); break; case 180: q180(EMP); break;
                case 181: q181(EMP); break; case 182: q182(EMP); break; case 183: q183(EMP); break; case 184: q184(EMP); break; case 185: q185(EMP); break;
                case 186: q186(EMP); break; case 187: q187(EMP); break; case 188: q188(EMP); break; case 189: q189(EMP); break; case 190: q190(EMP); break;
                case 191: q191(EMP); break; case 192: q192(EMP); break; case 193: q193(); break; case 194: q194(EMP); break; case 195: q195(EMP); break;
                case 196: q196(EMP); break; case 197: q197(EMP); break; case 198: q198(EMP); break; case 199: q199(EMP); break; case 200: q200(EMP); break;
                case 201: q201(EMP); break; case 202: q202(EMP); break; case 203: q203(EMP); break; case 204: q204(EMP); break; case 205: q205(EMP); break;
                case 206: q206(EMP); break; case 207: q207(EMP); break; case 208: q208(EMP); break; case 209: q209(EMP); break; case 210: q210(EMP); break;
                case 211: q211(EMP); break; case 212: q212(EMP); break; case 213: q213(EMP); break; case 214: q214(); break; case 215: q215(); break;
                case 216: q216(EMP); break; case 217: q217(EMP); break; case 218: q218(EMP); break; case 219: q219(); break; case 220: q220(EMP); break;
                case 221: q221(EMP); break; case 222: q222(EMP); break; case 223: q223(EMP); break; case 224: q224(); break; case 225: q225(); break;
                case 226: q226(); break; case 227: q227(EMP); break; case 228: q228(EMP); break; case 229: q229(EMP); break; case 230: q230(EMP); break;
                case 231: q231(EMP); break; case 232: q232(); break; case 233: q233(EMP); break; case 234: q234(EMP); break; case 235: q235(); break;
                case 236: q236(EMP); break; case 237: q237(); break; case 238: q238(EMP); break; case 239: q239(); break; case 240: q240(); break;
                case 241: q241(); break; case 242: q242(); break; case 243: q243(EMP); break; case 244: q244(EMP); break; case 245: q245(); break;
                case 246: q246(EMP); break; case 247: q247(); break; case 248: q248(EMP); break; case 249: q249(); break; case 250: q250(EMP); break;
                case 251: q251(EMP); break; case 252: q252(); break; case 253: q253(); break; case 254: q254(EMP); break; case 255: q255(EMP); break;
                case 256: q256(); break; case 257: q257(EMP); break; case 258: q258(); break; case 259: q259(EMP); break; case 260: q260(EMP); break;
                case 261: q261(EMP); break; case 262: q262(EMP); break; case 263: q263(EMP); break; case 264: q264(); break; case 265: q265(EMP); break;
                case 266: q266(); break; case 267: q267(); break; case 268: q268(EMP); break; case 269: q269(); break; case 270: q270(EMP); break;
                case 271: q271(EMP); break; case 272: q272(EMP); break; case 273: q273(EMP); break; case 274: q274(EMP); break; case 275: q275(EMP); break;
                case 276: q276(); break; case 277: q277(); break; case 278: q278(EMP); break; case 279: q279(); break; case 280: q280(); break;
                case 281: q281(EMP); break; case 282: q282(EMP); break; case 283: q283(EMP); break; case 284: q284(EMP); break; case 285: q285(EMP); break;
                case 286: q286(); break; case 287: q287(EMP); break; case 288: q288(EMP); break; case 289: q289(EMP); break; case 290: q290(EMP); break;
                case 291: q291(EMP); break; case 292: q292(EMP); break; case 293: q293(EMP); break; case 294: q294(EMP); break; case 295: q295(EMP); break;
                case 296: q296(EMP); break; case 297: q297(EMP); break; case 298: q298(); break; case 299: q299(EMP); break; case 300: q300(); break;
                default: System.out.println("Q"+q+" not implemented");
            }
        }

        // Implementations (reuse logic from our plan)
        void q91(List<Employee> EMP){ header("Q91 TreeMap id->name"); System.out.println(EMP.stream().collect(Collectors.toMap(e->e.id,e->e.name,(a,b)->a,TreeMap::new))); }
        void q92(List<Employee> EMP){ header("Q92 side-effects vs collectors"); List<String> unsafe=Collections.synchronizedList(new ArrayList<>()); EMP.parallelStream().forEach(e->unsafe.add(e.name)); List<String> safe=EMP.parallelStream().map(e->e.name).collect(Collectors.toList()); System.out.println("unsafe="+unsafe.size()+", safe="+safe.size()); }
        void q93(List<Employee> EMP){ header("Q93 template filter->map->sort"); System.out.println(EMP.stream().filter(e->e.active).map(e->e.name).sorted().collect(Collectors.toList())); }
        void q94(){ header("Q94 for-loop clearer for adjacent pairs"); System.out.println("Prefer for-loop for neighbor-dependent logic"); }
        void q95(){ header("Q95 checklist"); System.out.println("Side-effect free, right collector, BigDecimal for money, parallel only with payoff"); }
        void q96(List<Employee> EMP){ header("Q96 List->Map id->Employee"); System.out.println(EMP.stream().collect(Collectors.toMap(e->e.id, e->e)).size()); }
        void q97(List<Employee> EMP){ header("Q97 salaries list"); System.out.println(EMP.stream().map(e->e.salary).collect(Collectors.toList())); }
        void q98(List<Employee> EMP){ header("Q98 all project names"); System.out.println(EMP.stream().flatMap(e->e.projects.stream()).map(p->p.name).collect(Collectors.toSet())); }
        void q99(List<Employee> EMP){ header("Q99 youngest"); System.out.println(EMP.stream().min(Comparator.comparingInt(e->e.age)).orElse(null)); }
        void q100(List<Employee> EMP){ header("Q100 oldest"); System.out.println(EMP.stream().max(Comparator.comparingInt(e->e.age)).orElse(null)); }
        void q101(List<Employee> EMP){ header("Q101 avg salary by gender"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.gender, Collectors.averagingDouble(e->e.salary.doubleValue())))); }
        void q102(List<Employee> EMP){ header("Q102 dept->highest earner name"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(e->e.salary)), o->o.map(x->x.name).orElse(null))))); }
        void q103(List<Employee> EMP){ header("Q103 dept->ages sorted"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.mapping(e->e.age, Collectors.toList()), list->{Collections.sort(list); return list;})))); }
        void q104(List<Employee> EMP){ header("Q104 active count per dept"); System.out.println(EMP.stream().filter(e->e.active).collect(Collectors.groupingBy(e->e.deptId, Collectors.counting()))); }
        void q105(List<Employee> EMP){ header("Q105 dept->set of skills"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.mapping(e->e.skills, Collectors.toList()), list->list.stream().flatMap(Set::stream).collect(Collectors.toSet()))))); }
        void q106(List<Employee> EMP){ header("Q106 partition age >=30"); System.out.println(EMP.stream().collect(Collectors.partitioningBy(e->e.age>=30))); }
        void q107(List<Employee> EMP){ header("Q107 dept->billable vs non-billable counts"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.mapping(e->e.projects, Collectors.toList()), list->list.stream().flatMap(List::stream).collect(Collectors.groupingBy(p->p.billable, Collectors.counting())))))); }
        void q108(List<Employee> EMP){ header("Q108 employees without projects"); System.out.println(EMP.stream().filter(e->e.projects.isEmpty()).map(e->e.name).collect(Collectors.toList())); }
        void q109(List<Employee> EMP){ header("Q109 joined in 2023"); System.out.println(EMP.stream().filter(e->e.joinDate.getYear()==2023).map(e->e.name).collect(Collectors.toList())); }
        void q110(List<Employee> EMP){ header("Q110 highest-paid active engineer"); System.out.println(EMP.stream().filter(e->e.active && e.deptId==10).max(Comparator.comparing(e->e.salary)).orElse(null)); }
        void q111(List<Employee> EMP){ header("Q111 avg age per dept"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.averagingInt(e->e.age)))); }
        void q112(List<Employee> EMP){ header("Q112 median salary overall"); List<BigDecimal> s=EMP.stream().map(e->e.salary).sorted().collect(Collectors.toList()); BigDecimal med=s.size()%2==1?s.get(s.size()/2):s.get(s.size()/2-1).add(s.get(s.size()/2)).divide(new BigDecimal(2),2,RoundingMode.HALF_UP); System.out.println(med);}        
        void q113(List<Employee> EMP){ header("Q113 median salary per dept"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.mapping(e->e.salary, Collectors.toList()), list->{ List<BigDecimal> t=new ArrayList<>(list); t.sort(Comparator.naturalOrder()); return t.size()%2==1?t.get(t.size()/2):t.get(t.size()/2-1).add(t.get(t.size()/2)).divide(new BigDecimal(2),2,RoundingMode.HALF_UP);} )))); }
        void q114(List<Employee> EMP){ header("Q114 percentile 90 salary"); List<BigDecimal> s=EMP.stream().map(e->e.salary).sorted().collect(Collectors.toList()); int idx=Math.min(s.size()-1,(int)Math.ceil(0.9*s.size())-1); System.out.println(s.get(idx)); }
        void q115(List<Employee> EMP){ header("Q115 unique dept names"); System.out.println(EMP.stream().map(e->DEPTS.get(e.deptId).name).collect(Collectors.toSet())); }
        void q116(List<Employee> EMP){ header("Q116 name->vowel count"); System.out.println(EMP.stream().collect(Collectors.toMap(e->e.name, e-> e.name.toLowerCase().chars().filter(c->"aeiou".indexOf(c)>=0).count()))); }
        void q117(List<Employee> EMP){ header("Q117 top-5 most skilled"); System.out.println(EMP.stream().sorted(Comparator.comparingInt((Employee e)->e.skills.size()).reversed()).limit(5).map(e->e.name).collect(Collectors.toList())); }
        void q118(List<Employee> EMP){ header("Q118 with skill SQL"); System.out.println(EMP.stream().filter(e->e.skills.contains("SQL")).map(e->e.name).collect(Collectors.toList())); }
        void q119(List<Employee> EMP){ header("Q119 dept with max payroll"); Map<Integer,BigDecimal> totals=EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.reducing(BigDecimal.ZERO, e->e.salary, BigDecimal::add))); System.out.println(totals.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null)); }
        void q120(List<Employee> EMP){ header("Q120 (employee,project) pairs"); System.out.println(EMP.stream().flatMap(e->e.projects.stream().map(p->e.name+"->"+p.name)).collect(Collectors.toList())); }
        void q121(List<Employee> EMP){ header("Q121 sort by join date asc"); System.out.println(EMP.stream().sorted(Comparator.comparing(e->e.joinDate)).map(e->e.name).collect(Collectors.toList())); }
        void q122(List<Employee> EMP){ header("Q122 sort by join date desc"); System.out.println(EMP.stream().sorted(Comparator.comparing((Employee e)->e.joinDate).reversed()).map(e->e.name).collect(Collectors.toList())); }
        void q123(List<Employee> EMP){ header("Q123 first joined after 2022-01-01"); System.out.println(EMP.stream().filter(e->e.joinDate.isAfter(LocalDate.of(2022,1,1))).findFirst().orElse(null)); }
        void q124(List<Employee> EMP){ header("Q124 any with >3 skills"); System.out.println(EMP.stream().anyMatch(e->e.skills.size()>3)); }
        void q125(List<Employee> EMP){ header("Q125 count billable projects"); System.out.println(EMP.stream().flatMap(e->e.projects.stream()).filter(p->p.billable).count()); }
        void q126(List<Employee> EMP){ header("Q126 avg tenure years per dept"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.averagingLong(e->java.time.temporal.ChronoUnit.YEARS.between(e.joinDate, LocalDate.now()))))); }
        void q127(List<Employee> EMP){ header("Q127 most experienced per dept"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.minBy(Comparator.comparing(e->e.joinDate)), o->o.orElse(null))))); }
        void q128(List<Employee> EMP){ header("Q128 Eng missing Java"); System.out.println(EMP.stream().filter(e->e.deptId==10 && !e.skills.contains("Java")).map(e->e.name).collect(Collectors.toList())); }
        void q129(List<Employee> EMP){ header("Q129 name->annual pay"); System.out.println(EMP.stream().map(e->e.name+":"+ e.salary.multiply(new BigDecimal("12"))).collect(Collectors.toList())); }
        void q130(List<Employee> EMP){ header("Q130 top-3 skills per dept"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.mapping(e->e.skills, Collectors.toList()), list->{ Map<String,Long> f=list.stream().flatMap(Set::stream).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); return f.entrySet().stream().sorted(Map.Entry.<String,Long>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toList()); })))); }
        void q131(List<Employee> EMP){ header("Q131 skill->employees"); System.out.println(EMP.stream().flatMap(e->e.skills.stream().map(s->new AbstractMap.SimpleEntry<>(s,e.name))).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())))); }
        void q132(List<Employee> EMP){ header("Q132 name length stats"); System.out.println(EMP.stream().mapToInt(e->e.name.length()).summaryStatistics()); }
        void q133(){ header("Q133 duplicate ids demo"); System.out.println("Use toMap merge or throw"); }
        void q134(){ header("Q134 merge two lists dedupe by id"); System.out.println("Stream.of(l1,l2).flatMap(List::stream).collect(toMap(id, keep last))"); }
        void q135(List<Employee> EMP){ header("Q135 chunk page3 size2"); List<Employee> s=EMP.stream().sorted(Comparator.comparing(e->e.id)).collect(Collectors.toList()); System.out.println(s.stream().skip((3-1)*2).limit(2).collect(Collectors.toList())); }
        void q136(List<Employee> EMP){ header("Q136 validate salaries > 0"); System.out.println(EMP.stream().allMatch(e->e.salary.compareTo(BigDecimal.ZERO)>0)); }
        void q137(List<Employee> EMP){ header("Q137 inactive by salary desc"); System.out.println(EMP.stream().filter(e->!e.active).sorted(Comparator.comparing((Employee e)->e.salary).reversed()).collect(Collectors.toList())); }
        void q138(List<Employee> EMP){ header("Q138 first5 distinct skills"); System.out.println(EMP.stream().flatMap(e->e.skills.stream()).distinct().sorted().limit(5).collect(Collectors.toList())); }
        void q139(List<Employee> EMP){ header("Q139 count by join year"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.joinDate.getYear(), Collectors.counting()))); }
        void q140(List<Employee> EMP){ header("Q140 csv names per dept sorted"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.mapping(e->e.name, Collectors.toList()), list->{Collections.sort(list); return String.join(",", list);} )))); }
        void q141(List<Employee> EMP){ header("Q141 dept->TreeSet names"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.mapping(e->e.name, Collectors.toCollection(TreeSet::new))))); }
        void q142(List<Employee> EMP){ header("Q142 min age per dept"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.minBy(Comparator.comparingInt(e->e.age))))); }
        void q143(List<Employee> EMP){ header("Q143 employees without any skill"); System.out.println(EMP.stream().filter(e->e.skills.isEmpty()).map(e->e.name).collect(Collectors.toList())); }
        void q144(List<Employee> EMP){ header("Q144 names on non-billable"); System.out.println(EMP.stream().filter(e->e.projects.stream().anyMatch(p->!p.billable)).map(e->e.name).collect(Collectors.toList())); }
        void q145(List<Employee> EMP){ header("Q145 total assignments"); System.out.println(EMP.stream().mapToLong(e->e.projects.size()).sum()); }
        void q146(List<Employee> EMP){ header("Q146 pairs same first letter"); Map<Character,List<Employee>> g=EMP.stream().collect(Collectors.groupingBy(e->e.name.charAt(0))); List<String> res=g.values().stream().flatMap(list->{ List<String> ps=new ArrayList<>(); for(int i=0;i<list.size();i++) for(int j=i+1;j<list.size();j++) ps.add(list.get(i).name+"+"+list.get(j).name); return ps.stream(); }).collect(Collectors.toList()); System.out.println(res); }
        void q147(List<Employee> EMP){ header("Q147 salary then name"); System.out.println(EMP.stream().sorted(Comparator.comparing((Employee e)->e.salary).thenComparing(e->e.name)).collect(Collectors.toList())); }
        void q148(List<Employee> EMP){ header("Q148 cumulative salaries (loop)"); List<BigDecimal> s=EMP.stream().map(e->e.salary).collect(Collectors.toList()); List<BigDecimal> cum=new ArrayList<>(); BigDecimal acc=BigDecimal.ZERO; for(BigDecimal x:s){ acc=acc.add(x); cum.add(acc);} System.out.println(cum);}        
        void q149(List<Employee> EMP){ header("Q149 parallel read-only"); System.out.println(EMP.parallelStream().map(e->e.name.toUpperCase()).collect(Collectors.toList())); }
        void q150(){ header("Q150 avoid shared state"); System.out.println("Prefer collectors over external mutation"); }
        void q151(List<Employee> EMP){ header("Q151 collectingAndThen"); System.out.println(EMP.stream().map(e->e.name).collect(Collectors.collectingAndThen(Collectors.toList(), list->{Collections.sort(list); return list;}))); }
        void q152(List<Employee> EMP){ header("Q152 groupingBy+mapping"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.mapping(e->e.name, Collectors.toList())))); }
        void q153(List<Employee> EMP){ header("Q153 reducing identity/mapper/combiner"); System.out.println(EMP.stream().collect(Collectors.reducing(BigDecimal.ZERO, e->e.salary, BigDecimal::add))); }
        void q154(List<Employee> EMP){ header("Q154 summarizing vs averaging"); DoubleSummaryStatistics stats=EMP.stream().collect(Collectors.summarizingDouble(e->e.salary.doubleValue())); double avg=EMP.stream().collect(Collectors.averagingDouble(e->e.salary.doubleValue())); System.out.println(stats+" avg="+avg);}        
        void q155(List<Employee> EMP){ header("Q155 immutable map copy"); System.out.println(Collections.unmodifiableMap(EMP.stream().collect(Collectors.toMap(e->e.id,e->e.name)))); }
        void q156(List<Employee> EMP){ header("Q156 LinkedHashMap order"); System.out.println(EMP.stream().collect(Collectors.toMap(e->e.id,e->e.name,(a,b)->a,LinkedHashMap::new))); }
        void q157(){ header("Q157 String->int[]"); System.out.println(java.util.Arrays.toString(Stream.of("1","2","3").mapToInt(Integer::parseInt).toArray())); }
        void q158(List<Employee> EMP){ header("Q158 check sorted by name"); List<String> n=EMP.stream().map(e->e.name).collect(Collectors.toList()); boolean ok=IntStream.range(0,n.size()-1).allMatch(i->n.get(i).compareTo(n.get(i+1))<=0); System.out.println(ok);}        
        void q159(){ header("Q159 merge two maps"); Map<Integer,String>a=new HashMap<>();a.put(1,"A");Map<Integer,String>b=new HashMap<>();b.put(1,"A1");b.put(2,"B"); System.out.println(Stream.of(a,b).flatMap(m->m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x,y)->y))); }
        void q160(List<Employee> EMP){ header("Q160 distinct-by-key util"); Set<String> seen=ConcurrentHashMap.newKeySet(); System.out.println(EMP.stream().filter(e->seen.add(e.name)).collect(Collectors.toList())); }
        void q161(List<Employee> EMP){ header("Q161 throw on duplicate ids"); System.out.println(EMP.stream().collect(Collectors.toMap(e->e.id,e->e,(x,y)->{throw new IllegalStateException("dup:"+x.id);})).size()); }
        void q162(List<Employee> EMP){ header("Q162 partition >1 project"); System.out.println(EMP.stream().collect(Collectors.partitioningBy(e->e.projects.size()>1))); }
        void q163(List<Employee> EMP){ header("Q163 avg project count by dept"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.averagingInt(e->e.projects.size())))); }
        void q164(List<Employee> EMP){ header("Q164 names in top10% salary"); List<Employee> s=EMP.stream().sorted(Comparator.comparing((Employee e)->e.salary).reversed()).collect(Collectors.toList()); int k=Math.max(1,(int)Math.ceil(s.size()*0.10)); System.out.println(s.stream().limit(k).map(e->e.name).collect(Collectors.toList())); }
        void q165(List<Employee> EMP){ header("Q165 normalize names"); System.out.println(EMP.stream().map(e->e.name.trim()).map(s->s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase()).collect(Collectors.toList())); }
        void q166(){ header("Q166 replace null skills"); List<Set<String>> L=Arrays.asList(new HashSet<>(Arrays.asList("A")), null, new HashSet<>()); System.out.println(L.stream().map(s->s==null?Collections.emptySet():s).collect(Collectors.toList())); }
        void q167(List<Employee> EMP){ header("Q167 join quarter buckets"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.joinDate.getYear()+"-Q"+((e.joinDate.getMonthValue()-1)/3+1), Collectors.counting()))); }
        void q168(List<Employee> EMP){ header("Q168 group by city"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->DEPTS.get(e.deptId).location, Collectors.mapping(e->e.name, Collectors.toList())))); }
        void q169(List<Employee> EMP){ header("Q169 city payroll"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->DEPTS.get(e.deptId).location, Collectors.reducing(BigDecimal.ZERO, e->e.salary, BigDecimal::add)))); }
        void q170(){ header("Q170 overlapping skills pairs note"); System.out.println("Use double loop to intersect skill sets"); }
        void q171(List<Employee> EMP){ header("Q171 lookup join month"); java.time.format.DateTimeFormatter f=java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.joinDate.format(f), Collectors.mapping(e->e.name, Collectors.toList())))); }
        void q172(List<Employee> EMP){ header("Q172 avg age of active"); System.out.println(EMP.stream().filter(e->e.active).mapToInt(e->e.age).average().orElse(0)); }
        void q173(List<Employee> EMP){ header("Q173 names not in Engineering"); System.out.println(EMP.stream().filter(e->e.deptId!=10).map(e->e.name).collect(Collectors.toList())); }
        void q174(List<Employee> EMP){ header("Q174 highest salary in Product"); System.out.println(EMP.stream().filter(e->e.deptId==20).max(Comparator.comparing(e->e.salary)).orElse(null)); }
        void q175(List<Employee> EMP){ header("Q175 payroll for Sales"); System.out.println(EMP.stream().filter(e->e.deptId==30).map(e->e.salary).reduce(BigDecimal.ZERO, BigDecimal::add)); }
        void q176(List<Employee> EMP){ header("Q176 sort by skills count desc"); System.out.println(EMP.stream().sorted(Comparator.comparingInt((Employee e)->e.skills.size()).reversed()).collect(Collectors.toList())); }
        void q177(){ header("Q177 all project start dates"); System.out.println(DataFactory.allProjects().stream().map(p->p.startDate).collect(Collectors.toList())); }
        void q178(List<Employee> EMP){ header("Q178 count names starting vowel"); System.out.println(EMP.stream().filter(e->"AEIOUaeiou".indexOf(e.name.charAt(0))>=0).count()); }
        void q179(List<Employee> EMP){ header("Q179 partition by age 30"); System.out.println(EMP.stream().collect(Collectors.partitioningBy(e->e.age>=30))); }
        void q180(List<Employee> EMP){ header("Q180 hash buckets (3)"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->Math.floorMod(e.name.hashCode(),3), Collectors.mapping(e->e.name, Collectors.toList())))); }
        void q181(List<Employee> EMP){ header("Q181 first active in HR"); System.out.println(EMP.stream().filter(e->e.active && e.deptId==40).findFirst().orElse(null)); }
        void q182(List<Employee> EMP){ header("Q182 any zero projects?"); System.out.println(EMP.stream().anyMatch(e->e.projects.isEmpty())); }
        void q183(List<Employee> EMP){ header("Q183 exactly two projects"); System.out.println(EMP.stream().filter(e->e.projects.size()==2).map(e->e.name).collect(Collectors.toList())); }
        void q184(List<Employee> EMP){ header("Q184 dept->salaries sorted desc"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.mapping(e->e.salary, Collectors.toList()), list->{list.sort(Comparator.reverseOrder()); return list;})))); }
        void q185(List<Employee> EMP){ header("Q185 CSV of all unique skills"); System.out.println(EMP.stream().flatMap(e->e.skills.stream()).distinct().sorted().collect(Collectors.joining(","))); }
        void q186(List<Employee> EMP){ header("Q186 dept->count of distinct skills"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.mapping(e->e.skills, Collectors.toList()), list->list.stream().flatMap(Set::stream).distinct().count())))); }
        void q187(List<Employee> EMP){ header("Q187 earliest joiner per dept"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.minBy(Comparator.comparing(e->e.joinDate)), o->o.orElse(null))))); }
        void q188(List<Employee> EMP){ header("Q188 latest joiner per dept"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(e->e.joinDate)), o->o.orElse(null))))); }
        void q189(List<Employee> EMP){ header("Q189 avg tenure of active"); System.out.println(EMP.stream().filter(e->e.active).mapToLong(e->java.time.temporal.ChronoUnit.YEARS.between(e.joinDate, LocalDate.now())).average().orElse(0)); }
        void q190(List<Employee> EMP){ header("Q190 validate positive ages"); System.out.println(EMP.stream().allMatch(e->e.age>0)); }
        void q191(List<Employee> EMP){ header("Q191 dense rank by salary"); List<Employee> s=EMP.stream().sorted(Comparator.comparing((Employee e)->e.salary).reversed()).collect(Collectors.toList()); Map<BigDecimal,Integer> rank=new LinkedHashMap<>(); int r=0; BigDecimal prev=null; for(Employee e:s){ if(prev==null||e.salary.compareTo(prev)!=0){r++; prev=e.salary;} rank.putIfAbsent(e.salary,r);} System.out.println(s.stream().map(e->e.name+" rank="+rank.get(e.salary)).collect(Collectors.toList())); }
        void q192(List<Employee> EMP){ header("Q192 salary z-scores"); List<Double> s=EMP.stream().map(e->e.salary.doubleValue()).collect(Collectors.toList()); double mean=s.stream().mapToDouble(x->x).average().orElse(0); double std=Math.sqrt(s.stream().mapToDouble(x->(x-mean)*(x-mean)).average().orElse(1)); System.out.println(s.stream().map(x->(x-mean)/(std==0?1:std)).map(z->String.format(Locale.US,"%.2f",z)).collect(Collectors.toList())); }
        void q193(){ header("Q193 distinct projects"); System.out.println(DataFactory.allProjects().stream().map(p->p.name).distinct().collect(Collectors.toList())); }
        void q194(List<Employee> EMP){ header("Q194 TreeSet of names"); System.out.println(EMP.stream().map(e->e.name).collect(Collectors.toCollection(TreeSet::new))); }
        void q195(List<Employee> EMP){ header("Q195 5% raise Engineering"); System.out.println(EMP.stream().filter(e->e.deptId==10).collect(Collectors.toMap(e->e.name, e->e.salary.multiply(new BigDecimal("1.05")).setScale(2,RoundingMode.HALF_UP)))); }
        void q196(List<Employee> EMP){ header("Q196 annual payroll by dept"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.reducing(BigDecimal.ZERO, e->e.salary.multiply(new BigDecimal("12")), BigDecimal::add)))); }
        void q197(List<Employee> EMP){ header("Q197 project with most employees"); Map<String,Long> c=EMP.stream().flatMap(e->e.projects.stream().map(p->p.name)).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); System.out.println(c.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null)); }
        void q198(List<Employee> EMP){ header("Q198 employees per project (including zeros)"); Map<String,Long> c=EMP.stream().flatMap(e->e.projects.stream().map(p->p.name)).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); Map<String,Long> all=DataFactory.allProjects().stream().collect(Collectors.toMap(p->p.name, p->c.getOrDefault(p.name,0L))); System.out.println(all); }
        void q199(List<Employee> EMP){ header("Q199 names on 'CardAuth'"); System.out.println(EMP.stream().filter(e->e.projects.stream().anyMatch(p->p.name.equals("CardAuth"))).map(e->e.name).collect(Collectors.toList())); }
        void q200(List<Employee> EMP){ header("Q200 dept->names by tenure desc"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.toList(), list->list.stream().sorted(Comparator.comparing((Employee e)->e.joinDate)).map(e->e.name).collect(Collectors.toList()))))); }

        void q201(List<Employee> EMP){ header("Q201 harmonic mean salary"); List<Double>s=EMP.stream().map(e->e.salary.doubleValue()).collect(Collectors.toList()); double denom=s.stream().mapToDouble(x->1.0/x).sum(); double hm=s.isEmpty()?0:s.size()/denom; System.out.println(String.format(Locale.US,"%.2f",hm)); }
        void q202(List<Employee> EMP){ header("Q202 salary quantiles"); List<BigDecimal>s=EMP.stream().map(e->e.salary).sorted().collect(Collectors.toList()); int n=s.size(); java.util.function.IntFunction<BigDecimal> at=i->s.get(Math.min(n-1,Math.max(0,i))); System.out.println("q25="+at.apply((int)Math.ceil(0.25*n)-1)+", q50="+at.apply((int)Math.ceil(0.5*n)-1)+", q75="+at.apply((int)Math.ceil(0.75*n)-1)); }
        void q203(List<Employee> EMP){ header("Q203 sliding window avg note"); System.out.println("Prefer loops for sliding windows"); }
        void q204(List<Employee> EMP){ header("Q204 k-most common skills (3)"); Map<String,Long> f=EMP.stream().flatMap(e->e.skills.stream()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); System.out.println(f.entrySet().stream().sorted(Map.Entry.<String,Long>comparingByValue().reversed()).limit(3).collect(Collectors.toList())); }
        void q205(List<Employee> EMP){ header("Q205 top-2 skills per dept"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.mapping(e->e.skills, Collectors.toList()), list->{ Map<String,Long> f=list.stream().flatMap(Set::stream).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); return f.entrySet().stream().sorted(Map.Entry.<String,Long>comparingByValue().reversed()).limit(2).map(Map.Entry::getKey).collect(Collectors.toList()); })))); }
        void q206(List<Employee> EMP){ header("Q206 min-max normalize salaries"); List<Double>s=EMP.stream().map(e->e.salary.doubleValue()).collect(Collectors.toList()); double min=s.stream().mapToDouble(x->x).min().orElse(0), max=s.stream().mapToDouble(x->x).max().orElse(1); System.out.println(s.stream().map(x->max==min?0:(x-min)/(max-min)).collect(Collectors.toList())); }
        void q207(List<Employee> EMP){ header("Q207 bipartite edges"); System.out.println(EMP.stream().flatMap(e->e.projects.stream().map(p->e.name+"->"+p.name)).collect(Collectors.toList())); }
        void q208(List<Employee> EMP){ header("Q208 only non-billable employees"); System.out.println(EMP.stream().filter(e->!e.projects.isEmpty() && e.projects.stream().allMatch(p->!p.billable)).map(e->e.name).collect(Collectors.toList())); }
        void q209(List<Employee> EMP){ header("Q209 validate unique names"); System.out.println(EMP.stream().map(e->e.name).distinct().count()==EMP.size()); }
        void q210(List<Employee> EMP){ header("Q210 simple trie note"); System.out.println("Build via loops; streams feed inserts"); }
        void q211(List<Employee> EMP){ header("Q211 Jaccard similarity of skills"); List<Employee>L=new ArrayList<>(EMP); for(int i=0;i<L.size();i++) for(int j=i+1;j<L.size();j++){ Set<String>a=L.get(i).skills,b=L.get(j).skills; Set<String> inter=new HashSet<>(a); inter.retainAll(b); Set<String> uni=new HashSet<>(a); uni.addAll(b); double J=uni.isEmpty()?0:(double)inter.size()/uni.size(); if(J>0) System.out.println(L.get(i).name+"-"+L.get(j).name+": "+String.format(Locale.US,"%.2f",J)); } }
        void q212(List<Employee> EMP){ header("Q212 outliers by MAD"); List<Double>s=EMP.stream().map(e->e.salary.doubleValue()).sorted().collect(Collectors.toList()); double m=s.get(s.size()/2); List<Double>d=s.stream().map(x->Math.abs(x-m)).sorted().collect(Collectors.toList()); double mad=d.get(d.size()/2); double thr=m+3*mad; System.out.println(">= "+thr); System.out.println(EMP.stream().filter(e->e.salary.doubleValue()>=thr).map(e->e.name).collect(Collectors.toList())); }
        void q213(List<Employee> EMP){ header("Q213 age histogram bins of 5"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->(e.age/5)*5, TreeMap::new, Collectors.counting()))); }
        void q214(){ header("Q214 stream from rows simulate"); System.out.println("Stream<Map<String,Object>> rows ..."); }
        void q215() throws IOException { header("Q215 dir file sizes"); Path dir=Files.createTempDirectory("d"); Files.write(dir.resolve("a.txt"), Arrays.asList("x")); Files.write(dir.resolve("b.txt"), Arrays.asList("y")); try(Stream<Path> st=Files.list(dir)){ System.out.println(st.collect(Collectors.toMap(p->p.getFileName().toString(), p->{ try{return Files.size(p);}catch(Exception ex){return -1L;} }))); } Files.walk(dir).sorted(Comparator.reverseOrder()).forEach(p->{ try{Files.deleteIfExists(p);}catch(Exception ig){} }); }
        void q216(List<Employee> EMP){ header("Q216 parallel calc safely"); System.out.println(EMP.parallelStream().map(e->Math.abs(e.name.hashCode())).collect(Collectors.toList()).size()); }
        void q217(List<Employee> EMP) throws IOException { header("Q217 write CSV per dept"); Path dir=Files.createTempDirectory("dept"); Map<Integer,List<String>> m=EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.mapping(e->e.id+","+e.name+","+e.salary, Collectors.toList()))); for(Map.Entry<Integer,List<String>> en: m.entrySet()) Files.write(dir.resolve("dept_"+en.getKey()+".csv"), en.getValue()); System.out.println("Wrote to "+dir); }
        void q218(List<Employee> EMP){ header("Q218 city->highest paid active"); System.out.println(EMP.stream().filter(e->e.active).collect(Collectors.groupingBy(e->DEPTS.get(e.deptId).location, Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(e->e.salary)), o->o.map(x->x.name).orElse(null))))); }
        void q219(){ header("Q219 time-to-hire note"); System.out.println("Parse HR events and average per month"); }
        void q220(List<Employee> EMP){ header("Q220 join employees with departments"); System.out.println(EMP.stream().map(e->e.name+"|"+DEPTS.get(e.deptId).name).collect(Collectors.toList())); }
        void q221(List<Employee> EMP){ header("Q221 left join employees->projects"); System.out.println(EMP.stream().flatMap(e->e.projects.isEmpty()? Stream.of(e.name+"|<none>"): e.projects.stream().map(p->e.name+"|"+p.name)).collect(Collectors.toList())); }
        void q222(List<Employee> EMP){ header("Q222 dedup by id keep most recent join"); System.out.println(EMP.stream().collect(Collectors.toMap(e->e.id, e->e, (a,b)-> a.joinDate.isAfter(b.joinDate)?a:b)).size()); }
        void q223(List<Employee> EMP){ header("Q223 distinct skills preserve insertion"); System.out.println(EMP.stream().flatMap(e->e.skills.stream()).collect(Collectors.toCollection(LinkedHashSet::new))); }
        void q224(){ header("Q224 partition projects active vs finished"); System.out.println(DataFactory.allProjects().stream().collect(Collectors.partitioningBy(p->p.endDate==null))); }
        void q225(){ header("Q225 switched departments note"); System.out.println("Need history data"); }
        void q226(){ header("Q226 benchmarking note"); System.out.println("Use System.nanoTime around stream vs loop"); }
        void q227(List<Employee> EMP){ header("Q227 memoize mapping"); Map<String,Integer> cache=new ConcurrentHashMap<>(); Function<String,Integer> f=s->cache.computeIfAbsent(s, k->{ try{Thread.sleep(5);}catch(Exception ig){} return k.hashCode(); }); System.out.println(EMP.stream().map(e->f.apply(e.name)).collect(Collectors.toList()).size()); }
        void q228(List<Employee> EMP){ header("Q228 adjacency >=2 shared skills"); List<Employee>L=new ArrayList<>(EMP); List<String> edges=new ArrayList<>(); for(int i=0;i<L.size();i++) for(int j=i+1;j<L.size();j++){ Set<String>a=L.get(i).skills,b=L.get(j).skills; Set<String> inter=new HashSet<>(a); inter.retainAll(b); if(inter.size()>=2) edges.add(L.get(i).name+"~"+L.get(j).name+" "+inter);} System.out.println(edges); }
        void q229(List<Employee> EMP){ header("Q229 JSON note"); System.out.println("Use Jackson/Gson externally; streams build DTOs"); }
        void q230(List<Employee> EMP){ header("Q230 dynamic predicates"); Predicate<Employee> p=e->true; boolean onlyActive=true; Integer minAge=30; String skill="Java"; if(onlyActive)p=p.and(e->e.active); if(minAge!=null)p=p.and(e->e.age>=minAge); if(skill!=null)p=p.and(e->e.skills.contains(skill)); System.out.println(EMP.stream().filter(p).map(e->e.name).collect(Collectors.toList())); }
        void q231(List<Employee> EMP){ header("Q231 dynamic comparator"); Comparator<Employee> c=Comparator.comparing((Employee e)->e.deptId).thenComparing(e->e.salary).thenComparing(e->e.name); System.out.println(EMP.stream().sorted(c).collect(Collectors.toList())); }
        void q232(){ header("Q232 Luhn-like note"); System.out.println("Implement check with digit stream if needed"); }
        void q233(List<Employee> EMP){ header("Q233 salary growth note"); System.out.println("Needs salary history"); }
        void q234(List<Employee> EMP){ header("Q234 emulate teeing (two-pass)"); double avg=EMP.stream().mapToDouble(e->e.salary.doubleValue()).average().orElse(0); double std=Math.sqrt(EMP.stream().mapToDouble(e->e.salary.doubleValue()).map(x->(x-avg)*(x-avg)).average().orElse(0)); System.out.println("avg="+avg+", std="+std); }
        void q235(){ header("Q235 custom Spliterator note"); System.out.println("Beyond short demo"); }
        void q236(List<Employee> EMP){ header("Q236 groupingByConcurrent"); System.out.println(EMP.parallelStream().collect(Collectors.groupingByConcurrent(e->e.deptId, Collectors.counting()))); }
        void q237(){ header("Q237 stable pagination note"); System.out.println("Snapshot first, then paginate"); }
        void q238(List<Employee> EMP){ header("Q238 mapMulti emulation"); System.out.println(EMP.stream().flatMap(e->e.projects.isEmpty()? Stream.of(e.name+"|<none>"): e.projects.stream().map(p->e.name+"|"+p.name)).collect(Collectors.toList())); }
        void q239(){ header("Q239 one-shot streams note"); System.out.println("Collect to list if you need to traverse twice"); }
        void q240(){ header("Q240 reactive note"); System.out.println("Use Reactor/Flow outside JDK8"); }
        void q241(){ header("Q241 not parallel for small data"); System.out.println("Parallel overhead can dominate"); }
        void q242(){ header("Q242 boxing/unboxing cost"); System.out.println("Prefer primitive streams"); }
        void q243(List<Employee> EMP){ header("Q243 short-circuit findFirst"); System.out.println(EMP.stream().filter(e->e.salary.compareTo(new BigDecimal("13000"))>0).findFirst().orElse(null)); }
        void q244(List<Employee> EMP){ header("Q244 Optional wrapper for exceptions"); Function<String,Optional<Integer>> safe=s->{ try{return Optional.of(Integer.parseInt(s));}catch(Exception ex){return Optional.empty();}}; System.out.println(Stream.of("10","x","20").map(safe).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList())); }
        void q245(){ header("Q245 retry note"); System.out.println("Wrap with try/catch, avoid complex retries inside streams"); }
        void q246(List<Employee> EMP){ header("Q246 group by (dept,year)"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId+"-"+e.joinDate.getYear(), Collectors.mapping(e->e.name, Collectors.toList())))); }
        void q247(){ header("Q247 flatMap over map values"); Map<String,List<Integer>>m=new HashMap<>(); m.put("A",Arrays.asList(1,2)); m.put("B",Arrays.asList(3)); System.out.println(m.entrySet().stream().flatMap(en->en.getValue().stream()).collect(Collectors.toList())); }
        void q248(List<Employee> EMP){ header("Q248 invert skill->names"); System.out.println(EMP.stream().flatMap(e->e.skills.stream().map(s->new AbstractMap.SimpleEntry<>(s,e.name))).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())))); }
        void q249(){ header("Q249 rolling headcount note"); System.out.println("Build monthly buckets and count active"); }
        void q250(List<Employee> EMP){ header("Q250 partition on-call eligible"); System.out.println(EMP.stream().collect(Collectors.partitioningBy(e->e.skills.contains("Java") && e.age>=30))); }
        void q251(List<Employee> EMP){ header("Q251 KPIs"); long active=EMP.stream().filter(e->e.active).count(); BigDecimal payroll=EMP.stream().map(e->e.salary).reduce(BigDecimal.ZERO, BigDecimal::add); double avgAge=EMP.stream().mapToInt(e->e.age).average().orElse(0); System.out.println("active="+active+", payroll="+payroll+", avgAge="+avgAge); }
        void q252(){ header("Q252 circular org chart note"); System.out.println("Graph traversal beyond streams"); }
        void q253(){ header("Q253 factorial via reduce"); int n=5; System.out.println(IntStream.rangeClosed(1,n).reduce(1,(a,b)->a*b)); }
        void q254(List<Employee> EMP){ header("Q254 longest name length"); System.out.println(EMP.stream().mapToInt(e->e.name.length()).max().orElse(0)); }
        void q255(List<Employee> EMP){ header("Q255 letter counts across names"); System.out.println(EMP.stream().flatMap(e->e.name.chars().mapToObj(c->(char)c)).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))); }
        void q256(){ header("Q256 parse CSV note"); System.out.println("Use Files.lines + split + map to Employee"); }
        void q257(List<Employee> EMP){ header("Q257 toCollection LinkedList"); System.out.println(EMP.stream().map(e->e.name).collect(Collectors.toCollection(java.util.LinkedList::new))); }
        void q258(){ header("Q258 weighted sum"); List<Integer> xs=Arrays.asList(1,2,3), ws=Arrays.asList(2,3,4); System.out.println(IntStream.range(0,xs.size()).map(i->xs.get(i)*ws.get(i)).sum()); }
        void q259(List<Employee> EMP){ header("Q259 skills superset {Java,SQL}"); Set<String> req=new HashSet<>(Arrays.asList("Java","SQL")); System.out.println(EMP.stream().filter(e->e.skills.containsAll(req)).map(e->e.name).collect(Collectors.toList())); }
        void q260(List<Employee> EMP){ header("Q260 pairs on same project"); List<String> rows=new ArrayList<>(); List<Employee>L=new ArrayList<>(EMP); for(int i=0;i<L.size();i++) for(int j=i+1;j<L.size();j++){ Set<String>a=L.get(i).projects.stream().map(p->p.name).collect(Collectors.toSet()); Set<String>b=L.get(j).projects.stream().map(p->p.name).collect(Collectors.toSet()); Set<String> inter=new HashSet<>(a); inter.retainAll(b); if(!inter.isEmpty()) rows.add(L.get(i).name+" & "+L.get(j).name+" -> "+inter);} System.out.println(rows);}        
        void q261(List<Employee> EMP){ header("Q261 project->distinct departments"); System.out.println(EMP.stream().flatMap(e->e.projects.stream().map(p->new AbstractMap.SimpleEntry<>(p.name,e.deptId))).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toSet())))); }
        void q262(List<Employee> EMP){ header("Q262 dept->projects covered"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.mapping(e->e.projects, Collectors.toList()), list->list.stream().flatMap(List::stream).map(p->p.name).collect(Collectors.toSet()))))); }
        void q263(List<Employee> EMP){ header("Q263 total billable days approx"); System.out.println(DataFactory.allProjects().stream().filter(p->p.billable).mapToLong(p->java.time.temporal.ChronoUnit.DAYS.between(p.startDate, p.endDate==null?LocalDate.now():p.endDate)).sum()); }
        void q264(){ header("Q264 idle last 90 days note"); System.out.println("Need assignment dates timeline"); }
        void q265(List<Employee> EMP){ header("Q265 heatmap dept vs skill freq"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.mapping(e->e.skills, Collectors.toList()), list->list.stream().flatMap(Set::stream).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())))))); }
        void q266(){ header("Q266 cohort retention note"); System.out.println("Needs exit dates"); }
        void q267(){ header("Q267 org chart prep note"); System.out.println("Manager->reports data needed"); }
        void q268(List<Employee> EMP){ header("Q268 min/max via reduce"); Employee min=EMP.stream().reduce((a,b)->a.age<=b.age?a:b).orElse(null); Employee max=EMP.stream().reduce((a,b)->a.age>=b.age?a:b).orElse(null); System.out.println("min="+min+", max="+max); }
        void q269(){ header("Q269 stream reuse error"); Stream<String>s=Stream.of("a","b"); s.count(); try{s.count();}catch(IllegalStateException ex){System.out.println("Cannot reuse: "+ex.getMessage());} }
        void q270(List<Employee> EMP){ header("Q270 partitioningBy+mapping"); System.out.println(EMP.stream().collect(Collectors.partitioningBy(e->e.active, Collectors.mapping(e->e.name, Collectors.toList())))); }
        void q271(List<Employee> EMP){ header("Q271 groupingBy -> top2 names by salary"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.toList(), list->list.stream().sorted(Comparator.comparing((Employee e)->e.salary).reversed()).limit(2).map(e->e.name).collect(Collectors.toList()))))); }
        void q272(List<Employee> EMP){ header("Q272 first letter -> employees"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.name.charAt(0)))); }
        void q273(List<Employee> EMP){ header("Q273 salary - dept avg"); Map<Integer,Double> avg=EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.averagingDouble(e->e.salary.doubleValue()))); System.out.println(EMP.stream().map(e->e.name+":"+String.format(Locale.US,"%.2f",(e.salary.doubleValue()-avg.get(e.deptId)))).collect(Collectors.toList())); }
        void q274(List<Employee> EMP){ header("Q274 list->multimap (dept->employees)"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId))); }
        void q275(List<Employee> EMP){ header("Q275 multimap->flat list"); Map<Integer,List<Employee>> m=EMP.stream().collect(Collectors.groupingBy(e->e.deptId)); System.out.println(m.values().stream().flatMap(List::stream).collect(Collectors.toList()).size()); }
        void q276(){ header("Q276 merge duplicates aggregate skills note"); System.out.println("toMap(id, e, merge union skills)"); }
        void q277(){ header("Q277 map vs flatMap"); System.out.println("map maps 1->1, flatMap 1->many"); }
        void q278(List<Employee> EMP){ header("Q278 reduce vs collector"); BigDecimal r=EMP.stream().map(e->e.salary).reduce(BigDecimal.ZERO, BigDecimal::add); BigDecimal c=EMP.stream().collect(Collectors.reducing(BigDecimal.ZERO, e->e.salary, BigDecimal::add)); System.out.println("reduce="+r+", collecting="+c); }
        void q279(){ header("Q279 why BigDecimal"); System.out.println("Precision for money"); }
        void q280(){ header("Q280 nullsFirst/nullsLast"); List<String>L=Arrays.asList("Bob",null,"Alice"); System.out.println(L.stream().sorted(Comparator.nullsFirst(Comparator.naturalOrder())).collect(Collectors.toList())); System.out.println(L.stream().sorted(Comparator.nullsLast(Comparator.naturalOrder())).collect(Collectors.toList())); }
        void q281(List<Employee> EMP){ header("Q281 predicate combinators"); Predicate<Employee> p=e->e.active; p=p.and(e->e.age>=30).or(e->e.skills.contains("Leadership")); System.out.println(EMP.stream().filter(p).map(e->e.name).collect(Collectors.toList())); }
        void q282(List<Employee> EMP){ header("Q282 precompute before grouping"); System.out.println(EMP.stream().map(e->new AbstractMap.SimpleEntry<>(e.deptId, e.name.toUpperCase())).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())))); }
        void q283(List<Employee> EMP){ header("Q283 simulate grouping+mapping two-pass"); Map<Integer,List<Employee>> g=EMP.stream().collect(Collectors.groupingBy(e->e.deptId)); System.out.println(g.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, en->en.getValue().stream().map(e->e.name).collect(Collectors.toList())))); }
        void q284(List<Employee> EMP){ header("Q284 method ref toString"); System.out.println(EMP.stream().map(Employee::toString).collect(Collectors.toList())); }
        void q285(List<Employee> EMP){ header("Q285 id->salary range"); Function<BigDecimal,String> b=s->s.doubleValue()<9000?"LOW":s.doubleValue()<12000?"MID":"HIGH"; System.out.println(EMP.stream().collect(Collectors.toMap(e->e.id, e->b.apply(e.salary)))); }
        void q286(){ header("Q286 merge two sorted lists note"); System.out.println("Use two-pointer approach"); }
        void q287(List<Employee> EMP){ header("Q287 age outliers IQR"); List<Integer>a=EMP.stream().map(e->e.age).sorted().collect(Collectors.toList()); int n=a.size(); int q1=a.get(n/4), q3=a.get(3*n/4); int iqr=q3-q1; int thr=q3+(int)(1.5*iqr); System.out.println(">="+thr); System.out.println(EMP.stream().filter(e->e.age>=thr).map(e->e.name).collect(Collectors.toList())); }
        void q288(List<Employee> EMP){ header("Q288 skill->dept counts"); System.out.println(EMP.stream().flatMap(e->e.skills.stream().map(s->new AbstractMap.SimpleEntry<>(s,e.deptId))).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.groupingBy(Map.Entry::getValue, Collectors.counting())))); }
        void q289(List<Employee> EMP){ header("Q289 dept median ignoring outliers"); System.out.println(EMP.stream().collect(Collectors.groupingBy(e->e.deptId, Collectors.collectingAndThen(Collectors.mapping(e->e.salary, Collectors.toList()), list->{ List<BigDecimal>s=new ArrayList<>(list); s.sort(Comparator.naturalOrder()); if(s.size()>2){ int cut=Math.max(1,s.size()/10); s=s.subList(cut, s.size()-cut);} return s.get(s.size()/2);} )))); }
        void q290(List<Employee> EMP){ header("Q290 Gini coefficient"); List<Double>s=EMP.stream().map(e->e.salary.doubleValue()).sorted().collect(Collectors.toList()); double mean=s.stream().mapToDouble(x->x).average().orElse(1); double sum=0; for(int i=0;i<s.size();i++) for(int j=0;j<s.size();j++) sum+=Math.abs(s.get(i)-s.get(j)); double g=sum/(2*s.size()*s.size()*mean); System.out.println(String.format(Locale.US,"%.3f",g)); }
        void q291(List<Employee> EMP){ header("Q291 palindromic names"); System.out.println(EMP.stream().map(e->e.name).filter(n->new StringBuilder(n).reverse().toString().equalsIgnoreCase(n)).collect(Collectors.toList())); }
        void q292(List<Employee> EMP){ header("Q292 random sample 3"); List<Employee>L=new ArrayList<>(EMP); Collections.shuffle(L); System.out.println(L.stream().limit(3).collect(Collectors.toList())); }
        void q293(List<Employee> EMP){ header("Q293 shuffle employees"); List<Employee>L=new ArrayList<>(EMP); Collections.shuffle(L); System.out.println(L); }
        void q294(List<Employee> EMP){ header("Q294 dropWhile salary < 10000 (emulated)"); List<Employee>s=EMP.stream().sorted(Comparator.comparing(e->e.salary)).collect(Collectors.toList()); int i=0; while(i<s.size() && s.get(i).salary.compareTo(new BigDecimal("10000"))<0) i++; System.out.println(s.subList(i,s.size())); }
        void q295(List<Employee> EMP){ header("Q295 takeWhile salary < 12000 (emulated)"); List<Employee>s=EMP.stream().sorted(Comparator.comparing(e->e.salary)).collect(Collectors.toList()); int i=0; while(i<s.size() && s.get(i).salary.compareTo(new BigDecimal("12000"))<0) i++; System.out.println(s.subList(0,i)); }
        void q296(List<Employee> EMP){ header("Q296 time pipeline"); long t1=System.nanoTime(); long c=EMP.stream().filter(e->e.active).count(); long t2=System.nanoTime(); System.out.println("count="+c+" ns="+(t2-t1)); }
        void q297(List<Employee> EMP){ header("Q297 presized collection"); ArrayList<String> list=new ArrayList<>(EMP.size()); list.addAll(EMP.stream().map(e->e.name).collect(Collectors.toList())); System.out.println(list.size()); }
        void q298(){ header("Q298 terminal op one-shot"); System.out.println("A stream cannot be reused after terminal op"); }
        void q299(List<Employee> EMP){ header("Q299 read-only vs copy"); List<Employee> ro=Collections.unmodifiableList(EMP); List<Employee> copy=new ArrayList<>(EMP); System.out.println("ro="+ro.size()+", copy="+copy.size()); }
        void q300(){ header("Q300 tiny Stream-like API note"); System.out.println("Educational; out of scope here"); }
    }
}
