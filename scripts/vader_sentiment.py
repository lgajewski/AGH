#!/usr/bin/python

import json

from optparse import OptionParser
from tqdm import tqdm
from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer
from pprint import pprint

# CMD LINE PARSER
parser = OptionParser()
parser.add_option("-i", dest="input_file", help="input file")
parser.add_option("-o", dest="output_file", help="output file")
(options, args) = parser.parse_args()

if (options.input_file is None):
    print("Wrong arguments! Type -h for more info.")
    quit()

if (options.output_file is None):
    options.output_file = options.input_file

# SCRIPT
keys_to_copy = ["reviewerID", "asin", "reviewerName", "overall", "unixReviewTime"]

output_reviews = []

try:
    with open(options.input_file) as json_data:
        input_reviews = json.load(json_data)
except ValueError:
    print("Oops! Unable to parse input file as a JSON, trying to fix.")
    with open(options.input_file) as json_data:
        input_reviews = json.loads("[" + ",".join(json_data.readlines()) + "]")

analyzer = SentimentIntensityAnalyzer()
for review in tqdm(input_reviews):
    sentiment = analyzer.polarity_scores(review.get("reviewText", ""))
    output_review = { key: str(review.get(key, "")) for key in keys_to_copy }
    output_review["reviewSentiment"] = sentiment["compound"]
    output_reviews.append(output_review)

with open(options.output_file, 'w') as outfile:
    json.dump(output_reviews, outfile, indent=2, sort_keys=True)
