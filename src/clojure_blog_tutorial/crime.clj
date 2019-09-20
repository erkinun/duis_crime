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

(defn compute-crime
  [file field-name]
  (->> file
       load-json
       (sort-by field-name)
       (filter #(< 0 (:county_population %)))
       (map (fn [county]
              (let [crime-count (field-name county)
                    population (:county_population county)]
                {:city_name (fips (fips-code county))
                 :crime-count crime-count
                 :population population
                 :prevalence (double (/ crime-count population))})))))

(defn most-prevalent
  [file field-name]
  (sort-by field-name (compute-crime file field-name)))

(defn duis-by-prevalence
  [file]
  (most-prevalent file :driving_under_influence))

(defn most-duis
  "Given a JSON filename of UCR crime data for a particular year, finds the
  counties with the most DUIs."
  [file]
  (take-last 10 (compute-crime file :driving_under_influence)))