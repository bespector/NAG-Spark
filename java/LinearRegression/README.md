# NAGLinearRegression on Spark

This examples uses a variety of NAG functions to compute a multiple linear regression on big data. The data is process in blocks of *chucksize* set in the *NAGLinearRegression.java* file.

NAG functions used:
- G02BU (Computes Weighted Sum of Squares Matrix)
- G02BZ (Combines two sums of squares matrices)
- G02CG (Multiple linear regression, from correlation coefficients, with constant term)
- G02BW (Computes a correlation matrix from a sum of squares matrix)
- F01ZA (Utility routine for matrix conversion)

### The Data

The data is generated in the same format as the mllib regression data. It is designed to predict the salary of an employee based on a number of factors (age, experience, supervisor rating, industry).

| Salery  | Age  | Experience  | Rating  | Industry  |
|---|---|---|---|---|
|0.0|18.0|0.0|3.58|0|
|68391.7|36.9|0.0 |9.94|2|
|54803.7|25.8|3.8 |5.91|1|
|62919.5|32.7|8.0 |2.18|2|

You can generate the data via
```sh
$  java LinearRegression generateData <folder> <numRecords> <numFiles>"
```