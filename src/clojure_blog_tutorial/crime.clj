(ns clojure-blog-tutorial.crime
  (:require [cheshire.core :as json]))

(defn load-json
  "Given a filename, reads a JSON file and returns it, parsed, with keywords."
  [file]
  (json/parse-string (slurp file) true))

(def fips
  "A map of FIPS codes to their county names."
  (->> (json/parse-string (slurp "fips.json") true)
       :table
       :rows
       (into {})))

(defn fips-code
  "Given a county (a map with :fips_state_code and :fips_county_code keys),
   returns the five-digit FIPS code for the county, as a string."
  [county]
  (str (:fips_state_code county) (:fips_county_code county)))

(defn most-duis
  "Given a JSON filename of UCR crime data for a particular year, finds the
  counties with the most DUIs."
  [file]
  (->> file
       load-json
       (sort-by :driving_under_influence)
       (take-last 10)
       (map (fn [county]
              [(fips (fips-code county))
               (:driving_under_influence county)]))
       (into {})))

(defn duis-by-prevalence
  [file]
  (->> file
       load-json
       (sort-by :driving_under_influence)
       (filter #(< 0 (:county_population %)))
       (map (fn [county]
              (let [duis-count (:driving_under_influence county)
                    population (:county_population county)]
                [(fips (fips-code county))
                 duis-count
                 (double (/ duis-count population))])))))