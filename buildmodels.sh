#!/usr/bin/env sh
# ./buildmodels.sh <training corpus dir> <training edit1s file>

java -Xmx8024m -cp classes edu.stanford.cs276.BuildModels $1 $2

