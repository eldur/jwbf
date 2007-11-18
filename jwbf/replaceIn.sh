#!/bin/sh

file=$1
echo "$file"
dos2unix "$file"
temp1='temp.tmp'
temp2='temp2.tmp'
tr '\n' '§' < "$file" > "$temp1"
sed -e :a -e 's/\(<script[^>]*>.*<\/script>\)//g;/</N;//ba' < "$temp1" > "$temp2"
tr '§' '\n' < "$temp2" > "$file"
