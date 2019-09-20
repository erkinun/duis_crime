(ns clojure-blog-tutorial.crime-test
  (:require [clojure.test :refer :all]
            [clojure-blog-tutorial.crime :refer :all]))

(deftest fips-code-test
  (is (= "12345" (fips-code {:fips_state_code "12" :fips_county_code "345"}))))

; {:city_name "NJ, Salem",
;  :crime-count 1432,
;  :population 65968,
;  :prevalence 0.02170749454280863}
