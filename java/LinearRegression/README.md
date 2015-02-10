# NAGLinearRegression on Spark

This examples uses a variety of NAG functions to compute a multiple linear regression (with constant term) on big data. The data is processed in blocks of *chucksize* set in the *NAGCorrelation.java* file.

NAG functions used:
- [G02BU]  - Computes weighted sum of squares matrix
- [G02BZ]  - Combines two sums of squares matrices
- [G02BW]  - Computes a correlation matrix from a sum of squares matrix
- [G02CG]  - Multiple linear regression, from correlation coefficients, with constant term
- [F01ZA]  - Utility routine for matrix conversion

### The Data
We tested the linear regression functionality on two datasets:

1. The MLLIB Data
2. Data designed to predict the salary of an employee based on a number of factors (age [18>], years experience [0>], rating [0-10], industry [0, 1, 2]).

| Salery  | Age  | Experience  | Rating  | Industry  |
|---|---|---|---|---|
|0.0|18.0|0.0|3.58|0|
|68391.7|36.9|0.0 |9.94|2|
|54803.7|25.8|3.8 |5.91|1|
|62919.5|32.7|8.0 |2.18|2|

## Results
Prediction for 5 points:
## Dataset 1
|Actual Value	|	NAG Value|	MLLIB Value|
|---|---|---|
|2.553		|	2.623	|	-0.224	|
|-0.163		|	0.449	|	-1.135	|
|2.973		|	3.027	|	0.111	|
|3.013		|	3.195	|	1.055	|
|0.372		|	1.700	|	-0.425	|

## Dataset 2
|Actual Value	|	NAG Value|	MLLIB Value|
|---|---|---|
|66251.300|			66418.811|		NaN|	
|61928.800|			61255.407|		NaN|	
|71142.700|			72274.045|		NaN|	
|49972.500|			47876.686|		NaN|	
|61774.400|			62186.918|		NaN|	

[G02BU]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02buf.html
[G02BZ]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02bzf.html
[G02BW]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02bwf.html
[G02CG]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02cgf.html
[F01ZA]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/F01/f01zaf.html


