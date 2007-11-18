#!/bin/sh
rm -r *.xml *.txt


find -name "*.html" -exec replaceIn.sh "{}" \;
rm -r *.tmp
