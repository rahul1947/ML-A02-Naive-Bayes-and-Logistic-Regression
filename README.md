## ML-A02: Naive Bayes and Logistic Regression
Implementation of Naive Bayes and Logistic Regression Algorithms for Assignment 02 in the course CS6375: Machine Learning.

- [Rahul Nalawade](https://github.com/rahul1947) 

#### Problem: 
[Assignment 02](https://github.com/rahul1947/ML-A02-Naive-Bayes-and-Logistic-Regression/blob/master/Assignment%202.pdf)

#### Solution:
- [NaiveBayes](https://github.com/rahul1947/ML-A02-Naive-Bayes-and-Logistic-Regression/blob/master/NaiveBayes.java) 
- [LogisticRegression](https://github.com/rahul1947/ML-A02-Naive-Bayes-and-Logistic-Regression/blob/master/LogisticRegression.java) 
- [Report](https://github.com/rahul1947/ML-A02-Naive-Bayes-and-Logistic-Regression/blob/master/rsn170330_A02.pdf)

### How to Run:

**1. NAIVE BAYES:** 
```
Compile:
$ javac NaiveBayes.java
Execution:
$ java NaiveBayes <train dataset> <test dataset> <filter stopwords? yes/no>
Example: 
$ java NaiveBayes train test yes
```

**2. LOGISTIC REGRESSION:** 
```
Compile: 
$ javac LogisticRegression.java
Execution:
$ java LogisticRegression <train dataset> <test dataset> <remove stopwords? yes/no> <learning rate> <lambda> <iterations>
Example: 
$ java LogisticRegression train test no 0.75 0.75 100
```
