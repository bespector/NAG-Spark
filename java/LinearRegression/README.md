# NAGLinearRegression on Spark

This examples uses a variety of NAG functions to compute a multiple linear regression on big data. The data is process in blocks of *chucksize* set in the *NAGLinearRegression.java* file.

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

[G02BU]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02buf.html
[G02BZ]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02bzf.html
[G02BW]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02bwf.html
[G02CG]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02cgf.html
[F01ZA]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/F01/f01zaf.html


