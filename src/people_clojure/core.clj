(ns people-clojure.core
  (:require [clojure.string :as str] ;creating a new namespace
            [clojure.walk :as walk] ;converts keys to keywords
            [compojure.core :as c]
            [ring.adapter.jetty :as j])
  
  (:gen-class))

(defn read-people []
  (let [people (slurp "people.csv") ; reads file, defines people variable
        people (str/split-lines people) ; splits into lines, keep overwriting people variable with new fxn
        people (map (fn [line] ; map applies function to a collection
                      (str/split line #",")) ; regular expression in  has # in front of string, separating into columns
                    people)
        header (first people)
        people (rest people)
        people (map (fn [line]
                      (apply hash-map (interleave header line))) ; join 2 vectors, parse into HM
                    people) ; second argument
        people (walk/keywordize-keys people) ; keys to keywords
        people (filter (fn [line]
                        (= (:country line) "Brazil"))
                       people)]
    ;(spit "filtered_people.edn" (pr-str people)) ; to write file                     
    people))

(c/defroutes app
  (c/GET "/" request
    "Hello, world!"))

(defn -main []
  (j/run-jetty app {:port 3000})) ; ring for web



