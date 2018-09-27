# Brief Introduction
log-extractor is a simple log tool for extracting some basic information.

# The Core Problems To Solve
- Extract the information from the log files for analyzing problems.
- The VLOOKUP function is extreamly slow or even blocked when merging such a number of data 60,000 * 60,000.

# How To Configure It

The configure file `config.json` includes 3 parts: 

- log source: where to get the log files.
- the rules: how to extract the entities composite of many fields from log files.
- The output: how to join the entities and where to output the results. 

## Log sources

- path
- filter

## The rules

The entity rule matches a line of the log by the regex, match-Text, or the multi-matched Text. 

The property rule extracts the property value from matched line. There are 4 property rules: 

- boundary: cut the string with the attributes: beginText-endText, beginIndex-endIndex, beginIndex-length.
- regex: extract the string by regex-group Index.
- match: given the fixed value while matching the current line. 
- const: the fixed value, no matched-conditions required

## The output

- outputPath
- output 
	+ main entity rule
	+ join entity rules

# The Plan
- Support simple data type and format(date-time, number) in order to do more in-depth analysis.
- Parse logs with the blocks not only lines.
- Ouput data into a file with Excel format directly. 
