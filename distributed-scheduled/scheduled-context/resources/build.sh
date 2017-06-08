#!/bin/bash
filelist='./dependency/*';
files='';
for file in $filelist
do
if [ -f $file ]
then
 files="$files$file:"
fi
done
java -cp $files com.zjaisino.task.FRSTask > taskinfo.log &