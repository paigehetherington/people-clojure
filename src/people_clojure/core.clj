(ns people-clojure.core
  (:require [clojure.string :as str] ;creating a new namespace
            [clojure.walk :as walk] ;converts keys to keywords
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [ring.middleware.params :as p]
            [hiccup.core :as h])
  
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
        people (walk/keywordize-keys people)] ; keys to keywords
       
    ;(spit "filtered_people.edn" (pr-str people)) ; to write file                     
    people))

(defn countries-html [people]
  (let [all-countries (map :country people)
        unique-countries (set all-countries)
        sorted-countries (sort unique-countries)]
    [:div
     (map (fn [country]
            [:span
             [:a {:href (str "/?country=" country)} country]
             " "])
       sorted-countries)]))

(defn people-html [people]
  [:ol
   (map (fn [person]
          [:li (str (:first_name person) " " (:last_name person))])
      people)])

(c/defroutes app
  (c/GET "/" request
    (let [params (:params request)
          country (get params "country")
          country (or country "United States")
          people (read-people)
          filtered-people (filter (fn [person]
                                    (= (:country person) country))
                            people)]
      (h/html [:html 
               [:body 
                (countries-html people)
                (people-html filtered-people)]])))) ; hiccup for html

(defn -main []
  (j/run-jetty (p/wrap-params app) {:port 3000})) ; ring for web



