# NAGLinearRegression on Spark

This examples computes the Principal Component Analysis on a *JavaRDD<LabeledPoint>* or *JavaRDD<Vector>*.

NAG functions used:
- [G02BU]  - Computes weighted sum of squares matrix
- [G02BZ]  - Combines two sums of squares matrices
- [G02BW]  - Computes a correlation matrix from a sum of squares matrix
- [F08FA]  - Computes all eigenvalues and, optionally, eigenvectors of a real symmetrix matrix
- [F01ZA]  - Utility routine for matrix conversion


[G02BU]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02buf.html
[G02BZ]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02bzf.html
[G02BW]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/G02/g02bwf.html
[F08FA]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/F08/f08faf.html
[F01ZA]: http://www.nag.com/numeric/fl/nagdoc_fl24/html/F01/f01zaf.html


