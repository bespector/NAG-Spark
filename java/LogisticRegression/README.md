# NAGLogisticRegression on Spark

This examples computes a logistic regression on sampledata.

NAG functions used:
- [E04KY] - Minimum, function of several variable, quasi-Newton algorithm, simple bounds, using first derivatives

### The Data

The data is a simple logistic regression. In addition, there is a constant term not included in the files.

| Dep  | Var1  | Var2  | Var3  |
|---|---|---|---|
|0| 1.73158| -1.03252| -1.08883|
|1| 1.71986| 2.28366| 1.20594|
|1| 1.88358| 1.95575| -0.925548|
|1| 1.11407| 1.40503| -0.661645|

[E04KY]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/E04/e04kyf.html
