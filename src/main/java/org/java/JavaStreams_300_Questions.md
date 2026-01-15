# Java Streams — 300 Questions from Basic to Expert (with Employee examples)

> Use alongside `EmployeeStreamPlaybook.java` (Q1–Q90 runnable demos). Questions beyond Q90 reference patterns you can solve by composing the same building blocks.

---

## How to use
- Open `EmployeeStreamPlaybook.java` and run it. For each Q#, read the **layman comments** above the method.
- This document lists **300 practice questions** with brief hints, grouped by difficulty.
- Many answers are **variations** of the demos. Where applicable, we reference demo numbers (e.g., "see Q28").

---

## BASIC (Q1–Q100)

1. Print all employee names. *(See code Q1)*
2. Filter only active employees. *(Q2)*
3. Get list of employee names. *(Q3)*
4. Compute total monthly payroll as BigDecimal. *(Q4)*
5. Sort employees by salary descending. *(Q5)*
6. Sort by dept then salary desc. *(Q6)*
7. Collect unique skills across all employees. *(Q7)*
8. Get first 3 employees. *(Q8 limit)*
9. Skip first 2, then take next 3. *(Q8 skip+limit)*
10. Is there anyone earning > 12k? *(Q9 anyMatch)*
11. Are all employees adults? *(Q9 allMatch)*
12. Ensure none are under 18. *(Q9 noneMatch)*
13. Print the first employee, if present. *(Q10 findFirst)*
14. Count how many are active. *(Q11)*
15. Collect all department ids into a Set. *(Q12)*
16. Build id→name map. *(Q13)*
17. Join names with commas. *(Q14)*
18. Remove nulls from a list safely. *(Q15)*
19. Debug a pipeline with `peek`. *(Q16)*
20. Sum ages with IntStream. *(Q17)*
21. Convert IntStream back to List<Integer>. *(Q17)*
22. Create numbers 1..10 with `iterate`. *(Q18)*
23. Create 3 random UUIDs with `generate`. *(Q18)*
24. Reduce to the employee with max salary. *(Q19)*
25. Reduce to total salaries in parallel. *(Q20)*
26. Min and max salary using collectors. *(Q21)*
27. Use Optional with default employee. *(Q22)*
28. Average age of employees. *(Q23)*
29. Top-3 earners. *(Q24)*
30. Find 2nd highest salary using skip. *(Q25)*
31. Group by department to List<Employee>. *(Q26)*
32. Count employees per dept. *(Q27)*
33. Sum salary per dept (BigDecimal). *(Q28)*
34. Average salary per dept (double). *(Q29)*
35. Group by dept → names. *(Q30)*
36. Partition employees: salary >= 10k. *(Q31)*
37. Summarize salaries (count/min/max/avg). *(Q32)*
38. Make unmodifiable list of names. *(Q33)*
39. Preserve insertion order with LinkedHashMap. *(Q34)*
40. Group employees by join month (YYYY-MM). *(Q35)*
41. Put employees into salary buckets. *(Q36)*
42. Find duplicate names using a Set. *(Q37)*
43. List all projects across employees (distinct). *(Q38)*
44. List unique skills sorted by name. *(Q39)*
45. Build map projectName → employee names. *(Q40)*
46. Build concurrent id→name map in parallel. *(Q41)*
47. Show parallel forEach is unordered. *(Q42)*
48. Multi-level grouping: dept→gender→names. *(Q43)*
49. Partition by active then group by dept. *(Q44)*
50. Keep first occurrence by name (distinct-by-key). *(Q45)*
51. Custom collector: Set→CSV. *(Q46)*
52. Copy list as immutable. *(Q47)*
53. Sort list of names nulls last. *(Q48)*
54. Sort by dept, gender, age, name. *(Q49)*
55. toMap with merge: keep higher salary. *(Q50)*
56. Join names with [prefix,suffix]. *(Q51)*
57. Map id→UPPERCASE(name). *(Q52)*
58. Top-2 earners per dept. *(Q53)*
59. Cheapest employee per dept. *(Q54)*
60. Partition active and summarize salary. *(Q55)*
61. Show lazy evaluation prints only on terminal op. *(Q56)*
62. Show short-circuit with anyMatch. *(Q57)*
63. Compose reusable predicates. *(Q58)*
64. Handle checked exceptions in map(). *(Q59)*
65. Read file lines and filter adults. *(Q60)*
66. Payroll total per dept with groupingBy+reducing. *(Q61)*
67. Case-insensitive sort of names. *(Q62)*
68. Use parallel with custom ForkJoinPool. *(Q63)*
69. Top-1 salary per dept (window). *(Q64)*
70. Stable sort note: comparator by name. *(Q65)*
71. Cartesian product of skills for 2 employees. *(Q66)*
72. Count frequency of skills. *(Q67)*
73. Find most common skill. *(Q68)*
74. Map name→tenure in years. *(Q69)*
75. Group employees into tenure brackets. *(Q70)*
76. Project durations in days. *(Q71)*
77. Employees working on multiple projects. *(Q72)*
78. Build index by name. *(Q73)*
79. Case-insensitive "contains" search. *(Q74)*
80. Pagination: page 2, size 3 by name. *(Q75)*
81. Stream vs loop total salary equivalence. *(Q76)*
82. Average salary via custom collector. *(Q77)*
83. Sum salary: double vs BigDecimal. *(Q78)*
84. Cache filtered list, reuse multiple times. *(Q79)*
85. Dept name → list of employee names. *(Q80)*
86. Most frequent skill per dept (Java 8 version). *(See `q81_topSkillPerDept_Java8`)*
87. Partition by having any billable project. *(Q82)*
88. Optional-like flatMap with nicknames. *(Q83)*
89. Group by first letter of name. *(Q84)*
90. Remove inactive then group by dept. *(Q85)*
91. Collect to TreeMap sorted by key. *(Q86)*
92. Demonstrate side-effects risk; prefer collectors. *(Q87)*
93. Template pipeline: filter→map→sort→collect. *(Q88)*
94. When a for-loop is clearer (stateful). *(Q89)*
95. Recap checklist for stream hygiene. *(Q90)*
96. Convert List<Employee> to Map<id,Employee>. *(See Q73 pattern)*
97. Extract List<BigDecimal> of salaries. *(Q4 pattern)*
98. Build Set<String> of all project names. *(Q38 pattern)*
99. Find youngest employee. *(Q21 minBy)*
100. Find oldest employee. *(Q21 maxBy)*

## INTERMEDIATE (Q101–Q200)

101. Group by gender → average salary. *(Combine Q29 + gender)*
102. Dept→highest earner’s name. *(Q53 with limit 1 + mapping name)*
103. Dept→list of ages sorted asc. *(groupingBy + mapping + sorting)*
104. Dept→count of active employees. *(groupingBy + filtering via pre-filter)*
105. Dept→set of skills (merged). *(groupingBy + flatMap idea; see q81 Java8 approach)*
106. Partition by age >= 30. *(partitioningBy)*
107. Dept→billable vs non-billable counts. *(flatMap projects + grouping + counting)*
108. Names of employees without projects. *(filter size==0 + map name)*
109. Employees joined in 2023. *(filter year from joinDate)*
110. Highest paid active engineer (dept 10). *(filter + maxBy)*
111. Average age per department. *(groupingBy + averagingInt)*
112. Median salary overall. *(sort + middle element)*
113. Median salary per department. *(groupingBy + custom median)*
114. 90th percentile salary. *(sort + index)*
115. Unique list of departments names used. *(map DEPTS.get + name + set)*
116. Map name→count of vowels in name. *(map)*
117. Top 5 most skilled employees (by skills count). *(sort by size desc + limit)*
118. Names of employees having skill “SQL”. *(filter skills contains)*
119. Dept with maximum payroll spend. *(grouping + reducing + maxBy)*
120. Flatten list of (employee, project) pairs. *(flatMap; see Q40 idea)*
121. Sort by join date oldest→newest. *(sorted comparator)*
122. Sort by join date newest→oldest. *(reversed)*
123. First employee who joined after 2022-01-01. *(filter + findFirst)*
124. Does any employee have >3 skills? *(anyMatch)*
125. Count projects that are billable. *(flatMap + filter + count)*
126. Build Map<dept, avg tenure years>. *(grouping + map tenure)*
127. Most experienced employee per dept. *(grouping + maxBy tenure)*
128. Find employees missing “Java” but in Engineering. *(filter)*
129. List of (name, salary*12) annual pay. *(map)*
130. Map dept→top 3 skills (by frequency). *(grouping + frequency per dept)*
131. Map skill→employees who have it. *(inverse index)*
132. Employee name length statistics. *(summarizingInt)*
133. Find duplicates by id (simulate). *(toMap merge checks)*
134. Safe merge two employee lists (dedupe by id). *(concat + toMap)*
135. Chunk employees into pages of size N. *(sorted + skip/limit in loop)*
136. Validate all salaries > 0. *(allMatch)*
137. List inactive employees sorted by salary desc. *(filter + sort)*
138. First 5 distinct skills alphabetically. *(flatMap + distinct + limit)*
139. Count employees per join year. *(grouping by year)*
140. Build CSV of names per dept (sorted). *(grouping + mapping + joining)*
141. Build Map<dept, TreeSet<name>>. *(grouping + mapping + toCollection)*
142. Dept→employee with min age. *(grouping + minBy)*
143. Find gaps: employees without any skill. *(filter size==0)*
144. Names of employees on any non-billable project. *(flatMap + filter)*
145. Total number of assignments (employee→project links). *(flatMap count)*
146. Pair employees with same first letter. *(grouping by first letter)*
147. Stable sort by salary then name. *(comparator chain)*
148. Convert list of salaries to cumulative sum. *(map with state in loop; stream not ideal)*
149. Safely parallelize read-only transformations. *(parallel + map + collect)*
150. Avoid shared mutable state in parallel streams. *(see Q87 note)*
151. Use `Collectors.collectingAndThen` for post-processing. *(Q33)*
152. `Collectors.mapping` inside grouping. *(Q30)*
153. `Collectors.reducing` with identity/mapper/combiner. *(Q28)*
154. `Collectors.summarizingDouble` vs averaging. *(Q32)*
155. Immutable copy of Map results. *(wrap with `Collections.unmodifiableMap`)*
156. Use LinkedHashMap to retain sorted order. *(Q34)*
157. Convert Stream<String> to int[] safely. *(mapToInt + toArray)*
158. Check if list is sorted by name. *(pairwise check, loop preferred)*
159. Merge two maps using streams. *(Stream.of(m1,m2).flatMap(entrySet).collect)*
160. Implement `distinctByKey` utility. *(Q45 pattern)*
161. Validate no duplicate ids, else throw. *(toMap merge throwing)*
162. Partition by having more than 1 project. *(partitioningBy)*
163. Dept-wise average project count. *(grouping + averagingInt)*
164. Names of employees whose salary in top 10%. *(sort + threshold)*
165. Normalize names (trim, capitalize). *(map)*
166. Replace null skill sets with empty. *(map)*
167. Convert LocalDate to year-quarter buckets. *(map)*
168. Group by city (from Department). *(map via DEPTS + grouping)*
169. Build Map<city, payroll>. *(grouping by city + reducing)*
170. Find employees with overlapping skills above threshold. *(pairs; double loop easier)*
171. Build lookup of joinMonth→employees. *(Q35)*
172. Average age of active employees. *(filter + averagingInt)*
173. Names of employees not in Engineering. *(filter deptId!=10)*
174. Highest salary in Product. *(filter + maxBy)*
175. Total payroll for Sales. *(filter + reducing)*
176. Sort employees by number of skills. *(comparingInt + reversed)*
177. Extract list of all project start dates. *(flatMap project + map date)*
178. Count employees whose name starts with vowel. *(filter + count)*
179. Partition by age <30 vs >=30. *(partitioningBy)*
180. Compute hash-based buckets for employees. *(map name hash % N)*
181. Find first active employee in HR. *(filter + findFirst)*
182. Is there any employee with zero projects? *(anyMatch)*
183. List names of employees with exactly two projects. *(filter size==2)*
184. Dept→list of salaries sorted desc. *(grouping + collect/sort)*
185. Build CSV of all unique skills alphabetically. *(flatMap + distinct + sorted + joining)*
186. Map<dept, count of distinct skills>. *(grouping + flatMap per dept)*
187. Dept→earliest joiner. *(grouping + minBy joinDate)*
188. Dept→latest joiner. *(grouping + maxBy joinDate)*
189. Average tenure of active employees. *(filter + map tenure + average)*
190. Validate everyone has positive age. *(allMatch)*
191. Rank employees by salary (dense rank). *(sort + map with index; loop easier)*
192. Compute z-scores of salaries. *(map with mean/std; loop or two-pass)*
193. Extract domain: List<Project> without duplicates. *(Q38)*
194. Build TreeSet of employee names. *(map + toCollection TreeSet)*
195. Grant 5% raise to Engineering, map name→new salary. *(filter + map)*
196. Annual payroll by dept. *(monthly*12 then group)*
197. Find project with most employees assigned. *(inverse index + maxBy)*
198. Count employees per project (including zeros). *(all projects list + map)*
199. Names of employees on “CardAuth”. *(inverse index filter)*
200. For each dept, list names sorted by tenure desc. *(group + sort by years)*

## ADVANCED (Q201–Q300)

201. Custom collector for harmonic mean of salaries.
202. Quantiles (25/50/75) of salary overall.
203. Sliding window average age (stream not ideal; loop).
204. K-most common skills overall.
205. K-most common skills per dept.
206. Normalize salaries to min–max range.
207. Build bipartite graph employee↔project as edges.
208. Detect employees working only non-billable projects.
209. Validate all names unique; else report duplicates.
210. Build trie of employee names (stream to feed builder).
211. Compute Jaccard similarity of skill sets pairwise.
212. Detect outliers by MAD of salaries.
213. Build histogram of age in bins of 5 years.
214. Stream from database rows (simulate with list maps).
215. Stream over directory files and compute sizes.
216. Parallel map of heavy calculation safely.
217. Batch employees by dept and write CSV files per dept.
218. Group by city → highest paid active employee.
219. Compute time-to-hire by month from HR logs (lines).
220. Join employees with departments (map join).
221. Left-join with projects (some have none).
222. Deduplicate (id) keeping most recent joinDate.
223. Distinct skills preserving insertion order.
224. Partition projects into active vs finished.
225. Employees who switched departments (need history; simulate).
226. Stream pipeline performance benchmarking.
227. Memoize expensive skill normalization function.
228. Build adjacency of employees sharing at least 2 skills.
229. Serialize summary to JSON (via library; outside pure JDK in demo).
230. Compose predicates dynamically from UI filters.
231. Build comparator dynamically from sort fields.
232. Validate Luhn-like id check (non-stream note).
233. Compute salary growth since join by year.
234. Use `Collectors.teeing` (Java 12+, but emulate with two passes in Java 8).
235. Explain custom Spliterator to stream HR events.
236. Use `groupingByConcurrent` for large parallel datasets.
237. Stable pagination under concurrent modifications (note!).
238. Implement safe `mapMulti` (Java 16) via flatMap in Java 8.
239. Hot/cold stream differences (note: streams are one-shot).
240. Convert streams to reactive (Project Reactor) (beyond JDK).
241. When to avoid parallel due to small data sizes.
242. Understand boxing/unboxing costs in primitives.
243. Short-circuiting with limit/findFirst in ordered stream.
244. Handle exceptions with wrapper function returning Optional.
245. Implement retry for I/O inside map (wrap try-catch).
246. Grouping with custom key object (e.g., (dept, year)).
247. Explode map values (flatMap over entrySet()).
248. Invert Map<K, List<V>> to Map<V, List<K>>.
249. Compute rolling headcount per month (requires time series; loop+stream).
250. Partition employees by on-call eligibility predicate.
251. Build KPI dashboard values using summarizing collectors.
252. Detect circular references in org chart (graph; streams help with mapping, not traversal).
253. Use `reduce` to compute factorial (toy example).
254. Compute longest name length (mapToInt + max).
255. Build lookup of letter→count across all names.
256. Stream lines of a CSV, map to Employee objects (parsing; demo Q60 shows lines approach).
257. Use `Collectors.toCollection` for custom collection types.
258. Use `Collectors.reducing` for weighted sums.
259. Find employees whose skills superset {Java, SQL}.
260. Find pairs of employees on same project.
261. For each project, list distinct departments involved.
262. For each department, list projects covered.
263. Total days employees spent on billable projects (needs dates; approximate).
264. Identify idle employees (no project in last 90 days).
265. Build heatmap data: dept vs skill frequency.
266. Build cohort by join quarter and compute retention.
267. Prepare data for org chart (manager→reports) (needs hierarchy; simulate).
268. Implement `minBy`/`maxBy` via `reduce` manually.
269. Explain stream source reuse error and fix by collecting first.
270. Use `Collectors.partitioningBy` with downstream `mapping`.
271. Use `Collectors.groupingBy` with downstream `collectingAndThen` for top-N.
272. Build index Map<firstLetter, List<Employee>>.
273. Compute pairwise salary differences vs dept average.
274. Convert list to multimap (dept→employees).
275. Convert multimap back to flat list.
276. Merge duplicate employees by id, aggregating skills.
277. Difference between `map` and `flatMap` with examples.
278. Contrast `reduce` vs collector-based sum.
279. Explain why BigDecimal is preferred for money.
280. Use `Comparator.nullsFirst/nullsLast`.
281. Create `Predicate` combinators for dynamic filters.
282. Precompute expensive mappings before grouping to avoid recompute.
283. Simulate `groupingBy` + `mapping` with two passes.
284. Use `map` to call instance method references (Employee::toString).
285. Build lookup id→salary range label.
286. Combine two sorted streams (merge; loop easier).
287. Find Outliers (IQR) of age.
288. Build index skill→dept counts.
289. Dept→median salary ignoring outliers.
290. Compute Gini coefficient of salaries (advanced).
291. Detect palindromic names just for fun.
292. Randomly sample 3 employees (shuffle or `limit` after `Collections.shuffle`).
293. Shuffle stream (collect then shuffle).
294. Drop first while salary < X (Java 9 dropWhile; emulate with index).
295. Take while salary < X (emulate with index).
296. Time a pipeline using `System.nanoTime()`.
297. Pre-size collection to avoid resizing (use ArrayList with capacity when known size).
298. Explain terminal ops: why stream cannot be reused after collect.
299. Build read-only view vs defensive copy.
300. Create your own tiny Stream-like API (educational).

---

### Notes
- All runnable examples are in `EmployeeStreamPlaybook.java` Q1–Q90. Many questions above are small variations.
- Everything is Java 8 compatible in the Java file. Where later JDK features are mentioned (e.g., `flatMapping`, `takeWhile`), we explain Java 8 alternatives.
- For money, prefer `BigDecimal` (see Q4, Q20, Q28, Q77, Q78).
