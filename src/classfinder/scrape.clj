(ns classfinder.scrape
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

(def base-url "https://admin.wwu.edu/pls/wwis/wwsktime.SelClass")

(defn fetch-url [url req-opts]
  (when (:method req-opts)
    (some-> req-opts
            (assoc :url url)
            client/request
            :body
            html/html-snippet)))

(defn get-list [select-name]
  (html/select (fetch-url base-url {:method :get})
               [[:select (html/attr= :name select-name)] :option]))

(defn get-terms []
  (get-list "term"))

(defn get-subjects []
  (get-list "sel_subj"))

(defn get-course-attributes []
  (get-list "sel_gur"))

(defn get-instructors []
  (get-list "sel_inst"))
