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

(def form-url "https://admin.wwu.edu/pls/wwis/wwsktime.ListClass")

(def default-params
  {:form-params
   {:sel_day  "dummy"
    :sel_open "dummy"
    :sel_crn  ""
    :sel_gur  ["dummy" "dummy"]
    :sel_subj ["dummy" "dummy"]
    :sel_inst "ANY"
    :sel_crse ""
    :begin_hh  0
    :begin_mi "A"
    :end_hh    0
    :end_mi   "A"
    :sel_cdts "%"}})

(def quarter->code {:winter 10
                    :spring 20
                    :summer 30
                    :fall   40})

(defn term-code [year quarter]
  (str year (quarter->code quarter)))

(defn find-classes [{{:keys [term sel_subj sel_inst sel_gur]}
                     :form-params
                     :as opts}]
  (if (and (or sel_subj sel_inst sel_gur)
           term)
    (let [opts (merge-with merge default-params opts)
          response-resource
          (-> form-url
              (client/post opts)
              :body
              html/html-snippet)]
      (if-not (html/select response-resource
                             [:h3 :b #(html/content %)])
        (html/select response-resource
                     [:tr])
        (do
          (println "Got a \"No classes found\" message.")
          response-resource)))
    (println "Didn't have the right stuff!")))
