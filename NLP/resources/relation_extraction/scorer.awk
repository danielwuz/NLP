#!/usr/bin/awk -f
BEGIN {
    print "-------------Stats----------" ;
}
$6 == "ARG1" && $7=="PRE_ARG1"  { tp++ ;}
$6 == "PRE_ARG1"                { fp++ ; print ;}
$6 != "ARG1" && $7=="PRE_ARG1"  { fp++ ; print ;}
$6 == "ARG1" && $7!="PRE_ARG1"  { fn++ ; print ;}

END { 
    precision = tp/(tp+fp);
    recall = tp/(tp+fn);
    f1 = 2*precision*recall/(precision+recall)
    print "True Positive: " tp ;
    print "False Positive: " fp;
    print "False Negative: " fn;
    print "Precision: " precision;
    print "Recall: " recall;
    print "F1 :" f1;
}