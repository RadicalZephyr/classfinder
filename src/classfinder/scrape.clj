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

(defn option->map [{{id :value} :attrs
                    [name] :content}]
  {:id id
   :name name})

(defn get-list [select-name]
  (map option->map
       (-> base-url
           (fetch-url {:method :get})
           (html/select [[:select
                          (html/attr= :name select-name)]
                         :option]))))

(defn get-terms []
  (get-list "term"))

(defn get-subjects []
  (drop 1 (get-list "sel_subj")))

(defn get-course-attributes []
  (drop 1 (get-list "sel_gur")))

(defn get-instructors []
  (drop 1 (get-list "sel_inst")))
