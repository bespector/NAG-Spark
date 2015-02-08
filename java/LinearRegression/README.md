# NAGLinearRegression on Spark

This examples uses a variety of NAG functions to compute a multiple linear regression (with constant term) on big data. The data is processed in blocks of *chucksize* set in the *NAGLinearRegression.java* file.

NAG functions used:
- [G02BU]  - Computes weighted sum of squares matrix
- [G02BZ]  - Combines two sums of squares matrices
- [G02BW]  - Computes a correlation matrix from a sum of squares matrix
- [G02CG]  - Multiple linear regression, from correlation coefficients, with constant term
- [F01ZA]  - Utility routine for matrix conversion

### The Data

The data is designed to predict the salary of an employee based on a number of factors (age [18>], years experience [0>], rating [0-10], industry [0, 1, 2]).

| Salery  | Age  | Experience  | Rating  | Industry  |
|---|---|---|---|---|
|0.0|18.0|0.0|3.58|0|
|68391.7|36.9|0.0 |9.94|2|
|54803.7|25.8|3.8 |5.91|1|
|62919.5|32.7|8.0 |2.18|2|

### Results
Prediction for 10 points:

Prediction: 72849.1 Actual: 70440.4

Prediction: 60568.0 Actual: 61580.5

Prediction: 66245.7 Actual: 67479.8

Prediction: 66640.6 Actual: 67588.3

Prediction: 67752.4 Actual: 69241.9

Prediction: 50944.3 Actual: 51197.7

Prediction: 62004.6 Actual: 62394.0

Prediction: 81347.8 Actual: 81243.0

Prediction: 70182.8 Actual: 71430.6

Prediction: 73303.2 Actual: 70555.6


[G02BU]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02buf.html
[G02BZ]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02bzf.html
[G02BW]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02bwf.html
[G02CG]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02cgf.html
[F01ZA]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/F01/f01zaf.html


